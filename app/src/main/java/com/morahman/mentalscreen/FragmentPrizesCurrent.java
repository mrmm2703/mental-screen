package com.morahman.mentalscreen;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.CrashlyticsRegistrar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.core.CrashlyticsCore;
import com.google.firebase.crashlytics.internal.CrashlyticsNativeComponent;

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
 * {@link FragmentPrizesCurrent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPrizesCurrent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPrizesCurrent extends Fragment {
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

    public FragmentPrizesCurrent() {
        // Required empty public constructor
        FirebaseCrashlytics.getInstance();
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
    public static FragmentPrizesCurrent newInstance(String param1, String param2) {
        FragmentPrizesCurrent fragment = new FragmentPrizesCurrent();
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
        usageStatsManager = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        new AsyncTask().execute(getResources().getString(R.string.domain) + "get_prizes.php?"
        + "school_id=" + school_id
        + "&year_group=" + year_group);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_prizes_current, container, false);
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
                Snackbar.make(view.findViewById(R.id.fragment_prizes_current_parent), "Updates times on server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("Connection failed\n")) {
                Snackbar.make(view.findViewById(R.id.fragment_prizes_current_parent), "Error 1: Endpoint could not connect to MySQL server", Snackbar.LENGTH_SHORT).show();
            } else if (result.equals("1\n")) {
//                Snackbar.make(view.findViewById(R.id.fragment_prizes_current_parent), "Updated server with times", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    json = new JSONArray(result);
                } catch (JSONException e) {
                    Snackbar.make(view.findViewById(R.id.fragment_prizes_current_parent), "Error 2: Couldn't create JSONArray", Snackbar.LENGTH_SHORT).show();
                }
                LinearLayout linearLayout = view.findViewById(R.id.fragment_prizes_current_linear_layout);
//                linearLayout.removeAllViews();
                for (int i=0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        String title = jsonObject.getString("prize_title");
                        String desc = jsonObject.getString("prize_desc");
                        String startDate = jsonObject.getString("start_date");
                        String endDate = jsonObject.getString("end_date");
                        String setBy = jsonObject.getString("set_by");
                        String year = jsonObject.getString("year_group");
                        String school = jsonObject.getString("school_id");
                        createCard(linearLayout, i+1, title, desc, startDate, endDate, setBy, school, year);
//                        LinearLayout parent = new LinearLayout(getContext());
//                        createCard(linearLayout, i+1, first_name_json+" "+last_name_json, Integer.parseInt(screen_time_minutes_json), class_+" (YEAR "+year+")");
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
                        Snackbar.make(view.findViewById(R.id.fragment_prizes_current_parent), "Error 3: Couldn't parse JSON data", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void createCard(LinearLayout parent, Integer position, String prize_title, String prize_desc, String start_date, String end_date, String set_by, String school, String year) {
        // Create CardView
        CardView cardView = new CardView(getContext());
        CardView.LayoutParams cardViewLayoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        // Create inner LinearLayout
        LinearLayout innerLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams innerLinearLayoutLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        innerLinearLayoutLayoutParams.weight = 0.9f;
        innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        innerLinearLayout.setLayoutParams(innerLinearLayoutLayoutParams);
        mainLinearLayout.addView(innerLinearLayout);
        // Create prize title TextView
        TextView title = new TextView(getContext());
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title.setText(prize_title.toUpperCase());
        title.setTextColor(getResources().getColor(R.color.blue));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//        }
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setLayoutParams(titleLayoutParams);
        title.setPadding(getDP(10), getDP(10), getDP(60), 0);
        innerLinearLayout.addView(title);
        // Create date range TextView
        TextView datetext = new TextView(getContext());
        LinearLayout.LayoutParams dateTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDP(20));
        dateTextLayoutParams.setMargins(0, 0, 0, getDP(10));
        datetext.setText(start_date + " TO " + end_date);
//        datetext.setTextColor(getResources().getColor(R.color.blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        datetext.setLayoutParams(dateTextLayoutParams);
        datetext.setPadding(getDP(10), 0, getDP(60), 0);
        innerLinearLayout.addView(datetext);
        // Create school TextView
        TextView schoolText = new TextView(getContext());
        LinearLayout.LayoutParams schoolTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDP(20));
        if (school.equals("0")) {
            schoolText.setText("ALL SCHOOLS");
        } else {
            schoolText.setText(school_name.toUpperCase() + " ONLY");
        }
//        schoolText.setTextColor(getResources().getColor(R.color.blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        schoolText.setLayoutParams(schoolTextLayoutParams);
        schoolText.setPadding(getDP(10), 0, getDP(10), 0);
        innerLinearLayout.addView(schoolText);
        // Create year group TextView
        TextView yearText = new TextView(getContext());
        LinearLayout.LayoutParams yearTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDP(20));
        if (year.equals("0")) {
            yearText.setText("ALL YEAR GROUPS");
        } else {
            yearText.setText("YEAR " + year + " ONLY");
        }
//        yearText.setTextColor(getResources().getColor(R.color.blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        yearText.setLayoutParams(yearTextLayoutParams);
        yearText.setPadding(getDP(10), 0, getDP(10), 0);
        innerLinearLayout.addView(yearText);
        // Create description text
        TextView descText = new TextView(getContext());
        LinearLayout.LayoutParams descTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descTextLayoutParams.setMargins(0, getDP(10), 0, 0);
        descText.setText(prize_desc);
//        descText.setTextColor(getResources().getColor(R.color.blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        descText.setLayoutParams(descTextLayoutParams);
        descText.setPadding(getDP(10), 0, getDP(10), 0);
        innerLinearLayout.addView(descText);
        // Create setBy TextView
        TextView setByText = new TextView(getContext());
        LinearLayout.LayoutParams setByTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDP(20));
        setByTextLayoutParams.setMargins(0, getDP(10), 0, getDP(10));
        setByText.setText(" - " + set_by);
//        setByText.setTextColor(getResources().getColor(R.color.blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            title.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        setByText.setLayoutParams(setByTextLayoutParams);
        setByText.setPadding(getDP(10), 0, getDP(10), 0);
        innerLinearLayout.addView(setByText);
        // Add CardView to parent
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
