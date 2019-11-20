package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Interface.IOnShoppingItemSelected;
import com.example.hairsalonbookingstaff.Interface.IRecyclerItemSelectedListener;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyShoppingItemAdapter extends RecyclerView.Adapter<MyShoppingItemAdapter.MyViewHolder> {
    Context context;
    List<ShoppingItem> shoppingItems;
    IOnShoppingItemSelected iOnShoppingItemSelected;

    public MyShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItems, IOnShoppingItemSelected iOnShoppingItemSelected) {
        this.context = context;
        this.shoppingItems = shoppingItems;
        this.iOnShoppingItemSelected = iOnShoppingItemSelected;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shopping_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(shoppingItems.get(position).getImage()).into(holder.img_shopping_item);
        holder.txt_shopping_item_name.setText(Common.formatShoppingItemName(shoppingItems.get(position).getName()));
        holder.txt_shopping_price.setText(new StringBuilder("$").append(shoppingItems.get(position).getPrice()));

        //Add to cart from staff app
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                iOnShoppingItemSelected.onShoppingItemSelected(shoppingItems.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_shopping_item_name, txt_shopping_price, txt_add_to_card;
        ImageView img_shopping_item;


        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            txt_shopping_item_name = itemView.findViewById(R.id.txt_name_shoppong_item);
            txt_shopping_price = itemView.findViewById(R.id.txt_price_shopping_item);
            txt_add_to_card = itemView.findViewById(R.id.txt_add_to_card);
            txt_add_to_card.setOnClickListener(this);

        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
