package com.example.whatshistory.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatshistory.Databases.Sqlitedatabase;
import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.R;
import com.example.whatshistory.Util.CustomDialogClass;
import com.example.whatshistory.Util.SharedPreference;
import com.google.android.material.button.MaterialButton;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    MaterialButton defaultwhatsappbtn, contactusbtn, adsfreeappbtn;
    String email = "info@tee.lk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        defaultwhatsappbtn = findViewById(R.id.defaultwhatsappbtn);
        contactusbtn = findViewById(R.id.contactusbtn);
        adsfreeappbtn = findViewById(R.id.adsfreeappbtn);
        defaultwhatsappbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogClass cdd = new CustomDialogClass(SettingsActivity.this);
                if (!cdd.isShowing()) {
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            }
        });
        contactusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("+94716295337");
            }
        });
        adsfreeappbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tee.lk/whatshistory"));
                startActivity(browserIntent);
            }
        });
    }

    private void sendMessage(String number) {
        String countryiso ="LKA";
        number = PhoneNumberWithoutCountryCode(number, countryiso);
        Log.e("numbercheckval", "" + PhoneNumberWithoutCountryCode(number, countryiso) + " " + PhoneNumberUtils.stripSeparators(number) + " " + number);
        String DEFAULTAPP = "DefaultApp";
        String apppackagename = new SharedPreference(getApplicationContext()).getPreference(DEFAULTAPP);
        String packagname = "";
        if (apppackagename.equals("") || apppackagename.equals("WhatsApp")) {
            packagname = "com.whatsapp";
        } else if (apppackagename.equals("WhatsApp Business")) {
            packagname = "com.whatsapp.w4b";
        }
        PackageManager packagemanager = getPackageManager();
        if (isPackageInstalled(packagname, packagemanager)) {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + number + "&text=" + "");
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.setPackage(packagname);
            sendIntent.setData(uri);
            if (sendIntent.resolveActivity(packagemanager) != null) {
                startActivity(sendIntent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "App Is Not Installed!", Toast.LENGTH_SHORT).show();
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

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}