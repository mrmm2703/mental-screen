package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.snackbar.Snackbar;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class LandingHome extends AppCompatActivity {
    String id;
    String first_name;
    String last_name;
    String school_id;
    String school_name;
    String class_;
    String year_group;
    TextView name_text;
    JSONArray json;
    UsageStatsManager usageStatsManager;
    SharedPreferences sharedPreferences;

    @Override
    public void onResume() {
        super.onResume();
        if (id == null) {
            Log.d("LANDING", "NULL ID");
            LandingHome.this.startActivity(new Intent(LandingHome.this, EnterSchool.class));
        } else {
            if (!isAccessGranted()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingHome.this);
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
                usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
                getAppTimes();
                getAppTimesYesterday();
                new AsyncTask().execute(getResources().getString(R.string.domain) + "get_leaderboard.php?class=" + class_+ "&school_id=" + school_id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_landing_home);
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", null);

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
        long start_of_day_epoch = 0;
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("\"yyyy-mm-dd hh:mm:ss\"");
        String strDate = dateFormat.format(currentTime);
        Log.d("DATE", strDate);
        if (Integer.parseInt(strDate.substring(12, 14)) >= 12) {
            start_of_day_epoch = calendar.getTimeInMillis() - 43200000;
        } else {
            start_of_day_epoch = calendar.getTimeInMillis();
        }
        long current_time_epoch = System.currentTimeMillis();
        Log.d("START OF DAY", Long.toString(start_of_day_epoch));
        Log.d("CURRENT EPOCH", Long.toString(current_time_epoch));
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
        long start_of_day_epoch = 0;
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("\"yyyy-mm-dd hh:mm:ss\"");
        String strDate = dateFormat.format(currentTime);
        if (Integer.parseInt(strDate.substring(12, 14)) >= 12) {
            start_of_day_epoch = calendar.getTimeInMillis() - 43200000;
        } else {
            start_of_day_epoch = calendar.getTimeInMillis();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        Log.d("START OF DAY", Long.toString(start_of_day_epoch));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
            } catch (IOException e) {
                e.printStackTrace();
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
                LinearLayout linearLayout = findViewById(R.id.activity_landing_home_leaderboard_parent);
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
                        }
                        parent.addView(textView1);
                        parent.addView(textView2);
                        linearLayout.addView(parent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
