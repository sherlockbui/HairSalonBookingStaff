package com.example.hairsalonbookingstaff.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.hairsalonbookingstaff.Model.Barber;
import com.example.hairsalonbookingstaff.Model.BookingInfomation;
import com.example.hairsalonbookingstaff.Model.Salon;
import com.example.hairsalonbookingstaff.R;

import java.util.Calendar;

public class Common {
    public static final Object DISABLE_TAG = "DISABLE_TAG";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String CONTENT_KEY = "CONTENT_KEY";
    public static final String SERVICES_ADDED = "SERVICES_ADDED";
    public static final String MONEY_SIGN = "$ ";
    public static final String SHOPPING_LIST = "SHOPPING_LIST";
    public static double DEFAULT_PRICE = 30;
    public static String state_name ="";
    public static Salon selectedSalon;
    public static Barber currentBarber;
    public static Calendar bookingDate = Calendar.getInstance();
    public static BookingInfomation currentBookingInfomation;

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00 - 9:30";
            case 1:
                return "9:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "11:30 - 12:00";
            case 6:
                return "12:00 - 12:30";
            case 7:
                return "12:30 - 13:00";
            case 8:
                return "13:00 - 13:30";
            case 9:
                return "13:30 - 14:00";
            case 10:
                return "14:00 - 14:30";
            case 11:
                return "14:30 - 15:00";
            case 12:
                return "15:00 - 15:30";
            case 13:
                return "15:30 - 16:00";
            case 14:
                return "16:00 - 16:30";
            case 15:
                return "16:30 - 17:00";
            case 16:
                return "17:00 - 17:30";
            case 17:
                return "17:30 - 18:00";
            case 18:
                return "18:00 - 18:30";
            case 19:
                return "18:30 - 19:00";
            default:
                return "Closed";
        }
    }

    public static void showNotification(Context context, int notification_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context, notification_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANEL_ID = "barberbooking_chanel_01";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANEL_ID,
                    "BARBER_BOOKING_STAFF_APP",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Staff app");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID);
        builder.setContentTitle(title).
                setContentText(content).
                setAutoCancel(false).
                setSmallIcon(R.drawable.ic_launcher_background);
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(notification_id, notification);
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0, 10)).append("...").toString() : name;

    }
}
