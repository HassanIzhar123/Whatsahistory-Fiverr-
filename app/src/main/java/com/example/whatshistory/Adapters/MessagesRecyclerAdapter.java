package com.example.whatshistory.Adapters;

import android.app.Activity;
import android.content.ComponentName;
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
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ContactHolder> {

    private ArrayList<MessagesModel> contactsList;
    private Context context;
    private InterstitialAd mInterstitialAd;

    public MessagesRecyclerAdapter(Context context, ArrayList<MessagesModel> contactsList) {
        this.contactsList = contactsList;
        this.context = context;
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
        final MessagesModel model = contactsList.get(position);
        holder.setPhoneName(model.getNumber());
        holder.setDate(model.getDate());
        holder.setTime(model.getTime());
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
                    ShowInterestialAds(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowInterestialAds(getAdapterPosition());
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

    private void ShowInterestialAds(int position) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, context.getString(R.string.InterstitialAdappid), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("loadaderror", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("loadaderror", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    OpenChat(position);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });
        } else {
            OpenChat(position);
        }
    }

    private void OpenChat(int position) {
        String number = contactsList.get(position).getNumber();
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
                MessagesModel model = contactsList.get(position);
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

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
}
