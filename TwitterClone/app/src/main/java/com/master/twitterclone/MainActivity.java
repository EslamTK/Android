package com.master.twitterclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    OnSuccessListener onSuccess;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1);
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent intent = new Intent(getApplicationContext(), AppActivity.class);
                startActivity(intent);
                this.finish();
            }
            setContentView(R.layout.activity_main);
            username = (EditText) findViewById(R.id.inputemail);
            password = (EditText) findViewById(R.id.inputpassword);
            firebaseAuth = FirebaseAuth.getInstance();
            onSuccess = new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            };
        }
    }

    public void LogOrSign(View view) {
        if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Enter Your Username & Password",Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(username.getText().toString() + "@app.com", password.getText().toString())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseAuth.createUserWithEmailAndPassword(username.getText().toString() + "@app.com", password.getText().toString())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnSuccessListener(onSuccess);
                    }
                })
                .addOnSuccessListener(onSuccess);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1);
            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(getApplicationContext(), AppActivity.class);
                    startActivity(intent);
                    this.finish();
                }
                setContentView(R.layout.activity_main);
                username = (EditText) findViewById(R.id.inputemail);
                password = (EditText) findViewById(R.id.inputpassword);
                firebaseAuth = FirebaseAuth.getInstance();
                onSuccess = new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                };
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
