package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.hairsalonbookingstaff.Model.CartItem;
import com.example.hairsalonbookingstaff.Model.MyToken;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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
        this.context = context;
        timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<BookingInfomation> timeSlotList) {
        mSocket.connect();
        this.context = context;
        this.timeSlotList =  timeSlotList;
        cardViewList = new ArrayList<>();
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
            for (final BookingInfomation slotValue : timeSlotList) {
                final int slot = slotValue.getSlot();
                if (slot == position) {
                    if (!slotValue.isDone()) {
                        holder.card_time_slot.setTag(Common.DISABLE_TAG);
                        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                        holder.txt_time_slot_description.setText("Full");
                        holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                        holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                        holder.card_time_slot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSocket.emit("getBookInfomation", timeSlotList.get(timeSlotList.indexOf(slotValue)).get_id()).on("getBookInfomation", new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        JSONObject object = (JSONObject) args[0];
                                        if (object != null) {
                                            try {
                                                Common.currentBookingInfomation.set_id(object.getString("_id"));
                                                Common.currentBookingInfomation.setCustomerName(object.getString("customerName"));
                                                Common.currentBookingInfomation.setCustomerPhone(object.getString("customerPhone"));
                                                Common.currentBookingInfomation.setDate(object.getString("date"));
                                                Common.currentBookingInfomation.setBarberId(object.getString("barberId"));
                                                Common.currentBookingInfomation.setBarberName(object.getString("barberName"));
                                                Common.currentBookingInfomation.setSalonId(object.getString("salonId"));
                                                Common.currentBookingInfomation.setSalonName(object.getString("salonName"));
                                                Common.currentBookingInfomation.setSalonAddress(object.getString("salonAddress"));
                                                Common.currentBookingInfomation.setSlot(object.getInt("slot"));
                                                Common.currentBookingInfomation.setDone(object.getBoolean("done"));
                                                JSONArray jsonListCartItem = object.getJSONArray("cartItemListJson");
                                                String stringListCartItem = jsonListCartItem.toString();
                                                List<CartItem> cartItems = new Gson().fromJson(stringListCartItem, new TypeToken<List<CartItem>>() {
                                                }.getType());
                                                Common.currentBookingInfomation.setCartItemList(cartItems);
                                                mSocket.emit("getToken", object.getString("customerPhone")).on("getToken", new Emitter.Listener() {
                                                    @Override
                                                    public void call(Object... args) {
                                                        JSONObject object = (JSONObject) args[0];
                                                        if (object != null) {
                                                            MyToken token = new MyToken();
                                                            try {
                                                                token.setPhoneCustomber(object.getString("customerPhone"));
                                                                token.setToken(object.getString("token"));
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            Common.currentToken = token;
                                                            Log.d("TOKENCUSTOMER", "call: " + Common.currentToken.getToken());
                                                        } else {
                                                            Common.currentToken = null;
                                                        }
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                                context.startActivity(new Intent(context, DoneServiceActivity.class));

                            }
                        });
                    } else {
                        holder.card_time_slot.setTag(Common.DISABLE_TAG);
                        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                        holder.txt_time_slot_description.setText("Done");
                        holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                        holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                    }

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
