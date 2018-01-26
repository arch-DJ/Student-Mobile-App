package com.slashroot.studentmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TeacherMainActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_teacher_main);


        ListView teacherListView = findViewById(R.id.listView);
        final ArrayList<String> teacherList = new ArrayList<>();

        teacherList.add("Update Attendance");

        ArrayAdapter<String>ap = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,teacherList);
        teacherListView.setAdapter(ap);


        teacherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("Tapped",teacherList.get(position));
                switch (teacherList.get(position)){
                    case "Update Attendance":
                    {
                        Intent attendanceIntent = new Intent(view.getContext(),teacher_attendance.class);
                        startActivity(attendanceIntent);
                    }
                    default:
                        break;
                }

            }
        });

    }

}
