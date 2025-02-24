package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

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
import java.util.ArrayList;
import java.util.List;

public class SignUpClass extends AppCompatActivity {
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
    EditText year_entry;
    String solo = "0";
    Button create_class_button;
    List<String> spinner_choices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SignUpClass);
        super.onCreate(savedInstanceState);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up_class);
        create_class_button = findViewById(R.id.sign_up_class_create_class_btn);
        classcode_text = findViewById(R.id.sign_up_class_classcode_text);
        class_code = findViewById(R.id.sign_up_class_classcode);
        solo_switch = findViewById(R.id.sign_up_class_solo_switch);
        solo_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    solo = "1";
                    classcode_text.setVisibility(View.GONE);
                    class_code.setVisibility(View.GONE);
                    create_class_button.setVisibility(View.GONE);
                } else {
                    solo = "0";
                    classcode_text.setVisibility(View.VISIBLE);
                    class_code.setVisibility(View.VISIBLE);
                    create_class_button.setVisibility(View.VISIBLE);
                }
            }
        });
        school_name = getIntent().getStringExtra("school_name");
        school_id = getIntent().getStringExtra("school_id");
        email = getIntent().getStringExtra("email");
        first_name = getIntent().getStringExtra("first_name");
        last_name = getIntent().getStringExtra("last_name");
        year_entry = findViewById(R.id.sign_up_class_year);
        if (getIntent().hasExtra("classcode")) {
            class_code.setText(getIntent().getStringExtra("classcode"));
        }
//        new AsyncTask().execute(getResources().getString(R.string.domain) + "get_classes.php?school_id=" + school_id);
    }

    public void fab_click(View view) {
        if (year_entry.getText().toString().length() == 0 || Integer.valueOf(year_entry.getText().toString()) < 7 || Integer.valueOf(year_entry.getText().toString()) > 13) {
            Snackbar.make(findViewById(R.id.sign_up_class_relative_layout), "Enter a valid year group", Snackbar.LENGTH_SHORT).show();
        } else {
            if (solo.equals("0")) {
                new AsyncTask().execute(getResources().getString(R.string.domain) + "get_classes.php?school_id=" + school_id + "&passcode=" + class_code.getText().toString());
            } else {
                Intent myIntent = new Intent(SignUpClass.this, SignUpPassword.class);
                myIntent.putExtra("school_id", school_id);
                myIntent.putExtra("school_name", school_name);
                myIntent.putExtra("email", email);
                myIntent.putExtra("first_name", first_name);
                myIntent.putExtra("last_name", last_name);
                myIntent.putExtra("year", year_entry.getText().toString());
                myIntent.putExtra("class", "SOLO");
                myIntent.putExtra("solo", solo);
                SignUpClass.this.startActivity(myIntent);
            }
        }
    }

    public void create_class(View view) {
        Intent myIntent = new Intent(SignUpClass.this, SignUpCreateClass.class);
        myIntent.putExtra("school_id", school_id);
        myIntent.putExtra("school_name", school_name);
        myIntent.putExtra("email", email);
        myIntent.putExtra("first_name", first_name);
        myIntent.putExtra("last_name", last_name);
//        myIntent.putExtra("year", year_entry.getText().toString());
//        myIntent.putExtra("class", "SOLO");
        myIntent.putExtra("solo", "0");
        SignUpClass.this.startActivity(myIntent);
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(SignUpClass.this);
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
            Log.d("TTT", result);
            if (result.equals("No classes found, contact admin\n")) {
                 Snackbar.make(findViewById(R.id.sign_up_class_relative_layout), "Passcode entered is invalid.", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
//
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.sign_up_class_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            } else {
                try {
                    json = new JSONArray(result);
                    for (int i=0; i < json.length(); i++) {
                        try {
                            JSONObject jsonObject = json.getJSONObject(i);
                            class_ = jsonObject.getString("class_name");
                            String class_id = jsonObject.getString("id");
                            Intent myIntent = new Intent(SignUpClass.this, SignUpPassword.class);
                            myIntent.putExtra("school_id", school_id);
                            myIntent.putExtra("school_name", school_name);
                            myIntent.putExtra("email", email);
                            myIntent.putExtra("first_name", first_name);
                            myIntent.putExtra("last_name", last_name);
                            myIntent.putExtra("year", year_entry.getText().toString());
                            myIntent.putExtra("class", class_);
                            myIntent.putExtra("solo", solo);
                            myIntent.putExtra("class_id", class_id);
                            SignUpClass.this.startActivity(myIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
                    Snackbar.make(findViewById(R.id.sign_up_class_relative_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    return;
                }
            }
//            String id = "";
//            for (int i=0; i < json.length(); i++) {
//                try {
//                    JSONObject jsonObject = json.getJSONObject(i);
//                    if (first_run) {
//                        spinner_choices.add(jsonObject.getString("class_name"));
//                    } else {
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Snackbar.make(findViewById(R.id.sign_up_class_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
//                    if (pd.isShowing()) {
//                        pd.dismiss();
//                    }
//                }
//            }
//
//            if (first_run) {
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                        getApplicationContext(),
//                        android.R.layout.simple_spinner_item,
//                        spinner_choices
//                );
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                class_spinner.setAdapter(adapter);
//            }

            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
