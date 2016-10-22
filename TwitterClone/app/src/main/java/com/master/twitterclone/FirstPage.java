package com.master.twitterclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirstPage extends Fragment {

    DataSnapshot UserFollowingData;
    ProgressBar progressBar;
    boolean FirstCall;
    Button TweetBtn;
    EditText TweetContent;
    ArrayList<String> UserName;
    ArrayList<Bitmap> UserPhoto;
    ArrayList<String> UserPost;
    CustomListAdapter ListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_page, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.firstProgressBar);
        FirstCall = true;
        TweetContent = (EditText) view.findViewById(R.id.TweetContent);
        UserName = new ArrayList<>();
        UserPost = new ArrayList<>();
        UserPhoto = new ArrayList<>();
        ListView listView = (ListView) view.findViewById(R.id.TweetsList);
        ListAdapter = new CustomListAdapter(getActivity(), UserName, UserPost, UserPhoto);
        listView.setAdapter(ListAdapter);
        PostsListener();

        TweetBtn = (Button) view.findViewById(R.id.TweetBtn);
        TweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendTweet();
            }
        });

        return view;
    }

    private void SendTweet() {
        if (TweetContent.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Tweet Can't Be Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String key = FirebaseDatabase.getInstance().getReference().child("Posts").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key + "/user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        childUpdates.put("/posts/" + key + "/content", TweetContent.getText().toString());
        childUpdates.put("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Posts/" + key, TweetContent.getText().toString());
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error Occur Your Tweet Will Published Later", Toast.LENGTH_SHORT).show();
            }
        });
        TweetContent.setText("");
    }

    private void PostsListener() {
        DatabaseReference UserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");
        UserDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFollowingData = dataSnapshot;
                if (FirstCall) {
                    FirstCall = false;
                    setOnPostAdded();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOnPostAdded() {
        FirebaseDatabase.getInstance().getReference().child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot Snapshot, String s) {
                if (UserFollowingData.hasChild(Snapshot.child("user").getValue().toString())) {
                    readUserInfo(Snapshot.child("user").getValue().toString(), Snapshot.child("content").getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void readUserInfo(String userID, final String postContent) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            UserName.add(dataSnapshot.child("Name").getValue().toString());
                            UserPost.add(postContent);
                            (new photoDownloader()).execute(dataSnapshot.child("PictureUrl").getValue().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    class photoDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL Photourl = new URL(params[0]);
                return BitmapFactory.decodeStream(Photourl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            UserPhoto.add(bitmap);
            if (progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.INVISIBLE);
            ListAdapter.notifyDataSetChanged();
        }
    }
}
