package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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

public class EnterPassword extends AppCompatActivity {
    String school_id = "";
    String school_name = "";
    String email = "";
    String first_name = "";
    String last_name;
    String id;
    String class_;
    String year;
    EditText password_entry;
    ProgressDialog pd;
    JSONArray json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterPassword);
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_password);
        school_id = getIntent().getStringExtra("school_id");
        school_name = getIntent().getStringExtra("school_name");
        email = getIntent().getStringExtra("email");
        first_name = getIntent().getStringExtra("first_name");
        last_name = getIntent().getStringExtra("last_name");
        id = getIntent().getStringExtra("id");
        year = getIntent().getStringExtra("year");
        class_ = getIntent().getStringExtra("class");
        Log.d("ENTERPASSWORD", "Initial ID: " + id);
        password_entry = findViewById(R.id.enter_password_password);
        Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Hey there " + first_name + "!", Snackbar.LENGTH_SHORT).show();
    }

    public void fab_click(View view) {
        if (password_entry.getText().toString().length() == 0) {
            Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Enter a password", Snackbar.LENGTH_SHORT).show();
        } else {
            new AsyncTask().execute(getResources().getString(R.string.domain) + "enter_password.php?email=" + email + "&password=" + password_entry.getText().toString());
        }
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(EnterPassword.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
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
            Log.d("TTT", result);
            if (result.equals("No results found\n")) {
                Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Incorrect password", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            for (int i=0; i < json.length(); i++) {
                Snackbar.make(findViewById(R.id.enter_password_relative_layout), "Logged in successfully!", Snackbar.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.d("EEE" ,"FINAL ID: " + id);
                editor.putString("login_id", id);
                editor.putString("first_name", first_name);
                editor.putString("last_name", last_name);
                editor.putString("class", class_);
                editor.putString("year", year);
                editor.putString("school_name",school_name);
                editor.putString("school_id", school_id);
                editor.apply();
                EnterPassword.this.startActivity(new Intent(EnterPassword.this, LandingHome.class));
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
