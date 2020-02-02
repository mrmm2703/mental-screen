package com.morahman.mentalscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class EnterSchool extends AppCompatActivity {
    ProgressDialog pd;
    JSONArray json;
    EditText school_code_entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterSchool);
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_school);
        school_code_entry = findViewById(R.id.enter_school_school_code);
    }

    public void fab_click(View view) {
        if (school_code_entry.getText().toString().length() == 0) {
            Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Enter a school code", Snackbar.LENGTH_SHORT).show();
        } else {
            new AsyncTask().execute(getResources().getString(R.string.domain) + "enter_school.php?code=" + school_code_entry.getText().toString());
        }
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(EnterSchool.this);
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
                Snackbar.make(findViewById(R.id.enter_school_relative_layout), "School code not found", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            String id = "";
            String school_name = "";
            for (int i=0; i < json.length(); i++) {
                try {
                    JSONObject jsonObject = json.getJSONObject(i);
                    school_name = jsonObject.getString("school_name");
                    id = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.enter_school_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Intent myIntent = new Intent(EnterSchool.this, EnterEmail.class);
            myIntent.putExtra("school_id", id);
            myIntent.putExtra("school_name", school_name);
            EnterSchool.this.startActivity(myIntent);
        }
    }
}
