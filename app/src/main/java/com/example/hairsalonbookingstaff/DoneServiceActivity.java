package com.example.hairsalonbookingstaff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Fragment.ShopingFragment;
import com.example.hairsalonbookingstaff.Fragment.TotalPriceFragment;
import com.example.hairsalonbookingstaff.Interface.IOnShoppingItemSelected;
import com.example.hairsalonbookingstaff.Model.BarberServices;
import com.example.hairsalonbookingstaff.Model.CartItem;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DoneServiceActivity extends AppCompatActivity implements IOnShoppingItemSelected {
    TextView txt_customer_name, txt_customer_phone;
    ChipGroup chip_group_service, chip_group_shopping;
    AutoCompleteTextView edt_services;
    ImageView img_add_shopping;
    Button btn_finish;
    List<String> nameService;
    //    List<ShoppingItem> shoppingItems = new ArrayList<>();
    List<BarberServices> barberServices = new ArrayList<>();
    HashSet<BarberServices> serviceAdded = new HashSet<>();
    LayoutInflater inflater;
    private Socket mSocket = MySocket.getmSocket();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_service);
        getSupportActionBar().setTitle("Thanh Toán");
        mSocket.connect();
        inflater = LayoutInflater.from(this);
        nameService = new ArrayList<>();
        txt_customer_name = findViewById(R.id.txt_customer_name);
        txt_customer_phone = findViewById(R.id.txt_customer_phone);
        chip_group_service = findViewById(R.id.chip_group_service);
        chip_group_shopping = findViewById(R.id.chip_group_shopping);
        edt_services = findViewById(R.id.edt_service);
        img_add_shopping = findViewById(R.id.add_shopping);
        img_add_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopingFragment shopingFragment = ShopingFragment.getInstance(DoneServiceActivity.this);
                shopingFragment.show(getSupportFragmentManager(), "Shopping");
            }
        });
        btn_finish = findViewById(R.id.btn_finish);
        setCustomerInfomation();
        mSocket.emit("getServices").on("getServices", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        barberServices.add(new BarberServices(object.getString("name"), object.getLong("price")));
                        nameService.add(object.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, nameService);
        edt_services.setThreshold(1);
        edt_services.setAdapter(adapter);
        edt_services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int index = nameService.indexOf(edt_services.getText().toString().trim());
                if (!serviceAdded.contains(barberServices.get(index))) {
                    serviceAdded.add(barberServices.get(index));
                    final Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
                    item.setText(edt_services.getText().toString());
                    item.setTag(index);
                    edt_services.setText("");
                    item.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chip_group_service.removeView(v);
                            serviceAdded.remove(barberServices.get(index));
                        }
                    });
                    chip_group_service.addView(item);
                } else {
                    edt_services.setText("");
                }
            }
        });
        loadExtraItems();
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TotalPriceFragment fragment = TotalPriceFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString(Common.SERVICES_ADDED, new Gson().toJson(serviceAdded));
//                bundle.putString(Common.SHOPPING_LIST, new Gson().toJson(shoppingItems));
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "Price");
            }
        });
    }

    private void setCustomerInfomation() {
        txt_customer_name.setText(Common.currentBookingInfomation.getCustomerName());
        txt_customer_phone.setText(Common.currentBookingInfomation.getCustomerPhone());
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
//        shoppingItems.add(shoppingItem);
        CartItem cartItem = new CartItem();
        cartItem.setProductId(shoppingItem.getId());
        cartItem.setProductImage(shoppingItem.getImage());
        cartItem.setProductName(shoppingItem.getName());
        cartItem.setProductPrice(Long.valueOf(shoppingItem.getPrice()));
        cartItem.setProductQuantity(1);
        cartItem.setUserPhone(Common.currentBookingInfomation.getCustomerPhone());

        if (Common.currentBookingInfomation.getCartItemList() == null) {
            Common.currentBookingInfomation.setCartItemList(new ArrayList<CartItem>());
        }
        boolean flag = false;
        for (int i = 0; i < Common.currentBookingInfomation.getCartItemList().size(); i++) {
            if (Common.currentBookingInfomation.getCartItemList().get(i).getProductName().equals(shoppingItem.getName())) {
                flag = true;
                CartItem itemUpdate = Common.currentBookingInfomation.getCartItemList().get(i);
                itemUpdate.setProductQuantity(itemUpdate.getProductQuantity() + 1);
                Common.currentBookingInfomation.getCartItemList().set(i, itemUpdate);

            }
        }
        if (!flag) {
            Common.currentBookingInfomation.getCartItemList().add(cartItem);
            final Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
            item.setText(cartItem.getProductName());
            item.setTag(Common.currentBookingInfomation.getCartItemList().indexOf(cartItem));
            item.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chip_group_shopping.removeView(v);
                    Common.currentBookingInfomation.getCartItemList().remove(item.getTag());
                }
            });
            chip_group_shopping.addView(item);
        } else {
            chip_group_shopping.removeAllViews();
            loadExtraItems();
        }
    }

    private void loadExtraItems() {
        if (Common.currentBookingInfomation.getCartItemList() != null) {
            for (CartItem cartItem : Common.currentBookingInfomation.getCartItemList()) {
                final Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
                item.setText(new StringBuilder(cartItem.getProductName()).append(" x").append(cartItem.getProductQuantity()));
                item.setTag(Common.currentBookingInfomation.getCartItemList().indexOf(cartItem));
                item.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chip_group_shopping.removeView(v);
                        Common.currentBookingInfomation.getCartItemList().remove(item.getTag());
                    }
                });
                chip_group_shopping.addView(item);
            }
        }

    }


}
