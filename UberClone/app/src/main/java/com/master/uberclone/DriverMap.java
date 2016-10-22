package com.master.uberclone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverMap extends Activity implements OnMapReadyCallback {
    static Button AcBtn;
    static Activity activity;
    static GoogleMap Map;
    static UserInfo user;
    static LatLng Driver, Customer;

    public static void onUserCancelRequest(String id) {
        if (user == null) return;
        if (id == user.getId()) {
            new AlertDialog.Builder(activity).setTitle("Request Canceled").setMessage("The User Request You Are Viewing Now Is Canceled")
                    .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                }
            });
        }
    }

    public static void ChangeMapLocation(Location location) {
        if (Map != null) {
            Map.clear();
            Driver = new LatLng(location.getLatitude(), location.getLongitude());
            Customer = new LatLng(user.getLatitude(), user.getLongitude());
            Map.animateCamera(CameraUpdateFactory.newLatLngZoom(Driver, 12));
            Map.addMarker(new MarkerOptions().position(Driver).title("Driver"));
            Map.addMarker(new MarkerOptions().position(Customer).title("Customer").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            Map.addPolyline(new PolylineOptions().add(Customer, Driver).width(5).visible(true));
        }
        if (AcBtn != null) {
            if (AcBtn.getVisibility() == View.INVISIBLE) {
                try {
                    FirebaseDatabase.getInstance().getReference().child("Requests").child(user.getId()).child("Driver").setValue(Driver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        activity = this;
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        map.getMapAsync(this);

        final int userIndex = getIntent().getIntExtra("UserIndex", -1);
        user = ReqList.Users.get(userIndex);

        AcBtn = (Button) findViewById(R.id.AcReq);
        AcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(user.getId());
                    dataRef.removeValue();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + user.getMobile()));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        ChangeMapLocation(ReqList.location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            new AlertDialog.Builder(this).setTitle("Accept Request").setMessage("Do You Want To Accept This Request")
                    .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("Requests").child(user.getId()).child("Driver").setValue(Driver)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AcBtn.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).setNegativeButton("No", null).create().show();
        }
    }
}
