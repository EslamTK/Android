package com.master.twitterclone;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

public class ThirdPage extends Fragment {
    DatabaseReference UserDataRef;
    String userName;
    Bitmap photo;
    View view;
    RelativeLayout relativeLayout;
    CustomListAdapter listAdapter;
    ArrayList<String> UserPost;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_third_page, container, false);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        listView = (ListView) view.findViewById(R.id.profilePostsList);
        UserPost = new ArrayList<>();
        UserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUserData();
        return view;
    }

    private void getUserPosts(final String userName, final Bitmap userPhoto) {
        listAdapter = new CustomListAdapter(getActivity(), null, UserPost, null) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = context.getLayoutInflater();
                View rowView = inflater.inflate(R.layout.post, null, true);
                ((TextView) rowView.findViewById(R.id.DisplayName)).setText(userName);
                ((TextView) rowView.findViewById(R.id.postContent)).setText(UserPost.get(position));
                ((ImageView) rowView.findViewById(R.id.UserPhoto)).setImageBitmap(userPhoto);

                return rowView;
            }

            @Override
            public int getCount() {
                return UserPost.size();
            }
        };
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private void getUserData() {
        UserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    userName = dataSnapshot.child("Name").getValue().toString();
                    (new photoDownloader()).execute(dataSnapshot.child("PictureUrl").getValue().toString());
                    ((TextView) view.findViewById(R.id.UserProfileName)).setText(userName);
                    ((TextView) view.findViewById(R.id.UserProfileAge)).setText(dataSnapshot.child("Age").getValue().toString() + " Years");

                    for (DataSnapshot post : dataSnapshot.child("Posts").getChildren()) {
                        UserPost.add(post.getValue().toString());
                    }

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
            photo = bitmap;
            ((ImageView) view.findViewById(R.id.UserProfilePhoto)).setImageBitmap(photo);
            if (relativeLayout.getVisibility() == View.VISIBLE)
                relativeLayout.setVisibility(View.INVISIBLE);
            getUserPosts(userName, photo);
        }
    }

}
