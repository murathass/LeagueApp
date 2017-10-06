package com.bilgeadam.interview.leagueapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilgeadam.interview.leagueapp.Entity.Match;
import com.bilgeadam.interview.leagueapp.R;

import java.util.List;

/**
 * Created by murathas on 5.10.2017.
 */

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyViewHolder> {

    private List<Match> matchList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView score, date, team1,team2;

        public MyViewHolder(View view) {
            super(view);
            score = (TextView) view.findViewById(R.id.score);
            date = (TextView) view.findViewById(R.id.date);
            team1 = (TextView) view.findViewById(R.id.team1name);
            team2 = (TextView) view.findViewById(R.id.team2name);
        }
    }


    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_matches_of_week_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.score.setText(match.getTeam1score()+":"+match.getTeam2score());
        holder.date.setText(match.getMatchDate());
        holder.team1.setText(match.getTeam1name()+"("+match.getTeam1code()+")");
        holder.team2.setText(match.getTeam2name()+"("+match.getTeam2code()+")");
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }
}
