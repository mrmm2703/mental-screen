package com.morahman.mentalscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.morahman.mentalscreen.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class LandingHome extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String id;
    String first_name;
    String last_name;
    String school_id;
    String school_name;
    String class_;
    String year_group;
    DrawerLayout drawer;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", "null");
        first_name = sharedPreferences.getString("first_name", "null");
        last_name = sharedPreferences.getString("last_name", "null");
        school_id = sharedPreferences.getString("school_id", "null");
        school_name = sharedPreferences.getString("school_name", "null");
        class_ = sharedPreferences.getString("class", "null");
        year_group = sharedPreferences.getString("year", "null");

        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name);
        navName.setText(first_name.toUpperCase() + " " + last_name.toUpperCase());
        TextView navClass = headerView.findViewById(R.id.nav_class);
        navClass.setText(class_.toUpperCase() + " (YEAR " + year_group + ")");
        TextView navSchool = headerView.findViewById(R.id.nav_school);
        navSchool.setText(school_name.toUpperCase());
        final LinearLayout navParent = headerView.findViewById(R.id.nav_parent);
        Picasso.get().load(getResources().getString(R.string.domain) + "school_logos/" + school_id + ".png").into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                navParent.setBackground(new BitmapDrawable(bitmap));

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeholderDrawable) {

            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
