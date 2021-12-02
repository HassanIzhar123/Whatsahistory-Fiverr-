package com.example.whatshistory.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatshistory.Fragments.CallsFragment;
import com.example.whatshistory.Fragments.HistoryFragment;
import com.example.whatshistory.Fragments.SmsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new CallsFragment();
        } else if (position == 1) {
            fragment = new SmsFragment();
        } else if (position == 2) {
            fragment = new HistoryFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Calls";
        } else if (position == 1) {
            title = "SMS";
        } else if (position == 2) {
            title = "History";
        }
        return title;
    }
}