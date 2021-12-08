package com.example.whatshistory.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                sumDuplicates(historyarray);
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

    public void sumDuplicates(ArrayList<HistoryModel> strarr) {
//        int[] array = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 9, 10};
//        int sum = 0;
//        for (int j = 0; j < array.length; j++) {
//            for (int k = j + 1; k < array.length; k++) {
//                if (k != j && array[k] == array[j]) {
//                    sum = sum + array[k];
//                    System.out.println("Duplicate found: " + array[k] + " " + "Sum of the duplicate value is " + sum);
//                }
//            }
//        }
//        int sum = 1;
//        for (int i = 0; i < strarr.size(); i++) {
//            for (int j = i + 1; j < strarr.size(); j++) {
//                if (j != i && strarr.get(j).getNumber().equals(strarr.get(i).getNumber())) {
//                   sum=sum+strarr.get();
//                    Log.e("Duplicatefound", "" + strarr.get(j).getNumber() + " " + "Sum of the duplicate value is " + sum);
//                }
//            }
//        }
    }
}
