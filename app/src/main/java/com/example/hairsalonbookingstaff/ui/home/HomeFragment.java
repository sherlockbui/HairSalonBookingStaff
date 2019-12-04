package com.example.hairsalonbookingstaff.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminStateAdapter;
import com.example.hairsalonbookingstaff.Common.AdminCustomStateDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogClickListener;
import com.example.hairsalonbookingstaff.Model.City;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements IAdminDialogClickListener {
    RecyclerView recycler_state;
    AdminStateAdapter myStateAdapter;
    List<City> cityList;
    Button btn_add_state;
    private Socket mSocket = MySocket.getmSocket();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mSocket.connect();
        cityList = new ArrayList<>();
        getData(cityList);
        mSocket.emit("getAllSalon", "");
        recycler_state = root.findViewById(R.id.recycler_state);
        recycler_state.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_state.addItemDecoration(new SpaceItemDecoration(8));
        recycler_state.setHasFixedSize(true);
        btn_add_state = root.findViewById(R.id.btn_add_state);
        btn_add_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminCustomStateDialog.getInstance().showLoginDialog("Thêm địa điểm", "Thêm", "Hủy", getContext(), true, false, HomeFragment.this);
            }
        });
        return root;
    }

    private void getData(final List<City> areaNameList) {
        myStateAdapter = new AdminStateAdapter(getContext(), areaNameList);
        mSocket.on("getAllSalon", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                final JSONObject object = (JSONObject) args[0];
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            City city = new City(object.getString("name"));
                            areaNameList.add(city);
                            recycler_state.setAdapter(myStateAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String edt1) {
        mSocket.emit("addState", edt1).once("addState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                final JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Thành Công", Toast.LENGTH_SHORT).show();
                            try {
                                cityList.add(new City(object.getString("name")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialogInterface.dismiss();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Thất Bại", Toast.LENGTH_SHORT).show();
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