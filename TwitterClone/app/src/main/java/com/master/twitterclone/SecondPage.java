package com.master.twitterclone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SecondPage extends Fragment {
    View view;
    boolean FirstCall;
    ProgressBar progressBar;
    DataSnapshot UserFollowingData;
    ListView Followinglist;
    DatabaseReference UserDataRef;
    ArrayList<String> UsersNames;
    ArrayList<String> UsersIds;
    ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_second_page, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.secondProgressBar);
        Followinglist = ((ListView) view.findViewById(R.id.followingListView));
        UserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");
        FirstCall = true;
        UsersNames = new ArrayList<>();
        UsersIds = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, UsersNames);
        Followinglist.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        initializeFollowingList();
        setOnClick();
        return view;
    }

    private void setOnClick() {
        Followinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Object obj = null;
                if (Followinglist.isItemChecked(position)) {
                    obj = true;
                }
                UserDataRef.child(UsersIds.get(position)).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Followinglist.setItemChecked(position, Followinglist.isItemChecked(position));
                        arrayAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void initializeFollowingList() {
        UserDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFollowingData = dataSnapshot;
                if (FirstCall) {
                    FirstCall = false;
                    addUsers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUsers() {
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("Name").getValue() != null) {
                    UsersIds.add(dataSnapshot.getKey());
                    UsersNames.add(dataSnapshot.child("Name").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
                    Followinglist.setItemChecked(UsersNames.size() - 1, false);

                    if (UserFollowingData.hasChild(dataSnapshot.getKey())) {
                        Followinglist.setItemChecked(UsersNames.size() - 1, true);
                    }
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.INVISIBLE);
                    arrayAdapter.notifyDataSetChanged();
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

}