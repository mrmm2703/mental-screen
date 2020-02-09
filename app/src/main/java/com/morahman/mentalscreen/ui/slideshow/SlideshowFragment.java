package com.morahman.mentalscreen.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.morahman.mentalscreen.R;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterSchool;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterYear;

public class SlideshowFragment extends Fragment {


    SharedPreferences sharedPreferences;
    String class_;
    String id;
    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_school, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        class_ = sharedPreferences.getString("class", "null");
        id = sharedPreferences.getString("class", null);
        SectionsPagerAdapterSchool sectionsPagerAdapter = new SectionsPagerAdapterSchool(getContext(), getFragmentManager());
        ViewPager viewPager = root.findViewById(R.id.view_pager_school);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = root.findViewById(R.id.tabs_school);
        tabs.setupWithViewPager(viewPager);
        return root;
    }
}