package com.morahman.mentalscreen.ui.tools;

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
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterGlobal;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    String id;
    String class_;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_global, container, false);
//        final TextView textView = root.findViewById(R.id.text_tools);
//        toolsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        class_ = sharedPreferences.getString("class", "null");
        id = sharedPreferences.getString("class", null);
        SectionsPagerAdapterGlobal sectionsPagerAdapter = new SectionsPagerAdapterGlobal(getContext(), getFragmentManager());
        ViewPager viewPager = root.findViewById(R.id.view_pager_global);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = root.findViewById(R.id.tabs_global);
        tabs.setupWithViewPager(viewPager);
        return root;
    }
}