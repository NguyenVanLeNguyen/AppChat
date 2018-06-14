package com.example.nguyen.appchat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nguyen.appchat.Model.Messages;
import com.example.nguyen.appchat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private final Context context;

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    final DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public MessageAdapter(Context context, List<Messages> mMessageList) {
        this.context = context;

        this.mMessageList = mMessageList;

    }

    @Override
    public int getItemViewType(int position) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userID.equals(mMessageList.get(position).getFrom())){
            return 0;
        }
        return 1;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.r_message_single_layout, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout, parent, false);
        }
        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLayout;
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView displayTime;
        public MapView showLocation;
        public double lat = 0.0;
        public double lng = 0.0;
        public GoogleMap map;
        public MessageViewHolder(View view) {
            super(view);

            rootLayout = view.findViewById(R.id.message_single_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            displayTime = (TextView) view.findViewById(R.id.time_text_layout);
            showLocation = (MapView) view.findViewById(R.id.mapView2);

            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);


        }


    }

    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.map != null) {
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                viewHolder.displayName.setText(name);

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        Date time = new Date(c.getTime());
        String timestr = df1.format(time);
        if(message_type.equals("text") ) {
            viewHolder.showLocation.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageImage.setVisibility(View.GONE);

            viewHolder.messageText.setText(c.getMessage());

            viewHolder.displayTime.setText(timestr);


        } else if(message_type.equals("image")) {

            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.showLocation.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

            viewHolder.displayTime.setText(timestr);
        }else if(message_type.equals("location")){
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.showLocation.setVisibility(View.VISIBLE);
            String currentString = c.getMessage();
            viewHolder.displayTime.setText(timestr);
            String[] separated = currentString.split(",");

            viewHolder.lat = Double.parseDouble(separated[0]);
            viewHolder.lng = Double.parseDouble(separated[1]);
            //  MapView.LayoutParams params=new MapView.LayoutParams(320,200);
            // viewHolder.showLocation.setLayoutParams(new MapView.LayoutParams(320,200));
            if (viewHolder.showLocation != null) {
                viewHolder.showLocation.onCreate(null);
                viewHolder.showLocation.onResume();
                viewHolder.showLocation.getMapAsync(googleMap -> {
                    MapsInitializer.initialize(context.getApplicationContext());
                    viewHolder.map = googleMap;

                    LatLng coordinates = new LatLng(viewHolder.lat, viewHolder.lng);
                    googleMap.addMarker(new MarkerOptions().title("My Location")
                            .position(coordinates));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

                });
            }


        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}