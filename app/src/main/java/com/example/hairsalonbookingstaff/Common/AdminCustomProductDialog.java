package com.example.hairsalonbookingstaff.Common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateProduct;
import com.example.hairsalonbookingstaff.R;

public class AdminCustomProductDialog {
    public static AdminCustomProductDialog mDialog;
    public TextView txt_title;
    public EditText name, price;
    public ImageView img_product;
    public Button btn_left, btn_right;
    public IAdminDialogUpdateProduct iDialogClickListener;

    public static AdminCustomProductDialog getInstance() {
        if (mDialog == null) {
            mDialog = new AdminCustomProductDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title, String positiveText, String negativeText, final Context context, final IAdminDialogUpdateProduct iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_layout_product_dialog);
        txt_title = dialog.findViewById(R.id.txt_title);
        name = dialog.findViewById(R.id.edt_name_product);
        price = dialog.findViewById(R.id.edt_price);
        img_product = dialog.findViewById(R.id.img_add_photo);
        btn_left = dialog.findViewById(R.id.btn_left);
        btn_right = dialog.findViewById(R.id.btn_right);


        if (!TextUtils.isEmpty(title)) {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);
        }
        btn_left.setText(positiveText);
        btn_right.setText(negativeText);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickPositiveButton(dialog, name.getText().toString(), price.getText().toString());

            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickNegativeButton(dialog);
            }
        });
        img_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
