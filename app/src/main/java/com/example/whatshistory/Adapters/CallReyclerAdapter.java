package com.example.whatshistory.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Databases.Sqlitedatabase;
import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.Models.MessagesModel;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.SharedPreference;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CallReyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CallsModel> contactsList;
    private Context context;

    public CallReyclerAdapter(Context context, ArrayList<CallsModel> contactsList) {
        this.contactsList = contactsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.progress_item, parent, false);
            return new ItemHolder(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.call_list_item, parent, false);
            return new ContactHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (contactsList.get(position) == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return contactsList == null ? 0 : contactsList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactHolder) {
            ContactHolder viewholder = (ContactHolder) holder;
            final CallsModel model = contactsList.get(position);
            Log.e("callmodelcheck", "" + model.getName());
            if (model.getName() != null) {
                viewholder.setPhoneName(model.getName());
            } else {
                viewholder.setPhoneName(model.getNumber());
            }
            viewholder.setDate(model.getDate());
            viewholder.setTime(model.getTime());
        }
    }

    public Intent findTwitterClient(String packagename) {

//        final String[] twitterApps = {
//                "com.whatsapp.w4b", "com.whatsapp"
//        };
        final String[] twitterApps = {
                "com.whatsapp", "com.whatsapp.w4b"
        };
        Intent tweetIntent = new Intent();
        tweetIntent.setType("text/plain");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
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
                    String countryiso = contactsList.get(getAdapterPosition()).getCountryIso();
//                    number = number.replaceAll(" ", "").replace("+", "");
                    number = PhoneNumberWithoutCountryCode(number, countryiso);
                    Log.e("numbercheckval", "" + PhoneNumberWithoutCountryCode(number, countryiso) + " " + PhoneNumberUtils.stripSeparators(number) + " " + number);
                    String DEFAULTAPP = "DefaultApp";
                    String apppackagename = new SharedPreference(context).getPreference(DEFAULTAPP);
                    String packagname = "";
                    if (apppackagename.equals("") || apppackagename.equals("WhatsApp")) {
                        packagname = "com.whatsapp";
                    } else if (apppackagename.equals("WhatsApp Business")) {
                        packagname = "com.whatsapp.w4b";
                    }
                    PackageManager packagemanager = context.getPackageManager();
                    if (isPackageInstalled(packagname, packagemanager)) {
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + number + "&text=" + "");
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                        sendIntent.setPackage(packagname);
                        sendIntent.setData(uri);
                        if (sendIntent.resolveActivity(packagemanager) != null) {
                            Sqlitedatabase database = new Sqlitedatabase(context);
                            CallsModel model = contactsList.get(getAdapterPosition());
                            Calendar c = getCalendar();
                            model.setDate(getCurrentDate(c));
                            model.setTime(getCurrentTime(c));
                            database.insertData(model);
                            Toast.makeText(context, "Data inserted", Toast.LENGTH_SHORT).show();
                            context.startActivity(sendIntent);
                        }
                    } else {
                        Toast.makeText(context, "App Is Not Installed!", Toast.LENGTH_SHORT).show();
                    }
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

    private Calendar getCalendar() {
        return Calendar.getInstance();
    }

    private String getCurrentTime(Calendar c) {
        SimpleDateFormat timesdf = new SimpleDateFormat("HH:mm a");
        return timesdf.format(c.getTime());
    }

    private String getCurrentDate(Calendar c) {
        SimpleDateFormat datesdf = new SimpleDateFormat("dd/MM/yyyy");
        return datesdf.format(c.getTime());
    }

    public String PhoneNumberWithoutCountryCode(String phoneNumberWithCountryCode, String countryiso) {
        PhoneNumberUtil util = null;
        if (util == null) {
            util = PhoneNumberUtil.getInstance();

        }
        try {
            final Phonenumber.PhoneNumber phoneNumber = util.parse(phoneNumberWithCountryCode, countryiso);
            return util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemview) {
            super(itemview);
        }
    }
}