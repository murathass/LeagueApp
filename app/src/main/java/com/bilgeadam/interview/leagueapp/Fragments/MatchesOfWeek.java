package com.bilgeadam.interview.leagueapp.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilgeadam.interview.leagueapp.Adapters.MatchAdapter;
import com.bilgeadam.interview.leagueapp.Entity.Match;
import com.bilgeadam.interview.leagueapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by murathas on 5.10.2017.
 */

public class MatchesOfWeek extends Fragment {

    private List<Match> matchList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MatchAdapter mAdapter;
    private TextView titleTv;
    private int selectedWeek;


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mNetChangedReceiver, new IntentFilter("com.bilgeadam.interview.leagueapp.NetChange"));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches_of_week_list,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        titleTv = (TextView)view.findViewById(R.id.titleWeek);
        mAdapter = new MatchAdapter(matchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        String week = getArguments().getString("week");
        selectedWeek =Integer.parseInt(week);

        fetchData(selectedWeek);
        

        return view;
    }

    private void fetchData(int week) {

        matchList.clear();

        if (isConnected()){
            new PrefetchData(week).execute();
            Log.e("network","true");
        }else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!getActivity().isFinishing()){
                        final AlertDialog.Builder alert =  new AlertDialog.Builder(getActivity())
                                .setTitle("Uyarı!")
                                .setMessage("İnternet Bağlantınızı Kontrol Ediniz!")
                                .setCancelable(false)
                                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert.show();
                    }
                }
            });

        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private class PrefetchData extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;

        private int index;

        public PrefetchData(int index){
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new  ProgressDialog(getActivity());
            dialog.setTitle("Veriler Getiriliyor..");
            dialog.show();
        }

        String result = "";

        @Override
        protected String doInBackground(Void... params) {

            BufferedReader reader = null;
            StringBuilder sb = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getActivity().getAssets().open("match_results.json"), "UTF-8"));

                String mLine;
                sb = new StringBuilder();
                while ((mLine = reader.readLine()) != null) {
                    //process line
                    sb.append(mLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("JSON",result);
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray rounds = obj.getJSONArray("rounds");
                JSONObject matchday = rounds.getJSONObject(index);
                String title = matchday.getString("name");
                titleTv.setText(title);
                JSONArray matches = matchday.getJSONArray("matches");

                for (int i = 0; i <matches.length() ; i++) {
                    JSONObject matchobj = matches.getJSONObject(i);
                    JSONObject team1obj = matchobj.getJSONObject("team1");
                    JSONObject team2obj = matchobj.getJSONObject("team2");
                    Match match = new Match();
                    match.setMatchDate(matchobj.getString("date"));
                    match.setTeam1code(team1obj.getString("code"));
                    match.setTeam1name(team1obj.getString("name"));
                    match.setTeam1key(team1obj.getString("key"));
                    match.setTeam1score(matchobj.getString("score1"));

                    match.setTeam2code(team2obj.getString("code"));
                    match.setTeam2name(team2obj.getString("name"));
                    match.setTeam2key(team2obj.getString("key"));
                    match.setTeam2score(matchobj.getString("score2"));

                    matchList.add(match);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                mAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();

        }
    }

    private final BroadcastReceiver mNetChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("NEtwork Değişikliği", "NEtwork Receiver ");
            fetchData(selectedWeek);


        }
    };

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mNetChangedReceiver != null) {
                getActivity().unregisterReceiver(mNetChangedReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
