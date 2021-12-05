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
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.whatshistory.Adapters.Spinners.SpinnerAdapter;
import com.example.whatshistory.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CustomDialogClass extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    private Context context;
    private MaterialButton savebtn;
    private AppCompatSpinner spinner;
    private boolean check = false;
    private SpinnerAdapter adapter;
    private SharedPreference pref = new SharedPreference(getContext());
    private String DEFAULTAPP = "DefaultApp";

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
        spinner = findViewById(R.id.spinner);
        ArrayList<String> array = new ArrayList<>();
        array.add("WhatsApp");
        array.add("WhatsApp Business");
        adapter = new SpinnerAdapter(getContext(), array);
        spinner.setAdapter(adapter);
        String str = pref.getPreference(DEFAULTAPP);
        if (str.equals("WhatsApp")) {
            spinner.setSelection(0);
            adapter.setSelectedItem(0);
        } else if (str.equals("WhatsApp Business")) {
            spinner.setSelection(1);
            adapter.setSelectedItem(1);
        }
        adapter.notifyDataSetChanged();
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                check = true;
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (check) {
                    adapter.setSelectedItem(position);
                    adapter.notifyDataSetChanged();
                    check = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setStringWithApply(DEFAULTAPP, spinner.getSelectedItem().toString());
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}