package com.example.hairsalonbookingstaff;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminBarberAdapter;
import com.example.hairsalonbookingstaff.Common.AdminCustomBarberDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateBarber;
import com.example.hairsalonbookingstaff.Model.Barber;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminBarberActivity extends AppCompatActivity implements IAdminDialogUpdateBarber {
    String id_salon;
    private Socket mSocket = MySocket.getmSocket();
    private List<Barber> barberList;
    private Button btn_add_barber;
    private RecyclerView recycler_barber;
    private AdminBarberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.connect();
        setContentView(R.layout.activity_admin_barber);
        btn_add_barber = findViewById(R.id.btn_add_barber);
        recycler_barber = findViewById(R.id.recycler_barber);
        recycler_barber.setLayoutManager(new LinearLayoutManager(this));
        recycler_barber.addItemDecoration(new SpaceItemDecoration(8));
        recycler_barber.setHasFixedSize(true);
        barberList = new ArrayList<>();
        adapter = new AdminBarberAdapter(this, barberList);
        adapter.notifyDataSetChanged();
        recycler_barber.setAdapter(adapter);
        id_salon = getIntent().getStringExtra("ID_SALON");
        mSocket.emit("getBarbers", id_salon).on("getBarbers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        barberList.add(new Barber(object.getString("_id"), object.getString("name"), object.getString("username"), object.getString("idBranch"), object.getLong("rating")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btn_add_barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminCustomBarberDialog dialog = AdminCustomBarberDialog.getInstance();
                dialog.showLoginDialog("Thêm Nhân Viên", "Thêm", "Đóng", AdminBarberActivity.this, AdminBarberActivity.this);
            }
        });


    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String username, String password) {
        mSocket.emit("addBarber", name, username, password, id_salon).once("addBarber", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                if (args[0].toString().equalsIgnoreCase("exits")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AdminBarberActivity.this, "Tồn tại tên đăng nhập", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                } else {
                    JSONObject object = (JSONObject) args[0];
                    if (object != null) {
                        try {

                            barberList.add(new Barber(object.getString("_id"), object.getString("name"), object.getString("username"), object.getString("idBranch"), object.getLong("rating")));
                            dialogInterface.dismiss();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AdminBarberActivity.this, "Thành Công", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminBarberActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });

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
