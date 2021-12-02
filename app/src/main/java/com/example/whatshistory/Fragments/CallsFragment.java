package com.example.whatshistory.Fragments;

import static android.view.View.GONE;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Adapters.CallReyclerAdapter;
import com.example.whatshistory.CustomView.SmoothProgressBar;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.TaskRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class CallsFragment extends Fragment {
    String TAG = "CallsFragment";
    ArrayList<CallsModel> callarray = new ArrayList<>();
    RelativeLayout nocallrel;
    RecyclerView callsrecycler;
    RelativeLayout progressrel;
    CallReyclerAdapter adapter;
    SmoothProgressBar progressBar;

    public CallsFragment() {
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        nocallrel = view.findViewById(R.id.nocallrel);
        callsrecycler = view.findViewById(R.id.callsrecycler);
        progressrel = view.findViewById(R.id.progressrel);
        progressBar = view.findViewById(R.id.progressbar);
        progressBar.progressiveStart();
        adapter = new CallReyclerAdapter(getContext(), callarray);
        callsrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        callsrecycler.setAdapter(adapter);
//        adapter = new CallReyclerAdapter();
//        adapter.submitList(getContext(), callarray);


        GetCallHistory();
//        GetCalls();
        return view;
    }

    @SuppressLint("Range")
    private void GetCallHistory() {
        progressBar.setVisibility(View.VISIBLE);
        progressrel.setVisibility(View.VISIBLE);
        nocallrel.setVisibility(GONE);
        new TaskRunner().executeAsync(new Callable<String>() {
            @Override
            public String call() throws Exception {
                callarray.clear();
                StringBuffer sb = new StringBuffer();
                ContentResolver cr = getContext().getContentResolver();
                Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");
                if (managedCursor.getCount() > 0) {
                    while (managedCursor.moveToNext()) {
                        String phNumber = managedCursor.getString(number);
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phNumber));
                        Cursor c = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                        if (c != null) {
                            if (c.moveToFirst()) {
                                phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            }
                            c.close();
                        }
                        String callType = managedCursor.getString(type);
                        String callDate = managedCursor.getString(date);
                        Date callDayTime = new Date(Long.valueOf(callDate));
                        String callDuration = managedCursor.getString(duration);
                        String dir = null;
                        int dircode = Integer.parseInt(callType);
                        switch (dircode) {
                            case CallLog.Calls.OUTGOING_TYPE:
                                dir = "OUTGOING";
                                break;

                            case CallLog.Calls.INCOMING_TYPE:
                                dir = "INCOMING";
                                break;

                            case CallLog.Calls.MISSED_TYPE:
                                dir = "MISSED";
                                break;
                        }
                        sb.append("\nPhone Number:--- " + phNumber +
                                " \nCall Type:--- " + dir +
                                " \nCall Date:--- " + callDayTime +
                                " \nCall duration in sec :--- " + callDuration);
                        sb.append("\n----------------------------------");
                        CallsModel callsmodel = new CallsModel();
                        callsmodel.setDuration(callDuration);
                        callsmodel.setNumber(phNumber);
                        callsmodel.setType(dir);
                        Calendar cal = DateToCalendar(callDayTime);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int month = cal.get(Calendar.MONTH);
                        int year = cal.get(Calendar.YEAR);
                        String datestr = day + "/" + (month + 1) + "/" + year;
                        String timestr = (String) DateFormat.format("HH:mm a", cal.getTime());
                        callsmodel.setTime(timestr);
                        callsmodel.setDate(datestr);
                        callarray.add(callsmodel);
                    }
                    Log.e("sbtextStruing", "" + sb);
                    callarray = removeDuplicates(callarray);
                    adapter = new CallReyclerAdapter(getContext(), callarray);
                    callsrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    callsrecycler.setAdapter(adapter);
//                    callsrecycler.swapAdapter(adapter,true);
//                    callsrecycler.scrollBy(0, 0);
//                    adapter.notifyDataSetChanged();
                    managedCursor.close();
                    return "complete";
                } else {
                    managedCursor.close();
                    return "no_data";
                }
            }
        }, new TaskRunner.Callback<String>() {
            @Override
            public void onComplete(String result) {
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                progressBar.progressiveStop();
                if (result.equals("complete")) {
                    progressBar.setVisibility(GONE);
                    progressrel.setVisibility(GONE);
                    callsrecycler.setVisibility(View.VISIBLE);
                } else if (result.equals("no_data")) {
                    progressBar.setVisibility(GONE);
                    progressrel.setVisibility(GONE);
                    callsrecycler.setVisibility(GONE);
                    nocallrel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public ArrayList<CallsModel> removeDuplicates(ArrayList<CallsModel> list) {
        Set<CallsModel> set = new TreeSet(new Comparator<CallsModel>() {

            @Override
            public int compare(CallsModel o1, CallsModel o2) {
                if (o1.getNumber().equalsIgnoreCase(o2.getNumber())) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);
        final ArrayList newList = new ArrayList(set);
        return newList;
    }

    private Calendar DateToCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
}
