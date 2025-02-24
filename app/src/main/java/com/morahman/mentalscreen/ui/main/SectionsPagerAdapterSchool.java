package com.morahman.mentalscreen.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.morahman.mentalscreen.FragmentSchoolDaily;
import com.morahman.mentalscreen.FragmentSchoolMonthly;
import com.morahman.mentalscreen.FragmentSchoolWeekly;
import com.morahman.mentalscreen.FragmentYearDaily;
import com.morahman.mentalscreen.FragmentYearMonthly;
import com.morahman.mentalscreen.FragmentYearWeekly;
import com.morahman.mentalscreen.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterSchool extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapterSchool(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = FragmentSchoolDaily.newInstance("","");
                break;
            case 1:
                fragment = FragmentSchoolWeekly.newInstance("","");
                break;
            case 2:
                fragment = FragmentSchoolMonthly.newInstance("","");
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DAILY";
            case 1:
                return "WEEKLY";
            case 2:
                return "MONTHLY";
        }
        return "NULL";
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}