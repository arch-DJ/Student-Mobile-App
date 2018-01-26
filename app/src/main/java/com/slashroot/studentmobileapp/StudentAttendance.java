package com.slashroot.studentmobileapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StudentAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<SubjectAttendance> listOfSubjects;
    private static final String attendanceURL= "https://simplifiedcoding.net/demos/view-flipper/heroes.php";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        recyclerView =(RecyclerView) findViewById(R.id.studentAttendanceRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listOfSubjects=new ArrayList<>();

        loadRecyclerViewData();

        /*
        ListView subjectList=(ListView)findViewById(R.id.subjectList);

        JSONArray jArray = new JSONArray();
        JSONObject json_data;
        ArrayList<String> items = new ArrayList<String>();
        for(int i=0; i < jArray.length() ; i++)
        {
            try {
                json_data = jArray.getJSONObject(i);
                int id = json_data.getInt("id");
                String name = json_data.getString("name");
                items.add(name);
                Log.d(name,"Output");
            }catch(Exception e)
            {
                System.out.print(e.getStackTrace());
            }
        }

        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, items);
        subjectList.setAdapter(mArrayAdapter);*/





        return super.onCreateOptionsMenu(menu);
    }

    private void loadRecyclerViewData()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, attendanceURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.dismiss();
                        try
                        {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("heroes");

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject currentJSON=jsonArray.getJSONObject(i);
                                SubjectAttendance item=new SubjectAttendance(currentJSON.getString("name"));
                                listOfSubjects.add(item);
                            }

                            adapter = new AttendanceAdapterClass(listOfSubjects,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
            ,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Some Error Occured please Try Again",Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(),volleyError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }

        );

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
    }
}
