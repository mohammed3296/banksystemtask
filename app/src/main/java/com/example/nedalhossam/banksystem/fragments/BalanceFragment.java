package com.example.nedalhossam.banksystem.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.nedalhossam.banksystem.TransferActivity;
import com.example.nedalhossam.banksystem.utils.Sesstion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nedal Hossam on 09/04/2018.
 */

public class BalanceFragment extends Fragment {
    private EditText editText;
    private TextView textView;

    public BalanceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.balance_fragment, container, false);
        editText = (EditText) rootView.findViewById(R.id.input_value);
        textView = (TextView) rootView.findViewById(R.id.my_balance);
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.173.1/bank_system_api/mybalance.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("<>", response);

                            JSONObject baseJsonResponse = null;
                            try {
                                baseJsonResponse = new JSONObject(response);
                                String balanceValue = baseJsonResponse.getString("balance");
                                textView.setText("Your Balance: $" + balanceValue);
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    return params;
                }
            };
            queue.add(stringRequest);


        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        final FloatingActionButton deposit = (FloatingActionButton) rootView.findViewById(R.id.deposit);
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate())
                    changeBalance(deposit, 1);
            }
        });


        final FloatingActionButton withdraw = (FloatingActionButton) rootView.findViewById(R.id.withdraw);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate())
                    changeBalance(withdraw, 0);
            }
        });


        FloatingActionButton transfer = (FloatingActionButton) rootView.findViewById(R.id.transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("VALUE", editText.getText().toString().trim());
                    Intent trasverActivity = new Intent(getContext(), TransferActivity.class);
                    trasverActivity.putExtras(bundle);
                    startActivity(trasverActivity);
                }
            }
        });


        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void changeBalance(final View v, int type) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        final String date = day + "/" + month + "/" + year;
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

            String url = null;
            if (type == 1) {
                url = "http://192.168.173.1/bank_system_api/deposit.php";
            } else {
                url = "http://192.168.173.1/bank_system_api/withdraw.php";
            }


            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject baseJsonResponse = new JSONObject(response);
                                boolean error = baseJsonResponse.getBoolean("error");
                                String message = baseJsonResponse.getString("message");
                                Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                if (!error) {
                                    String balanceValue = baseJsonResponse.getString("balance");
                                    textView.setText("Your Balance: $" + balanceValue);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    params.put("value", editText.getText().toString().trim());
                    params.put("date", date);
                    params.put("timeInMillisecon", String.valueOf(System.currentTimeMillis()));
                    return params;
                }
            };
            queue.add(stringRequest);

        } else {
            Snackbar.make(v, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public boolean validate() {
        boolean valid = true;
        String value = editText.getText().toString().trim();
        if (value.isEmpty() || Integer.parseInt(value) <= 0) {
            editText.setError("Value must be bigger than 0 ");
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }
}
