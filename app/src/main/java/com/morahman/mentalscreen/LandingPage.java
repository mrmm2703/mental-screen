package com.morahman.mentalscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.pusher.pushnotifications.PushNotifications;

public class LandingPage extends AppCompatActivity {
    String id;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", null);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LandingPage.this);
            builder.setMessage("App needs internet access to work.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.create().show();
        } else {
            PushNotifications.start(getApplicationContext(), "bf463df6-f25a-40d9-9bd9-6b92dc82d63e");
            PushNotifications.clearDeviceInterests();
            PushNotifications.addDeviceInterest("everyone");
            if (id == null) {
                LandingPage.this.startActivity(new Intent(LandingPage.this, EnterSchool.class));
                PushNotifications.addDeviceInterest("loggedin=false");
            } else {
                if (!isAccessGranted()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LandingPage.this);
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
                    if (!isNetworkAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LandingPage.this);
                        builder.setMessage("App needs internet access to work.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        });
                        builder.create().show();
                    } else {
                        new AsyncTask().execute();
                        String class_ = sharedPreferences.getString("class", "NULL");
                        String year = sharedPreferences.getString("year", "NULL");
                        String school_name = sharedPreferences.getString("school_name", "NULL");
                        String school_id = sharedPreferences.getString("school_id", "NULL");
                        String solo = sharedPreferences.getString("solo", "NULL");
                        String class_id = sharedPreferences.getString("class_id", "NULL");
                        if (solo.equals("1")) {
                            PushNotifications.addDeviceInterest("solo=true");
                        } else {
                            PushNotifications.addDeviceInterest("solo=false");
                        }
                        PushNotifications.addDeviceInterest("studentid="+id);
                        PushNotifications.addDeviceInterest("classid="+class_id);
                        PushNotifications.addDeviceInterest("year="+year);
//                        PushNotifications.addDeviceInterest("schoolname="+school_name);
                        PushNotifications.addDeviceInterest("schoolid="+school_id);
                        PushNotifications.addDeviceInterest("loggedin=true");
                    }
                }
            }
        }
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
        protected void onPreExecute() { }

        protected String doInBackground(String... paramgs) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(getResources().getString(R.string.domain) + "latest_version.php");
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
            Log.d("VERSION", result);
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                final float server_version = Float.parseFloat(result);
                float local_version = Float.parseFloat(version);
                if (!(Float.compare(server_version, local_version) == 0)) {
                    Log.d("VERSION", "DIFFERENT");
                    AlertDialog.Builder builder = new AlertDialog.Builder(LandingPage.this);
                    builder.setMessage("New update available. Press continue to download new update.");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("UPDATE", getResources().getString(R.string.domain) + "packages/" + String.valueOf(server_version) + ".apk");
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.domain) + "packages/" + String.valueOf(server_version) + ".apk"));
                            LandingPage.this.startActivity(browserIntent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
                    builder.create().show();
                } else {
                    LandingPage.this.startActivity(new Intent(LandingPage.this, LandingHome.class));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
