package com.example.hairsalonbookingstaff;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyNotificationAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Model.MyNotification;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotificationActivity extends AppCompatActivity {
    Socket mSocket = MySocket.getmSocket();
    RecyclerView recycler_notification;
    List<MyNotification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
        getData(notificationList);

    }

    private void getData(final List<MyNotification> notificationList) {
        mSocket.emit("getNotification", Common.currentBarber.getId()).on("getNotification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    MyNotification myNotification = new MyNotification();
                    try {
                        myNotification.set_id(object.getString("_id"));
                        myNotification.setTitle(object.getString("title"));
                        myNotification.setContent(object.getString("content"));
                        myNotification.setRead(object.getBoolean("read"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notificationList.add(myNotification);
                }
            }
        });
    }

    private void initView() {
        mSocket.connect();
        notificationList = new ArrayList<>();
        recycler_notification = findViewById(R.id.recycler_notification);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_notification.setLayoutManager(linearLayoutManager);
        recycler_notification.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recycler_notification.setHasFixedSize(true);
        MyNotificationAdapter notificationAdapter = new MyNotificationAdapter(this, notificationList);
        recycler_notification.setAdapter(notificationAdapter);
    }
}
