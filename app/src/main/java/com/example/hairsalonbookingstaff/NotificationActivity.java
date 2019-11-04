package com.example.hairsalonbookingstaff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.hairsalonbookingstaff.Adapter.MyNotificationAdapter;
import com.example.hairsalonbookingstaff.Common.Common;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recycler_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
    }

    private void initView() {
        recycler_notification = findViewById(R.id.recycler_notification);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_notification.setLayoutManager(linearLayoutManager);
        recycler_notification.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recycler_notification.setHasFixedSize(true);
        MyNotificationAdapter notificationAdapter = new MyNotificationAdapter(this, Common.listNotification);
        Log.d("AAA", "listNotification: "+ Common.listNotification.get(0));
        recycler_notification.setAdapter(notificationAdapter);
    }
}
