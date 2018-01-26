package com.slashroot.studentmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<SubjectAttendance> listOfSubjects;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        recyclerView =(RecyclerView) findViewById(R.id.studentAttendanceRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listOfSubjects=new ArrayList<>();
        for(int i=0;i<=5;i++)
        {
            SubjectAttendance subject=new SubjectAttendance("MATHS"+i+1);
            listOfSubjects.add(subject);
        }
        adapter = new AttendanceAdapterClass(listOfSubjects,this);
        recyclerView.setAdapter(adapter);
        /*ListView subjectList=(ListView)findViewById(R.id.subjectList);

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
    }
}
