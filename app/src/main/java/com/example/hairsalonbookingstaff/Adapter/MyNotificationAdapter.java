package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Model.MyNotification;
import com.example.hairsalonbookingstaff.R;

import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyViewHolder> {

Context context;
List<MyNotification> notificationList;

    public MyNotificationAdapter(Context context, List<MyNotification> myNotifications) {
        this.context = context;
        this.notificationList = myNotifications;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notification_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.notification_title.setText(notificationList.get(position).getTitle());
        holder.notification_content.setText(notificationList.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView notification_title, notification_content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_title= itemView.findViewById(R.id.txt_notification_title);
            notification_content = itemView.findViewById(R.id.txt_notification_content);

        }
    }
}
