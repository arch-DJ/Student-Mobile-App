package com.slashroot.studentmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class ForgotPassword extends AppCompatActivity {
    // UI elements declaration
    TextView usernameTextView, sendOtpTo, otpTextView, passwordTextView, confirmPasswordTextView;
    RadioButton emailRadioButton, mobileRadioButton;
    Button sendOtpButton, submitOtpButton, resendOtpButton, changePasswordButton, confirmUsernameButton;
    String username, email, phone, otpType, domain = "https://lit-springs-26930.herokuapp.com";
    ProgressBar busyIndicator;

    // Assigning UI elements to our variables
    public void initialize() {
        usernameTextView = findViewById(R.id.forgotPasswordUsername);
        emailRadioButton = findViewById(R.id.forgotPasswordEmail);
        confirmUsernameButton = findViewById(R.id.forgotPasswordConfirmUsernameButton);
        mobileRadioButton = findViewById(R.id.forgotPasswordMobile);
        sendOtpTo = findViewById(R.id.forgotPasswordSendOtpTextView);
        sendOtpButton = findViewById(R.id.forgotPasswordSendOtpButton);
        submitOtpButton = findViewById(R.id.forgotPasswordSubmitOtp);
        resendOtpButton = findViewById(R.id.forgotPasswordResendOtp);
        otpTextView = findViewById(R.id.forgotPasswordOtp);
        passwordTextView = findViewById(R.id.forgotPasswordNewPassword);
        confirmPasswordTextView = findViewById(R.id.forgotPasswordConfirmNewPassword);
        changePasswordButton = findViewById(R.id.forgotPasswordChangePassword);
        busyIndicator = findViewById(R.id.forgotPasswordBusy);
    }


    // Confirm validity of entered username
    public void confirmUsername(View view) {
        username = usernameTextView.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username!", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = domain + "/user/login/forgot";
        final JSONObject json = new JSONObject();
        try {
            json.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Requesting email and mobile number associated with the provided username if username is valid
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
                        confirmUsernameButton.setVisibility(View.GONE);
                        emailRadioButton.setText(email);
                        mobileRadioButton.setText(phone);
                        sendOtpTo.setVisibility(View.VISIBLE);
                        emailRadioButton.setVisibility(View.VISIBLE);
                        mobileRadioButton.setVisibility(View.VISIBLE);
                        sendOtpButton.setVisibility(View.VISIBLE);
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
                    }
                });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    // Method which requests server to send OTP to email/mobile
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

    // Method which requests server to validate entered OTP
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
                sendOtpTo.setVisibility(View.GONE);
                emailRadioButton.setVisibility(View.GONE);
                mobileRadioButton.setVisibility(View.GONE);
                sendOtpButton.setVisibility(View.GONE);
                otpTextView.setVisibility(View.GONE);
                submitOtpButton.setVisibility(View.GONE);
                resendOtpButton.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.VISIBLE);
                confirmPasswordTextView.setVisibility(View.VISIBLE);
                changePasswordButton.setVisibility(View.VISIBLE);
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
                    Toast.makeText(ForgotPassword.this, "Wrong OTP entered!", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(ForgotPassword.this, "Server error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Method which requests server to resend OTP to email/mobile
    public void onClickResendOtp(View view) {
        String url;
        JSONObject jsonObject = new JSONObject();

        if (otpType.equals("email")) {
            url = domain + "/user/email/sendVerification/" + email;
            try {
                jsonObject.put("resend", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            url = domain + "/user/mobile/sendVerification/" + phone;
            try {
                jsonObject.put("resend", "message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                busyIndicator.setVisibility(View.INVISIBLE);
                Toast.makeText(ForgotPassword.this, "OTP Resent", Toast.LENGTH_SHORT).show();

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
                serverError();
            }
        });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Method which validates newly entered password with specified rules and if validated it requests server to reset password
    public void onClickChangePassword(View view) {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String confirmPassword = passwordTextView.getText().toString();

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
        }

        else if (!StudentRegistration.isValidPassword(password)) {
            Toast.makeText(this, "Password should be atleast 8 characters long, alphanumeric and contain one special character", Toast.LENGTH_LONG).show();
        }

        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm Password is not same as Password", Toast.LENGTH_SHORT).show();
        }

        else {
            String url = domain + "/user/login/resetpassword";
            JSONObject json = new JSONObject();
            try {
                json.put("username", username);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String credentials = json.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    busyIndicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotPassword.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    busyIndicator.setVisibility(View.INVISIBLE);
                    NetworkResponse networkResponse = error.networkResponse;

                    if (networkResponse == null) {
                        Toast.makeText(ForgotPassword.this, "No Internet!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    serverError();

                }
            })  {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return credentials == null ? null : credentials.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", credentials, "utf-8");
                        return null;
                    }
                }
            };

            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    // Method which displays server error message to user and jumps back to Main Activity
    public void serverError() {
        Toast.makeText(ForgotPassword.this, "Server error!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();
    }
}
