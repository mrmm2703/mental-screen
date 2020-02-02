package com.morahman.mentalscreen;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class LandingHome extends AppCompatActivity {
    String id;
    UsageStatsManager usageStatsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_landing_home);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", null);
        if (id == null) {
            Log.d("LANDING", "NULL ID");
            LandingHome.this.startActivity(new Intent(LandingHome.this, EnterSchool.class));
        }
        Log.d("LANDING", "ID: " + id);
        usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        getAppTimes();
    }

    public void getAppTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time_snapchat = 0;
        long time_instagram = 0;
        long time_twitter = 0;
        long time_facebook = 0;
        long time_youtube = 0;
        long time_tiktok = 0;
        long time_whatsapp = 0;
        long total_time = 0;
        Map<String, UsageStats> usageStatsMap;
        long start_of_day_epoch = calendar.getTimeInMillis();
        long current_time_epoch = System.currentTimeMillis();
        usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(start_of_day_epoch, current_time_epoch);
        for (Map.Entry<String, UsageStats> entry : usageStatsMap.entrySet()) {
            String package_name = entry.getValue().getPackageName();
            long time_spent = entry.getValue().getTotalTimeInForeground();
            total_time += time_spent;
            switch (package_name) {
                case "com.facebook.katana":
                    time_facebook += time_spent;
                    Log.d("TIME", "Facebook" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.snapchat.android":
                    time_snapchat += time_spent;
                    Log.d("TIME", "Snapchat" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.instagram.android":
                    time_instagram += time_spent;
                    Log.d("TIME", "Instagram" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.twitter.android":
                    time_twitter += time_spent;
                    Log.d("TIME", "Twitter" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.google.android.youtube":
                    time_youtube += time_spent;
                    Log.d("TIME", "YouTube" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.zhiliaoapp.musically":
                    time_tiktok += time_spent;
                    Log.d("TIME", "TikTok" + ": " + (time_spent / 60000) + " minutes");
                    break;
                case "com.whatsapp":
                    time_whatsapp += time_spent;
                    Log.d("TIME", "WhatsApp" + ": " + (time_spent / 60000) + " minutes");
                    break;
            }
        }
        Log.d("TIME", "Total time" + ": " + (total_time / 60000) + " minutes");
    }
}
