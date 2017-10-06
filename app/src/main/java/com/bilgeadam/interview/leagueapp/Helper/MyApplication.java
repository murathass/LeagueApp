package com.bilgeadam.interview.leagueapp.Helper;

import android.app.Application;
import android.content.Intent;
import android.os.Binder;

import static android.app.Service.START_STICKY;

/**
 * Created by murathas on 5.10.2017.
 */

public class MyApplication extends Application{

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }




    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectvityReceiver.ConnectivityReceiverListener listener) {
        ConnectvityReceiver.connectivityReceiverListener = listener;
    }
}
