package com.example.whatshistory.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Adapters.CallReyclerAdapter;
import com.example.whatshistory.CustomView.SmoothProgressBar;
import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.TaskRunner;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
    private boolean loading = true;
    LinearLayoutManager linearlayout;
    boolean isLoading = false;
    AdView mAdView;

    public CallsFragment() {
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        nocallrel = view.findViewById(R.id.nocallrel);
        callsrecycler = view.findViewById(R.id.callsrecycler);
        progressrel = view.findViewById(R.id.progressrel);
        progressBar = view.findViewById(R.id.progressbar);
        adapter = new CallReyclerAdapter(getContext(), callarray);
        linearlayout = new LinearLayoutManager(getContext());
        callsrecycler.setLayoutManager(linearlayout);
        callsrecycler.setAdapter(adapter);
//        callsrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == callarray.size() - 1) {
//                        Log.e("loadingindexcheck", "" + (callarray.size() - 1));
//                        callarray.add(null);
//                        adapter.notifyItemInserted(callarray.size() - 1);
//                        isLoading = true;
//                        int loadingindex = callarray.size() - 1;
//                        getMoreDataInBackground(loadingindex, isLoading);
//                        isLoading = false;
//                    }
//                }
//            }
//        });

        GetCallHistoryInBackground();
        return view;
    }

    private void getMoreDataInBackground(int loadingindex, boolean isLoading) {
        boolean[] loading = {isLoading};
        new TaskRunner().executeAsync(new Callable<String>() {
            @Override
            public String call() throws Exception {
//                callarray.clear();
                progressBar.setVisibility(View.VISIBLE);
                progressrel.setVisibility(View.VISIBLE);
                return getCallLogs();
            }
        }, new TaskRunner.Callback<String>() {
            @Override
            public void onComplete(String result) {
                Log.e("progressbarcheck", "" + result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            if (result.equals("complete")) {
                                callarray.remove(loadingindex);
                                adapter.notifyItemRemoved(loadingindex);
//                        adapter.notifyItemRangeInserted(loadingindex, callarray.size() - 1);
                                progressBar.setVisibility(GONE);
                                progressrel.setVisibility(GONE);
                                callsrecycler.setVisibility(View.VISIBLE);
                                loading[0] = false;
                            } else if (result.equals("no_data")) {
                                progressBar.setVisibility(GONE);
                                progressrel.setVisibility(GONE);
                                callsrecycler.setVisibility(GONE);
                                nocallrel.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                progressrel.setVisibility(GONE);
                callsrecycler.setVisibility(GONE);
                nocallrel.setVisibility(VISIBLE);
            }

            @Override
            public void onStart() {
                progressrel.setVisibility(VISIBLE);
                callsrecycler.setVisibility(GONE);
                nocallrel.setVisibility(GONE);
            }
        });
    }

    private String getCallLogs() {
        String result = "no_data";
        ContentResolver cr = getContext().getContentResolver();
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, mPhoneNumberProjection, null, null, CallLog.Calls.DATE + " DESC LIMIT 50"/*CallLog.Calls.DATE + " DESC limit 1;"*/);
        int idcolumn = managedCursor.getColumnIndex(CallLog.Calls._ID);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int c_iso = managedCursor.getColumnIndex(CallLog.Calls.COUNTRY_ISO);
        Log.e("cursorsizecheck", "" + managedCursor.getCount());
        if (managedCursor != null) {
            while (managedCursor.moveToNext()) {
                String name = null;
                if (number != -1) {
                    String phNumber = managedCursor.getString(number);
                    Log.e("phnumbercheck", "" + PhoneNumberUtils.formatNumber(phNumber));
                    if (!phNumber.equals("")) {
                        name = getContactName(getContext(), phNumber);
                    } else {
                        name = "Private Number";
                    }
                    String id = managedCursor.getString(idcolumn);
                    Log.e("idcheckvalue", "" + id);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.parseLong(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String countryiso = managedCursor.getString(c_iso);
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
                    CallsModel callsmodel = new CallsModel();
                    callsmodel.setDuration(callDuration);
                    callsmodel.setName(name);
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
                    callsmodel.setCountryIso(countryiso);
                    callarray.add(callsmodel);
                }
//                managedCursor.moveToPrevious();
            }

            Log.e("resultarraycheck", "" + callarray.size());
//            callarray = removeDuplicates(callarray);
            result = "complete";
            managedCursor.close();
        } else {
            result = "no_data";
            managedCursor.close();
        }
        return result;
    }

    @SuppressLint("Range")
    private void GetCallHistoryInBackground() {
        nocallrel.setVisibility(GONE);
        new TaskRunner().executeAsync(new Callable<String>() {
            String result = null;

            @Override
            public String call() {
                Log.e("asynctask", "onBackground");
                callarray.clear();
                return getCallLogs();
//                StringBuffer sb = new StringBuffer();
//                ContentResolver cr = getContext().getContentResolver();
//                Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
//                int idcolumn = managedCursor.getColumnIndex(CallLog.Calls._ID);
//                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
//                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
//                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//                sb.append("Call Details :");
//                if (managedCursor.getCount() > 0) {
////                    progressBar.progressiveStart();
//                    for (int i = 0; i < managedCursor.getCount(); i++) {
//                        managedCursor.moveToNext();
//                        String name = null;
//                        if (number != -1) {
//                            String phNumber = managedCursor.getString(number);
//                            if (!phNumber.equals("")) {
//                                name = getContactName(getContext(), phNumber);
//                            } else {
//                                name = "Private Number";
//                            }
//                            String id = managedCursor.getString(idcolumn);
//                            Log.e("idcheckvalue", "" + id);
//                            String callType = managedCursor.getString(type);
//                            String callDate = managedCursor.getString(date);
//                            Date callDayTime = new Date(Long.parseLong(callDate));
//                            String callDuration = managedCursor.getString(duration);
//                            String dir = null;
//                            int dircode = Integer.parseInt(callType);
//                            switch (dircode) {
//                                case CallLog.Calls.OUTGOING_TYPE:
//                                    dir = "OUTGOING";
//                                    break;
//
//                                case CallLog.Calls.INCOMING_TYPE:
//                                    dir = "INCOMING";
//                                    break;
//
//                                case CallLog.Calls.MISSED_TYPE:
//                                    dir = "MISSED";
//                                    break;
//                            }
//                            sb.append("\nPhone Number:--- " + phNumber +
//                                    " \nCall Type:--- " + dir +
//                                    " \nCall Date:--- " + callDayTime +
//                                    " \nCall duration in sec :--- " + callDuration);
//                            sb.append("\n----------------------------------");
//                            CallsModel callsmodel = new CallsModel();
//                            callsmodel.setDuration(callDuration);
//                            callsmodel.setName(name);
//                            callsmodel.setNumber(phNumber);
//                            callsmodel.setType(dir);
//                            Calendar cal = DateToCalendar(callDayTime);
//                            int day = cal.get(Calendar.DAY_OF_MONTH);
//                            int month = cal.get(Calendar.MONTH);
//                            int year = cal.get(Calendar.YEAR);
//                            String datestr = day + "/" + (month + 1) + "/" + year;
//                            String timestr = (String) DateFormat.format("HH:mm a", cal.getTime());
//                            callsmodel.setTime(timestr);
//                            callsmodel.setDate(datestr);
//                            callarray.add(callsmodel);
//                        }
//                    }
//                    Log.e("sbtextStruing", "" + sb);
//                    callarray = removeDuplicates(callarray);
//                    managedCursor.close();
//                    result = "complete";
//                } else {
//                    managedCursor.close();
//                    result = "no_data";
//                }
//                return result;
            }
        }, new TaskRunner.Callback<String>() {

            @Override
            public void onComplete(String result) {
//                progressBar.progressiveStop();
                Log.e("asynctask", "onComplete");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("progressbarcheck", "" + result);
                        if (result != null) {
                            if (result.equals("complete")) {
                                adapter.notifyDataSetChanged();
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
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("Exceptionincall", "" + e.toString());
                progressrel.setVisibility(VISIBLE);
                callsrecycler.setVisibility(GONE);
                nocallrel.setVisibility(GONE);
            }

            @Override
            public void onStart() {
                progressrel.setVisibility(VISIBLE);
                callsrecycler.setVisibility(GONE);
                nocallrel.setVisibility(GONE);
            }
        });
    }

    @SuppressLint("Range")
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
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
