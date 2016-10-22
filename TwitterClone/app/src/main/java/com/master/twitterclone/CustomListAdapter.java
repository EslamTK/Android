package com.master.twitterclone;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {
    Activity context;
    ArrayList<String> UserName;
    ArrayList<String> UserPost;
    ArrayList<Bitmap> UserPhoto;

    public CustomListAdapter(Activity context, ArrayList<String> UserName, ArrayList<String> UserPost, ArrayList<Bitmap> UserPhoto) {
        super(context, R.layout.post, UserName);
        this.context = context;
        this.UserName = UserName;
        this.UserPost = UserPost;
        this.UserPhoto = UserPhoto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.post, null, true);

        ((TextView) rowView.findViewById(R.id.DisplayName)).setText(UserName.get(position));
        ((TextView) rowView.findViewById(R.id.postContent)).setText(UserPost.get(position));
        ((ImageView) rowView.findViewById(R.id.UserPhoto)).setImageBitmap(UserPhoto.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return UserPhoto.size();
    }
}
