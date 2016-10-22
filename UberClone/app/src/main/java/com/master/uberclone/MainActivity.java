package com.master.uberclone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    final int permissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE}, permissionCode);
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals("Customer")) {
                    Intent intent = new Intent(getApplicationContext(), CustomerMap.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ReqList.class);
                    startActivity(intent);
                }
                this.finish();
            }
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionCode) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals("Customer")) {
                        Intent intent = new Intent(getApplicationContext(), CustomerMap.class);
                        startActivity(intent);
                        this.finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ReqList.class);
                        startActivity(intent);
                        this.finish();
                    }
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("This App won't work without Location services please enable them and accept the use for it")
                        .setIcon(android.R.drawable.alert_light_frame)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE}, permissionCode);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        }).create().show();
            }
        }
    }

    public void LogIn(View view) {
        String type;
        Switch switchRef = (Switch) findViewById(R.id.switchId);
        if (switchRef.isChecked()) type = "Driver";
        else type = "Customer";
        Intent intent = new Intent(getApplicationContext(), Loading.class);
        intent.putExtra("type", type);
        startActivity(intent);
        this.finish();
    }
}
