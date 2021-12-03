package com.example.whatshistory.Adapters;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.Models.MessagesModel;
import com.example.whatshistory.R;

import java.util.ArrayList;
import java.util.List;

public class CallReyclerAdapter extends RecyclerView.Adapter<CallReyclerAdapter.ContactHolder> {

    private ArrayList<CallsModel> contactsList;
    private Context mContext;

    public CallReyclerAdapter(Context context, ArrayList<CallsModel> contactsList) {
        this.contactsList = contactsList;
        this.mContext = context;
    }

    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.call_list_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactsList == null ? 0 : contactsList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, final int position) {
        final CallsModel model = contactsList.get(position);
        Log.e("callmodelcheck", "" + model.getName());
        if (model.getName() != null) {
            holder.setPhoneName(model.getName());
        } else {
            holder.setPhoneName(model.getNumber());
        }
        holder.setDate(model.getDate());
        holder.setTime(model.getTime());
    }

    public Intent findTwitterClient() {
        final String[] twitterApps = {
                "com.whatsapp.w4b",
                "com.whatsapp"};
        Intent tweetIntent = new Intent();
        tweetIntent.setType("text/plain");
        final PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int i = 0; i < twitterApps.length; i++) {
            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                if (p != null && p.startsWith(twitterApps[i])) {
                    tweetIntent.setPackage(p);
                    return tweetIntent;
                }
            }
        }

        return null;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private TextView number_textview, date_textview, time_textview;
        private ImageView enter_whatsapp_btn;

        public ContactHolder(View itemView) {
            super(itemView);
            number_textview = itemView.findViewById(R.id.number_textview);
            date_textview = itemView.findViewById(R.id.date_textview);
            time_textview = itemView.findViewById(R.id.time_textview);
            enter_whatsapp_btn = itemView.findViewById(R.id.enter_whatsapp);
            enter_whatsapp_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = contactsList.get(getAdapterPosition()).getNumber();
                    number = number.replaceAll(" ", "").replace("+", "");
                    Intent shareIntent = findTwitterClient();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "test");
                    shareIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    shareIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
                    mContext.startActivity(Intent.createChooser(shareIntent, "Select App"));


//                    String contact = contactsList.get(getAdapterPosition()).getNumber();
//                    String url = "https://api.whatsapp.com/send?phone=" + contact;
//                    try {
//                        PackageManager pm = mContext.getPackageManager();
//                        pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(url));
//                        mContext.startActivity(i);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        Toast.makeText(mContext, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
////                        intent.setPackage("com.whatsapp");
//                        intent.setPackage("com.whatsapp.w4b");
//                        // Give your message here
//                        intent.putExtra(Intent.EXTRA_TEXT, "Enter Message");
//                        // Checking whether Whatsapp
//                        // is installed or not
//                        if (intent.resolveActivity(mContext.getPackageManager()) == null) {
//                            Toast.makeText(mContext, "Please install whatsapp first.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        mContext.startActivity(intent);
//                    } catch (Exception e) {
//                        if (e instanceof ActivityNotFoundException) {
//
//                        }
//                    }
                }
            });
        }

        public void setPhoneName(String number) {
            number_textview.setText(number);
        }

        public void setDate(String date) {
            date_textview.setText(date);
        }

        public void setTime(String time) {
            time_textview.setText(time);
        }
    }
}