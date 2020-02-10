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
    Fragment nav_host_fragment;
    View root;
//    private HomeViewModel homeViewModel;
    SectionsPagerAdapter sectionsPagerAdapter = null;


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
        if (sectionsPagerAdapter == null) {
            sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getFragmentManager());
            ViewPager viewPager = root.findViewById(R.id.view_pager);
            viewPager.setOffscreenPageLimit(3);
            viewPager.setAdapter(sectionsPagerAdapter);

            TabLayout tabs = root.findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }
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


        Fragment fragment = (Fragment) FragmentClassDaily.newInstance("", "");
        List<Fragment> fragments = getFragmentManager().getFragments();
        for (Fragment fragment1 : fragments) {
            Log.d("FRAG", String.valueOf(getResources().getResourceName(fragment1.getId())));
            nav_host_fragment = fragment1;
        }
        for (Fragment fragment1 : nav_host_fragment.getChildFragmentManager().getFragments()) {
            Log.d("FRAG2", String.valueOf(getResources().getResourceName(fragment1.getId())));
        }
//        getChildFragmentManager().beginTransaction().replace(root.findViewById(R.id.fragment_class_monthly_scroll_view).getId(), fragment).addToBackStack(null).commit();
    }
}