package com.slashroot.studentmobileapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParentRegistration extends AppCompatActivity {
    private TextView emailTextView,aadhaarTextView;
    private String email,aadhaar,domain = "https://lit-springs-26930.herokuapp.com";
    public void verifyEmail(View view) {
       boolean checked = ((CheckBox) view).isChecked();
       emailTextView = findViewById(R.id.parentEmail);
       if(checked){
           email = emailTextView.getText().toString();
           if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
               if(!((CheckBox)findViewById(R.id.parentAadhaarCheckBox)).isChecked()) {
                   Toast.makeText(ParentRegistration.this, "Please verify your aadhaar first", Toast.LENGTH_SHORT).show();
               }else{
                   ServerConnect serverConnect = new ServerConnect();
                   serverConnect.execute(domain + "/user/check/email/" + email, "emailVerification");
               }
           }

       }
    }
    public void verifyAadhaar(View view){
        boolean checked = ((CheckBox) view).isChecked();
        aadhaarTextView = findViewById(R.id.parentAadhaar);
        if(checked){
            aadhaar = aadhaarTextView.getText().toString();
            if(aadhaar != ""){
                JSONObject json = new JSONObject();
                try{
                    json.put("aadhaarNumber",aadhaar);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                ServerConnect serverConnect = new ServerConnect();
                serverConnect.execute(domain + "/user/aadhar/verification","aadhaarVerification",json.toString());
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_parent_registration);
        EditText email = findViewById(R.id.parentEmail);

    }

    public class ServerConnect extends AsyncTask<String,Void,String>{
        private ProgressDialog dialog;
        private String operation = "";
        private ProgressBar busyIndicator = findViewById(R.id.parentRegistrationBusy);

        protected void onPreExecute(){
            busyIndicator.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... strings){
            URL url;
            operation = strings[1];
            HttpURLConnection urlConnection;
            try{
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                if(strings.length == 3){
                    String json = strings[2];
                    OutputStream outputStream = urlConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    outputStreamWriter.write(json);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                }

                int responseCode = urlConnection.getResponseCode();
                if(responseCode == 200){
                    return("200");
                }
                else{
                    return Integer.toString(responseCode);
                }
            }catch (Exception e){
                Log.e("Exception", e.getMessage());
                return "Network Failure";
            }
        }

        protected void onPostExecute(String response){
            busyIndicator.setVisibility(View.INVISIBLE);
            String responseCode = response.substring(0, 3);
            switch(operation){
                case "aadhaarVerification":
                    if(responseCode.equals("200")){
                        Toast.makeText(ParentRegistration.this, "Aadhaar Verified Successfully", Toast.LENGTH_SHORT).show();
                        ServerConnect serverConnect = new ServerConnect();
                        serverConnect.execute(domain + "/user/check/aadhaar/" + aadhaarTextView.getText().toString(),"aadhaarUniqueness");
                        //aadhaarTextView.setEnabled(false);
                        //
                    }else{
                        Toast.makeText(ParentRegistration.this, "Invalid Aadhaar number", Toast.LENGTH_SHORT).show();
                        ((CheckBox)findViewById(R.id.parentAadhaarCheckBox)).setChecked(false);
                    }
                    break;
                case "aadhaarUniqueness":
                    if(responseCode.equals("200")){
                        aadhaarTextView.setEnabled(false);
                        ((CheckBox)findViewById(R.id.parentAadhaarCheckBox)).setEnabled(false);
                        Toast.makeText(ParentRegistration.this, "Aadhaar is available", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ParentRegistration.this, "Aadhaar is unavailable", Toast.LENGTH_SHORT).show();
                        ((CheckBox)findViewById(R.id.parentAadhaarCheckBox)).setChecked(false);
                    }
                    break;
                case "emailVerification":
                    if(responseCode.equals("200")){
                        Toast.makeText(ParentRegistration.this, "Email is available", Toast.LENGTH_SHORT).show();
                        emailTextView.setEnabled(false);
                        ((CheckBox)findViewById(R.id.parentEmailCheckBox)).setEnabled(false);
                    }else{
                        Toast.makeText(ParentRegistration.this, responseCode, Toast.LENGTH_SHORT).show();

                    }


            }
        }
    }
}
