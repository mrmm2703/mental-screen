package com.morahman.mentalscreen.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.morahman.mentalscreen.FragmentPrizesCurrent;
import com.morahman.mentalscreen.FragmentPrizesPast;
import com.morahman.mentalscreen.FragmentSelfDaily;
import com.morahman.mentalscreen.FragmentSelfMonthly;
import com.morahman.mentalscreen.FragmentSelfWeekly;
import com.morahman.mentalscreen.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterPrizes extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapterPrizes(Context context, FragmentManager fm) {
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
                fragment = FragmentPrizesCurrent.newInstance("","");
                break;
            case 1:
                fragment = FragmentPrizesPast.newInstance("","");
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CURRENT";
            case 1:
                return "PAST (IN PROGRESS)";
        }
        return "NULL";
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}