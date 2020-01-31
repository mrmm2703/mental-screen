package com.morahman.mentalscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class LandingHome extends AppCompatActivity {
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_home);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", null);
        if (id == null) {
            LandingHome.this.startActivity(new Intent(LandingHome.this, EnterSchool.class));
        }
    }
}
