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

public class StudentMainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu,menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logout:
                Log.i("Tapped","Log out");
                return true;
            default:
                return false;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_student_main);

        final ListView studentListView = findViewById(R.id.listView);
        final ArrayList<String> studentList = new ArrayList<>();

        studentList.add("View Attendance");

        ArrayAdapter<String>ap = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,studentList);

        studentListView.setAdapter(ap);

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("Tapped",studentList.get(position));
                switch (studentList.get(position)){
                    case "View Attendance":
                    {
                        Intent attendanceIntent = new Intent(view.getContext(),StudentAttendance.class);
                        startActivity(attendanceIntent);
                    }
                    default:
                        break;
                }

            }
        });


    }
}
