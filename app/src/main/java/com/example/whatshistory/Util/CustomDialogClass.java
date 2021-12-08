package com.example.whatshistory.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.whatshistory.Adapters.Spinners.SpinnerAdapter;
import com.example.whatshistory.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CustomDialogClass extends Dialog {

    public Activity c;
    private Context context;
    private MaterialButton savebtn;
    private SharedPreference pref = new SharedPreference(getContext());
    private String DEFAULTAPP = "DefaultApp";
    private RadioGroup radiogroup;
    private RadioButton whatsappradio, whatsappbradio;

    public CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
        this.context = c.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        savebtn = findViewById(R.id.savebtn);
        radiogroup = findViewById(R.id.radiogroup);
        whatsappradio = findViewById(R.id.whatsappbtn);
        whatsappbradio = findViewById(R.id.whatsappbbtn);
        String str = pref.getPreference(DEFAULTAPP);
        if (str.equals("WhatsApp")) {
            whatsappradio.setChecked(true);
        } else if (str.equals("WhatsApp Business")) {
            whatsappbradio.setChecked(true);
        } else {
            whatsappradio.setChecked(true);
            pref.setStringWithApply(DEFAULTAPP, "WhatsApp");
        }
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radiogroup.getCheckedRadioButtonId();
                if (selectedId == R.id.whatsappbtn) {
                    pref.setStringWithApply(DEFAULTAPP, "WhatsApp");
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (selectedId == R.id.whatsappbbtn) {
                    pref.setStringWithApply(DEFAULTAPP, "WhatsApp Business");
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
}