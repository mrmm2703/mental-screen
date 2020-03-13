package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandingHomeDaily extends AppCompatActivity {
    String id;
    String first_name;
    String last_name;
    String school_id;
    String school_name;
    String class_;
    String year_group;
    TextView name_text;
    TextView secondary_text;
    JSONArray json;
    UsageStatsManager usageStatsManager;
    SharedPreferences sharedPreferences;
    long background_time;
    long foreground_time;
    Map<String, String> timesMap = new HashMap<>();

    public void onWeeklyButtonPress(View view) {
        LandingHomeDaily.this.startActivity(new Intent(LandingHomeDaily.this, LandingHomeWeekly.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (id == null) {
            Log.d("LANDING", "NULL ID");
            LandingHomeDaily.this.startActivity(new Intent(LandingHomeDaily.this, EnterSchool.class));
        } else {
            if (!isAccessGranted()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingHomeDaily.this);
                builder.setMessage("App needs usage access to work. Press OK to open settings and enable usage access to Mental Screen.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
                builder.create().show();
            } else {
                Log.d("LANDING", "ID: " + id);
                first_name = sharedPreferences.getString("first_name", "null");
                last_name = sharedPreferences.getString("last_name", "null");
                school_id = sharedPreferences.getString("school_id", "null");
                school_name = sharedPreferences.getString("school_name", "null");
                class_ = sharedPreferences.getString("class", "null");
                year_group = sharedPreferences.getString("year", "null");
                name_text = findViewById(R.id.activity_landing_home_hey_text);
                name_text.setText("HEY " + first_name.toUpperCase() + "!");
                secondary_text = findViewById(R.id.activity_landing_home_hey_text2);
                secondary_text.setText(class_.toUpperCase() + " LEADERBOARD");
                usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
                getAppTimes();
//                getAppTimesYesterday();
                new AsyncTask().execute(getResources().getString(R.string.domain) + "get_leaderboard.php?class=" + class_+ "&school_id=" + school_id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterSchool);
        super.onCreate(savedInstanceState);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_landing_home_daily);
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", null);
        name_text = findViewById(R.id.activity_landing_home_hey_text);
        secondary_text = findViewById(R.id.activity_landing_home_hey_text2);

    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void getAppTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
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
//        long start_of_day_epoch = System.currentTimeMillis() - 1000;
        long current_time_epoch = System.currentTimeMillis();
        Log.d("START OF DAY", Long.toString(start_of_day_epoch));
        Log.d("CURRENT EPOCH", Long.toString(current_time_epoch));
        usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(start_of_day_epoch, current_time_epoch);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start_of_day_epoch, current_time_epoch);
        UsageEvents eventList = usageStatsManager.queryEvents(start_of_day_epoch, current_time_epoch);
        Log.d("EVENT", " - STARTING - ");
        String current_package = "";
        int i = 0;
        while (eventList.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            eventList.getNextEvent(currentEvent);
            if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                current_package = currentEvent.getPackageName();
                foreground_time = currentEvent.getTimeStamp();
            } else if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                background_time = currentEvent.getTimeStamp();
                if (timesMap.size() == 0) {
                    timesMap.put(current_package, Long.toString(background_time - foreground_time));
                } else {
                    if (timesMap.containsValue(current_package)) {
                        String existing_time = timesMap.get(current_package);
                        long existing_time_long = Long.parseLong(existing_time);
                        existing_time_long = existing_time_long + (background_time - foreground_time);
                        timesMap.remove(current_package);
                        timesMap.put(current_package, Long.toString(existing_time_long));
                    } else {
                        timesMap.put(current_package, Long.toString(background_time - foreground_time));
                    }
                }
            }
        }

        while (eventList.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            eventList.getNextEvent(currentEvent);
            Log.d(currentEvent.getPackageName(), Long.toString(currentEvent.getTimeStamp()));
            if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                String current_app = currentEvent.getPackageName();
                Log.d("TIME_EX", current_app);
                foreground_time = currentEvent.getTimeStamp();
                Log.d("TIME_EX", Long.toString(foreground_time));
                while (eventList.hasNextEvent()) {
                    UsageEvents.Event anotherEvent = new UsageEvents.Event();
                    eventList.getNextEvent(anotherEvent);
                    Log.d("TIME_EXX", anotherEvent.getPackageName());
                    Log.d("TIME_EXX", Integer.toString(anotherEvent.getEventType()));
                    if (anotherEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        background_time = currentEvent.getTimeStamp();
                    }
                }
                if (timesMap.size() == 0) {
                    timesMap.put(current_app, Long.toString(background_time - foreground_time));
                } else {
                    if (timesMap.containsValue(current_app)) {
                        String existing_time = timesMap.get(current_app);
                        long existing_time_long = Long.parseLong(existing_time);
                        existing_time_long = existing_time_long + (background_time - foreground_time);
                        timesMap.remove(current_app);
                        timesMap.put(current_app, Long.toString(existing_time_long));
                    } else {
                        timesMap.put(current_app, Long.toString(background_time - foreground_time));
                    }
                }
            }
            switch (currentEvent.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    Log.d(currentEvent.getPackageName(), "MOVE_TO_FOREGROUND");
                    break;
                case UsageEvents.Event.MOVE_TO_BACKGROUND:
                    Log.d(currentEvent.getPackageName(), "MOVE_TO_BACKGROUND");
                    break;
                case UsageEvents.Event.NONE:
                    Log.d(currentEvent.getPackageName(), "NONE");
                    break;
                case UsageEvents.Event.SHORTCUT_INVOCATION:
                    Log.d(currentEvent.getPackageName(), "SHORTCUT_INVOCATION");;
                    break;
                case UsageEvents.Event.USER_INTERACTION:
                    Log.d(currentEvent.getPackageName(), "USER_INTERACTION");
                    break;
                case UsageEvents.Event.CONFIGURATION_CHANGE:
                    Log.d(currentEvent.getPackageName(), "CONFIGURATION_CHANGE");
                    break;
            }
        }
        for (Map.Entry<String, String> entry : timesMap.entrySet()) {
            Log.d("TIMEEEEE", entry.getKey() + ": " + entry.getValue());
        }

        // queryUsageStats method
        long totalTime2 = 0;
        for (UsageStats usageStats : usageStatsList) {
            Log.d("PACKAGE"," - NEW PACKAGE -");
            Log.d("PACKAGE", usageStats.getPackageName());
            Log.d("PACKAGE", Long.toString(usageStats.getFirstTimeStamp()));
            Log.d("PACKAGE", Long.toString(usageStats.getLastTimeStamp()));
            Log.d("PACKAGE", Long.toString(usageStats.getTotalTimeInForeground()));
            totalTime2 += usageStats.getTotalTimeInForeground();
        }
        Log.d("PACKAGE DONE!", Long.toString(totalTime2));

        // queryAndAggregateUsageStats method
        for (Map.Entry<String, UsageStats> entry : usageStatsMap.entrySet()) {
            String package_name = entry.getValue().getPackageName();
            long time_spent = entry.getValue().getTotalTimeInForeground();
            Log.d("---USAGE---", package_name + ":" + Long.toString(time_spent));
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
        String url = getResources().getString(R.string.domain);
        url = url + "today_update.php"
                + "?student_id=" + id
                + "&first_name=" + first_name
                + "&last_name=" + last_name
                + "&school_id=" + school_id
                + "&school_name=" + school_name.replace("&", "!$4$!")
                + "&class=" + class_
                + "&year_group=" + year_group
                + "&snapchat=" + (time_snapchat / 60000)
                + "&instagram=" + (time_instagram / 60000)
                + "&twitter=" + (time_twitter / 60000)
                + "&facebook=" + (time_facebook / 60000)
                + "&youtube=" + (time_youtube / 60000)
                + "&tiktok=" + (time_tiktok / 60000)
                + "&whatsapp=" + (time_whatsapp / 60000)
                + "&screen=" + (total_time / 60000);
        new AsyncTask().execute(url);
    }

    public void getAppTimesYesterday() {
        Log.d("YESTERDAY STATS", "YESTERDAY STATSSSSSSSSSSS");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
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
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        Log.d("START OF DAY", Long.toString(start_of_day_epoch));
        long start_of_yesterday_epoch = start_of_day_epoch - 86400000;
        Log.d("YESTERDAY START", Long.toString(start_of_yesterday_epoch));
        usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(start_of_yesterday_epoch, start_of_day_epoch);
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
        String url = getResources().getString(R.string.domain);
        url = url + "yesterday_update.php"
                + "?student_id=" + id
                + "&first_name=" + first_name
                + "&last_name=" + last_name
                + "&school_id=" + school_id
                + "&school_name=" + school_name.replace("&", "!$4$!")
                + "&class=" + class_
                + "&year_group=" + year_group
                + "&snapchat=" + (time_snapchat / 60000)
                + "&instagram=" + (time_instagram / 60000)
                + "&twitter=" + (time_twitter / 60000)
                + "&facebook=" + (time_facebook / 60000)
                + "&youtube=" + (time_youtube / 60000)
                + "&tiktok=" + (time_tiktok / 60000)
                + "&whatsapp=" + (time_whatsapp / 60000)
                + "&screen=" + (total_time / 60000);
        new AsyncTask().execute(url);
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            Log.d("TTT", params[0]);
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
            } catch (IOException e) {
                e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("RRR", result);
            if (result.equals("No results found\n")) {
                Snackbar.make(findViewById(R.id.activity_landing_home_linear_layout), "Updates times on server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.activity_landing_home_linear_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("1\n")) {
                Snackbar.make(findViewById(R.id.activity_landing_home_linear_layout), "Updated server with times", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    json = new JSONArray(result);
                } catch (JSONException e) {
                    Snackbar.make(findViewById(R.id.activity_landing_home_linear_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                }
                LinearLayout linearLayout = findViewById(R.id.activity_landing_home_daily_leaderboard_linear_layout);
                linearLayout.removeAllViews();
                for (int i=0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        String first_name_json = jsonObject.getString("first_name");
                        String last_name_json = jsonObject.getString("last_name");
                        String screen_time_minutes_json = jsonObject.getString("screen_time_minutes");
                        String student_id_json = jsonObject.getString("student_id");
                        LinearLayout parent = new LinearLayout(getApplicationContext());
                        int pixels = (int) (40 * getApplicationContext().getResources().getDisplayMetrics().density);
                        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
                        parent.setOrientation(LinearLayout.HORIZONTAL);
                        TextView textView1 = new TextView(getApplicationContext());
                        textView1.setText(first_name_json + " " + last_name_json);
                        textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
                        textView1.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.fredoka_one));
                        textView1.setGravity(Gravity.CENTER_VERTICAL);
                        pixels = (int) (10 * getApplicationContext().getResources().getDisplayMetrics().density);
                        textView1.setPadding(pixels, pixels, pixels, pixels);
                        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        TextView textView2 = new TextView(getApplicationContext());
                        textView2.setText(screen_time_minutes_json);
                        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f));
                        textView2.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.fredoka_one));
                        textView2.setGravity(Gravity.CENTER_VERTICAL);
                        pixels = (int) (10 * getApplicationContext().getResources().getDisplayMetrics().density);
                        textView2.setPadding(pixels, pixels, pixels, pixels);
                        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        if (student_id_json.equals(id)) {
                            textView1.setTextColor(getResources().getColor(R.color.black));
                            textView2.setTextColor(getResources().getColor(R.color.black));
                            textView1.setTypeface(null, Typeface.BOLD);
                            textView2.setTypeface(null, Typeface.BOLD);
                        }
                        parent.addView(textView1);
                        parent.addView(textView2);
                        linearLayout.addView(parent);
                    } catch (JSONException e) {
                        e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
                        Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
