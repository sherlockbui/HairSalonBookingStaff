package com.example.hairsalonbookingstaff.Interface;

import android.content.DialogInterface;

public interface IAdminDialogUpdateSalon {
    void onClickPositiveButton(DialogInterface dialogInterface, String name, String address, String website, String phone);

    void onClickNegativeButton(DialogInterface dialogInterface);
}
