package com.example.hairsalonbookingstaff;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyStateAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SharedPrefManager;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Model.City;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    RecyclerView recycler_state;
    MyStateAdapter myStateAdapter;
    List<City> cityList;
    private Socket mSocket= MySocket.getmSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if(prefManager.isLoggedIn()){
            Common.currentBarber = prefManager.getBarber();
            startActivity(new Intent(MainActivity.this, StaffHomeActivity.class));
            finish();
        }else {
            setContentView(R.layout.activity_main);
            initView();
            getData(cityList);
        }


    }

    private void getData(final List<City> areaNameList) {
        myStateAdapter = new MyStateAdapter(this, areaNameList);
        mSocket.on("getAllSalon", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject object = (JSONObject) args[0];
                        try {
                            City city = new City(object.getString("name"));
                            areaNameList.add(city);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        recycler_state.setAdapter(myStateAdapter);
                        myStateAdapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }

    private void initView() {
        cityList = new ArrayList<>();
        mSocket.connect();
        mSocket.emit("getAllSalon", "");
        recycler_state = findViewById(R.id.recycler_state);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpaceItemDecoration(8));
        recycler_state.setHasFixedSize(true);

    }
}
