package com.bilgeadam.interview.leagueapp.Activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.bilgeadam.interview.leagueapp.Fragments.SeasonList;
import com.bilgeadam.interview.leagueapp.R;
import com.bilgeadam.interview.leagueapp.Services.NetworkChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isRunning = isLocationServiceRunning();
        Log.e("Service",isRunning+"");
        if (!isRunning){
            startService(new Intent(this, NetworkChecker.class));
            Log.i("Service","Running Location Service Starting");
            isLocationServiceRunning();
        }else{
            Log.i("Service","Running Location Service");
        }

        try {
            replaceFragment(new SeasonList());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        FrameLayout fl = (FrameLayout) findViewById(R.id.content_frame);
        if (fl.getChildCount() == 1) {
            super.onBackPressed();
            if (fl.getChildCount() == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Dikkat")
                        .setMessage("Çıkış Yapmak İstediğinizden Emin Misiniz?")
                        .setPositiveButton("EVET",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                })
                        .setNegativeButton("HAYIR",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        try {
                                            replaceFragment(new SeasonList());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            finish();
                                        }
                                    }
                                }).show();
                try {
                    replaceFragment(new SeasonList());
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        } else if (fl.getChildCount() == 0) {
            try {
                replaceFragment(new SeasonList());
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }


    public void replaceFragment (Fragment fragment)throws Exception{
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;
        Log.d("EKRANLAR",fragmentTag);
        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.e("Service", service.service.getClassName());
            if (service.service.getClassName().equals("com.bilgeadam.interview.leagueapp.Services.NetworkChecker")) {
                return true;
            }
        }
        return false;
    }
}
