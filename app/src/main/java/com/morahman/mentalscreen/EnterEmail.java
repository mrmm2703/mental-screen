package com.morahman.mentalscreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnterEmail extends AppCompatActivity {
    String school_id = "";
    String school_name = "";
    String first_name = "";
    ProgressDialog pd;
    JSONArray json;
    EditText email_entry;
    Intent myIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EnterEmail);
        super.onCreate(savedInstanceState);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_email);
        school_id = getIntent().getStringExtra("school_id");
        school_name = getIntent().getStringExtra("school_name");
        email_entry = findViewById(R.id.enter_email_email_address);
        Snackbar.make(findViewById(R.id.enter_email_relative_layout), school_name, Snackbar.LENGTH_SHORT).show();
    }

    public void fab_click(View view) {
        if (email_entry.getText().toString().length() == 0) {
            Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Enter an email address", Snackbar.LENGTH_SHORT).show();
        } else {
            String email = email_entry.getText().toString();
            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Matcher mat = pattern.matcher(email);
            if (mat.matches()) {
                new AsyncTask().execute(getResources().getString(R.string.domain) + "enter_email.php?email=" + email_entry.getText().toString() + "&school_id=" + school_id);
            } else {
                Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Invalid email address", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private class AsyncTask extends android.os.AsyncTask<String, String, String> {
        protected void onPreExecute() {
            pd = new ProgressDialog(EnterEmail.this);
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
                // Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Email not found", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
//                DialogFragment newFragment = new EmailNotFoundDialog();
//                newFragment.show(getSupportFragmentManager(), "email");
                AlertDialog.Builder builder = new AlertDialog.Builder(EnterEmail.this);
                builder.setMessage("Email not registested. Create an account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(EnterEmail.this, SignUpName.class);
                        myIntent.putExtra("school_id", school_id);
                        myIntent.putExtra("school_name", school_name);
                        myIntent.putExtra("email", email_entry.getText().toString());
                        EnterEmail.this.startActivity(myIntent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return;
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                return;
            }
            for (int i=0; i < json.length(); i++) {
                try {
                    JSONObject jsonObject = json.getJSONObject(i);
                    first_name = jsonObject.getString("first_name");
                    String id = jsonObject.getString("id");
                    String year = jsonObject.getString("year_group");
                    String class_= jsonObject.getString("class");
                    String solo = jsonObject.getString("solo");
                    String last_name = jsonObject.getString("last_name");
                    String class_id = jsonObject.getString("class_id");
                    Log.d("ENTEREMAIL", "ID: " + id);
                    Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Logged in: " + first_name + " " + last_name, Snackbar.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(EnterEmail.this, EnterPassword.class);
                    myIntent.putExtra("school_id", school_id);
                    myIntent.putExtra("school_name", school_name);
                    myIntent.putExtra("email", email_entry.getText().toString());
                    myIntent.putExtra("first_name", first_name);
                    myIntent.putExtra("last_name", last_name);
                    myIntent.putExtra("id", id);
                    myIntent.putExtra("year", year);
                    myIntent.putExtra("class", class_);
                    myIntent.putExtra("solo", solo);
                    myIntent.putExtra("class_id", class_id);
                    EnterEmail.this.startActivity(myIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.enter_email_relative_layout), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}