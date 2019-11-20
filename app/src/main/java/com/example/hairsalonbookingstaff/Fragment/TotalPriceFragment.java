package com.example.hairsalonbookingstaff.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyConfirmShoppingItemAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Model.BarberServices;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalPriceFragment extends BottomSheetDialogFragment {
    private static TotalPriceFragment instance;
    ChipGroup chip_group_service;
    RecyclerView recycler_view_shopping;
    Button btn_confirm;
    HashSet<BarberServices> servicesAdded;
    List<ShoppingItem> shoppingItemList;
    TextView txt_total_price;

    public TotalPriceFragment() {
        // Required empty public constructor
    }

    public static TotalPriceFragment getInstance() {
        return instance == null ? new TotalPriceFragment() : instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_price, container, false);
        init(view);
        getBundle(getArguments());
        setInfomation();
        return view;
    }

    private void init(View view) {
        txt_total_price = view.findViewById(R.id.txt_total_price);
        chip_group_service = view.findViewById(R.id.chip_group_service);
        recycler_view_shopping = view.findViewById(R.id.recycler_view_shopping);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        recycler_view_shopping.setHasFixedSize(true);
        recycler_view_shopping.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

    }

    private void setInfomation() {
        if (servicesAdded.size() > 0) {
            int i = 0;
            for (final BarberServices services : servicesAdded) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, null);
                chip.setText(services.getName());
                chip.setTag(i);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        servicesAdded.remove(services);
                        chip_group_service.removeView(v);
                        calculatePrice();

                    }
                });
                chip_group_service.addView(chip);
                i++;
            }
        }
        if (shoppingItemList.size() > 0) {
            MyConfirmShoppingItemAdapter adapter = new MyConfirmShoppingItemAdapter(getContext(), shoppingItemList);
            recycler_view_shopping.setAdapter(adapter);

        }
        calculatePrice();
    }

    private void calculatePrice() {
        double price = Common.DEFAULT_PRICE;
        for (BarberServices services : servicesAdded) {
            price += services.getPrice();
        }
        for (ShoppingItem shoppingItem : shoppingItemList) {
            price += Double.valueOf(shoppingItem.getPrice());
        }
        txt_total_price.setText(new StringBuilder(Common.MONEY_SIGN).append(price));
    }

    private void getBundle(Bundle arguments) {
        this.servicesAdded = new Gson().fromJson(arguments.getString(Common.SERVICES_ADDED), new TypeToken<HashSet<BarberServices>>() {
        }.getType());
        this.shoppingItemList = new Gson().fromJson(arguments.getString(Common.SHOPPING_LIST), new TypeToken<List<ShoppingItem>>() {
        }.getType());
    }


}
