package com.example.hairsalonbookingstaff;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminSalonAdapter;
import com.example.hairsalonbookingstaff.Common.AdminCustomSalonDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateSalon;
import com.example.hairsalonbookingstaff.Model.Salon;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminSalonActivity extends AppCompatActivity implements IAdminDialogUpdateSalon {
    RecyclerView recycler_salon;
    List<Salon> salonList;
    Button btn_add_salon;
    String name_state;
    AdminSalonAdapter adapter;
    private Socket mSocket = MySocket.getmSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_salon);
        mSocket.connect();
        salonList = new ArrayList<>();
        recycler_salon = findViewById(R.id.recycler_salon);
        recycler_salon.setLayoutManager(new LinearLayoutManager(this));
        recycler_salon.addItemDecoration(new SpaceItemDecoration(8));
        recycler_salon.setHasFixedSize(true);
        btn_add_salon = findViewById(R.id.btn_add_salon);
        adapter = new AdminSalonAdapter(AdminSalonActivity.this, salonList);
        recycler_salon.setAdapter(adapter);
        btn_add_salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminCustomSalonDialog.getInstance().showLoginDialog("Thêm Salon", "Thêm", "Hủy", AdminSalonActivity.this, AdminSalonActivity.this);
            }
        });
        name_state = getIntent().getStringExtra("NAME_STATE");
        mSocket.emit("getBranch", name_state).on("getBranch", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        salonList.add(new Salon(object.getString("name"), object.getString("adress"), object.getString("website"), object.getString("phone"), object.getString("openHours"), object.getString("_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String address, String website, String phone) {
        mSocket.emit("addSalon", name_state, name, address, website, phone).once("addSalon", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        salonList.add(new Salon(object.getString("name"), object.getString("adress"), object.getString("website"), object.getString("phone"), object.getString("openHours"), object.getString("_id")));
                        dialogInterface.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }
}
