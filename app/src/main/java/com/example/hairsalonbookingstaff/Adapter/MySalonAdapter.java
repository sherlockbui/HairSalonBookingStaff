package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.CustomLoginDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SharedPrefManager;
import com.example.hairsalonbookingstaff.Interface.IDialogClickListener;
import com.example.hairsalonbookingstaff.Interface.IRecyclerItemSelectedListener;
import com.example.hairsalonbookingstaff.Model.Barber;
import com.example.hairsalonbookingstaff.Model.Salon;
import com.example.hairsalonbookingstaff.R;
import com.example.hairsalonbookingstaff.StaffHomeActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> implements IDialogClickListener {
    Context context;
    List<Salon> salonList;
    List<CardView> cardViewList;
    private Socket mSocket= MySocket.getmSocket();
    public MySalonAdapter(Context context,
                          List<Salon> salonList) {
        mSocket.connect();
        this.context = context;
        this.salonList = salonList;
        cardViewList = new ArrayList<>();


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_salon, parent, false);
        mSocket.on("staffLogin",onStaffLogin);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.txt_salon_name.setText(salonList.get(position).getName());
        holder.txt_salon_adress.setText(salonList.get(position).getAdress());
        if (!cardViewList.contains(holder.card_salon))
            cardViewList.add(holder.card_salon);
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {

            @Override
            public void onItemSelectedListener(View view, int position) {
                Common.selectedSalon = salonList.get(position);
                showLoginDialog();
            }
        });


    }

    private void showLoginDialog() {
        CustomLoginDialog.getInstance().showLoginDialog("STAFF LOGIN",
                "LOGIN",
                "CANCEL",context,
                this);
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_salon_name, txt_salon_adress;
        CardView card_salon;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_salon = itemView.findViewById(R.id.card_salon);
            txt_salon_adress = itemView.findViewById(R.id.txt_salon_adress);
            txt_salon_name = itemView.findViewById(R.id.txt_salon_name);
            itemView.setOnClickListener(this);

        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String userName, String password) {
        mSocket.emit("staffLogin", Common.selectedSalon.getSalonId(),userName,password);

    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }
    Emitter.Listener onStaffLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            final JSONObject object = (JSONObject) args[0];
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(object!=null){
                        Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show();
                        try {
                            Common.currentBarber = new Barber(
                                    object.getString("_id"),
                                    object.getString("name"),
                                    object.getString("username"),
                                    object.getString("idBranch"),
                                    object.getLong("rating"));
                            SharedPrefManager.getInstance(context).saveInfoBarber(Common.currentBarber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(context, StaffHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "Wrong UserName Or Password!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    };
}
