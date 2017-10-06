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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bilgeadam.interview.leagueapp.Activities.MainActivity;
import com.bilgeadam.interview.leagueapp.Activities.SplashScreen;
import com.bilgeadam.interview.leagueapp.Adapters.WeeksAdapter;
import com.bilgeadam.interview.leagueapp.Helper.DividerItemDecoration;
import com.bilgeadam.interview.leagueapp.Helper.RecyclerTouchListener;
import com.bilgeadam.interview.leagueapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by murathas on 5.10.2017.
 */

public class SeasonList extends Fragment {

    private List<String> weeks = new ArrayList<>();
    private RecyclerView recyclerView;
    private WeeksAdapter mAdapter;
    private TextView titleTv;


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mNetChangedReceiver, new IntentFilter("com.bilgeadam.interview.leagueapp.NetChange"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_season_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        titleTv = view.findViewById(R.id.titleSeason);
        mAdapter = new WeeksAdapter(weeks);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String week = weeks.get(position);
                MatchesOfWeek mow = new MatchesOfWeek();
                Bundle b = new Bundle();
                b.putString("week", position + "");
                mow.setArguments(b);
                try {
                    ((MainActivity) getActivity()).replaceFragment(mow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getActivity(),week + " is selected!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(View view, int position) {
                String week = weeks.get(position);
                MatchesOfWeek mow = new MatchesOfWeek();
                Bundle b = new Bundle();
                b.putString("week", week);
                mow.setArguments(b);
                try {
                    ((MainActivity) getActivity()).replaceFragment(mow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getActivity(),week + " is selected!", Toast.LENGTH_SHORT).show();
            }
        }));

        fetchData();

        return view;
    }

    private void fetchData() {

        weeks.clear();

        if (isConnected()) {
            new PrefetchData().execute();
            Log.e("network", "true");
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!getActivity().isFinishing()) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
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

    private class PrefetchData extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            Log.e("JSON", result);
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray rounds = obj.getJSONArray("rounds");
                for (int i = 0; i < rounds.length(); i++) {
                    JSONObject weekObj = rounds.getJSONObject(i);
                    weeks.add(weekObj.getString("name"));
                }
                final String title = obj.getString("name");
                titleTv.setText(title);

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                mAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();

        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private final BroadcastReceiver mNetChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("NEtwork Değişikliği", "NEtwork Receiver ");
            fetchData();


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
