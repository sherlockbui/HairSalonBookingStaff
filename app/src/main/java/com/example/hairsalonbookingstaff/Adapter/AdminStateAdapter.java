package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.AdminSalonActivity;
import com.example.hairsalonbookingstaff.Common.AdminCustomStateDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogClickListener;
import com.example.hairsalonbookingstaff.Model.City;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

public class AdminStateAdapter extends RecyclerView.Adapter<AdminStateAdapter.MyViewHolder> implements IAdminDialogClickListener {
    Context context;
    List<City> cityList;
    String preState;
    int indexUpdate;
    private Socket mSocket = MySocket.getmSocket();

    public AdminStateAdapter(Context context, List<City> cityList) {
        mSocket.connect();
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public AdminStateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_layout_state, parent, false);
        return new AdminStateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminStateAdapter.MyViewHolder holder, final int position) {
        holder.txt_state_name.setText(cityList.get(position).getName());
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("deleteState", holder.txt_state_name.getText()).once("deleteState", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (args[0] != null) {
                                    cityList.remove(position);
                                    Log.d("AAA", "run: " + cityList.size());
                                    AdminStateAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Thất Bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });
        holder.img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminCustomStateDialog dialog = AdminCustomStateDialog.getInstance();
                dialog.showLoginDialog("Update Địa Điểm", "Cập Nhật", "Đóng", context, true, false, AdminStateAdapter.this);
                indexUpdate = position;
                preState = cityList.get(position).getName();
                dialog.edt_1.setText(cityList.get(position).getName());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminSalonActivity.class);
                intent.putExtra("NAME_STATE", cityList.get(position).getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, final String edt1) {
        mSocket.emit("updateState", preState, edt1).once("updateState", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (args[0] != null) {
                            Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                            cityList.set(indexUpdate, new City(edt1));
                            AdminStateAdapter.this.notifyDataSetChanged();
                            dialogInterface.dismiss();


                        } else {
                            Toast.makeText(context, "Thất Bại", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_state_name;
        ImageView img_delete, img_update;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_state_name = itemView.findViewById(R.id.txt_state_name);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_update = itemView.findViewById(R.id.img_update);

        }

    }
}
