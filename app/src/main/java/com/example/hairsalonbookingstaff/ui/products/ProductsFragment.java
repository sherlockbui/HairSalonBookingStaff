package com.example.hairsalonbookingstaff.ui.products;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminProductAdapter;
import com.example.hairsalonbookingstaff.Common.AdminCustomProductDialog;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateProduct;
import com.example.hairsalonbookingstaff.Model.ShoppingItem;
import com.example.hairsalonbookingstaff.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements IAdminDialogUpdateProduct {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    List<ShoppingItem> shoppingItems;
    RecyclerView recycler_items;
    Socket mSocket = MySocket.getmSocket();
    ChipGroup chipGroup;
    Chip chip_wax, chip_spray;
    AdminProductAdapter adapter;
    FloatingActionButton floatingActionButton;
    AdminCustomProductDialog dialog;
    String url_image;
    String selectedChip;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);
        mSocket.connect();
        progressDialog = new ProgressDialog(getContext());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        shoppingItems = new ArrayList<>();
        recycler_items = root.findViewById(R.id.recycler_items);
        floatingActionButton = root.findViewById(R.id.floating_action_button);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.setHasFixedSize(true);
        recycler_items.addItemDecoration(new SpaceItemDecoration(8));
        chipGroup = root.findViewById(R.id.chip_group);
        chip_wax = root.findViewById(R.id.chip_wax);
        chip_spray = root.findViewById(R.id.chip_spray);
        mSocket.on("getItemShopping", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        shoppingItems.add(new ShoppingItem(object.getString("_id"), object.getString("name"), object.getString("image"), object.getString("price")));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("BBB", "run: " + shoppingItems.size());
                                adapter = new AdminProductAdapter(getContext(), shoppingItems);
                                recycler_items.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        chip_spray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingItems = new ArrayList<>();
                setSelectedChip(chip_spray);
                mSocket.emit("getItemShopping", chip_spray.getText().toString());
            }
        });
        chip_wax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingItems = new ArrayList<>();
                setSelectedChip(chip_wax);
                mSocket.emit("getItemShopping", chip_wax.getText().toString());
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = AdminCustomProductDialog.getInstance();
                dialog.showLoginDialog("Thêm Sản Phẩm", "Thêm", "Hủy", getContext(), ProductsFragment.this);
                dialog.img_product.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                            }
                        }
                    }
                });
            }
        });
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
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
                selectedChip = chipItem.getText().toString();

            }

        }
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, String name, String price) {
        mSocket.emit("addProduct", selectedChip, name, Integer.valueOf(price), url_image).once("addProduct", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        shoppingItems.add(new ShoppingItem(object.getString("_id"), object.getString("name"), object.getString("image"), object.getString("price")));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(shoppingItems.size());
                                dialogInterface.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            dialog.img_product.setImageBitmap(photo);
            dialog.img_product.setDrawingCacheEnabled(true);
            dialog.img_product.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) dialog.img_product.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            StorageReference filepath = mStorageRef.child("Photos").child(data1.toString());
            UploadTask uploadTask = filepath.putBytes(data1);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            progressDialog.dismiss();
                            url_image = task.getResult().toString().substring(0, task.getResult().toString().indexOf("&token"));
                            Log.d("AAA", "onComplete: " + url_image);
                        }
                    });
                }
            });

        }
    }


}