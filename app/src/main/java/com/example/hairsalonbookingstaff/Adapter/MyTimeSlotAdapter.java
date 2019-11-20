package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.DoneServiceActivity;
import com.example.hairsalonbookingstaff.Interface.IRecyclerItemSelectedListener;
import com.example.hairsalonbookingstaff.Model.BookingInfomation;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {
    Context context;
    List<BookingInfomation> timeSlotList;
    List<CardView> cardViewList;
    Socket mSocket = MySocket.getmSocket();


    public MyTimeSlotAdapter(Context context) {
        mSocket.connect();
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<BookingInfomation> timeSlotList) {
        this.context = context;
        this.timeSlotList =  timeSlotList;
        if(this.timeSlotList==null){
            this.timeSlotList = new ArrayList<>();
        }
        this.cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_time_slot,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        if(timeSlotList.size()==0){
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

        }else {
            for (BookingInfomation slotValue : timeSlotList) {
                final int slot = Integer.parseInt(slotValue.getSlot());
                if(slot== position){
                    holder.card_time_slot.setTag(Common.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.txt_time_slot_description.setText("Full");
                    holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            mSocket.emit("getBookInfomation", timeSlotList.get(position).get_id()).on("getBookInfomation", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    JSONObject object = (JSONObject) args[0];
                                    if (object != null) {
                                        try {
                                            Common.currentBookingInfomation = new BookingInfomation();
                                            Common.currentBookingInfomation.set_id(object.getString("_id"));
                                            Common.currentBookingInfomation.setCustomerName(object.getString("customerName"));
                                            Common.currentBookingInfomation.setCustomerPhone(object.getString("customerPhone"));
                                            Common.currentBookingInfomation.setDate(object.getString("date"));
                                            Common.currentBookingInfomation.setBarberId(object.getString("barberId"));
                                            Common.currentBookingInfomation.setBarberName(object.getString("barberName"));
                                            Common.currentBookingInfomation.setSalonId(object.getString("salonId"));
                                            Common.currentBookingInfomation.setSalonName(object.getString("salonName"));
                                            Common.currentBookingInfomation.setSalonAddress(object.getString("salonAddress"));
                                            Common.currentBookingInfomation.setSlot(object.getString("slot"));
                                            Common.currentBookingInfomation.setDone(object.getBoolean("done"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            });
                            context.startActivity(new Intent(context, DoneServiceActivity.class));
                        }
                    });
                }
            }
        }
        if(!cardViewList.contains(holder.card_time_slot)){
            cardViewList.add(holder.card_time_slot);
            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int position) {
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null) {
                            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                        }

                    }
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));


                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            card_time_slot = itemView.findViewById( R.id.card_time_slot);
            txt_time_slot = itemView.findViewById( R.id.txt_time_slot);
            txt_time_slot_description = itemView.findViewById( R.id.txt_time_slot_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
