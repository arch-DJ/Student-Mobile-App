package com.slashroot.studentmobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
            LoginAttempt loginAttempt = new LoginAttempt();
            try {
                String response = loginAttempt.execute(serverURL, username, password).get();
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Handle signup
    public void onClickSignup(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);
    }

    public class LoginAttempt extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            try {
                // Opening Connection
                JSONObject json = new JSONObject();
                json.put("username", strings[1]);
                json.put("password", strings[2]);
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Sending login credentials
                OutputStream outputStream = urlConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(json.toString());
                outputStreamWriter.flush();
                outputStreamWriter.close();

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
                    return "Login successful!";
                }

                else {
                    return "Login failed! Wrong username or password";
                }

            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
                return "Network Failure";
            }
        }
    }
}
