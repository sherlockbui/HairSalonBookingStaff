package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Common.AdminCustomProductDialog;
import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateProduct;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.MyViewHolder> implements IAdminDialogUpdateProduct {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Context context;
    List<ShoppingItem> shoppingItems;
    Socket mSocket = MySocket.getmSocket();
    String id_product;
    int index_update;

    public AdminProductAdapter(Context context, List<ShoppingItem> shoppingItems) {
        mSocket.connect();
        this.context = context;
        this.shoppingItems = shoppingItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.admin_layout_products_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Picasso.get().load(shoppingItems.get(position).getImage()).placeholder(R.drawable.ic_image_black_24dp).into(holder.img_shopping_item);
        holder.txt_shopping_item_name.setText(Common.formatShoppingItemName(shoppingItems.get(position).getName()));
        holder.img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_product = shoppingItems.get(position).getId();
                index_update = position;
                AdminCustomProductDialog dialog = AdminCustomProductDialog.getInstance();
                dialog.showLoginDialog("Cập nhật sản phẩm", "Cập nhật", "Hủy", context, AdminProductAdapter.this);
                Picasso.get().load(shoppingItems.get(position).getImage()).placeholder(R.drawable.ic_image_black_24dp).into(dialog.img_product);
                dialog.name.setText(shoppingItems.get(position).getName());
                dialog.price.setText(shoppingItems.get(position).getPrice());
//                dialog.img_product.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                            {
//                                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//                            }
//                            else
//                            {
//                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                ((Activity)context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
//
//
//                            }
//                        }
//
//                    }
//                });
            }
        });
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("deleteProduct", shoppingItems.get(position).getId()).once("deleteProduct", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        JSONObject object = (JSONObject) args[0];
                        if (object != null) {
                            shoppingItems.remove(position);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                                    AdminProductAdapter.this.notifyItemRemoved(position);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String price) {
        mSocket.emit("updateProduct", id_product, name, price).once("updateProduct", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                Handler handler = new Handler(Looper.getMainLooper());
                if (object != null) {

                    try {
                        shoppingItems.set(index_update, new ShoppingItem(object.getString("_id"), object.getString("name"), object.getString("image"), object.getString("price")));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Thành Công", Toast.LENGTH_SHORT).show();
                                AdminProductAdapter.this.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_shopping_item_name;
        ImageView img_shopping_item, img_update, img_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            txt_shopping_item_name = itemView.findViewById(R.id.txt_name_shoppong_item);
            img_update = itemView.findViewById(R.id.img_update);
            img_delete = itemView.findViewById(R.id.img_delete);
        }


    }
}
