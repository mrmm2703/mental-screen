package com.morahman.mentalscreen.ui.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        ConstraintLayout fragment_parent = root.findViewById(R.id.fragment_global_constraint_layout);
        fragment_parent.removeAllViews();

        TabLayout tabs = new TabLayout(getContext());
        int tabs_id = View.generateViewId();
        int viewpager_id = View.generateViewId();
        tabs.setId(tabs_id);
        ConstraintLayout.LayoutParams tabs_layout_params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        tabs.setBackgroundColor(Color.parseColor("#2276F0"));
        tabs_layout_params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#9AB5DF"));
        tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.tabs_selected));
        tabs.setLayoutParams(tabs_layout_params);
        fragment_parent.addView(tabs);

        ViewPager viewPager = new ViewPager(getContext());
        viewPager.setId(viewpager_id);
        ConstraintLayout.LayoutParams viewpager_layout_params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        viewpager_layout_params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        viewpager_layout_params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        viewpager_layout_params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        viewpager_layout_params.topToBottom = tabs.getId();
        viewPager.setLayoutParams(viewpager_layout_params);
        fragment_parent.addView(viewPager);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        return root;
    }
}