package com.master.uberclone;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ReqList extends AppCompatActivity {
    public static ArrayList<UserInfo> Users;
    public static ArrayList<String> Distances;
    public static ArrayAdapter<String> arrayAdapter;
    public static Location location;
    MyLoction myLoction;
    ListView listView;

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_list);
        Users = new ArrayList<>();
        Distances = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Distances);
        listView.setAdapter(arrayAdapter);
        SetOnItemClickListener();
        myLoction = new MyLoction((LocationManager) getSystemService(LOCATION_SERVICE));
        myLoction.startListening();
        RequestsListener.StartListening();
    }

    private void SetOnItemClickListener() {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DriverMap.class);
                try {
                    intent.putExtra("UserIndex", position);
                } catch (Exception e) {
                    e.printStackTrace();
            }
                startActivity(intent);
        }
        };
        listView.setOnItemClickListener(onItemClickListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        myLoction.startListening();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onPause() {
        super.onPause();
        myLoction.removeUpdates();
    }
}
