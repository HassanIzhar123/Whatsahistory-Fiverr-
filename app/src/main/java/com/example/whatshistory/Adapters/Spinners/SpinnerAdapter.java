package com.example.whatshistory.Adapters.Spinners;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.whatshistory.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> titles;
    LayoutInflater inflter;
    int selectedItem = -1;

    public SpinnerAdapter(Context applicationContext, ArrayList<String> titles) {
        this.context = applicationContext;
        this.titles = titles;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public String getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_row, null);
        TextView names = (TextView) view.findViewById(R.id.text);
        names.setText(titles.get(i));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View v = null;
        v = super.getDropDownView(position, null, parent);
        TextView text = v.findViewById(R.id.text);
        // If this is the selected item position
        if (position == selectedItem) {
            v.setBackgroundColor(Color.parseColor("#871094"));
            text.setTextColor(Color.WHITE);
        } else {
            // for other views
            v.setBackgroundColor(Color.WHITE);
            text.setTextColor(Color.parseColor("#333333"));
        }
        return v;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
