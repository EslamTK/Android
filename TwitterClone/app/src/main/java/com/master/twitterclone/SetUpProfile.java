package com.master.twitterclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SetUpProfile extends AppCompatActivity {
    Uri imageUri;
    EditText Name,Age;
    ImageView imageView;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);
        Name = (EditText)findViewById(R.id.nameInput);
        Age = (EditText)findViewById(R.id.ageInput);
        imageView = (ImageView) findViewById(R.id.profilePicture);
        imageUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.twitteruser);
        imageView.setImageURI(imageUri);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Male");
        arrayList.add("Female");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrayList);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);

    }
    public void save(View view)
    {
        if(Name.getText().toString().isEmpty() || Age.getText().toString().isEmpty() || Integer.parseInt(Age.getText().toString())>100)
        {
           Toast.makeText(getApplicationContext(),"Please Enter A Correct Name And Age",Toast.LENGTH_LONG).show();
           return;
        }
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("users");
        StorageReference storegeRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://twitter-clone-ec6bc.appspot.com/ProfilePictures/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        final OnFailureListener onFail = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        };
        storegeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    UserProfile userProfile = new UserProfile(Name.getText().toString(), Age.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                    dataRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userProfile).addOnFailureListener(onFail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(getApplicationContext(), AppActivity.class);
                                    startActivity(intent);
                                    SetUpProfile.this.finish();
                                }
                            });
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(onFail);
    }
    public void pickImage(View view)
    {
        new AlertDialog.Builder(this).setTitle("Choose A Photo")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            } else if (requestCode == 2) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }
}
