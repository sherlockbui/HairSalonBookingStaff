package com.example.hairsalonbookingstaff.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.MyConfirmShoppingItemAdapter;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Model.BarberServices;
import com.example.hairsalonbookingstaff.Model.CartItem;
import com.example.hairsalonbookingstaff.Model.FCMResponse;
import com.example.hairsalonbookingstaff.Model.FCMSendData;
import com.example.hairsalonbookingstaff.R;
import com.example.hairsalonbookingstaff.Retrofit.IFCMService;
import com.example.hairsalonbookingstaff.Retrofit.RetrofitClient;
import com.example.hairsalonbookingstaff.StaffHomeActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalPriceFragment extends BottomSheetDialogFragment {
    private static TotalPriceFragment instance;
    ChipGroup chip_group_service;
    RecyclerView recycler_view_shopping;
    Button btn_confirm;
    HashSet<BarberServices> servicesAdded;
    //    List<ShoppingItem> shoppingItemList;
    TextView txt_total_price;
    SimpleDateFormat simpleDateFormat;
    IFCMService ifcmService;
    private Socket mSocket = MySocket.getmSocket();

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
        mSocket.connect();
        ifcmService = RetrofitClient.getInstance().create(IFCMService.class);
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        txt_total_price = view.findViewById(R.id.txt_total_price);
        chip_group_service = view.findViewById(R.id.chip_group_service);
        recycler_view_shopping = view.findViewById(R.id.recycler_view_shopping);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        recycler_view_shopping.setHasFixedSize(true);
        recycler_view_shopping.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.currentBookingInfomation.get_id() != null) {
                    mSocket.emit("doneService", Common.currentBookingInfomation.get_id()).on("doneService", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            final JSONObject object = (JSONObject) args[0];
                            Handler handler = new Handler(Looper.getMainLooper());

                            if (object != null) {
                                try {
                                    if (object.getString("ok").equalsIgnoreCase(String.valueOf(1))) {
                                        mSocket.emit("getTimeBooking", Common.currentBarber.getId(), simpleDateFormat.format(Common.bookingDate.getTime()));
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "Thanh Cong", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getActivity(), StaffHomeActivity.class));

                                            }
                                        });

                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            }
                                        });

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }


                        }
                    });
                }
                if (Common.currentBookingInfomation.getCustomerPhone() != null) {
                    FCMSendData sendRequest = new FCMSendData();
                    Map<String, String> dataSend = new HashMap<>();
                    dataSend.put(Common.TITLE_KEY, "Dịch Vụ Hoàn Tất");
                    dataSend.put(Common.CONTENT_KEY, "Vui lòng đánh giá Nhân Viên");
                    dataSend.put(Common.ID_BARBER_KEY, Common.currentBarber.getId());
                    sendRequest.setTo(Common.currentToken.getToken());
                    Log.d("token", "onClick: " + Common.currentToken.getToken());
                    sendRequest.setData(dataSend);
                    ifcmService.sendNotification(sendRequest).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(new Consumer<FCMResponse>() {
                        @Override
                        public void accept(FCMResponse fcmResponse) throws Exception {


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("NOTIFICATION_ERROR", "notification_error: " + throwable.getMessage());
                        }
                    });
                }

            }
        });

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
        if (Common.currentBookingInfomation.getCartItemList() != null) {
            if (Common.currentBookingInfomation.getCartItemList().size() > 0) {
                MyConfirmShoppingItemAdapter adapter = new MyConfirmShoppingItemAdapter(getContext(), Common.currentBookingInfomation.getCartItemList());
                recycler_view_shopping.setAdapter(adapter);
            }
            calculatePrice();
        }
    }

    private void calculatePrice() {
        double price = Common.DEFAULT_PRICE;
        for (BarberServices services : servicesAdded) {
            price += services.getPrice();
        }
        if (Common.currentBookingInfomation.getCartItemList() != null) {
            for (CartItem cartItem : Common.currentBookingInfomation.getCartItemList()) {
                price += (double) (cartItem.getProductPrice() * cartItem.getProductQuantity());
            }
        }
        txt_total_price.setText(new StringBuilder(Common.MONEY_SIGN).append(price));
    }

    private void getBundle(Bundle arguments) {
        this.servicesAdded = new Gson().fromJson(arguments.getString(Common.SERVICES_ADDED), new TypeToken<HashSet<BarberServices>>() {
        }.getType());
    }


}
