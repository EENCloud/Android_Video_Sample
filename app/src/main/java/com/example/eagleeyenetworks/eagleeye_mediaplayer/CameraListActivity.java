package com.example.eagleeyenetworks.eagleeye_mediaplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.eagleeyenetworks.eagleeye_mediaplayer.models.EENListDevice;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class CameraListActivity extends AppCompatActivity {
    protected ListView listView;
    protected ArrayList<EENListDevice> cameras;
    protected ArrayAdapter<EENListDevice> adapter;
    protected String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        authToken = getIntent().getStringExtra("auth_token");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                parseCameraData();
            }
        });

        listView = findViewById(R.id.camera_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EENListDevice device = adapter.getItem(position);
                Intent intent = new Intent(CameraListActivity.this, VideoPlayerActivity.class);
                intent.putExtra("esn", device.getESN());
                intent.putExtra("auth_token", authToken);
                startActivity(intent);
            }
        });
    }

    private void parseCameraData() {
        try {
            JSONArray camera_data = new JSONArray(getIntent().getStringExtra("camera_list"));
            cameras = new ArrayList<>();
            for (int i=0; i<camera_data.length(); i++) {
                JSONArray entry = camera_data.getJSONArray(i);
                String esn = entry.getString(1);
                String name = entry.getString(2);
                String type = entry.getString(3);

                EENListDevice device = new EENListDevice(esn, name, type);
                if (device.isCamera())
                    cameras.add(device);
            }

            CameraListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new ArrayAdapter<EENListDevice>(CameraListActivity.this, android.R.layout.simple_list_item_1, cameras);
                    listView.setAdapter(adapter);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
