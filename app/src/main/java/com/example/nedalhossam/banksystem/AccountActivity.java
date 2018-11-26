package com.example.nedalhossam.banksystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.age)
    EditText ageView;
    @BindView(R.id.second_name)
    EditText _second_name;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _input_mobile;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    @BindView(R.id.input_reEnterPassword)
    EditText input_reEnterPassword;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startPageIntent = new Intent(AccountActivity.this, LoginActivity.class);
                startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startPageIntent);
                finish();
            }
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        final String firstName = _nameText.getText().toString().trim();
        final String secondName = _second_name.getText().toString().trim();
        final String email = _emailText.getText().toString().trim();
        final String mobileNumber = _input_mobile.getText().toString().trim();
        final String password = _passwordText.getText().toString().trim();
        final String age = ageView.getText().toString().trim();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressDialog = new ProgressDialog(AccountActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.173.1/bank_system_api/register.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                final String message = jsonObject.getString("message");
                                final boolean error = jsonObject.getBoolean("error");
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                Toast.makeText(AccountActivity.this, message,
                                                        Toast.LENGTH_SHORT).show();
                                                if (error == false) {
                                                    progressDialog.dismiss();
                                                    createSuccess();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Snackbar.make(_signupButton, message, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }
                                        }, 1000);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AccountActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstname", firstName);
                    params.put("secondname", secondName);
                    params.put("email", email);
                    params.put("phone", mobileNumber);
                    params.put("age", age);
                    params.put("password", password);
                    return params;
                }
            };
            queue.add(stringRequest);
        } else {
            Snackbar.make(_signupButton, "No Internet Connection ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public void createSuccess() {
        setResult(RESULT_OK, null);
        try {
            Intent startPageIntent = new Intent(AccountActivity.this, LoginActivity.class);
            startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startPageIntent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(this, "Registeration failed", Toast.LENGTH_SHORT).show();
    }

    public boolean validate() {
        boolean valid = true;
        String name = _nameText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String age = ageView.getText().toString().trim();
        String mobileNumber = _input_mobile.getText().toString().trim();
        String re_enter = input_reEnterPassword.getText().toString().trim();
        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }
        if (age.isEmpty() || age.length() > 2) {
            ageView.setError("At most 2 digits");
            valid = false;
        } else {
            ageView.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobileNumber.isEmpty() || mobileNumber.length() < 11 || mobileNumber.length() > 11) {
            _input_mobile.setError("Enter a valid Number");
            valid = false;
        } else {
            _input_mobile.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!re_enter.equals(password)) {
            input_reEnterPassword.setError("Not Matched.");
            valid = false;
        } else {
            input_reEnterPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }
}
