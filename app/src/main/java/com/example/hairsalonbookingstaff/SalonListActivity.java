package com.example.hairsalonbookingstaff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.hairsalonbookingstaff.Adapter.MySalonAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Model.Salon;
import com.github.nkzawa.emitter.Emitter;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalonListActivity extends AppCompatActivity {
    TextView txt_salon_count;
    RecyclerView recycler_salon;
    List<Salon> salonList;
    private Socket mSocket = MySocket.getmSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_list);
        initView();
        getData(salonList);
    }


    private void getData(final List<Salon> salonList) {
        final MySalonAdapter adapter = new MySalonAdapter(this, salonList);
        recycler_salon.setAdapter(adapter);
        Emitter.Listener getBranch = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    JSONObject object = (JSONObject) args[0];
                    @Override
                    public void run() {
                        try {
                            salonList.add(new Salon(object.getString("name"), object.getString("adress"), object.getString("website"), object.getString("phone"), object.getString("openHours"), object.getString("_id")));
                            adapter.notifyDataSetChanged();
                            txt_salon_count.setText(new StringBuilder("All Salon (").append(salonList.size()).append(")"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        mSocket.on("getBranch", getBranch);
    }

    private void initView() {
        mSocket.connect();
        mSocket.emit("getBranch", Common.state_name);
        salonList = new ArrayList<>();
        txt_salon_count = findViewById(R.id.txt_salon_count);
        recycler_salon = findViewById(R.id.recycler_salon);
        recycler_salon.addItemDecoration(new SpaceItemDecoration(8));
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(this, 2));

    }

}
