package com.slashroot.studentmobileapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    final String serverURL = "http://192.168.117.221:3000/enter/";

    // Open student Activity (Temp)
    public void launchStudentActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
        startActivity(intent);
    }

    // Handle login
    public void onClickLogin(View view) {
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
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password", password);
                String response = serverConnect.execute(serverURL, json.toString()).get();
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }

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

                }

                else {

                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void verifyAadhar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please enter your Aadhar Number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your Aadhar Number to continue", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        String aadharNumber = input.getText().toString();
                        ServerConnect serverConnect = new ServerConnect();
                        String response = serverConnect.execute("http://lit-springs-26930.herokuapp.com/user/fetchData/aadhaar/" + aadharNumber, "GET").get();
                        int responseCode = Integer.parseInt(response.substring(0,3));
                        if (responseCode == 200) {
                            Intent intent = new Intent(getApplicationContext(), StudentRegistration.class);
                            intent.putExtra("Student Data",response.substring(3));
                            startActivity(intent);
                        }

                        else {
                            Toast.makeText(MainActivity.this, "Aadhar number not found in Student Database", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);
    }

    public class ServerConnect extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Fetching Data from Server...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
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

                if (strings.length > 2) {
                    // Sending login credentials
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
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
