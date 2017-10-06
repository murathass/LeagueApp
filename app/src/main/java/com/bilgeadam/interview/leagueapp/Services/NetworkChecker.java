package com.bilgeadam.interview.leagueapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bilgeadam.interview.leagueapp.Helper.ConnectvityReceiver;
import com.bilgeadam.interview.leagueapp.Helper.MyApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by murathas on 5.10.2017.
 */

public class NetworkChecker extends Service implements ConnectvityReceiver.ConnectivityReceiverListener {


    @Override
    public void onCreate() {

        super.onCreate();

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy );

        Log.e("Service"," NetworkChecker Service running");
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.e("AAAAA","BBBB"+isConnected);
        if (isConnected){
            if (isInternetAvailable()){
                Toast.makeText(getApplicationContext(),"Bağlandı!",Toast.LENGTH_SHORT).show();
                Log.e("Service","Baglandı");
                Intent intent= new Intent();
                intent.setAction("com.bilgeadam.interview.leagueapp.NetChange");
                sendBroadcast(intent);
            }else{
                Toast.makeText(getApplicationContext(),"Network Bağlantısı Koptu!",Toast.LENGTH_SHORT).show();
                Log.e("Service","Koptu!");
                Intent intent= new Intent();
                intent.setAction("com.bilgeadam.interview.leagueapp.NetChange");
                sendBroadcast(intent);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Network Bağlantısı Koptu!",Toast.LENGTH_SHORT).show();
            Log.e("Service","Koptu!");
            Intent intent= new Intent();
            intent.setAction("com.bilgeadam.interview.leagueapp.NetChange");
            sendBroadcast(intent);
        }
    }

    public boolean isInternetAvailable() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(3000);
            urlc.setReadTimeout(4000);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            return false;
        }

    }
}
