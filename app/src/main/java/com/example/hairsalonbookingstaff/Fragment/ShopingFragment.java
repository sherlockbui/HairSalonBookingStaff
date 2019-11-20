package com.example.hairsalonbookingstaff.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyShoppingItemAdapter;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IOnShoppingItemSelected;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopingFragment extends BottomSheetDialogFragment implements IOnShoppingItemSelected {
    private static ShopingFragment instance;
    List<ShoppingItem> shoppingItems;
    RecyclerView recycler_items;
    Socket mSocket = MySocket.getmSocket();
    ChipGroup chipGroup;
    Chip chip_wax, chip_spray;
    IOnShoppingItemSelected callBackToActivity;
    Emitter.Listener getItemShopping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

        }
    };

    public ShopingFragment(IOnShoppingItemSelected callBackToActivity) {
        this.callBackToActivity = callBackToActivity;
    }

    public ShopingFragment() {
    }

    public static ShopingFragment getInstance(IOnShoppingItemSelected iOnShoppingItemSelected) {
        return instance == null ? new ShopingFragment(iOnShoppingItemSelected) : instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {
        mSocket.on("getItemShopping", getItemShopping);
        shoppingItems = new ArrayList<>();
        mSocket.connect();
        recycler_items = view.findViewById(R.id.recycler_items);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.setHasFixedSize(true);
        recycler_items.addItemDecoration(new SpaceItemDecoration(8));
        chipGroup = view.findViewById(R.id.chip_group);
        chip_wax = view.findViewById(R.id.chip_wax);
        chip_spray = view.findViewById(R.id.chip_spray);
        chip_spray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(chip_spray);
                mSocket.emit("getItemShopping", chip_spray.getText().toString());
                loadShoppingItems(shoppingItems);
                shoppingItems = new ArrayList<>();

            }
        });
        chip_wax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(chip_wax);
                mSocket.emit("getItemShopping", chip_wax.getText().toString());
                loadShoppingItems(shoppingItems);
                shoppingItems = new ArrayList<>();
            }
        });
    }

    private void loadShoppingItems(final List<ShoppingItem> shoppingItems) {
        final MyShoppingItemAdapter adapter = new MyShoppingItemAdapter(getContext(), shoppingItems, this);
        recycler_items.setAdapter(adapter);
        mSocket.on("getItemShopping", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler mHandler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        shoppingItems.add(new ShoppingItem(object.getString("_id"), object.getString("name"), object.getString("image"), object.getString("price")));
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


        });
    }

    private void setSelectedChip(Chip chip) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipItem = (Chip) chipGroup.getChildAt(i);
            if (chipItem.getId() != chip.getId()) {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }

        }
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
        callBackToActivity.onShoppingItemSelected(shoppingItem);
    }
}
