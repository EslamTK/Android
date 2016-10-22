package com.master.uberclone;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerMap extends AppCompatActivity implements OnMapReadyCallback {
    static LatLng DriverLatLang;
    static LatLng latLng;
    static GoogleMap Map;
    Button RequestBtn;
    MyLoction myLoction;
    DatabaseReference dataRef;
    OnFailureListener onFail;
    OnSuccessListener onSucc;

    public static void ChangeMapLocation(Location location) {
        if (Map != null) {
            Map.clear();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Map.animateCamera(CameraUpdateFactory.newLatLngZoom(CustomerMap.latLng, 17));
            Map.addMarker(new MarkerOptions().title("Your Position").position(CustomerMap.latLng));
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        RequestBtn = (Button) (findViewById(R.id.RequestBtn));
        MapFragment fragmentMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragmentMap.getMapAsync(this);
        myLoction = new MyLoction((LocationManager) getSystemService(LOCATION_SERVICE));
        dataRef = FirebaseDatabase.getInstance().getReference();
        DatabaseCallBack();
    }

    public void RequestProcess(View view) {
        if (latLng != null) {
            if (RequestBtn.getText().equals("Request Uber")) {
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(this).setTitle("Please Enter Your Number").setView(editText)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!editText.getText().toString().isEmpty()) {
                                    RequestBtn.setVisibility(View.INVISIBLE);
                                    UserInfo newUser = new UserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(), editText.getText().toString(), latLng.latitude, latLng.longitude);
                                    dataRef.child("Requests").child(newUser.getId()).setValue(newUser).addOnFailureListener(onFail).addOnSuccessListener(onSucc);
                                    try {
                                        dataRef.child("Requests").child(newUser.getId()).child("Driver").setValue(null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Number Can't Be Empty", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).create().show();
            } else {
                RequestBtn.setVisibility(View.INVISIBLE);
                dataRef.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                        .addOnFailureListener(onFail).addOnSuccessListener(onSucc);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait Till Find Your Location", Toast.LENGTH_LONG).show();
        }
    }

    private void DatabaseCallBack() {
        onSucc = new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (RequestBtn.getText().equals("Request Uber")) {
                    Toast.makeText(getApplicationContext(), "Your Request Has Been Add", Toast.LENGTH_LONG).show();
                    onRequestListener();
                    RequestBtn.setText("Cancel Request");
                } else {
                    Toast.makeText(getApplicationContext(), "Your Request Has Been Canceled", Toast.LENGTH_LONG).show();
                    RequestBtn.setText("Request Uber");
                }

                RequestBtn.setVisibility(View.VISIBLE);
            }
        };
        onFail = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                RequestBtn.setVisibility(View.VISIBLE);
            }
        };
    }

    private void onRequestListener() {
        dataRef.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Driver")) {
                    RequestBtn.setVisibility(View.INVISIBLE);
                    myLoction.removeUpdates();
                    DriverLatLang = new LatLng((double) dataSnapshot.child("latitude").getValue(), (double) dataSnapshot.child("longitude").getValue());
                    Toast.makeText(getApplicationContext(), "Your Request Accepted You Can See The Driver Location Now", Toast.LENGTH_LONG).show();
                    onRequestAccepted();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Driver")) {
                    DriverLatLang = new LatLng((double) dataSnapshot.child("latitude").getValue(), (double) dataSnapshot.child("longitude").getValue());
                    onRequestAccepted();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onRequestAccepted() {
        Map.clear();
        Map.animateCamera(CameraUpdateFactory.newLatLngZoom(CustomerMap.latLng, 17));
        Map.addMarker(new MarkerOptions().title("Your Position").position(CustomerMap.latLng));
        Map.addMarker(new MarkerOptions().position(DriverLatLang).title("Driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        Map.addPolyline(new PolylineOptions().add(CustomerMap.latLng, DriverLatLang).width(5).visible(true));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLoction.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLoction.removeUpdates();
    }
}
