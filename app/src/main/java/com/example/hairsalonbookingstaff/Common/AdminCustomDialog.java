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

import com.example.hairsalonbookingstaff.Interface.IAdminDialogClickListener;
import com.example.hairsalonbookingstaff.R;

public class AdminCustomDialog {
    public static AdminCustomDialog mDialog;
    public TextView txt_title;
    public EditText edt_1, edt_2;
    public Button btn_left, btn_right;
    public IAdminDialogClickListener iDialogClickListener;


    public static AdminCustomDialog getInstance() {
        if (mDialog == null) {
            mDialog = new AdminCustomDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title, String positiveText, String negativeText, Context context, boolean edt1, boolean edt2, final IAdminDialogClickListener iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        txt_title = dialog.findViewById(R.id.txt_title);
        edt_1 = dialog.findViewById(R.id.edt_1);
        if (edt1) {
            edt_1.setVisibility(View.VISIBLE);
        }

        edt_2 = dialog.findViewById(R.id.edt_2);
        if (edt2) {
            edt_2.setVisibility(View.VISIBLE);
        }
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
                iDialogClickListener.onClickPositiveButton(dialog, edt_1.getText().toString().trim());

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
