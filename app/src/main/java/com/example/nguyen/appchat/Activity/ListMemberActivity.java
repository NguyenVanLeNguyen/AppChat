package com.example.nguyen.appchat.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguyen.appchat.Model.Users;
import com.example.nguyen.appchat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListMemberActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mFriendsList;
    private Button mFriendsButton;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserInGroupDatabase;
    private List<String> idList;
    private LinearLayoutManager mLayoutManager;
    private String groupId;
    private String groupName;
    private String type;
    private FirebaseAuth mAuth;
    private DatabaseReference mMessDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.friends_appBar);
        setSupportActionBar(mToolbar);
        groupId = getIntent().getStringExtra("group_id");
        groupName = getIntent().getStringExtra("group_name");
        type = getIntent().getStringExtra("type");
       // Toast.makeText(this, groupId, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        idList = new ArrayList<String>();
        final String mCurrentUserid = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserid);
        mUserInGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        mMessDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mLayoutManager = new LinearLayoutManager(this);
        mFriendsButton = (Button) findViewById(R.id.listmem_creat_btn) ;
                mFriendsList = (RecyclerView) findViewById(R.id.mfriends_in_group_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(mLayoutManager);

        if(type.equals("add")){
            mFriendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!idList.isEmpty()){
                        for (String uid: idList){
                            Map requestMap = new HashMap<String,String>();
                            requestMap.put("Groups/" + uid + "/" + groupId + "/name", groupName);
                            requestMap.put("Groups/" + uid + "/" + groupId + "/owner", mCurrentUserid);
                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError != null){
                                        Toast.makeText(getApplicationContext(), "error!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                    /*String toast = new String();
                    for (String uid: idList){
                        toast += uid;
                        toast += "/\n";
                    }
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();*/

                    finish();
                }
            });

        }
        else if(type.equals("remove") || type.equals("removenotowner")){

            if(type.equals("removenotowner")){
                mFriendsButton.setVisibility(View.GONE);

            }
            else{
                mFriendsButton.setVisibility(View.VISIBLE);
                mFriendsButton.setText("Remove Member");
            }

            mFriendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!idList.isEmpty()){
                        for (String uid: idList){
                            Map requestMap = new HashMap<String,String>();
                            requestMap.put("Groups/" + uid + "/" + groupId , null);
                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError != null){
                                        Toast.makeText(getApplicationContext(), "error!!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            finish();

                        }
                    }
                }
            });
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        idList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(

                Users.class,
                R.layout.friends_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {
            @Override
            public void onBindViewHolder(final FriendsViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);
                final String user_id = getRef(position).getKey();
                viewHolder.checkBox.setTag(position);
                viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        if(cb.isChecked())
                            idList.add(user_id);
                        else{
                            for (String uid: idList) {
                                if(uid.equals(user_id)){
                                    idList.remove(uid);
                                    //break;
                                }
                            }
                        }


                    }
                });
                mMessDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(groupId) && type.equals("add")) {
                            viewHolder.checkBox.setVisibility(View.INVISIBLE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Users users, final int position) {
                final String user_id = getRef(position).getKey();


                final String mCurrentUserid = mAuth.getCurrentUser().getUid();

                /*mcheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mcheck.isChecked()){
                            Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_LONG).show();

                        }
                    }
                });*/



                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userstatus = dataSnapshot.child("status").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.setDisplayName(userName);
                        friendsViewHolder.setUserStatus(userstatus);
                        friendsViewHolder.setUserImage(userThumb, getApplicationContext());


                       /* mcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                if(mcheck.isChecked()){
                                    Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_LONG).show();

                                }

                            }
                        });*/




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        if(type.equals("add")){
            mFriendsList.setAdapter(firebaseRecyclerAdapter);
        }
       else if(type.equals("remove") || type.equals("removenotowner")){
            MemberAdapter memberAdapter = new MemberAdapter(getListUidInGroup());

            mFriendsList.setAdapter(memberAdapter);
        }



    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CheckBox checkBox ;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            checkBox = (CheckBox) mView.findViewById(R.id.checkBox);

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.friend_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.friend_single_status);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.friend_single_image);

            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


        public CheckBox getCheckbox(){

            CheckBox checkBox = (CheckBox) mView.findViewById(R.id.checkBox);
            return checkBox;


        }


        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.friend_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }

    public class MemberAdapter extends
            RecyclerView.Adapter<ViewHolder> {
        private List<String> mMembers;

        // Pass in the contact array into the constructor
        public MemberAdapter(List<String> members) {
            mMembers = members;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.friends_single_layout, parent, false);

            ViewHolder viewHolder;
            viewHolder = new ViewHolder(contactView);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                String uidUser = mMembers.get(position);
            mUsersDatabase.child(uidUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    if(dataSnapshot.hasChild("online")) {

                        String userOnline = dataSnapshot.child("online").getValue().toString();
                        holder.setUserOnline(userOnline);

                    }

                    holder.setDisplayName(userName);
                    holder.setUserStatus(userstatus);
                    holder.setUserImage(userThumb, getApplicationContext());
                    if(type.equals("removenotowner")){
                        holder.checkBox.setVisibility(View.GONE);
                    }
                    else{
                    holder.checkBox.setTag(position);
                    holder.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            if(cb.isChecked())
                                idList.add(uidUser);
                            else{
                                for (String uid: idList) {
                                    if(uid.equals(uidUser)){
                                        idList.remove(uid);
                                        //break;
                                    }
                                }
                            }


                        }
                    });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return mMembers.size();
        }


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {


        View mView;
        CheckBox checkBox ;
        public ViewHolder (View itemView) {
            super(itemView);

            mView = itemView;
            checkBox = (CheckBox) mView.findViewById(R.id.checkBox);

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.friend_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.friend_single_status);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.friend_single_image);

            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


        public CheckBox getCheckbox(){

            CheckBox checkBox = (CheckBox) mView.findViewById(R.id.checkBox);
            return checkBox;


        }


        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.friend_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }
    private List<String> getListUidInGroup(){
        ArrayList<String> listmember = new ArrayList<>();
        mUserInGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot  uiduser: dataSnapshot.getChildren() ){
                    if(uiduser.hasChild(groupId))
                        listmember.add(uiduser.getKey().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return listmember;
    }

}
