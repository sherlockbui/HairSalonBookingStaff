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

import com.example.hairsalonbookingstaff.Interface.IAdminDialogUpdateBarber;
import com.example.hairsalonbookingstaff.R;

public class AdminCustomBarberDialog {
    public static AdminCustomBarberDialog mDialog;
    public TextView txt_title;
    public EditText name, username, password;
    public Button btn_left, btn_right;
    public IAdminDialogUpdateBarber iDialogClickListener;

    public static AdminCustomBarberDialog getInstance() {
        if (mDialog == null) {
            mDialog = new AdminCustomBarberDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title, String positiveText, String negativeText, Context context, final IAdminDialogUpdateBarber iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_layout_barber_dialog);
        txt_title = dialog.findViewById(R.id.txt_title);
        name = dialog.findViewById(R.id.edt_name_barber);
        username = dialog.findViewById(R.id.edt_username);
        password = dialog.findViewById(R.id.edt_password);
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
                iDialogClickListener.onClickPositiveButton(dialog, name.getText().toString(), username.getText().toString(), password.getText().toString());

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
