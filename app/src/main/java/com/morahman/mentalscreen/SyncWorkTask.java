package com.morahman.mentalscreen;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public class SyncWorkTask extends Worker {

    SharedPreferences sharedPreferences;
    UsageStatsManager usageStatsManager;
    String id;
    String first_name;
    String last_name;
    String school_id;
    String school_name;
    String class_;
    String year_group;
    JSONArray json;

    public SyncWorkTask(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", "null");
        first_name = sharedPreferences.getString("first_name", "null");
        last_name = sharedPreferences.getString("last_name", "null");
        school_id = sharedPreferences.getString("school_id", "null");
        school_name = sharedPreferences.getString("school_name", "null");
        class_ = sharedPreferences.getString("class", "null");
        year_group = sharedPreferences.getString("year", "null");
        usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        getAppTimes();
        getAppTimesYesterday();
        return Result.success();
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
//            if (result.equals("No results found\n")) {
//                Snackbar.make(findViewById(R.id.fragment_class_daily_parent), "Updates times on server", Snackbar.LENGTH_SHORT).show();
//            } else if (result.equals("Connection failed\n")) {
//                Snackbar.make(findViewById(R.id.fragment_class_daily_parent), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
//            } else if (result.equals("1\n")) {
////                Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Updated server with times", Snackbar.LENGTH_SHORT).show();
//            }
        }
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
        String url = getApplicationContext().getString(R.string.domain);
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
        String url = getApplicationContext().getResources().getString(R.string.domain);
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
}
