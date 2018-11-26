package com.example.nedalhossam.banksystem.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nedalhossam.banksystem.R;
import com.example.nedalhossam.banksystem.adapters.ActivityAdapter;
import com.example.nedalhossam.banksystem.model.UserActivity;
import com.example.nedalhossam.banksystem.utils.Sesstion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivitiesFragment extends Fragment {
    private Button mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public ActivitiesFragment() {

    }

    ListView listView;
    ActivityAdapter mAdapter;
    List<UserActivity> list;
    String dateFromDatePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activities_list, container, false);
        mDisplayDate = (Button) rootView.findViewById(R.id.select_date);
        rootView.findViewById(R.id.all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivities(0);
            }
        });
        listView = (ListView) rootView.findViewById(R.id.listView);

//        getActivities(0);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("[][][]", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                dateFromDatePicker = day + "/" + month + "/" + year;
                mDisplayDate.setText(dateFromDatePicker);
                getActivities(1);
            }
        };

        return rootView;
    }

    public void getActivities(final int i) {
        list = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.173.1/bank_system_api/activities.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("activities");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                int id = Integer.parseInt(jsonObject1.getString("id"));
                                int account_number = Integer.parseInt(jsonObject1.getString("account_number"));
                                String type = jsonObject1.getString("type");
                                String date = jsonObject1.getString("date");
                                String value = jsonObject1.getString("value");
                                String to = jsonObject1.getString("to");
                                String from = jsonObject1.getString("from");
                                String timeInMilliseconds = jsonObject1.getString("timeInMilliseconds");

                                list.add(new UserActivity(id, account_number, type, date, value, to, from,
                                        timeInMilliseconds));
                            }
                            mAdapter = new ActivityAdapter(getActivity(), list);
                            listView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Activities found ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("account_number", String.valueOf(Sesstion.getInstance(getContext()).getUser().getAccountNumber()));
                if (i == 1) {
                    params.put("date", dateFromDatePicker);
                }
                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
