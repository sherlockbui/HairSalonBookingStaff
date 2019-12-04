package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.AdminCustomServiceDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateProduct;
import com.example.hairsalonbookingstaff.Model.BarberServices;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.MyViewHolder> implements IAdminDialogUpdateProduct {
    Context context;
    List<BarberServices> servicesList;
    String id_service;
    int index_update;
    private Socket mSocket = MySocket.getmSocket();

    public AdminServiceAdapter(Context context, List<BarberServices> servicesList) {
        mSocket.connect();
        this.context = context;
        this.servicesList = servicesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_layout_service_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txt_service_name.setText(servicesList.get(position).getName());
        holder.txt_service_price.setText(String.valueOf(servicesList.get(position).getPrice()));
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("deleteService", servicesList.get(position).getId_service()).once("deleteService", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject object = (JSONObject) args[0];
                        if (object != null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    servicesList.remove(position);
                                    AdminServiceAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Thất Bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        holder.img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index_update = position;
                id_service = servicesList.get(position).getId_service();
                AdminCustomServiceDialog dialog = AdminCustomServiceDialog.getInstance();
                dialog.showLoginDialog("Cập nhật dịch vụ", "Cập nhật", "Hủy", context, AdminServiceAdapter.this);
                dialog.edt_1.setText(servicesList.get(position).getName());
                dialog.edt_2.setText(String.valueOf(servicesList.get(position).getPrice()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }


    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String price) {
        mSocket.emit("updateService", id_service, name, price).once("updateService", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        servicesList.set(index_update, new BarberServices(object.getString("_id"), object.getString("name"), Long.valueOf(object.getString("price"))));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                AdminServiceAdapter.this.notifyDataSetChanged();
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
                            Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_service_name, txt_service_price;
        ImageView img_update, img_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_service_name = itemView.findViewById(R.id.txt_service_name);
            txt_service_price = itemView.findViewById(R.id.txt_service_price);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_update = itemView.findViewById(R.id.img_update);
        }
    }
}
