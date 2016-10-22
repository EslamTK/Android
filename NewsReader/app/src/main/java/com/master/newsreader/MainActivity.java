package com.master.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> arrayList;
    SQLiteDatabase sqLiteDatabase;
    class DownloadWebContent extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String res = Read(urls[0]);
                if(!res.isEmpty()) {
                    JSONObject jsonObject;
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < 50; ++i) {
                        res = Read("https://hacker-news.firebaseio.com/v0/item/" + jsonArray.getString(i) + ".json?print=pretty");
                        try {
                            jsonObject = new JSONObject(res);
                            String sql = "INSERT INTO Links(title,url) VALUES(?,?)";
                            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);
                            sqLiteStatement.bindString(1, jsonObject.getString("title"));
                            sqLiteStatement.bindString(2, jsonObject.getString("url"));
                            sqLiteStatement.execute();
                        }
                        catch (Exception e)
                        {
                           e.printStackTrace();
                        }
                    }
                    return "Done";
                }
                else return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Links", null);
            int titleIndex = cursor.getColumnIndex("title");
            cursor.moveToLast();
            int end = cursor.getCount()>50?50:cursor.getCount();
            for (int i = 0; i < end; ++i) {
                arrayList.add(cursor.getString(titleIndex));
                cursor.moveToPrevious();
            }
            if(s==null)
            {
               Toast.makeText(getApplicationContext(),"Please Connect To Internet To Update",Toast.LENGTH_LONG).show();
            }
            UpdateView();
        }

        private String Read(String Url) {
            String res = new String();
            try {
                URL url = new URL(Url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) res += scanner.nextLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!res.isEmpty()) return res;
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        sqLiteDatabase = this.openOrCreateDatabase("News",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Links(id INTEGER PRIMARY KEY,title TEXT,url TEXT)");
        (new DownloadWebContent()).execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        Toast.makeText(getApplicationContext(),"Please wait until Update",Toast.LENGTH_LONG).show();
    }
    public void UpdateView()
    {
        ListView listView = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Links", null);
        final int urlIndex = cursor.getColumnIndex("url");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NewsView.class);
                cursor.moveToPosition(position);
                intent.putExtra("url",cursor.getString(urlIndex));
                startActivity(intent);
            }
        });
    }
}