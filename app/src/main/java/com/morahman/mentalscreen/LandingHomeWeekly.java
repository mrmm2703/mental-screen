package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LandingHomeWeekly extends AppCompatActivity {
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
    Map<String, ArrayList<String>> data_map = new HashMap<String, ArrayList<String>>();

    public void onDailyButtonPress(View view) {
        LandingHomeWeekly.this.startActivity(new Intent(LandingHomeWeekly.this, LandingHomeDaily.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (id == null) {
            Log.d("LANDING", "NULL ID");
            LandingHomeWeekly.this.startActivity(new Intent(LandingHomeWeekly.this, EnterSchool.class));
        } else {
            if (!isAccessGranted()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingHomeWeekly.this);
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
                new AsyncTask().execute(getResources().getString(R.string.domain) + "get_weekly_leaderboard.php?class=" + class_+ "&school_id=" + school_id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterPassword);
        super.onCreate(savedInstanceState);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_landing_home_weekly);
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
                LinearLayout linearLayout = findViewById(R.id.activity_landing_home_weekly_leaderboard_linear_layout);
                linearLayout.removeAllViews();
                for (int i=0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        String first_name_json = jsonObject.getString("first_name");
                        String last_name_json = jsonObject.getString("last_name");
                        String screen_time_minutes_json = jsonObject.getString("screen_time_minutes");
                        String student_id_json = jsonObject.getString("student_id");
                        if (data_map.containsKey(student_id_json)) {
                            String existing_name = data_map.get(student_id_json).get(0);
                            Integer existing_time = Integer.parseInt(data_map.get(student_id_json).get(1));
                            Integer new_time = existing_time + Integer.parseInt(screen_time_minutes_json);
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(existing_name);
                            arrayList.add(new_time.toString());
                            data_map.put(student_id_json, arrayList);
                        } else {
                            String new_name = first_name_json + " " + last_name_json;
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(new_name);
                            arrayList.add(screen_time_minutes_json);
                            data_map.put(student_id_json, arrayList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    }
                }
                Iterator iterator = data_map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    String name = (String) ((ArrayList<String>) pair.getValue()).get(0);
                    String time = (String) ((ArrayList<String>) pair.getValue()).get(1);
                    LinearLayout parent = new LinearLayout(getApplicationContext());
                    int pixels = (int) (40 * getApplicationContext().getResources().getDisplayMetrics().density);
                    parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
                    parent.setOrientation(LinearLayout.HORIZONTAL);
                    TextView textView1 = new TextView(getApplicationContext());
                    textView1.setText(name);
                    textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
                    textView1.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.fredoka_one));
                    textView1.setGravity(Gravity.CENTER_VERTICAL);
                    pixels = (int) (10 * getApplicationContext().getResources().getDisplayMetrics().density);
                    textView1.setPadding(pixels, pixels, pixels, pixels);
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(textView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    TextView textView2 = new TextView(getApplicationContext());
                    textView2.setText(time);
                    textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f));
                    textView2.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.fredoka_one));
                    textView2.setGravity(Gravity.CENTER_VERTICAL);
                    pixels = (int) (10 * getApplicationContext().getResources().getDisplayMetrics().density);
                    textView2.setPadding(pixels, pixels, pixels, pixels);
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(textView2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    if (pair.getKey().equals(id)) {
                        textView1.setTextColor(getResources().getColor(R.color.black));
                        textView2.setTextColor(getResources().getColor(R.color.black));
                    }
                    parent.addView(textView1);
                    parent.addView(textView2);
                    linearLayout.addView(parent);
                }
            }
        }
    }
}
