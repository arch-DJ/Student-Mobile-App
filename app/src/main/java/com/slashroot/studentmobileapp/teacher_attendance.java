package com.slashroot.studentmobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class teacher_attendance extends AppCompatActivity {

    final String serverURL = "http://192.168.117.34:3000/user/year";


    public void onClk(View view ){

        try{
            ServerConnect serverConnect = new ServerConnect();
            //JSONObject json = new JSONObject();
            String response = (serverConnect.execute(serverURL).get());
            String[] items = response.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

            int[] res= new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    res[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                };
            }

            Log.i("response",response);

        }
        catch(Exception e){

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu,menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_teacher_attendance);


    }
    public class ServerConnect extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.e("mes",strings[0]);
                urlConnection.connect();




                int responseCode = urlConnection.getResponseCode();
                Log.e("res",Integer.toString(responseCode));
                if(responseCode==200){
                    String response;
                    StringBuffer stringBuffer = new StringBuffer();
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuffer.append(response);
                    }
                    bufferedReader.close();
                    response = stringBuffer.toString();
                    JSONObject jsonObject = new JSONObject(response);

                    response = jsonObject.getString("data");

                    return (response);



                   }


            } catch (Exception e) {
                Log.e("Error",e.getMessage());
            }
            return null;
        }
    }
}
