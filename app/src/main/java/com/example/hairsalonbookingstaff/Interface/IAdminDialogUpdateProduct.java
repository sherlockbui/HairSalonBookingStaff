package com.example.hairsalonbookingstaff.Interface;

import android.content.DialogInterface;

public interface IAdminDialogUpdateProduct {
    void onClickPositiveButton(DialogInterface dialogInterface, String name, String price);

    void onClickNegativeButton(DialogInterface dialogInterface);

}
