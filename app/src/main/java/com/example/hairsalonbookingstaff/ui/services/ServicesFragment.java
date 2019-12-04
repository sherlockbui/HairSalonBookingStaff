package com.example.hairsalonbookingstaff.ui.services;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminServiceAdapter;
import com.example.hairsalonbookingstaff.Common.AdminCustomServiceDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateProduct;
import com.example.hairsalonbookingstaff.Model.BarberServices;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment implements IAdminDialogUpdateProduct {
    FloatingActionButton floatingActionButton;
    RecyclerView recycler_service;
    List<BarberServices> servicesList;
    Socket mSocket = MySocket.getmSocket();
    AdminServiceAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);
        mSocket.connect();
        servicesList = new ArrayList<>();
        recycler_service = root.findViewById(R.id.recycler_service);
        recycler_service.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_service.addItemDecoration(new SpaceItemDecoration(8));
        recycler_service.setHasFixedSize(true);
        floatingActionButton = root.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminCustomServiceDialog dialog = AdminCustomServiceDialog.getInstance();
                dialog.showLoginDialog("Thêm Dịch Vụ", "Thêm", "Hủy", getContext(), ServicesFragment.this);
            }
        });
        mSocket.emit("getServices", "").on("getServices", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        servicesList.add(new BarberServices(object.getString("_id"), object.getString("name"), Long.valueOf(object.getString("price"))));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new AdminServiceAdapter(getContext(), servicesList);
                                recycler_service.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String price) {
        mSocket.emit("addService", name, price).once("addService", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        servicesList.add(new BarberServices(object.getString("_id"), object.getString("name"), Long.valueOf(object.getString("price"))));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(servicesList.size());
                                Toast.makeText(getContext(), "Thành Công", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialogInterface.dismiss();
                            Toast.makeText(getContext(), "Thất bại", Toast.LENGTH_SHORT).show();
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