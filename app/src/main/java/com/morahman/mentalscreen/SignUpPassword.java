package com.morahman.mentalscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

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

public class SignUpPassword extends AppCompatActivity {
    ProgressDialog pd;
    EditText password_entry;
    String email;
    String school_id;
    String school_name;
    String class_;
    String year;
    String first_name;
    String last_name;
    JSONArray json;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterPassword);
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up_password);
        password_entry = findViewById(R.id.sign_up_password_password_entry);
        school_name = getIntent().getStringExtra("school_name");
        school_id = getIntent().getStringExtra("school_id");
        email = getIntent().getStringExtra("email");
        first_name = getIntent().getStringExtra("first_name");
        last_name = getIntent().getStringExtra("last_name");
        class_ = getIntent().getStringExtra("class");
        year = getIntent().getStringExtra("year");
    }

    public void fab_click(View view) {
        String password = password_entry.getText().toString();
        RelativeLayout relativeLayout = findViewById(R.id.sign_up_password_relative_layout);
        if (password.length() < 8) {
            Snackbar.make(relativeLayout, "Password must be at least 8 characters long", Snackbar.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Snackbar.make(relativeLayout, "Enter a password", Snackbar.LENGTH_SHORT).show();
        } else {
            Log.d("EEE", school_name.replace("&", "!$4$!"));
            String url = getResources().getString(R.string.domain) + "create_user.php?"
                    + "email=" + email
                    + "&password=" + password.replace("&", "!$4$!")
                    + "&first_name=" + first_name
                    + "&last_name=" + last_name
                    + "&school_id=" + school_id
                    + "&school_name=" + school_name.replace("&", "!$4$!")
                    + "&year=" + year
                    + "&class=" + class_;
            new AsyncTask().execute(url);
        }
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(SignUpPassword.this);
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
                Snackbar.make(findViewById(R.id.sign_up_password_relative_layout), "Couldn't retrieve newly created user", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
//
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.sign_up_password_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.sign_up_password_relative_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            for (int i=0; i < json.length(); i++) {
                try {
                    JSONObject jsonObject = json.getJSONObject(i);
                    id = jsonObject.getString("id");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login_id", id);
                    editor.putString("first_name", first_name);
                    editor.putString("last_name", last_name);
                    editor.putString("class", class_);
                    editor.putString("year", year);
                    editor.putString("school_name",school_name);
                    editor.putString("school_id", school_id);
                    editor.apply();
                    Snackbar.make(findViewById(R.id.sign_up_password_relative_layout), "ID: " + id, Snackbar.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.sign_up_password_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    return;
                }
            }
            SignUpPassword.this.startActivity(new Intent(SignUpPassword.this, LandingHomeDaily.class));
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
