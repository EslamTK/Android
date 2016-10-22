package com.master.uberclone;

import android.location.Location;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestsListener {

    public static void StartListening() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChild("Driver")) return;
                UserInfo user = new UserInfo((String) dataSnapshot.child("id").getValue(), (String) dataSnapshot.child("mobile").getValue()
                        , (double) dataSnapshot.child("latitude").getValue(), (double) dataSnapshot.child("longitude").getValue());
                Location loc = new Location("");
                loc.setLatitude(user.getLatitude());
                loc.setLongitude(user.getLongitude());
                ReqList.Users.add(user);
                ReqList.Distances.add(Integer.toString((Math.round(ReqList.location.distanceTo(loc))) / 1000) + " Km From You");
                ReqList.arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DriverMap.onUserCancelRequest((String) dataSnapshot.child("id").getValue());
                for (int i = 0; i < ReqList.Users.size(); ++i) {
                    if (ReqList.Users.get(i).getId() == dataSnapshot.child("id").getValue()) {
                        ReqList.Users.remove(i);
                        ReqList.Distances.remove(i);
                        ReqList.arrayAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
