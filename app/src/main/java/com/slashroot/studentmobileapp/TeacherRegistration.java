package com.slashroot.studentmobileapp;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeacherRegistration extends AppCompatActivity {
    String domain = "https://lit-springs-26930.herokuapp.com", otpType = "", gender = "";
    String[] universities, colleges, streams, departments;
    TextView teacherUniversity, teacherCollege, teacherDob, teacherStream, teacherDepartment, teacherAadhaar,
            teacherEmail, teacherMobile, teacherUsername, teacherPassword, teacherConfirmPassword, teacherName, teacherAddress;
    AutoCompleteTextView teacherUniversitySearch, teacherCollegeSearch, teacherStreamSearch, teacherDepartmentSearch;
    CheckBox aadharCheckBox, emailCheckBox, phoneCheckBox, loginCheckBox;

    public void initialize() {
        teacherUniversity = findViewById(R.id.teacherUniversity);
        teacherCollege = findViewById(R.id.teacherCollege);
        teacherUniversitySearch = findViewById(R.id.teacherUniversitySearch);
        teacherCollegeSearch = findViewById(R.id.teacherCollegeSearch);
        teacherDob = findViewById(R.id.teacherDob);
        teacherStreamSearch = findViewById(R.id.teacherStreamSearch);
        teacherStream = findViewById(R.id.teacherStream);
        teacherDepartmentSearch = findViewById(R.id.teacherDeptSearch);
        teacherDepartment = findViewById(R.id.teacherDept);
        teacherAadhaar = findViewById(R.id.teacherAadhaar);
        teacherEmail = findViewById(R.id.teacherEmail);
        teacherMobile = findViewById(R.id.teacherPhoneNumber);
        teacherUsername = findViewById(R.id.teacherUserid);
        teacherPassword = findViewById(R.id.teacherPassword);
        teacherConfirmPassword = findViewById(R.id.teacherConfirmPassword);
        teacherName = findViewById(R.id.teacherName);
        teacherAddress = findViewById(R.id.teacherAddress);
        aadharCheckBox = findViewById(R.id.teacherAadhaarCheckBox);
        phoneCheckBox = findViewById(R.id.teacherPhoneCheckBox);
        emailCheckBox = findViewById(R.id.teacherEmailCheckBox);
        loginCheckBox = findViewById(R.id.teacherVerifyUserIdCheckBox);

        teacherCollegeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teacherUniversity.getText().toString().isEmpty()) {
                    Toast.makeText(TeacherRegistration.this, "Enter University first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        teacherStreamSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teacherCollege.getText().toString().isEmpty()) {
                    Toast.makeText(TeacherRegistration.this, "Enter College first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        teacherDepartmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teacherStream.getText().toString().isEmpty()) {
                    Toast.makeText(TeacherRegistration.this, "Enter Stream first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                teacherDob.setText(Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherRegistration.this,
                dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    public void fetchUniversityList() {
        String url = domain + "/fetch/academic/university";
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "GET", "FetchUniversityList");
    }

    public void fetchColleges(String university) {
        String url = domain + "/fetch/academic/college";
        JSONObject json = new JSONObject();
        try {
            json.put("university", university);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "POST", "FetchCollegeList", json.toString());
    }

    public void fetchStreams(String college) {
        String url = domain + "/fetch/academic/stream";
        JSONObject json = new JSONObject();
        try {
            json.put("college", college);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "POST", "FetchStreamList", json.toString());
    }

    public void fetchDepartments(String stream) {
        String url = domain + "/fetch/academic/branch";
        JSONObject json = new JSONObject();
        try {
            json.put("stream", stream);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "POST", "FetchDepartmentList", json.toString());
    }

    public void verifyAadhaar(View view) {
        boolean isChecked = aadharCheckBox.isChecked();
        if (isChecked) {
            String aadhaar = teacherAadhaar.getText().toString();
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
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(url, "POST", "ValidAadhaar", json.toString());
            }
        }

    }



    public void uniqueAadhaar() {
        String aadhaar = teacherAadhaar.getText().toString();
        String url = domain + "/user/check/aadhaar/" + aadhaar;
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "POST", "UnregisteredAadhaar");
    }

    public void verifyEmail(View view) {
        boolean checked = emailCheckBox.isChecked();

        if (checked) {
            String email = teacherEmail.getText().toString();
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(domain + "/user/check/email/" + email, "POST", "VerifyEmail");
            }
            else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                emailCheckBox.setChecked(false);
            }
        }
    }

    public void verifyPhone(View view) {
        String phone = teacherMobile.getText().toString();
        if (!TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches() && phone.length() == 10) {
            ServerConnect serverConnect = new ServerConnect();
            serverConnect.execute(domain + "/user/check/phone/" + phone, "POST", "VerifyPhone");
        }
        else {
            Toast.makeText(this, "Please enter a valid Phone number", Toast.LENGTH_SHORT).show();
            phoneCheckBox.setChecked(false);
        }
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&*+=_!]).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public void teacherVerifyUserId(View view) {
        String loginId = teacherUsername.getText().toString();
        String password = teacherPassword.getText().toString();
        String confirmPassword = teacherConfirmPassword.getText().toString();


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

        else if (!isValidPassword(password)) {
            Toast.makeText(this, "Password should be atleast 8 characters long, alphanumeric and contain one special character", Toast.LENGTH_LONG).show();
            loginCheckBox.setChecked(false);
        }

        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm Password is not same as Password", Toast.LENGTH_SHORT).show();
            loginCheckBox.setChecked(false);
        }

        else {
            ServerConnect serverConnect = new ServerConnect();
            serverConnect.execute(domain + "/user/check/username/" + loginId, "POST", "VerifyUsername");
        }
    }

    public void onClickRegister(View view) {
        RadioButton teacherMaleButton = findViewById(R.id.teacherMaleButton);
        RadioButton teacherFemaleButton = findViewById(R.id.teacherFemaleButton);
        RadioButton emailOtp = findViewById(R.id.teacherEmailOtp);
        RadioButton phoneOtp = findViewById(R.id.teacherPhoneOtp);
        if (teacherName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Name", Toast.LENGTH_SHORT).show();
        }

        else if (teacherDob.getText().toString().equals("Date Of Birth")) {
            Toast.makeText(this, "Please select your Date of Birth", Toast.LENGTH_SHORT).show();
        }

        else if (!teacherMaleButton.isChecked() && !teacherFemaleButton.isChecked()) {
            Toast.makeText(this, "Please select your Gender", Toast.LENGTH_SHORT).show();
        }

        else if (teacherAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Address", Toast.LENGTH_SHORT).show();
        }

        else if (teacherUniversity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select your University", Toast.LENGTH_SHORT).show();
        }

        else if (teacherCollege.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select your College", Toast.LENGTH_SHORT).show();
        }

        else if (teacherStream.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select your Stream", Toast.LENGTH_SHORT).show();
        }

        else if (teacherDepartment.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select your Department", Toast.LENGTH_SHORT).show();
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
            if (teacherMaleButton.isChecked()) {
                gender = "Male";
            }
            else {
                gender = "Female";
            }

            if (emailOtp.isChecked()) {
                url = domain + "/user/email/sendVerification/" + teacherEmail.getText().toString();
                otpType = "email";
            }
            else {
                url = domain + "/user/mobile/sendVerification/" + teacherMobile.getText().toString();
                otpType = "phone";
            }

            final ServerConnect serverConnect = new ServerConnect();
            serverConnect.execute(url, "POST", "SendOtp");
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
                            url = domain + "/user/verify/" + teacherEmail.getText().toString();
                        } else {
                            url = domain + "/user/mobile/verify/" + teacherMobile.getText().toString();
                        }
                        ServerConnect serverConnect = new ServerConnect();
                        serverConnect.execute(url, "POST", "VerifyOtp", json.toString());
                    }
                })
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                // Resend OTP
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    String url = "", json = "";
                    JSONObject jsonObject = new JSONObject();

                    @Override
                    public void onClick(View view) {
                        if (otpType.equals("email")) {
                            url = domain + "/user/email/sendVerification/" + teacherEmail.getText().toString();
                            try {
                                jsonObject.put("resend", true);
                                json = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            url = domain + "/user/mobile/sendVerification/" + teacherMobile.getText().toString();
                            try {
                                jsonObject.put("resend", "message");
                                json = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ServerConnect serverConnect = new ServerConnect();
                        serverConnect.execute(url, "POST", "ResendOtp", json);
                    }
                });
            }
        });
        dialog.show();
    }

    public void finalSubmission() {
        JSONObject json = new JSONObject();
        String url = domain + "/user/registration/teacher/";
        try {
            json.put("aadhaar", teacherAadhaar.getText().toString());
            json.put("username", teacherUsername.getText().toString());
            json.put("password", teacherPassword.getText().toString());
            json.put("name", teacherName.getText().toString());
            json.put("college", teacherCollege.getText().toString());
            json.put("department", teacherDepartment.getText().toString());
            json.put("gender", gender);
            json.put("dob", teacherDob.getText().toString());
            json.put("email", teacherEmail.getText().toString());
            json.put("phone", teacherMobile.getText().toString());
            json.put("address", teacherAddress.getText().toString());
            json.put("university", teacherUniversity.getText().toString());
            json.put("stream", teacherStream.getText().toString());
            Log.e("json", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "POST", "FinalRegistration", json.toString());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_teacher_registration);
        initialize();
        fetchUniversityList();
    }

    public void serverError() {
        Toast.makeText(TeacherRegistration.this, "Server Error", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    // Responsible for communicating with server
    class ServerConnect extends AsyncTask<String, Void, String> {
        private String operation = "";
        private ProgressBar busyIndicator = findViewById(R.id.teacherRegistrationBusy);
        @Override
        protected void onPreExecute() {
            busyIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            operation = strings[2];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(strings[0]);
                String requestMethod = strings[1], response = "";
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod(requestMethod);
                httpURLConnection.connect();

                if (strings.length > 3) {
                    // Sending login credentials
                    String json = strings[3];
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    outputStreamWriter.write(json);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                }

                // Receiving response from server
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    StringBuffer stringBuffer = new StringBuffer();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuffer.append(response);
                    }
                    bufferedReader.close();
                    response = stringBuffer.toString();
                    return ("200" + response);
                }

                else {
                    return Integer.toString(responseCode);
                }

            } catch (Exception e) {
                return "Server Error";
            }

        }

        @Override
        protected void onPostExecute(String response) {
            busyIndicator.setVisibility(View.INVISIBLE);
            String responseCode = response.substring(0, 3);
            
           switch(operation) {

               case "FetchUniversityList":
                   switch(responseCode) {
                       case "200":
                           JSONObject json;
                           try {
                               json = new JSONObject(response.substring(3));
                               final JSONArray jsonArray = json.getJSONArray("universityName");
                               universities = new String[jsonArray.length()];

                               for (int i = 0; i < jsonArray.length(); i++) {
                                   universities[i] = jsonArray.getString(i);
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                           ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                   TeacherRegistration.this, android.R.layout.simple_list_item_1, universities);

                           AutoCompleteTextView autoCompleteTextView = findViewById(R.id.teacherUniversitySearch);
                           autoCompleteTextView.setThreshold(1);
                           autoCompleteTextView.setAdapter(adapter);
                           autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                               @Override
                               public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                   teacherUniversity.setVisibility(View.VISIBLE);
                                   String selected = (String) adapterView.getItemAtPosition(position);
                                   teacherUniversity.setText(selected);
                                   teacherCollegeSearch.setText("");
                                   teacherCollege.setText("");
                                   teacherStreamSearch.setText("");
                                   teacherStream.setText("");
                                   teacherDepartmentSearch.setText("");
                                   teacherDepartment.setText("");
                                   fetchColleges(selected);
                               }
                           });
                           break;
                       default:
                           serverError();
                           break;
                   }
                   break;



               case "FetchCollegeList":
                   switch (responseCode) {
                       case "200":
                           JSONObject json;
                           try {
                               json = new JSONObject(response.substring(3));
                               final JSONArray jsonArray = json.getJSONArray("collegeName");
                               colleges = new String[jsonArray.length()];

                               for (int i = 0; i < jsonArray.length(); i++) {
                                   colleges[i] = jsonArray.getString(i);

                               }
                               ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                       TeacherRegistration.this, android.R.layout.simple_list_item_1, colleges);
                               AutoCompleteTextView autoCompleteTextView = findViewById(R.id.teacherCollegeSearch);
                               autoCompleteTextView.setThreshold(1);
                               autoCompleteTextView.setAdapter(adapter);
                               autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                       teacherCollege.setVisibility(View.VISIBLE);
                                       String selected = (String) adapterView.getItemAtPosition(position);
                                       teacherCollege.setText(selected);
                                       teacherStreamSearch.setText("");
                                       teacherStream.setText("");
                                       teacherDepartmentSearch.setText("");
                                       teacherDepartment.setText("");
                                       fetchStreams(selected);
                                   }
                               });

                           } catch (Exception e) {
                               Log.e("Error", e.getMessage());
                           }

                           break;

                       default:
                           serverError();
                           break;
                   }
                   break;




               case "FetchStreamList":
                   switch (responseCode) {
                       case "200":
                           JSONObject json;
                           try {
                               json = new JSONObject(response.substring(3));
                               final JSONArray jsonArray = json.getJSONArray("stream");
                               streams = new String[jsonArray.length()];

                               for (int i = 0; i < jsonArray.length(); i++) {
                                   streams[i] = jsonArray.getString(i);
                               }
                               ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                       TeacherRegistration.this, android.R.layout.simple_list_item_1, streams);
                               AutoCompleteTextView autoCompleteTextView = findViewById(R.id.teacherStreamSearch);
                               autoCompleteTextView.setThreshold(1);
                               autoCompleteTextView.setAdapter(adapter);
                               autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                       teacherStream.setVisibility(View.VISIBLE);
                                       String selected = (String) adapterView.getItemAtPosition(position);
                                       teacherStream.setText(selected);
                                       teacherDepartmentSearch.setText("");
                                       teacherDepartment.setText("");
                                       fetchDepartments(selected);
                                   }
                               });

                           } catch (Exception e) {
                               Log.e("Error", e.getMessage());
                               e.printStackTrace();
                           }

                           break;

                       default:
                           serverError();
                           break;
                   }
                   break;


               case "FetchDepartmentList":
                   switch (responseCode) {
                       case "200":
                           JSONObject json;
                           try {
                               json = new JSONObject(response.substring(3));
                               final JSONArray jsonArray = json.getJSONArray("branch");
                               departments = new String[jsonArray.length()];

                               for (int i = 0; i < jsonArray.length(); i++) {
                                   departments[i] = jsonArray.getString(i);
                               }
                               ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                       TeacherRegistration.this, android.R.layout.simple_list_item_1, departments);
                               AutoCompleteTextView autoCompleteTextView = findViewById(R.id.teacherDeptSearch);
                               autoCompleteTextView.setThreshold(1);
                               autoCompleteTextView.setAdapter(adapter);
                               autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                       teacherDepartment.setVisibility(View.VISIBLE);
                                       String selected = (String) adapterView.getItemAtPosition(position);
                                       teacherDepartment.setText(selected);
                                   }
                               });

                           } catch (Exception e) {
                               Log.e("Error", e.getMessage());
                               e.printStackTrace();
                           }

                           break;

                       default:
                           serverError();
                           break;
                   }
                   break;



               case "ValidAadhaar":
                   switch(responseCode) {
                       case "200":
                           uniqueAadhaar();
                           break;

                       case "400":
                           Toast.makeText(TeacherRegistration.this, "This aadhaar number is not valid!", Toast.LENGTH_SHORT).show();
                            aadharCheckBox.setChecked(false);
                           break;

                       default:
                           aadharCheckBox.setChecked(false);
                           serverError();
                           break;
                   }
                   break;


               case "UnregisteredAadhaar":
                   switch (responseCode) {
                       case "200":
                           aadharCheckBox.setEnabled(false);
                           teacherAadhaar.setEnabled(false);
                           break;

                       case "400":
                           Toast.makeText(TeacherRegistration.this, "This aadhaar number is already registered!", Toast.LENGTH_SHORT).show();
                           aadharCheckBox.setChecked(false);
                           break;

                       default:
                           aadharCheckBox.setChecked(false);
                           serverError();
                           break;
                   }
                   break;

               case "VerifyEmail":
                   switch (responseCode) {
                       case "200":
                           emailCheckBox.setEnabled(false);
                           teacherEmail.setEnabled(false);
                           break;

                       case "400":
                           Toast.makeText(TeacherRegistration.this, "This email is already registered!", Toast.LENGTH_SHORT).show();
                           emailCheckBox.setChecked(false);
                           break;

                       default:
                           emailCheckBox.setChecked(false);
                           serverError();
                           break;
                   }
                   break;


               case "VerifyPhone":
                   switch (responseCode) {
                       case "200":
                           phoneCheckBox.setEnabled(false);
                           teacherMobile.setEnabled(false);
                           break;

                       case "400":
                           Toast.makeText(TeacherRegistration.this, "This Phone number is already registered!", Toast.LENGTH_SHORT).show();
                           phoneCheckBox.setChecked(false);
                           break;

                       default:
                           phoneCheckBox.setChecked(false);
                           serverError();
                           break;
                   }
                   break;


               case "VerifyUsername":
                   switch (responseCode) {
                       case "200":
                           loginCheckBox.setEnabled(false);
                           teacherUsername.setEnabled(false);
                           teacherPassword.setEnabled(false);
                           teacherConfirmPassword.setEnabled(false);
                           break;

                       case "400":
                           Toast.makeText(TeacherRegistration.this, "This UserID is already registered!", Toast.LENGTH_SHORT).show();
                           loginCheckBox.setChecked(false);
                           break;

                       default:
                           loginCheckBox.setChecked(false);
                           serverError();
                           break;
                   }
                   break;

               case "SendOtp":
                   switch (responseCode) {
                       case "200":
                           sendOTP(otpType);
                           break;
                       case "400":
                           Toast.makeText(TeacherRegistration.this, "You have currently requested for an email verification! Try again after 5 minutes.", Toast.LENGTH_LONG).show();
                           break;
                       default:
                           serverError();
                           break;
                   }
                   break;

               case "VerifyOtp":
                   switch (responseCode) {
                       case "200":
                           finalSubmission();
                           break;
                       case "400":
                           Toast.makeText(TeacherRegistration.this, "Wrong OTP entered", Toast.LENGTH_SHORT).show();
                           break;
                       default:
                           serverError();
                           break;
                   }
                   break;



               case "ResendOtp":
                   if (responseCode.equals("200")) {
                       Toast.makeText(TeacherRegistration.this, "OTP Resent", Toast.LENGTH_SHORT).show();
                   }

                   else {
                       serverError();
                   }
                   break;

               case "FinalRegistration": Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   switch(responseCode) {
                       case "200":
                           Toast.makeText(TeacherRegistration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                           startActivity(intent);
                           break;

                       default:
                           serverError();
                           break;
                   }
                   break;
           }

        }
    }
}
