package com.example.hairsalonbookingstaff.Interface;

import android.content.DialogInterface;

public interface IAdminDialogUpdateBarber {
    void onClickPositiveButton(DialogInterface dialogInterface, String name, String username, String password);

    void onClickNegativeButton(DialogInterface dialogInterface);
}
