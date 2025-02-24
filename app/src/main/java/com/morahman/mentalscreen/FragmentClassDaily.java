package com.morahman.mentalscreen;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Calendar;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentClassDaily.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentClassDaily#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentClassDaily extends Fragment {
    JSONArray json;
    View view;

    String id;
    String first_name;
    String last_name;
    String school_id;
    String school_name;
    String class_;
    String year_group;
    UsageStatsManager usageStatsManager;
    SharedPreferences sharedPreferences;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentClassDaily() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentClassDaily.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentClassDaily newInstance(String param1, String param2) {
        FragmentClassDaily fragment = new FragmentClassDaily();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("FragmentClassDaily", "onCreate called");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void createUI() {
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("login_id", "null");
        first_name = sharedPreferences.getString("first_name", "null");
        last_name = sharedPreferences.getString("last_name", "null");
        school_id = sharedPreferences.getString("school_id", "null");
        school_name = sharedPreferences.getString("school_name", "null");
        class_ = sharedPreferences.getString("class", "null");
        year_group = sharedPreferences.getString("year", "null");

//        FloatingActionButton fab = view.findViewById(R.id.fragment_class_daily_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout linearLayout = view.findViewById(R.id.fragment_class_daily_linear_layout);
//                linearLayout.removeAllViews();
//                createUI();
//            }
//        });
        new AsyncTask().execute(getResources().getString(R.string.domain) + "get_leaderboard.php?class=" + class_+ "&school_id=" + school_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_class_daily, container, false);
        Log.d("FragmentClassDaily", "onCreateView called");
        createUI();
        return view;
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
            Log.d("RRR", result);
            if (result.equals("No results found\n")) {
                Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Updates times on server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("1\n")) {
//                Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Updated server with times", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    json = new JSONArray(result);
                } catch (JSONException e) {
                    Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                }
                LinearLayout linearLayout = view.findViewById(R.id.fragment_class_daily_linear_layout);
//                linearLayout.removeAllViews();
                for (int i=0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        String first_name_json = jsonObject.getString("first_name");
                        String last_name_json = jsonObject.getString("last_name");
                        String screen_time_minutes_json = jsonObject.getString("screen_time_minutes");
                        String student_id_json = jsonObject.getString("student_id");
                        String class_ = jsonObject.getString("class");
                        String year = jsonObject.getString("year_group");
                        String solo = jsonObject.getString("solo");
//                        LinearLayout parent = new LinearLayout(getContext());
                        if (solo.equals("0")) {
                            createCard(linearLayout, i+1, first_name_json+" "+last_name_json, Integer.parseInt(screen_time_minutes_json), class_+" (YEAR "+year+")");
                        } else {
                            createCard(linearLayout, i+1, first_name_json+" "+last_name_json, Integer.parseInt(screen_time_minutes_json), "SOLO"+" (YEAR "+year+")");
                        }
//                        int pixels = (int) (40 * getContext().getResources().getDisplayMetrics().density);
//                        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
//                        parent.setOrientation(LinearLayout.HORIZONTAL);
//                        TextView textView1 = new TextView(getContext());
//                        textView1.setText(first_name_json + " " + last_name_json);
//                        textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
////                        textView1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
//                        textView1.setGravity(Gravity.CENTER_VERTICAL);
//                        pixels = (int) (10 * getContext().getResources().getDisplayMetrics().density);
//                        textView1.setPadding(pixels, pixels, pixels, pixels);
//                        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//                        TextView textView2 = new TextView(getContext());
//                        textView2.setText(screen_time_minutes_json);
//                        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f));
////                        textView2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
//                        textView2.setGravity(Gravity.CENTER_VERTICAL);
//                        pixels = (int) (10 * getContext().getResources().getDisplayMetrics().density);
//                        textView2.setPadding(pixels, pixels, pixels, pixels);
//                        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//                        if (student_id_json.equals(id)) {
//                            textView1.setTextColor(getResources().getColor(R.color.black));
//                            textView2.setTextColor(getResources().getColor(R.color.black));
//                            textView1.setTypeface(null, Typeface.BOLD);
//                            textView2.setTypeface(null, Typeface.BOLD);
//                        }
//                        parent.addView(textView1);
//                        parent.addView(textView2);
//                        linearLayout.addView(parent);
                    } catch (JSONException e) {
                        e.printStackTrace();
FirebaseCrashlytics.getInstance().recordException(e);
                        Snackbar.make(view.findViewById(R.id.fragment_class_daily_parent), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void createCard(LinearLayout parent, Integer position, String name, Integer score, String class_) {
        // Create CardView
        CardView cardView = new CardView(getContext());
        CardView.LayoutParams cardViewLayoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, getDP(70));
        if (position == 1) {
            cardViewLayoutParams.setMargins(getDP(15), getDP(15), getDP(15), getDP(15));
        } else {
            cardViewLayoutParams.setMargins(getDP(15), 0, getDP(15), getDP(15));
        }
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setRadius(12.0f);
        cardView.setElevation(10.0f);
        // Create container LinearLayout
        LinearLayout mainLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams mainLinearLayoutLayourParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardView.addView(mainLinearLayout);
        // Create position TextView
        TextView positionText = new TextView(getContext());
        LinearLayout.LayoutParams positionTextLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
//        positionTextLayoutParams.gravity = Gravity.CENTER;
        positionText.setGravity(Gravity.CENTER);
        positionTextLayoutParams.weight = 0.2f;
        positionText.setLayoutParams(positionTextLayoutParams);
//        positionText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
        positionText.setPadding(0, getDP(20), 0, getDP(20));
        positionText.setText(position.toString());
        positionText.setTextColor(getResources().getColor(R.color.blue));
        TextViewCompat.setAutoSizeTextTypeWithDefaults(positionText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            positionText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        mainLinearLayout.addView(positionText);
        // Create name and class LinearLayout
        LinearLayout nameLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams nameLinearLayoutLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        nameLinearLayoutLayoutParams.weight = 0.9f;
        nameLinearLayout.setPadding(0, getDP(3), 0, 0);
        nameLinearLayout.setOrientation(LinearLayout.VERTICAL);
        nameLinearLayout.setLayoutParams(nameLinearLayoutLayoutParams);
        mainLinearLayout.addView(nameLinearLayout);
        // Create name TextView
        TextView nameText = new TextView(getContext());
        LinearLayout.LayoutParams nameTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        nameTextLayoutParams.weight = 0.5f;
//        nameText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
        nameText.setTextColor(getResources().getColor(R.color.black));
        nameText.setPadding(0, getDP(12), 0, 0);
        nameText.setText(name.toUpperCase());
        nameText.setLayoutParams(nameTextLayoutParams);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(nameText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nameText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        nameLinearLayout.addView(nameText);
        // Create class TextView
        TextView classText = new TextView(getContext());
        LinearLayout.LayoutParams classTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        classTextLayoutParams.weight = 0.5f;
//        classText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
        classText.setTextColor(getResources().getColor(R.color.grey));
        classText.setPadding(0, 0, 0, getDP(20));
        classText.setText(class_);
        classText.setLayoutParams(classTextLayoutParams);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(classText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            classText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        nameLinearLayout.addView(classText);
        // Create points TextView
        TextView pointsText = new TextView(getContext());
        LinearLayout.LayoutParams pointsTextLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//        pointsTextLayoutParams.gravity = Gravity.CENTER;
        pointsTextLayoutParams.weight = 0.3f;
        pointsText.setGravity(Gravity.CENTER);
//        pointsText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.fredoka_one));
        pointsText.setTextColor(getResources().getColor(R.color.blue));
        pointsText.setText(Integer.toString(score));
        pointsText.setLayoutParams(pointsTextLayoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pointsText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        mainLinearLayout.addView(pointsText);
        parent.addView(cardView);
    }

    public Integer getDP(Integer dp) {
        Integer pixels = (int) (dp * getContext().getResources().getDisplayMetrics().density);
        return pixels;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
