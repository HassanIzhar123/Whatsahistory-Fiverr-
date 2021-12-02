package com.example.whatshistory.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.whatshistory.R;

import java.util.ArrayList;

public class SmsFragment extends Fragment {
    public SmsFragment() {
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        ArrayList<Message> smsInbox = new ArrayList<Message>();
        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = getContext().getContentResolver()
                .query(uriSms,
                        new String[]{"_id", "address", "date", "body",
                                "type", "read"}, "type=" + 1, null,
                        "date" + " COLLATE LOCALIZED ASC");
        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {

                do {

                    Message message = new Message();
                    message.messageNumber = cursor.getString(cursor
                            .getColumnIndex("address"));
                    message.messageContent = cursor.getString(cursor
                            .getColumnIndex("body"));
                    Log.e("messagescheck",""+message.messageNumber + " " + message.messageContent);
                    smsInbox.add(message);
                } while (cursor.moveToPrevious());
            }
        }
        cursor.close();


        return view;
    }
}
