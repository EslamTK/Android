package com.master.uberclone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Loading extends Activity implements OnFailureListener {
    boolean Fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Fail = false;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            firebaseAuth.signInAnonymously().addOnFailureListener(this);
        }

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String type = getIntent().getStringExtra("type");
                    UserProfileChangeRequest userPro = new UserProfileChangeRequest.Builder()
                            .setDisplayName(type).build();
                    firebaseAuth.getCurrentUser().updateProfile(userPro).addOnFailureListener(Loading.this);
                    if (!Fail) {
                        Toast.makeText(getApplicationContext(), "Registered Success", Toast.LENGTH_SHORT).show();
                        if (type.equals("Customer")) {
                            Intent intent = new Intent(getApplicationContext(), CustomerMap.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), ReqList.class);
                            startActivity(intent);
                        }
                    }
                    firebaseAuth.removeAuthStateListener(this);
                    Loading.this.finish();
                }
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Fail = true;
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        this.finish();
    }
}
