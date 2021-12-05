package com.example.whatshistory.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Adapters.HistoryReyclerAdapter;
import com.example.whatshistory.Adapters.MessagesRecyclerAdapter;
import com.example.whatshistory.Databases.Sqlitedatabase;
import com.example.whatshistory.Models.HistoryModel;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.TaskRunner;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class HistoryFragment extends Fragment {
    RelativeLayout nohistoryrel, progressrel;
    RecyclerView historyrecycler;
    ArrayList<HistoryModel> historyarray = new ArrayList<>();
    AdView mAdView;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        nohistoryrel = view.findViewById(R.id.nohistoryrel);
        progressrel = view.findViewById(R.id.progressrel);
        historyrecycler = view.findViewById(R.id.historyrecycler);
        getHistory();
        return view;
    }

    public void getHistory() {

        new TaskRunner().executeAsync(new Callable<String>() {
            @Override
            public String call() {
                Sqlitedatabase database = new Sqlitedatabase(getContext());
                historyarray = database.getAllHistory();
                if (historyarray.size() > 0) {
                    return "complete";
                } else {
                    return "no_data";
                }
            }
        }, new TaskRunner.Callback<String>() {
            @Override
            public void onStart() {
                progressrel.setVisibility(View.VISIBLE);
                historyrecycler.setVisibility(View.GONE);
                nohistoryrel.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(String result) {
                if (result.equals("no_data")) {
                    progressrel.setVisibility(View.GONE);
                    historyrecycler.setVisibility(View.GONE);
                    nohistoryrel.setVisibility(View.VISIBLE);
                } else if (result.equals("complete")) {
                    HistoryReyclerAdapter adapter = new HistoryReyclerAdapter(getContext(), historyarray);
                    historyrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    historyrecycler.setAdapter(adapter);
                    progressrel.setVisibility(View.GONE);
                    historyrecycler.setVisibility(View.VISIBLE);
                    nohistoryrel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                progressrel.setVisibility(View.GONE);
                nohistoryrel.setVisibility(View.VISIBLE);
                historyrecycler.setVisibility(View.GONE);
            }
        });
    }
}
