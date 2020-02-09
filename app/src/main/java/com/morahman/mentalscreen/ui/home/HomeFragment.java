package com.morahman.mentalscreen.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.morahman.mentalscreen.FragmentClassDaily;
import com.morahman.mentalscreen.R;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapter;

import java.util.List;

public class HomeFragment extends Fragment {
    SharedPreferences sharedPreferences;
    String id;
    String class_;
//    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragment", "onCreateView called");
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_class, container, false);
//        Snackbar.make(root.findViewById(R.id.fragment_class_constraint_layout), "HEHE!", Snackbar.LENGTH_SHORT).show();
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        class_ = sharedPreferences.getString("class", "null");
        id = sharedPreferences.getString("class", null);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getFragmentManager());
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
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        Fragment fragment = (Fragment) FragmentClassDaily.newInstance("", "");
//        getFragmentManager().beginTransaction().replace(getFragmentManager().findFragmentById(R.id.fragment_class_daily_parent).getId(), fragment).addToBackStack(null).commit();
        TabLayout tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}