package com.morahman.mentalscreen.ui.gallery;

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
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapter;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterYear;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    SharedPreferences sharedPreferences;
    String id;
    String class_;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_year, container, false);
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        class_ = sharedPreferences.getString("class", "null");
        id = sharedPreferences.getString("class", null);
        SectionsPagerAdapterYear sectionsPagerAdapter = new SectionsPagerAdapterYear(getContext(), getFragmentManager());
        ViewPager viewPager = root.findViewById(R.id.view_pager_year);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = root.findViewById(R.id.tabs_year);
        tabs.setupWithViewPager(viewPager);
        return root;
    }
}