package com.morahman.mentalscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class SignUpName extends AppCompatActivity {
    String school_name;
    String school_id;
    String email;
    EditText first_name_entry;
    EditText last_name_entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SignUpName);
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up_name);
        school_name = getIntent().getStringExtra("school_name");
        school_id = getIntent().getStringExtra("school_id");
        email = getIntent().getStringExtra("email");
        first_name_entry = findViewById(R.id.sign_up_name_first_name);
        last_name_entry = findViewById(R.id.sign_up_name_last_name);
    }

    public void fab_click(View view)  {
        if (first_name_entry.getText().toString().length() == 0 || last_name_entry.getText().toString().length() == 0) {
            Snackbar.make(findViewById(R.id.sign_up_name_relative_layout), "Enter both your names", Snackbar.LENGTH_SHORT).show();
        } else {
            Intent myIntent = new Intent(SignUpName.this, SignUpClass.class);
            myIntent.putExtra("school_id", school_id);
            myIntent.putExtra("school_name", school_name);
            myIntent.putExtra("email", email);
            myIntent.putExtra("first_name", first_name_entry.getText().toString());
            myIntent.putExtra("last_name", last_name_entry.getText().toString());
            SignUpName.this.startActivity(myIntent);
        }
    }
}
