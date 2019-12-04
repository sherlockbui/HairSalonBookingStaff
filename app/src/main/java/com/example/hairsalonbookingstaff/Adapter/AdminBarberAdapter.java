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

import com.example.hairsalonbookingstaff.Common.AdminCustomBarberDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateBarber;
import com.example.hairsalonbookingstaff.Model.Barber;
import com.example.hairsalonbookingstaff.R;
import com.example.hairsalonbookingstaff.StaffAssessmentActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminBarberAdapter extends RecyclerView.Adapter<AdminBarberAdapter.MyViewHolder> implements IAdminDialogUpdateBarber {
    Socket mSocket = MySocket.getmSocket();
    Context context;
    List<Barber> barberList;
    String id_barber;
    int index_update;

    public AdminBarberAdapter(Context context, List<Barber> barberList) {
        mSocket.connect();
        this.context = context;
        this.barberList = barberList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_layout_barber, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txt_name_barber.setText(barberList.get(position).getName());
        holder.img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index_update = position;
                id_barber = barberList.get(position).getId();
                AdminCustomBarberDialog dialog = AdminCustomBarberDialog.getInstance();
                dialog.showLoginDialog("Cập nhật thông tin nhân viên", "Cập nhật", "Đóng", context, AdminBarberAdapter.this);
                dialog.username.setText(barberList.get(position).getUsername());
                dialog.name.setText(barberList.get(position).getName());
            }
        });
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("deleteBarber", barberList.get(position).getId()).once("deleteBarber", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        JSONObject object = (JSONObject) args[0];
                        if (object != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    barberList.remove(position);
                                    AdminBarberAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StaffAssessmentActivity.class);
                intent.putExtra("ID_BARBER", barberList.get(position).getId());
                intent.putExtra("NAME_BARBER", barberList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String username, String password) {
        mSocket.emit("updateBarber", id_barber, name, username, password).once("updateBarber", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        barberList.set(index_update, new Barber(object.getString("_id"), object.getString("name"), object.getString("username"), object.getString("idBranch"), object.getLong("rating")));
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AdminBarberAdapter.this.notifyDataSetChanged();
                                Toast.makeText(context, "Update Thành Công", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name_barber;
        ImageView img_delete, img_update;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name_barber = itemView.findViewById(R.id.txt_baber_name);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_update = itemView.findViewById(R.id.img_update);
        }
    }
}
