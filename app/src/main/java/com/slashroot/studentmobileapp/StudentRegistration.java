package com.slashroot.studentmobileapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegistration extends AppCompatActivity {
    // Declarations
    private static String name, roll, college, email, phone, branch, dob, address, batch, gender, aadhar, loginId,
            password, confirmPassword, domain = "https://lit-springs-26930.herokuapp.com", otpType;
    private TextView loginTextView, passwordTextView, confirmPasswordTextView, emailTextView, phoneTextView;
    private Button registerStudentButton;
    private CheckBox emailOtp, phoneOtp, usernameCheckBox;

    // Retrieve Student's Data from server
    void getStudentData() {
        Intent intent = getIntent();
        String studentData = intent.getStringExtra("Student Data");
        aadhar = intent.getStringExtra("Aadhar");
        Map studentDataMap = new Gson().fromJson(studentData, Map.class);
        name = studentDataMap.get("name").toString();
        roll = studentDataMap.get("rollnumber").toString();
        college = studentDataMap.get("college").toString();
        batch = studentDataMap.get("batch").toString();
        email = studentDataMap.get("email").toString();
        phone = studentDataMap.get("phone").toString();
        branch = studentDataMap.get("branch").toString();
        gender = studentDataMap.get("gender").toString();
        dob = studentDataMap.get("dob").toString();
        address = studentDataMap.get("address").toString();
        TextView nameTextView = findViewById(R.id.studentName);
        TextView collegeTextView = findViewById(R.id.studentCollegeName);
        TextView batchTextView = findViewById(R.id.studentBatch);
        TextView rollTextView = findViewById(R.id.studentRoll);
        TextView emailTextView = findViewById(R.id.studentEmail);
        TextView phoneTextView = findViewById(R.id.studentPhoneNumber);
        TextView dobTextView = findViewById(R.id.studentDob);
        TextView genderTextView = findViewById(R.id.studentGender);
        TextView branchTextView = findViewById(R.id.studentBranch);
        TextView addressTextView = findViewById(R.id.studentAddress);
        loginTextView = findViewById(R.id.studentLoginIdRegistration);
        passwordTextView = findViewById(R.id.studentPasswordRegistration);
        confirmPasswordTextView = findViewById(R.id.studentConfirmPasswordRegistration);
        registerStudentButton = findViewById(R.id.registerStudentButton);
        emailOtp = findViewById(R.id.studentRegistrationEmailOtpCheckbox);
        phoneOtp = findViewById(R.id.studentRegistrationPhoneOtpCheckbox);
        usernameCheckBox = findViewById(R.id.studentVerifyUserIdCheckBox);
        nameTextView.setText("Name - " + name);
        collegeTextView.setText("College - " + college);
        batchTextView.setText("Batch - " + batch);
        rollTextView.setText("Roll - " + roll);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        dobTextView.setText("Date of Birth - " + dob);
        addressTextView.setText("Address - " + address);
        genderTextView.setText("Gender - " + gender);
        branchTextView.setText("Branch - " + branch);
    }

    // Check email format and if it is already registered
    public void verifyEmail(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        emailTextView = findViewById(R.id.studentEmail);

        if (checked) {
            email = emailTextView.getText().toString();
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(domain + "/user/check/email/" + email, "VerifyEmail");
            }
            else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                ((CheckBox) view).setChecked(false);
            }
        }

        if (!checked) {
            emailTextView.setEnabled(true);
        }
    }

    // Check Mobile number format and if it is already registered
    public void verifyPhone(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        phoneTextView = findViewById(R.id.studentPhoneNumber);
        phone = phoneTextView.getText().toString();
        CheckBox emailCheckBox = findViewById(R.id.studentEmailCheckBox);

        if (checked && emailCheckBox.isChecked()) {
            if (!TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches() && phone.length() == 10) {
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(domain + "/user/check/phone/" + phone, "VerifyPhone");
            }

            else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                ((CheckBox) view).setChecked(false);
            }
        }

        else if (!emailCheckBox.isChecked()) {
            Toast.makeText(this, "Please confirm your email first", Toast.LENGTH_SHORT).show();
            ((CheckBox) view).setChecked(false);
        }

        else {
            phoneTextView.setEnabled(true);
        }
    }

    // Ensure password follows the given pattern
    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#*$%^&+=_!]).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    // Check if user id to be registered already exists
    public void studentVerifyUserId(View view) {
        CheckBox checkBox = findViewById(R.id.studentVerifyUserIdCheckBox);
        boolean isChecked = checkBox.isChecked();
        loginId = loginTextView.getText().toString();
        password = passwordTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();

        if (!isChecked) {
            loginTextView.setEnabled(true);
            passwordTextView.setEnabled(true);
            confirmPasswordTextView.setEnabled(true);
        }

        else {

            if (loginId.isEmpty()) {
                Toast.makeText(this, "Please enter login ID", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            }

            else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            }

            else if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            }

            else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password should be atleast 8 characters long, alphanumeric and contain one special character", Toast.LENGTH_LONG).show();
                checkBox.setChecked(false);
            }

            else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Confirm Password is not same as Password", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            }

            else {
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(domain + "/user/check/username/" + loginId, "VerifyUsername");
            }
        }
    }

    public void selectEmailOtp(View view) {
        boolean checked = emailOtp.isChecked();

        if (checked) {
            phoneOtp.setChecked(false);
        }
    }

    public void selectPhoneOtp(View view) {
        boolean checked = phoneOtp.isChecked();

        if (checked) {
            emailOtp.setChecked(false);
        }
    }


    public void onClickStudentRegister(View view) {
        if (!emailOtp.isChecked() && !phoneOtp.isChecked()) {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show();
        }

        else {
            String url;

            if (emailOtp.isChecked()) {
                url = domain + "/user/email/sendVerification/" + email;
                otpType = "email";
            }
            else {
                url = domain + "/user/mobile/sendVerification/" + phone;
                otpType = "phone";
            }

            final ServerConnect serverConnect = new ServerConnect();
            serverConnect.execute(url, "SendOtp");

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
                            url = domain + "/user/verify/" + email;
                        } else {
                            url = domain + "/user/mobile/verify/" + phone;
                        }
                        ServerConnect serverConnect = new ServerConnect();
                        serverConnect.execute(url, "VerifyOtp", json.toString());
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
                            url = domain + "/user/email/sendVerification/" + email;
                            try {
                                jsonObject.put("resend", true);
                                json = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            url = domain + "/user/mobile/sendVerification/" + phone;
                            try {
                                jsonObject.put("resend", "message");
                                json = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ServerConnect serverConnect = new ServerConnect();
                        serverConnect.execute(url, "ResendOtp", json);
                    }
                });
            }
        });
        dialog.show();
    }

    public void finalSubmission() {
        JSONObject json = new JSONObject();
        String url = domain + "/user/registration/student/";
        try {
            json.put("aadhaar", aadhar);
            json.put("username", loginId);
            json.put("password", password);
            json.put("name", name);
            json.put("branch", branch);
            json.put("college", college);
            json.put("batch", batch);
            json.put("dob", dob);
            json.put("email", email);
            json.put("gender", gender);
            json.put("phone", phone);
            json.put("rollNumber", roll);
            json.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(url, "FinalRegistration", json.toString());

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_student_registration);
        getStudentData();
    }

    // Responsible for connection to server, sending request with/without json, receiving response with/without json
    public class ServerConnect extends AsyncTask<String, Void, String> {
        private String operation = "";
        private ProgressBar busyIndicator = findViewById(R.id.studentRegistrationBusy);

        @Override
        protected void onPreExecute() {
            busyIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            operation = strings[1];
            HttpURLConnection urlConnection;
            try {
                // Opening Connection

                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();


                if (strings.length == 3) {
                    // Sending Data to Server
                    String json = strings[2];
                    OutputStream outputStream = urlConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    outputStreamWriter.write(json);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                }

                // Receiving response from server
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String response;
                    StringBuffer stringBuffer = new StringBuffer();
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
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
                Log.e("Exception", e.getMessage());
                return "Network Failure";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            busyIndicator.setVisibility(View.INVISIBLE);
            String responseCode = response.substring(0, 3);

            switch(operation) {
                case "VerifyEmail":
                    if (responseCode.equals("200")){
                        emailTextView.setEnabled(false);
                        ((CheckBox)findViewById(R.id.studentEmailCheckBox)).setEnabled(false);
                    }

                    else {
                        ((CheckBox)findViewById(R.id.studentEmailCheckBox)).setChecked(false);
                        Toast.makeText(StudentRegistration.this, "Entered email is already registered", Toast.LENGTH_SHORT).show();
                    }
                    break;




                case "VerifyPhone":
                    if (responseCode.equals("200")) {
                        phoneTextView.setEnabled(false);
                        loginTextView.setVisibility(View.VISIBLE);
                        passwordTextView.setVisibility(View.VISIBLE);
                        confirmPasswordTextView.setVisibility(View.VISIBLE);
                        ((CheckBox)findViewById(R.id.studentVerifyUserIdCheckBox)).setVisibility(View.VISIBLE);
                        ((CheckBox)findViewById(R.id.studentPhoneCheckBox)).setEnabled(false);
                    }
                    else {
                        ((CheckBox)findViewById(R.id.studentPhoneCheckBox)).setChecked(false);
                        Toast.makeText(StudentRegistration.this, "Entered phone number is already registered", Toast.LENGTH_SHORT).show();
                    }
                    break;



                case "VerifyUsername":
                    if (responseCode.equals("200")) {
                        loginTextView.setEnabled(false);
                        passwordTextView.setEnabled(false);
                        confirmPasswordTextView.setEnabled(false);
                        usernameCheckBox.setEnabled(false);
                        emailOtp.setVisibility(View.VISIBLE);
                        phoneOtp.setVisibility(View.VISIBLE);
                        registerStudentButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(StudentRegistration.this, "Username is not available!", Toast.LENGTH_SHORT).show();
                        usernameCheckBox.setChecked(false);
                    }
                    break;



                case "SendOtp":
                    switch (responseCode) {
                        case "200":
                            sendOTP(otpType);
                            break;
                        case "400":
                            Toast.makeText(StudentRegistration.this, "You have currently requested for an email verification! Try again after 5 minutes.", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(StudentRegistration.this, "Server Error. Please try again later.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;



                case "VerifyOtp":
                    switch (responseCode) {
                        case "200":
                            finalSubmission();
                            break;
                        case "400":
                            Toast.makeText(StudentRegistration.this, "Wrong OTP entered", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(StudentRegistration.this, "Server error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;



                case "ResendOtp":
                    if (responseCode.equals("200")) {
                        Toast.makeText(StudentRegistration.this, "OTP Resent", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(StudentRegistration.this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                    break;



                case "FinalRegistration": Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    switch(responseCode) {
                        case "200":
                            Toast.makeText(StudentRegistration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            break;

                        default:
                            Toast.makeText(StudentRegistration.this, "Server error", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            break;
                    }
                    break;

            }
        }
    }
}