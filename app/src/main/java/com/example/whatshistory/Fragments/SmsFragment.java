package com.example.whatshistory.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Adapters.CallReyclerAdapter;
import com.example.whatshistory.Adapters.MessagesRecyclerAdapter;
import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.Models.Message;
import com.example.whatshistory.Models.MessagesModel;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.TaskRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

public class SmsFragment extends Fragment {
    ArrayList<MessagesModel> messagesarray = new ArrayList<>();
    MessagesRecyclerAdapter adapter;
    RecyclerView messagesrecycler;

    public SmsFragment() {
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);
        messagesrecycler = view.findViewById(R.id.messagesrecycler);
//        new TaskRunner().executeAsync(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                Uri uriSms = Uri.parse("content://sms/inbox");
//                Cursor cursor = getContext().getContentResolver()
//                        .query(uriSms,
//                                new String[]{"_id", "address", "date", "body",
//                                        "type", "read", "person", "service_center"}, "type=" + 1, null,
//                                "date" + " COLLATE LOCALIZED ASC");
//                if (cursor != null) {
//                    cursor.moveToLast();
//                    if (cursor.getCount() > 0) {
//                        messagesarray.clear();
//                        android.util.Log.e("COLUMNS", Arrays.toString(cursor.getColumnNames()));
//                        do {
//                            String no = cursor.getString(cursor.getColumnIndex("service_center"));
//                            String messageNumber = cursor.getString(cursor.getColumnIndex("address"));
//                            String messageContent = cursor.getString(cursor.getColumnIndex("body"));
//                            String date = cursor.getString(cursor.getColumnIndex("date"));
//                            String type = cursor.getString(cursor.getColumnIndex("type"));
//                            Date callDayTime = new Date(Long.valueOf(date));
//                            String dir = null;
//                            int dircode = Integer.parseInt(type);
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
//                            Log.e("messagescheck", no + " " + messageNumber + " " + callDayTime.toString() + " " + dir);
//                            String name = getContactName(getContext(), messageNumber);
//                            if (name != null) {
//                                MessagesModel messagesmodel = new MessagesModel();
//                                messagesmodel.setName(name);
//                                messagesmodel.setNumber(messageNumber);
//                                messagesmodel.setType(dir);
//                                Calendar cal = DateToCalendar(callDayTime);
//                                int day = cal.get(Calendar.DAY_OF_MONTH);
//                                int month = cal.get(Calendar.MONTH);
//                                int year = cal.get(Calendar.YEAR);
//                                String datestr = day + "/" + (month + 1) + "/" + year;
//                                String timestr = (String) DateFormat.format("HH:mm a", cal.getTime());
//                                messagesmodel.setTime(timestr);
//                                messagesmodel.setDate(datestr);
//                                messagesarray.add(messagesmodel);
//                            }
//                        } while (cursor.moveToPrevious());
//
//                    } else {
//                        return "no_data";
//                    }
//                    cursor.close();
//                    return "complete";
//                } else {
//                    return "no_data";
//                }
//            }
//        }, new TaskRunner.Callback<String>() {
//            @Override
//            public void onComplete(String result) {
//                if (result.equals("complete")) {
//                    adapter = new MessagesRecyclerAdapter(getContext(), messagesarray);
//                    messagesrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//                    messagesrecycler.setAdapter(adapter);
//                } else if (result.equals("no_data")) {
//
//                }
//            }
//        });

        return view;
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

    private Calendar DateToCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
}
