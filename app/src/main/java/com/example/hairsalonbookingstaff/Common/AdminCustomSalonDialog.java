package com.example.hairsalonbookingstaff.Common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateSalon;
import com.example.hairsalonbookingstaff.R;

public class AdminCustomSalonDialog {
    public static AdminCustomSalonDialog mDialog;
    public TextView txt_title;
    public EditText edt_name_salon, edt_address, edt_website, edt_phone_number;
    public Button btn_left, btn_right;
    public IAdminDialogUpdateSalon iDialogClickListener;


    public static AdminCustomSalonDialog getInstance() {
        if (mDialog == null) {
            mDialog = new AdminCustomSalonDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title, String positiveText, String negativeText, Context context, final IAdminDialogUpdateSalon iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_layout_update_dialog);
        txt_title = dialog.findViewById(R.id.txt_title);
        edt_name_salon = dialog.findViewById(R.id.edt_name_salon);
        edt_address = dialog.findViewById(R.id.edt_address);
        edt_website = dialog.findViewById(R.id.edt_website);
        edt_phone_number = dialog.findViewById(R.id.edt_phone_number);
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
                iDialogClickListener.onClickPositiveButton(dialog, edt_name_salon.getText().toString(), edt_address.getText().toString(), edt_website.getText().toString(), edt_phone_number.getText().toString());

            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickNegativeButton(dialog);
            }
        });
    }
}
