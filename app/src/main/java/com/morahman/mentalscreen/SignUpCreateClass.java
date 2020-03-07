package com.morahman.mentalscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

public class SignUpCreateClass extends AppCompatActivity {
    boolean first_run = true;
    String school_name;
    String school_id;
    String email;
    String first_name;
    String last_name;
    ProgressDialog pd;
    JSONArray json;
    EditText class_code;
    TextView classcode_text;
    Switch solo_switch;
    String class_;
//    EditText year_entry;
    EditText class_name;
    String solo = "0";
    List<String> spinner_choices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SignUpClass);
        super.onCreate(savedInstanceState);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up_create_class);
//        classcode_text = findViewById(R.id.sign_up_create_class_classcode_text);

        class_code = findViewById(R.id.sign_up_create_class_classcode);
        class_name = findViewById(R.id.sign_up_create_class_class_name);

        school_name = getIntent().getStringExtra("school_name");
        school_id = getIntent().getStringExtra("school_id");
        email = getIntent().getStringExtra("email");
        first_name = getIntent().getStringExtra("first_name");
        last_name = getIntent().getStringExtra("last_name");

//        year_entry = findViewById(R.id.sign_up_create_class_class_name);
//        new AsyncTask().execute(getResources().getString(R.string.domain) + "get_classes.php?school_id=" + school_id);
    }

    public void fab_click(View view) {
        new AsyncTask().execute(getResources().getString(R.string.domain) + "create_class.php?" +
                "school_name=" + school_name.replace("&", "!$4$!")
                + "&school_id=" + school_id
                + "&class_name=" + class_name.getText().toString()
                + "&passcode=" + class_code.getText().toString()
        );
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(SignUpCreateClass.this);
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
            if (result.equals("exists\n")) {
                 Snackbar.make(findViewById(R.id.sign_up_create_class_relative_layout), "Passcode or team name exists.", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
//
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.sign_up_create_class_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            } else {
                Intent myIntent = new Intent(SignUpCreateClass.this, SignUpClass.class);
                myIntent.putExtra("school_id", school_id);
                myIntent.putExtra("school_name", school_name);
                myIntent.putExtra("email", email);
                myIntent.putExtra("first_name", first_name);
                myIntent.putExtra("last_name", last_name);
                myIntent.putExtra("classcode", class_code.getText().toString());
                SignUpCreateClass.this.startActivity(myIntent);
            }

            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
