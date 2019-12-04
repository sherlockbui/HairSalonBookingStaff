package com.example.hairsalonbookingstaff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyStateAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.CustomLoginDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SharedPrefManager;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IDialogClickListener;
import com.example.hairsalonbookingstaff.Model.City;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IDialogClickListener {
    RecyclerView recycler_state;
    MyStateAdapter myStateAdapter;
    List<City> cityList = new ArrayList<>();
    private Socket mSocket= MySocket.getmSocket();

    Emitter.Listener getAllSalon = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = (JSONObject) args[0];
            try {
                City city = new City(object.getString("name"));
                cityList.add(city);
                myStateAdapter = new MyStateAdapter(MainActivity.this, cityList);
                Handler handler = new Handler(getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recycler_state.setAdapter(myStateAdapter);
                        cityList = new ArrayList<>();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
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
            mSocket.on("getAllSalon", getAllSalon);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.admin_menu) {
            CustomLoginDialog.getInstance().showLoginDialog("ADMIN LOGIN",
                    "LOGIN",
                    "CANCEL", MainActivity.this,
                    MainActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.emit("getAllSalon", "");
    }

    private void initView() {
        mSocket.connect();
//        mSocket.emit("getAllSalon", "");
        recycler_state = findViewById(R.id.recycler_state);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpaceItemDecoration(8));
        recycler_state.setHasFixedSize(true);
        cityList = new ArrayList<>();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String userName, String password) {
        mSocket.emit("adminLogin", userName, password).once("adminLogin", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Login Thành Công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, AdminActivity.class));
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Login Thất Bại", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }
}
