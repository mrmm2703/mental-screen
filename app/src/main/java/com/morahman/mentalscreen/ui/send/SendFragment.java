package com.morahman.mentalscreen.ui.send;

import android.content.Context;
import android.content.Intent;
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

import com.morahman.mentalscreen.EnterSchool;
import com.morahman.mentalscreen.LandingPage;
import com.morahman.mentalscreen.R;
import com.pusher.pushnotifications.PushNotifications;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        PushNotifications.clearDeviceInterests();
        getActivity().startActivity(new Intent(getActivity(), LandingPage.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
//        final TextView textView = root.findViewById(R.id.text_send);
//        sendViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}