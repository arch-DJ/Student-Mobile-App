package com.slashroot.studentmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    Button sendOtpButton, submitOtpButton;
    String username, email, phone, otpType, domain = "https://lit-springs-26930.herokuapp.com";

    public void initialize() {
        usernameTextView = findViewById(R.id.forgotPasswordUsername);
        usernameCheckbox = findViewById(R.id.forgotPasswordUsernameCheckBox);
        emailRadioButton = findViewById(R.id.forgotPasswordEmail);
        mobileRadioButton = findViewById(R.id.forgotPasswordMobile);
        sendOtpTo = findViewById(R.id.forgotPasswordSendOtpTextView);
        sendOtpButton = findViewById(R.id.forgotPasswordSendOtpButton);
        submitOtpButton = findViewById(R.id.forgotPasswordSubmitOtp);
        otpTextView = findViewById(R.id.forgotPasswordOtp);
    }

    public void confirmUsername(View view) {
        boolean checked = usernameCheckbox.isChecked();

        if (checked) {
            username = usernameTextView.getText().toString();
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
                            NetworkResponse networkResponse = error.networkResponse;
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
                            otpTextView.setVisibility(View.VISIBLE);
                            submitOtpButton.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                serverError();
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void onClickSubmitOtp(View view) {

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
