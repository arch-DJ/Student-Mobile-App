package com.slashroot.studentmobileapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class TeacherRegistration extends AppCompatActivity {
    String domain = "https://lit-springs-26930.herokuapp.com";
    String[] universities, colleges;
    TextView teacherUniversity, teacherCollege, teacherDob;
    AutoCompleteTextView teacherUniversitySearch, teacherCollegeSearch;

    public void initialize() {
        teacherUniversity = findViewById(R.id.teacherUniversity);
        teacherCollege = findViewById(R.id.teacherCollege);
        teacherUniversitySearch = findViewById(R.id.teacherUniversitySearch);
        teacherCollegeSearch = findViewById(R.id.teacherCollegeSearch);
        teacherDob = findViewById(R.id.teacherDob);
        teacherCollegeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teacherUniversity.getText().toString().isEmpty()) {
                    Toast.makeText(TeacherRegistration.this, "Enter University's name first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                                   teacherUniversity.setText("University - " +  selected);
                                   teacherCollegeSearch.setText("");
                                   teacherCollege.setText("");
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
                                       teacherCollege.setText("College - " +  selected);
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
           }

        }
    }
}
