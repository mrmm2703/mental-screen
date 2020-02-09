package com.morahman.mentalscreen.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.morahman.mentalscreen.LandingPage;
import com.morahman.mentalscreen.R;
import com.morahman.mentalscreen.ui.main.SectionsPagerAdapterYear;

public class RefreshFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    SharedPreferences sharedPreferences;
    String id;
    String class_;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_refresh, container, false);
        getActivity().startActivity(new Intent(getActivity(), LandingPage.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        return root;
    }
}