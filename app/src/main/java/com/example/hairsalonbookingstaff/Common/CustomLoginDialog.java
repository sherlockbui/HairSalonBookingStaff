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

import com.example.hairsalonbookingstaff.Interface.IDialogClickListener;
import com.example.hairsalonbookingstaff.R;

public class CustomLoginDialog {
    TextView txt_title;
    EditText edt_user,edt_password;
    Button btn_login, btn_cancel;
    public static CustomLoginDialog mDialog;
    public IDialogClickListener iDialogClickListener;




    public static CustomLoginDialog getInstance() {
        if(mDialog==null){
            mDialog = new CustomLoginDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title, String positiveText, String negativeText, Context context, final IDialogClickListener iDialogClickListener){
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login);
        txt_title= dialog.findViewById(R.id.txt_title);
        edt_user = dialog.findViewById(R.id.edt_user);
        edt_password = dialog.findViewById(R.id.edt_password);
        btn_login = dialog.findViewById(R.id.btn_login);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);



        if(!TextUtils.isEmpty(title)){
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);

        }
        btn_login.setText(positiveText);
        btn_cancel.setText(negativeText);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickPositiveButton(dialog, edt_user.getText().toString().trim(), edt_password.getText().toString().trim());

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickNegativeButton(dialog);
            }
        });
    }
}
