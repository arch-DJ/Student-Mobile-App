package com.slashroot.studentmobileapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    static String aadharNumber = "", domain = "https://lit-springs-26930.herokuapp.com";

    // Handle login
    public void onClickLogin(View view) {
        String url = domain + "/user/login";
        TextView usernameField = findViewById(R.id.loginId);
        String username = usernameField.getText().toString();
        TextView passwordField = findViewById(R.id.password);
        String password = passwordField.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your Username", Toast.LENGTH_SHORT).show();
        }

        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
        }

        else {
            ServerConnect serverConnect = new ServerConnect();
            JSONObject json = new JSONObject();
            try {
                json.put("username", username);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serverConnect.execute(url,"POST", "Login", json.toString());
        }
    }

    // Handle signup
    public void onClickSignup(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Please choose the user type");
        CharSequence[] options = {"Student", "Teacher", "Parent"};
        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int userNumber) {
                if (userNumber== 0) {
                    verifyAadhar();
                }

                else if (userNumber == 1) {
                    Intent intent = new Intent(getApplicationContext(), TeacherRegistration.class);
                    startActivity(intent);
                }

                else {
                    Intent intent = new Intent(getApplicationContext(), ParentRegistration.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void verifyAadhar() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please enter your Aadhar Number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your Aadhar Number to continue", Toast.LENGTH_SHORT).show();
                }
                else {
                    aadharNumber = input.getText().toString();
                    ServerConnect serverConnect = new ServerConnect();
                    serverConnect.execute(domain + "/user/check/aadhaar/" + aadharNumber, "POST", "UnregisteredAadhaar");
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(input);
        alertDialog.show();
    }

    public void getStudentDetailsFromAadhaar() {
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.execute(domain + "/user/fetchData/aadhaar/" + aadharNumber, "GET", "StudentRegistration");
    }

    public void onClickForgotPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);
        if (sharedPreferences.getString("userType", "").equals("student")) {
            Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
            startActivity(intent);
            finish();
        }

        else if (sharedPreferences.getString("userType", "").equals("teacher")) {
            Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
            startActivity(intent);
            finish();
        }

        else if (sharedPreferences.getString("userType", "").equals("parent")) {
            //Intent intent = new Intent(getApplicationContext(), ParentMainActivity.class);
            Toast.makeText(this, "Welcome " + sharedPreferences.getString("name", "") + "!", Toast.LENGTH_SHORT).show();
            //startActivity(intent);
            finish();
        }

    }

    public class ServerConnect extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        private String operation = "";

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            operation = strings[2];
            URL url;
            HttpURLConnection urlConnection;

            try {
                // Opening Connection
                url = new URL(strings[0]);
                String requestMethod = strings[1];
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod(requestMethod);
                urlConnection.connect();

                if (strings.length > 3) {
                    // Sending login credentials
                    String json = strings[3];
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
            String responseCode = response.substring(0, 3);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            switch (operation) {
                case "UnregisteredAadhaar":
                    switch (responseCode) {
                        case "200":
                            getStudentDetailsFromAadhaar();
                            break;

                        case "400":
                            Toast.makeText(MainActivity.this, "This aadhaar number is already registered!", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;



                case "StudentRegistration":
                    switch (responseCode) {
                        case "200":
                            Intent intent = new Intent(getApplicationContext(), StudentRegistration.class);
                            intent.putExtra("Student Data", response.substring(3));
                            intent.putExtra("Aadhar", aadharNumber);
                            startActivity(intent);
                            break;
                        case "400":
                            Toast.makeText(MainActivity.this, "Aadhaar number not found in Student Central Database", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;


                case "Login":
                    switch (responseCode) {
                        case "200":
                            String userType = "";
                            try {
                                JSONObject json = new JSONObject(response.substring(3));
                                userType = json.getString("usertype");
                                switch (userType) {
                                    case "student": {
                                        Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userType", userType);
                                        editor.putString("name", json.getString("name"));
                                        editor.putString("userName", json.getString("userName"));
                                        editor.putString("gender", json.getString("gender"));
                                        editor.putString("rollNumber", json.getString("rollNumber"));
                                        editor.putString("dob", json.getString("dob"));
                                        editor.putString("college", json.getString("college"));
                                        editor.putString("branch", json.getString("branch"));
                                        editor.putString("batch", json.getString("batch"));
                                        editor.putString("address", json.getString("address"));
                                        editor.putString("email", json.getString("email"));
                                        editor.putString("aadhaar", json.getString("aadhaar"));
                                        editor.putString("phone", json.getString("phone"));
                                        editor.apply();
                                        Toast.makeText(MainActivity.this, "Welcome " + json.getString("name") + "!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                    case "teacher": {
                                        Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userType", userType);
                                        editor.putString("name", json.getString("name"));
                                        editor.putString("userName", json.getString("userName"));
                                        editor.putString("gender", json.getString("gender"));
                                        editor.putString("dob", json.getString("dob"));
                                        editor.putString("college", json.getString("college"));
                                        editor.putString("department", json.getString("department"));
                                        editor.putString("address", json.getString("address"));
                                        editor.putString("email", json.getString("email"));
                                        editor.putString("aadhaar", json.getString("aadhaar"));
                                        editor.putString("phone", json.getString("phone"));
                                        editor.putString("stream", json.getString("stream"));
                                        editor.putString("university", json.getString("university"));
                                        editor.apply();
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                    default:
                                        //intent = new Intent(getApplicationContext(), ParentMainActivity.class);
                                        //startActivity(intent);
                                        finish();
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            break;

                        case "400":
                            Toast.makeText(MainActivity.this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

                default: break;

            }
        }
    }
}
