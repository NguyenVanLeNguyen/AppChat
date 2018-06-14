package com.example.nguyen.appchat.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nguyen.appchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;
    private ListaUserAdapter listaUserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
        ArrayList<String> li = getListUidInGroup();

         listaUserAdapter = new ListaUserAdapter(li);


        mUsersList.setAdapter(listaUserAdapter);

    }
   /* @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.searchmenu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listaUserAdapter.filter("");

                } else {
                    listaUserAdapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }*/
    @Override
    protected void onStart() {
        super.onStart();



    }


   /* private ArrayList<String> filter(ArrayList<String> mUsers, String query) {

        final ArrayList<String> filteredModelList = new ArrayList<String>();
        for (String uid : mUsers) {

           mUsersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   String name = dataSnapshot.child("name").getValue().toString();
                   if(name.contains(query)){
                       filteredModelList.add(name);
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
        }
        return filteredModelList;
    }*/

    public class ListaUserAdapter extends
            RecyclerView.Adapter<UsersViewHolder> {
        private List<String> mUsers;
        private List<String> mSearchUsers;



        // Pass in the contact array into the constructor

        public ListaUserAdapter(List<String> mUsers) {
            this.mUsers = mUsers;
            this.mSearchUsers = new ArrayList<String>(mUsers);
            //Toast.makeText(UsersActivity.this,"mUser: " + mUsers.size()+" ",Toast.LENGTH_SHORT).show();
            //Toast.makeText(UsersActivity.this,mSearchUsers.size()+" ",Toast.LENGTH_SHORT).show();

        }

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.users_single_layout, parent, false);

            UsersViewHolder viewHolder;
            viewHolder = new UsersViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
            String uid = mUsers.get(position);
           // Toast.makeText(UsersActivity.this,"li:"+ mUsers.size() ,Toast.LENGTH_SHORT).show();
            mUsersDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.setDisplayName(dataSnapshot.child("name").getValue().toString());
                    holder.setUserStatus(dataSnapshot.child("status").getValue().toString());
                    holder.setUserImage(dataSnapshot.child("thumb_image").getValue().toString(), getApplicationContext());
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("user_id", uid);
                            startActivity(profileIntent);
                        }

                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

       /* public void setSearchResult(ArrayList<String> result) {
            mUsers = result;
            notifyDataSetChanged();
        }
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            //Toast.makeText(UsersActivity.this,charText,Toast.LENGTH_SHORT).show();
            mUsers.clear();
            if (charText.length() == 0) {
                mUsers.addAll(mSearchUsers);
            } else {
                Toast.makeText(UsersActivity.this,mSearchUsers.size()+" ",Toast.LENGTH_SHORT).show();
                for (String s : mSearchUsers) {

                    String finalCharText = charText;

                    mUsersDatabase.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            if (name.toLowerCase(Locale.getDefault()).contains(finalCharText)) {

                                mUsers.add(s);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
            notifyDataSetChanged();
        }*/

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

    }
    private ArrayList<String> getListUidInGroup(){
        ArrayList<String> listmember;
        listmember = new ArrayList<>();
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot  uiduser: dataSnapshot.getChildren() ){
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
