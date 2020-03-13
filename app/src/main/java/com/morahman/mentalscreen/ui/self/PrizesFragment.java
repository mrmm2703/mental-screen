package com.morahman.mentalscreen.ui.self;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.morahman.mentalscreen.R;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterPrizes;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterSelf;

public class PrizesFragment extends Fragment {
    SharedPreferences sharedPreferences;
    String id;
    String class_;
    String class_id;
    String school_id;
    Fragment nav_host_fragment;
    View root;
//    private HomeViewModel homeViewModel;
    SectionsPagerAdapterPrizes sectionsPagerAdapter = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("HomeFragment", "onCreate called");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragment", "onCreateView called");
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_class, container, false);
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        class_ = sharedPreferences.getString("class", "null");
        id = sharedPreferences.getString("class", null);
        school_id = sharedPreferences.getString("school_id", null);
        class_id = sharedPreferences.getString("class_id", null);
        sectionsPagerAdapter = new SectionsPagerAdapterPrizes(getContext(), getFragmentManager());

        ConstraintLayout fragment_parent = root.findViewById(R.id.fragment_class_constraint_layout);
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

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //        Snackbar.make(root.findViewById(R.id.fragment_class_constraint_layout), "HEHE!", Snackbar.LENGTH_SHORT).show();

//        sectionsPagerAdapter.getItem(0);
//        sectionsPagerAdapter.getItem(1);
//        sectionsPagerAdapter.getItem(2);
//        Fragment frg = sectionsPagerAdapter.getItem(0);
//        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
//        List<Fragment> fragmentList = getFragmentManager().getFragments();
//        for (Fragment fragment : fragmentList) {
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.detach(fragment);
//            fragmentTransaction.attach(fragment);
//            fragmentTransaction.commit();
//        }


//        Fragment fragment = (Fragment) FragmentClassDaily.newInstance("", "");
//        List<Fragment> fragments = getFragmentManager().getFragments();
//        for (Fragment fragment1 : fragments) {
//            Log.d("FRAG", String.valueOf(getResources().getResourceName(fragment1.getId())));
//            nav_host_fragment = fragment1;
//        }
//        for (Fragment fragment1 : nav_host_fragment.getChildFragmentManager().getFragments()) {
//            Log.d("FRAG2", String.valueOf(getResources().getResourceName(fragment1.getId())));
//        }
//        getChildFragmentManager().beginTransaction().replace(root.findViewById(R.id.fragment_class_monthly_scroll_view).getId(), fragment).addToBackStack(null).commit();
    }
}