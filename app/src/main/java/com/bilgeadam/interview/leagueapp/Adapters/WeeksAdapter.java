package com.bilgeadam.interview.leagueapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilgeadam.interview.leagueapp.R;

import java.util.List;

/**
 * Created by murathas on 5.10.2017.
 */

public class WeeksAdapter extends RecyclerView.Adapter<WeeksAdapter.MyViewHolder> {


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView weekTv;

        public MyViewHolder(View view) {
            super(view);
            weekTv = (TextView) view.findViewById(R.id.week);
        }
    }


    private List<String> weeks;

    public WeeksAdapter(List<String> weeks){
        this.weeks = weeks;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_season_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String week = weeks.get(position);
        holder.weekTv.setText(week);
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }


}
