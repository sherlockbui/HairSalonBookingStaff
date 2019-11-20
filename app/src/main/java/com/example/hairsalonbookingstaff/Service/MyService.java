package com.example.hairsalonbookingstaff.Service;

import android.util.Log;

import com.example.hairsalonbookingstaff.Common.Common;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SharedPrefManager;
import com.example.hairsalonbookingstaff.Model.MyToken;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Random;

public class MyService extends FirebaseMessagingService {
    SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
    Socket mSocket = MySocket.getmSocket();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("token", "onNewToken: " + token);
        if (prefManager.isLoggedIn()) {
            MyToken myToken = new MyToken();
            myToken.setToken(token);
            myToken.setIdbarber(Common.currentBarber.getId());
            String jsonToken = new Gson().toJson(myToken);
            mSocket.emit("updateToken", jsonToken);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("token", "onMessageReceived: " + remoteMessage);
        Common.showNotification(this, new Random().nextInt(),
                remoteMessage.getData().get(Common.TITLE_KEY),
                remoteMessage.getData().get(Common.CONTENT_KEY), null);
    }
}
