package com.slashroot.studentmobileapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;


public class ParentRegistration extends AppCompatActivity {
    String domain = "https://lit-springs-26930.herokuapp.com", otpType = "", gender = "";

    TextView parentDob, parentAadhaar, parentEmail, parentMobile, parentUsername, parentPassword, parentConfirmPassword, parentName, parentAddress;
    CheckBox aadharCheckBox, emailCheckBox, phoneCheckBox, loginCheckBox;
    ProgressBar busyIndicator;


    public void initialize() {
        parentDob = findViewById(R.id.parentDob);
        parentAadhaar = findViewById(R.id.parentAadhaar);
        parentEmail = findViewById(R.id.parentEmail);
        parentMobile = findViewById(R.id.parentPhoneNumber);
        parentUsername = findViewById(R.id.parentUserid);
        parentPassword = findViewById(R.id.parentPassword);
        parentConfirmPassword = findViewById(R.id.parentConfirmPassword);
        parentName = findViewById(R.id.parentName);
        parentAddress = findViewById(R.id.parentAddress);
        aadharCheckBox = findViewById(R.id.parentAadhaarCheckBox);
        phoneCheckBox = findViewById(R.id.parentPhoneCheckBox);
        emailCheckBox = findViewById(R.id.parentEmailCheckBox);
        loginCheckBox = findViewById(R.id.parentVerifyUserIdCheckBox);
        busyIndicator = findViewById(R.id.parentRegistrationBusy);
    }

    public void selectDob(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                parentDob.setText(Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(ParentRegistration.this,
                dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    public void verifyAadhaar(View view) {
        boolean isChecked = aadharCheckBox.isChecked();
        if (isChecked) {
            String aadhaar = parentAadhaar.getText().toString();
            if (aadhaar.isEmpty()) {
                Toast.makeText(this, "Please enter your Aadhar number!", Toast.LENGTH_SHORT).show();
                aadharCheckBox.setChecked(false);
            }
            else {
                String url = domain + "/user/aadhar/verification";
                JSONObject json = new JSONObject();
                try {
                    json.put("aadhaarNumber", aadhaar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                busyIndicator.setVisibility(View.INVISIBLE);
                                uniqueAadhaar();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                busyIndicator.setVisibility(View.INVISIBLE);
                                aadharCheckBox.setChecked(false);
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse == null) {
                                    Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int responseCode = networkResponse.statusCode;
                                if (responseCode == 400) {
                                    Toast.makeText(ParentRegistration.this, "This aadhaar number is not valid!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    serverError();
                                }
                            }
                        });
                busyIndicator.setVisibility(View.VISIBLE);
                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            }
        }

    }

    public void uniqueAadhaar() {
        String aadhaar = parentAadhaar.getText().toString();
        String url = domain + "/user/check/aadhaar/" + aadhaar;

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        busyIndicator.setVisibility(View.INVISIBLE);
                        aadharCheckBox.setEnabled(false);
                        parentAadhaar.setEnabled(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        busyIndicator.setVisibility(View.INVISIBLE);
                        aadharCheckBox.setChecked(false);
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse == null) {
                            Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int responseCode = networkResponse.statusCode;
                        if (responseCode == 400) {
                            Toast.makeText(ParentRegistration.this, "This aadhaar number is already registered!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            serverError();
                        }
                    }
                });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void verifyEmail(View view) {
        boolean checked = emailCheckBox.isChecked();

        if (checked) {
            String email = parentEmail.getText().toString();
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                String url = domain + "/user/check/email/" + email;

                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                busyIndicator.setVisibility(View.INVISIBLE);
                                emailCheckBox.setEnabled(false);
                                parentEmail.setEnabled(false);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                busyIndicator.setVisibility(View.INVISIBLE);
                                emailCheckBox.setChecked(false);
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse == null) {
                                    Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int responseCode = networkResponse.statusCode;
                                if (responseCode == 400) {
                                    Toast.makeText(ParentRegistration.this, "This email is already registered!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    serverError();
                                }
                            }
                        });
                busyIndicator.setVisibility(View.VISIBLE);
                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            }
            else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                emailCheckBox.setChecked(false);
            }
        }
    }

    public void verifyPhone(View view) {
        String phone = parentMobile.getText().toString();
        if (!TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches() && phone.length() == 10) {
            String url = domain + "/user/check/phone/" + phone;

            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            phoneCheckBox.setEnabled(false);
                            parentMobile.setEnabled(false);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            phoneCheckBox.setChecked(false);
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int responseCode = networkResponse.statusCode;
                            if (responseCode == 400) {
                                Toast.makeText(ParentRegistration.this, "This Phone number is already registered!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                serverError();
                            }
                            phoneCheckBox.setChecked(false);
                        }
                    });
            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
        else {
            Toast.makeText(this, "Please enter a valid Phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void parentVerifyUserId(View view) {
        String loginId = parentUsername.getText().toString();
        String password = parentPassword.getText().toString();
        String confirmPassword = parentConfirmPassword.getText().toString();

        if (loginId.isEmpty()) {
            Toast.makeText(this, "Please enter login ID", Toast.LENGTH_SHORT).show();
            loginCheckBox.setChecked(false);
        }

        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            loginCheckBox.setChecked(false);
        }

        else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
            loginCheckBox.setChecked(false);
        }

        else if (!StudentRegistration.isValidPassword(password)) {
            Toast.makeText(this, "Password should be atleast 8 characters long, alphanumeric and contain one special character", Toast.LENGTH_LONG).show();
            loginCheckBox.setChecked(false);
        }

        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm Password is not same as Password", Toast.LENGTH_SHORT).show();
            loginCheckBox.setChecked(false);
        }

        else {
            String url = domain + "/user/check/username/" + loginId;

            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            loginCheckBox.setEnabled(false);
                            parentUsername.setEnabled(false);
                            parentPassword.setEnabled(false);
                            parentConfirmPassword.setEnabled(false);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            loginCheckBox.setChecked(false);
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int responseCode = networkResponse.statusCode;
                            if (responseCode == 400) {
                                Toast.makeText(ParentRegistration.this, "This UserID is already registered!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                serverError();
                            }

                        }
                    });
            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void onClickRegister(View view) {
        RadioButton parentMaleButton = findViewById(R.id.parentMaleButton);
        RadioButton parentFemaleButton = findViewById(R.id.parentFemaleButton);
        RadioButton emailOtp = findViewById(R.id.parentEmailOtp);
        RadioButton phoneOtp = findViewById(R.id.parentPhoneOtp);
        if (parentName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Name", Toast.LENGTH_SHORT).show();
        }

        else if (parentDob.getText().toString().equals("Date Of Birth")) {
            Toast.makeText(this, "Please select your Date of Birth", Toast.LENGTH_SHORT).show();
        }

        else if (!parentMaleButton.isChecked() && !parentFemaleButton.isChecked()) {
            Toast.makeText(this, "Please select your Gender", Toast.LENGTH_SHORT).show();
        }

        else if (parentAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Address", Toast.LENGTH_SHORT).show();
        }

        else if (aadharCheckBox.isEnabled()) {
            Toast.makeText(this, "Please enter your Aadhaar number", Toast.LENGTH_SHORT).show();
        }

        else if (emailCheckBox.isEnabled()) {
            Toast.makeText(this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
        }

        else if (phoneCheckBox.isEnabled()) {
            Toast.makeText(this, "Please enter your Phone number", Toast.LENGTH_SHORT).show();
        }

        else if (loginCheckBox.isEnabled()) {
            Toast.makeText(this, "Please enter your LoginId and Password", Toast.LENGTH_SHORT).show();
        }

        else if (!emailOtp.isChecked() && !phoneOtp.isChecked()) {
            Toast.makeText(this, "Please choose OTP type", Toast.LENGTH_SHORT).show();
        }

        else {
            String url;
            if (parentMaleButton.isChecked()) {
                gender = "Male";
            }
            else {
                gender = "Female";
            }

            if (emailOtp.isChecked()) {
                url = domain + "/user/email/sendVerification/" + parentEmail.getText().toString();
                otpType = "email";
            }
            else {
                url = domain + "/user/mobile/sendVerification/" + parentMobile.getText().toString();
                otpType = "phone";
            }

            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            sendOTP(otpType);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            busyIndicator.setVisibility(View.INVISIBLE);
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int responseCode = networkResponse.statusCode;
                            if (responseCode == 400) {
                                Toast.makeText(ParentRegistration.this, "You have currently requested for an email verification! Try again after 5 minutes.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                serverError();
                            }
                        }
                    });
            busyIndicator.setVisibility(View.VISIBLE);
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }

    }

    public void sendOTP(final String otpType) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(input)
                .setCancelable(false)
                .setMessage("Please enter the OTP received")
                .setNegativeButton("Resend", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url;
                        JSONObject json = new JSONObject();

                        try {
                            json.put("otp", input.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (otpType.equals("email")) {
                            url = domain + "/user/verify/" + parentEmail.getText().toString();
                        } else {
                            url = domain + "/user/mobile/verify/" + parentMobile.getText().toString();
                        }

                        JsonObjectRequest jsonObjectRequest =
                                new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        busyIndicator.setVisibility(View.INVISIBLE);
                                        finalSubmission();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        busyIndicator.setVisibility(View.INVISIBLE);
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse == null) {
                                            Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        int responseCode = networkResponse.statusCode;
                                        if (responseCode == 400) {
                                            Toast.makeText(ParentRegistration.this, "Wrong OTP entered!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            serverError();
                                        }
                                    }
                                });
                        busyIndicator.setVisibility(View.VISIBLE);
                        VolleySingleton.getInstance(ParentRegistration.this).addToRequestQueue(jsonObjectRequest);
                    }
                })
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Resend OTP
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    String url = "";
                    JSONObject jsonObject = new JSONObject();

                    @Override
                    public void onClick(View view) {
                        if (otpType.equals("email")) {
                            url = domain + "/user/email/sendVerification/" + parentEmail.getText().toString();
                            try {
                                jsonObject.put("resend", true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            url = domain + "/user/mobile/sendVerification/" + parentMobile.getText().toString();
                            try {
                                jsonObject.put("resend", "message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        JsonObjectRequest jsonObjectRequest =
                                new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        busyIndicator.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ParentRegistration.this, "OTP Resent", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        busyIndicator.setVisibility(View.INVISIBLE);
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse == null) {
                                            Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        serverError();                                        
                                    }
                                });
                        busyIndicator.setVisibility(View.VISIBLE);
                        VolleySingleton.getInstance(ParentRegistration.this).addToRequestQueue(jsonObjectRequest);
                    }
                });
            }
        });
        dialog.show();
    }

    public void finalSubmission() {
        JSONObject json = new JSONObject();
        String url = domain + "/user/registration/parent";
        try {
            json.put("aadhaar", parentAadhaar.getText().toString());
            json.put("username", parentUsername.getText().toString());
            json.put("password", parentPassword.getText().toString());
            json.put("name", parentName.getText().toString());
            json.put("gender", gender);
            json.put("dob", parentDob.getText().toString());
            json.put("email", parentEmail.getText().toString());
            json.put("phone", parentMobile.getText().toString());
            json.put("address", parentAddress.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        busyIndicator.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(ParentRegistration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        busyIndicator.setVisibility(View.INVISIBLE);
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse == null) {
                            Toast.makeText(ParentRegistration.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        serverError();
                    }
                });
        busyIndicator.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(ParentRegistration.this).addToRequestQueue(jsonObjectRequest);
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_registration);
        initialize();
    }

    public void serverError() {
        Toast.makeText(ParentRegistration.this, "Server error!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
