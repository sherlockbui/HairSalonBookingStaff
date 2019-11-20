package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyConfirmShoppingItemAdapter extends RecyclerView.Adapter<MyConfirmShoppingItemAdapter.MyViewHolder> {
    Context context;
    List<ShoppingItem> shoppingItemList;

    public MyConfirmShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_confirm_shopping, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(shoppingItemList.get(position).getImage()).into(holder.item_image);
        holder.txt_name.setText(shoppingItemList.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView txt_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            txt_name = itemView.findViewById(R.id.txt_name);
        }
    }
}
