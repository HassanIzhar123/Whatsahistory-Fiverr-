package com.example.whatshistory.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatshistory.Databases.Sqlitedatabase;
import com.example.whatshistory.Models.MessagesModel;
import com.example.whatshistory.R;
import com.google.android.material.button.MaterialButton;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCustomDialog extends Dialog {
    public Activity activity;
    private Context context;
    private Button sendbtn;
    private EditText add_edittext;
    private ImageButton exitbtn;
    private CountryCodePicker ccp;
    private ImageView rightimg, wrongimg;
    private Boolean valid = false;

    public AddCustomDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_custom_dialog);
        add_edittext = findViewById(R.id.add_edittext);
        sendbtn = findViewById(R.id.sendbtn);
        exitbtn = findViewById(R.id.exitbtn);
        rightimg = findViewById(R.id.rightimg);
        wrongimg = findViewById(R.id.wrongimg);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(add_edittext);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!add_edittext.getText().toString().isEmpty()) {
                    if (valid) {
                        String phone_no = ccp.getFullNumberWithPlus();
                        SendMessage(phone_no);
                    } else {
                        Toast.makeText(getContext(), "Phone Number is not valid!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Enter Mobile Number!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                valid = isValidNumber;
                Log.e("onvalidatychange", "" + isValidNumber + " " + ccp.getFullNumberWithPlus());
                if (isValidNumber) {
                    rightimg.setVisibility(View.VISIBLE);
                    wrongimg.setVisibility(View.GONE);
                } else {
                    rightimg.setVisibility(View.GONE);
                    wrongimg.setVisibility(View.VISIBLE);
                }
            }
        });
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void SendMessage(String number) {
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
                MessagesModel model = new MessagesModel();
                Calendar c = getCalendar();
                model.setName(getContactName(getContext(), number));
                model.setNumber(number);
                model.setDate(getCurrentDate(c));
                model.setTime(getCurrentTime(c));
                database.insertData(model);
                Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show();
                activity.startActivity(sendIntent);
                dismiss();
            }
        } else {
            Toast.makeText(context, "App Is Not Installed!", Toast.LENGTH_SHORT).show();
        }
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
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
