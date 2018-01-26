package com.slashroot.studentmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;



public class ForgotPassword extends AppCompatActivity {
    TextView usernameTextView, sendOtpTo, otpTextView;
    CheckBox usernameCheckbox;
    RadioButton emailRadioButton, mobileRadioButton;
    Button sendOtpButton, submitOtpButton, resendOtpButton;
    String username, email, phone, otpType, domain = "https://lit-springs-26930.herokuapp.com";
    ProgressBar busyIndicator;

    public void initialize() {
        usernameTextView = findViewById(R.id.forgotPasswordUsername);
        usernameCheckbox = findViewById(R.id.forgotPasswordUsernameCheckBox);
        emailRadioButton = findViewById(R.id.forgotPasswordEmail);
        mobileRadioButton = findViewById(R.id.forgotPasswordMobile);
        sendOtpTo = findViewById(R.id.forgotPasswordSendOtpTextView);
        sendOtpButton = findViewById(R.id.forgotPasswordSendOtpButton);
        submitOtpButton = findViewById(R.id.forgotPasswordSubmitOtp);
        resendOtpButton = findViewById(R.id.forgotPasswordResendOtp);
        otpTextView = findViewById(R.id.forgotPasswordOtp);
        busyIndicator = findViewById(R.id.forgotPasswordBusy);
    }

    public void confirmUsername(View view) {
        boolean checked = usernameCheckbox.isChecked();

        if (checked) {
            username = usernameTextView.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username!", Toast.LENGTH_SHORT).show();
                usernameCheckbox.setChecked(false);
                return;
            }
            String url = domain + "/user/login/forgot";
            final JSONObject json = new JSONObject();
            try {
                json.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            try {
                                email = response.getString("email");
                                phone = response.getString("phone");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            emailRadioButton.setText(email);
                            mobileRadioButton.setText(phone);
                            sendOtpTo.setVisibility(View.VISIBLE);
                            emailRadioButton.setVisibility(View.VISIBLE);
                            mobileRadioButton.setVisibility(View.VISIBLE);
                            sendOtpButton.setVisibility(View.VISIBLE);
                            usernameCheckbox.setEnabled(false);
                            usernameTextView.setEnabled(false);
                            emailRadioButton.setText(email);
                            mobileRadioButton.setText(phone);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                usernameCheckbox.setChecked(false);
                                Toast.makeText(ForgotPassword.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int responseCode = networkResponse.statusCode;
                            if (responseCode == 400) {
                                Toast.makeText(ForgotPassword.this, "Invalid Login ID!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                serverError();
                            }
                            usernameCheckbox.setChecked(false);
                        }
                    });
            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }

    }

    public void onClickSendOtp(View view) {
        if (!emailRadioButton.isChecked() && !mobileRadioButton.isChecked()) {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show();
        }

        else {
            String url;
            if (emailRadioButton.isChecked()) {
                otpType = "email";
                url = domain + "/user/email/sendVerification/" + email;
            }
            else {
                otpType = "mobile";
                url = domain + "/user/mobile/sendVerification/" + phone;
            }

            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            otpTextView.setVisibility(View.VISIBLE);
                            submitOtpButton.setVisibility(View.VISIBLE);
                            resendOtpButton.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                Toast.makeText(ForgotPassword.this, "Internet Service not available!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int responseCode = networkResponse.statusCode;

                            if (responseCode == 400) {
                                Toast.makeText(ForgotPassword.this, "You've already requested for an OTP. Please try again after 5 minutes!", Toast.LENGTH_LONG).show();
                            }

                            else {
                                Toast.makeText(ForgotPassword.this, "Server error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void onClickSubmitOtp(View view) {
        String otp = otpTextView.getText().toString();

        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter the OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url;
        JSONObject json = new JSONObject();
        try {
            json.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (otpType.equals("email"))
            url = domain + "/user/verify/" + email;

        else
            url = domain + "/user/mobile/verify/" + phone;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                busyIndicator.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                busyIndicator.setVisibility(View.INVISIBLE);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse == null) {
                    Toast.makeText(ForgotPassword.this, "No internet!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int statusCode = networkResponse.statusCode;

                if (statusCode == 400) {

                }
            }
        });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }



    public void serverError() {
        Toast.makeText(ForgotPassword.this, "Server error!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();
    }
}
