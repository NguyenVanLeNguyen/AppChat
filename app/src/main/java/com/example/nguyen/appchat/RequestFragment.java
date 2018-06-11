package com.example.nguyen.appchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView mRequestList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    private View mMainView;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mRequestDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRequestList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);

        mRequestDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersDatabase.keepSynced(true);


        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(mLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Request,RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(

                Request.class,
                R.layout.users_single_layout,
                RequestViewHolder.class,
                mRequestDatabase

        ) {
            @Override
            public void onBindViewHolder(final RequestViewHolder viewHolder, final int position) {
                super.onBindViewHolder(viewHolder, position);



            }

            @Override
            protected void populateViewHolder(final RequestViewHolder usersViewHolder, final Request users, final int position) {
                usersViewHolder.hideDate();
                final String list_user_id = getRef(position).getKey();
                if(users.getRequest_type().equals("sent")){
                    usersViewHolder.mView.setVisibility(View.GONE);
                    usersViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                else{
                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            /**/

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            if (dataSnapshot.hasChild("online")) {

                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                usersViewHolder.setUserOnline(userOnline);

                            }

                            usersViewHolder.setName(userName);
                            usersViewHolder.setUserImage(userThumb, getContext());

                            usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                    profileIntent.putExtra("user_id", list_user_id);
                                    startActivity(profileIntent);

                                }
                            });
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                }

        };




        mRequestList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void hideDate(){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setVisibility(View.INVISIBLE);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }


}
