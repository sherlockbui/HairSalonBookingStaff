package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Interface.IRecyclerItemSelectedListener;
import com.example.hairsalonbookingstaff.Model.City;
import com.example.hairsalonbookingstaff.R;
import com.example.hairsalonbookingstaff.SalonListActivity;

import java.util.List;

public class MyStateAdapter extends RecyclerView.Adapter<MyStateAdapter.MyViewHolder> {
    Context context;
    List<City> cityList;


    public MyStateAdapter(Context context, List<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_state,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_state_name.setText(cityList.get(position).getName());
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                Common.state_name = cityList.get(position).getName();
                context.startActivity(new Intent(context, SalonListActivity.class));


            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_state_name;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_state_name = itemView.findViewById(R.id.txt_state_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
