package com.example.hairsalonbookingstaff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyTimeSlotAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SharedPrefManager;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.ITimeSlotLoadListener;
import com.example.hairsalonbookingstaff.Model.BookingInfomation;
import com.example.hairsalonbookingstaff.Model.MyToken;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class StaffHomeActivity extends AppCompatActivity implements ITimeSlotLoadListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    RecyclerView recycler_time_slot;
    HorizontalCalendarView calendarView;
    List<BookingInfomation> timeSlotList;
    SimpleDateFormat simpleDateFormat;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    TextView txt_notification_badge;
    HorizontalCalendar horizontalCalendar;

    Socket mSocket = MySocket.getmSocket();

    Emitter.Listener countNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final String count = args[0].toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (count != null) {
                        loadAvailableTimeSlotOfBarber(Common.currentBarber.getId(), simpleDateFormat.format(Common.bookingDate.getTime()));
                        if (!count.equalsIgnoreCase("0")) {
                            txt_notification_badge.setVisibility(View.VISIBLE);
                            txt_notification_badge.setText(count);
                        }
                    } else {
                        txt_notification_badge.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.connect();
        iTimeSlotLoadListener = this;
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        MyToken myToken = new MyToken();
                        myToken.setToken(token);
                        myToken.setIdbarber(Common.currentBarber.getId());
                        String jsonToken = new Gson().toJson(myToken);
                        mSocket.emit("updateToken", jsonToken);
                        Log.d("token", "onComplete: " + token);
                    }
                });
        setContentView(R.layout.activity_staff_home);
        initView();
    }


    private void loadAvailableTimeSlotOfBarber(String id, String time) {
        timeSlotList = new ArrayList<>();
        mSocket.emit("getTimeBooking", id, time);
        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlotList);
    }

    private void initView() {

        drawerLayout = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_exit) {
                    logOut();
                }
                return true;
            }
        });
        calendarView = findViewById(R.id.calendarView);
        recycler_time_slot = findViewById(R.id.recycler_time_slot);
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpaceItemDecoration(8));
        //Calendar
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 2);//2 day left
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 0);
        horizontalCalendar = new HorizontalCalendar
                .Builder(this, R.id.calendarView)
                .range(startDate, endDate).datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis()) {
                    Common.bookingDate = date;
                    loadAvailableTimeSlotOfBarber(Common.currentBarber.getId()
                            , simpleDateFormat.format(date.getTime()));
                }
            }
        });
        loadAvailableTimeSlotOfBarber(Common.currentBarber
                .getId(), simpleDateFormat.format(date.getTime()));
    }

    private void logOut() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(item.getItemId() == R.id.action_new_notification){
            startActivity(new Intent(this, NotificationActivity.class));
            txt_notification_badge.setVisibility(View.INVISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_home_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_new_notification);
      txt_notification_badge = menuItem.getActionView().findViewById(R.id.notification_badge);
        loadNotification();
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void loadNotification() {
        mSocket.emit("countNotification", Common.currentBarber.getId());
        mSocket.on("countNotification", countNotification);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("Are you sure want to exit ?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(StaffHomeActivity.this, "Fake function exit", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onTimeSlotLoadSuccess(final List<BookingInfomation> timeSlotList) {
        final MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this, timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        Emitter.Listener getTimeBooking = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    JSONObject object = (JSONObject) args[0];
                    @Override
                    public void run() {
                        try {
                            if (object == null) {
                                iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                            } else {
                                BookingInfomation bookingInfomation = new BookingInfomation();
                                bookingInfomation.set_id(object.getString("_id"));
                                bookingInfomation.setCustomerName(object.getString("customerName"));
                                bookingInfomation.setCustomerPhone(object.getString("customerPhone"));
                                bookingInfomation.setDate(object.getString("date"));
                                bookingInfomation.setBarberId(object.getString("barberId"));
                                bookingInfomation.setBarberName(object.getString("barberName"));
                                bookingInfomation.setSalonId(object.getString("salonId"));
                                bookingInfomation.setSalonName(object.getString("salonName"));
                                bookingInfomation.setSalonAddress(object.getString("salonAddress"));
                                bookingInfomation.setSlot(object.getString("slot"));
                                bookingInfomation.setDone(object.getBoolean("done"));
                                timeSlotList.add(bookingInfomation);
                                adapter.notifyDataSetChanged();
                                Log.d("AAA", "run: " + object.getString("slot"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        mSocket.on("getTimeBooking", getTimeBooking);
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
    }
}

