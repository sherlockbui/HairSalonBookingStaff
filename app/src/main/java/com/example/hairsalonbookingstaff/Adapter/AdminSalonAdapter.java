package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.hairsalonbookingstaff.AdminBarberActivity;
import com.example.hairsalonbookingstaff.Common.AdminCustomSalonDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateSalon;
import com.example.hairsalonbookingstaff.Model.Salon;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminSalonAdapter extends RecyclerView.Adapter<AdminSalonAdapter.MyViewHolder> implements IAdminDialogUpdateSalon {
    Context context;
    List<Salon> salonList;
    String id_salon;
    int index_update;
    private Socket mSocket = MySocket.getmSocket();

    public AdminSalonAdapter(Context context, List<Salon> salonList) {
        mSocket.connect();
        this.context = context;
        this.salonList = salonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_layout_salon, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txt_salon_name.setText(salonList.get(position).getName());
        holder.txt_salon_adress.setText(salonList.get(position).getAdress());
        holder.img_delete_salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("deleteSalon", salonList.get(position).getSalonId()).once("deleteSalon", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject object = (JSONObject) args[0];
                        if (object != null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    salonList.remove(position);
                                    AdminSalonAdapter.this.notifyItemRemoved(position);
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
        holder.img_update_salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index_update = position;
                id_salon = salonList.get(position).getSalonId();
                AdminCustomSalonDialog dialog = AdminCustomSalonDialog.getInstance();
                dialog.showLoginDialog("Cập Nhật Salon", "Cập Nhật", "Hủy", context, AdminSalonAdapter.this);
                dialog.edt_name_salon.setText(salonList.get(position).getName());
                dialog.edt_address.setText(salonList.get(position).getAdress());
                dialog.edt_website.setText(salonList.get(position).getWebsite());
                dialog.edt_phone_number.setText(salonList.get(position).getPhone());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminBarberActivity.class);
                intent.putExtra("ID_SALON", salonList.get(position).getSalonId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String address, String website, String phone) {
        mSocket.emit("updateSalon", id_salon, name, address, website, phone).once("updateSalon", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        salonList.set(index_update, new Salon(object.getString("name"), object.getString("adress"), object.getString("website"), object.getString("phone"), object.getString("openHours"), object.getString("_id")));
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AdminSalonAdapter.this.notifyDataSetChanged();
                                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
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


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_salon_name, txt_salon_adress;
        ImageView img_update_salon, img_delete_salon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_salon_adress = itemView.findViewById(R.id.txt_salon_adress);
            txt_salon_name = itemView.findViewById(R.id.txt_salon_name);
            img_delete_salon = itemView.findViewById(R.id.img_delete);
            img_update_salon = itemView.findViewById(R.id.img_update);
        }
    }
}
