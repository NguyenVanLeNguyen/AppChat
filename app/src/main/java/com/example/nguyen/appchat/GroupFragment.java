package com.example.nguyen.appchat;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private Button creatButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mGroupsDatabase;
    private DatabaseReference mMessDatabase;
    private RecyclerView mGroupsList;
    View view;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group, container, false);
        creatButton =  view.findViewById(R.id.grfr_creat_btn);
        mGroupsList = view.findViewById(R.id.groups_list);
        mAuth = FirebaseAuth.getInstance();
        mGroupsDatabase =  FirebaseDatabase.getInstance().getReference().child("Groups");
        mMessDatabase = FirebaseDatabase.getInstance().getReference();
        mGroupsList.setHasFixedSize(true);
        mGroupsList.setLayoutManager(new LinearLayoutManager(getContext()));

       LayoutInflater uinflater = inflater;
       makegroup(uinflater);




        return view;
    }


    private void makegroup(final LayoutInflater uinflater) {
        creatButton.setOnClickListener(new View.OnClickListener() {
            final String mCurrentUserId = mAuth.getCurrentUser().getUid();

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Sự Kiện", Toast.LENGTH_SHORT).show();
                View uview = uinflater.inflate(R.layout.group_name_dialog, null);
                final TextInputLayout groupNametxt  = uview.findViewById(R.id.group_name_edt);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setView(uview);


                builder.setPositiveButton("Creat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String groupId = groupNametxt.getEditText().getText().toString()+mCurrentUserId  ;
                        mGroupsDatabase =  FirebaseDatabase.getInstance().getReference().child("Groups").child(mCurrentUserId);
                        HashMap<String, String> groupMap = new HashMap<>();
                        groupMap.put("owner",mCurrentUserId);
                        groupMap.put("name",groupNametxt.getEditText().getText().toString());
                        mGroupsDatabase.child(groupId).setValue(groupMap);

                        Map messGroupMap = new HashMap();
                        messGroupMap.put( "messages/" + mCurrentUserId + "/" + groupId , null);
                        mMessDatabase.updateChildren(messGroupMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError == null){
                                    Toast.makeText(getActivity(), "succes!!", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        Toast.makeText(getActivity(), groupId, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Canel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();


        final String mCurrentUserId = mAuth.getCurrentUser().getUid();
        final DatabaseReference listtGroupDatabase =  mGroupsDatabase.child(mCurrentUserId);
        FirebaseRecyclerAdapter<Group,GroupViewHolder> firebaseGroupAdapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>(
                Group.class,
                R.layout.groups_singer_layout,
                GroupViewHolder.class,
                listtGroupDatabase
        ) {
            @Override
            protected void populateViewHolder(final GroupViewHolder viewHolder, Group model, int position) {
                final String list_user_id = getRef(position).getKey();
                final String newGroupName = model.getName();
                viewHolder.setName(newGroupName);
              //  viewHolder.addlis(getRef(position).getParent().getKey().toString());
                final String groupId = getRef(position).getKey().toString();

                Button addButton = viewHolder.addlis();
               addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(getActivity(), ListMemberActivity.class);
                        profileIntent.putExtra("group_id", groupId);
                        profileIntent.putExtra("group_name", newGroupName);

                        startActivity(profileIntent);
                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                        chatIntent.putExtra("user_id", groupId);
                        chatIntent.putExtra("user_name", newGroupName);
                        chatIntent.putExtra("type", "Group");
                        startActivity(chatIntent);
                    }
                });
            }
        };

        mGroupsList.setAdapter(firebaseGroupAdapter);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userNameView;
        public GroupViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            userNameView = (TextView) mView.findViewById(R.id.group_single_name);

        }



        public void setName(String name){


            userNameView.setText(name);

        }

        public Button addlis(){
            Button addButton = mView.findViewById(R.id.group_add_Button);
            return addButton;
        }



    }

   /* public  void setonclik(final String groupId , View mView){
        Button addbut = mView.findViewById(R.id.group_add_Button);
        addbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), ListMemberActivity.class);
                profileIntent.putExtra("group_id", groupId);
                startActivity(profileIntent);
            }
        });
    }*/


}
