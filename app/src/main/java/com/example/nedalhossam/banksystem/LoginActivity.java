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
import com.example.nedalhossam.banksystem.model.User;
import com.example.nedalhossam.banksystem.utils.Sesstion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Sesstion.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            return;
        }
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startPageIntent = new Intent(LoginActivity.this, AccountActivity.class);
                startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startPageIntent);
                finish();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String email = _emailText.getText().toString().trim();
        final String password = _passwordText.getText().toString().trim();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Auth...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.173.1/bank_system_api/login.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("<>", response);

                            try {
                                JSONObject baseJsonResponse = new JSONObject(response);
                                boolean error = baseJsonResponse.getBoolean("error");
                                final String message = baseJsonResponse.getString("message");
                                if (error == false) {
                                    JSONObject userObject = baseJsonResponse.getJSONObject("user");
                                    final String firstname = userObject.getString("firstname");
                                    final String lastname = userObject.getString("secondname");
                                    final String email = userObject.getString("email");
                                    final String accountNumber = userObject.getString("account_number");

                                     new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                    Toast.makeText(LoginActivity.this, message,
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    onLoginSuccess(new User(Integer.parseInt(accountNumber), firstname, lastname, email));
                                                }
                                            }, 1000);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, message,
                                            Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            queue.add(stringRequest);


        } else {
            Snackbar.make(_loginButton, "No Internet Connection ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    public void onLoginSuccess(User user) {

        try {
            Sesstion.getInstance(this).userLogin(user);
            Intent startPageIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startPageIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onLoginFailed() {
        Snackbar.make(_loginButton, "Inputs Error", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address .");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
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
