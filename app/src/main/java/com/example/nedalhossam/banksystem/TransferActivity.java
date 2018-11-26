package com.example.nedalhossam.banksystem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nedalhossam.banksystem.utils.Sesstion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferActivity extends AppCompatActivity {

    @BindView(R.id.reciver_account)
    EditText reciver_account;
    @BindView(R.id.send_money)
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        Bundle data = getIntent().getExtras();

        final String value = data.getString("VALUE");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String receiver = reciver_account.getText().toString().trim();
                    String senderr = String.valueOf(Sesstion.getInstance(TransferActivity.this).getUser().getAccountNumber());
                    if (receiver.equals(senderr)) {
                        Snackbar.make(sendButton, "This is your account number", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        transfer(senderr, receiver, value);
                    }

                }
            }
        });

    }

    private void transfer(final String sender, final String re, final String value) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        final String date = day + "/" + month + "/" + year;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.173.1/bank_system_api/transfer.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject baseJsonResponse = new JSONObject(response);
                                boolean error = baseJsonResponse.getBoolean("error");
                                String message = baseJsonResponse.getString("message");
                                Snackbar.make(sendButton, message, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                if (!error) {
                                    startActivity(new Intent(TransferActivity.this, HomeActivity.class));
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(TransferActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender", sender);
                    params.put("sender_first_name", Sesstion.getInstance(TransferActivity.this).getUser().getFirstName());
                    params.put("sender_last_name", Sesstion.getInstance(TransferActivity.this).getUser().getSecondName());
                    params.put("value", value);
                    params.put("receiver", re);
                    params.put("date", date);
                    params.put("timeInMillisecon", String.valueOf(System.currentTimeMillis()));
                    return params;
                }
            };
            queue.add(stringRequest);


        } else {
            Snackbar.make(sendButton, "No Internet Connection ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public boolean validate() {
        boolean valid = true;
        String accountReciver = reciver_account.getText().toString().trim();
        if (accountReciver.isEmpty()) {
            reciver_account.setError("Required");
            valid = false;
        } else {
            reciver_account.setError(null);
        }
        return valid;
    }
}
