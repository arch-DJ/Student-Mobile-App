package com.slashroot.studentmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Map;

public class StudentRegistration extends AppCompatActivity {
    private static String name, roll, college, email, phone, branch, dob, address, gender;

    void getStudentData() {
        Intent intent = getIntent();
        String studentData = intent.getStringExtra("Student Data");
        Map studentDataMap = new Gson().fromJson(studentData, Map.class);
        Log.e("Data", studentData);
        Log.e("Aadhaar", studentDataMap.get("aadharnumber").toString());
        name = studentDataMap.get("name").toString();
        roll = studentDataMap.get("rollnumber").toString();
        college = studentDataMap.get("college").toString();
        email = studentDataMap.get("email").toString();
        phone = studentDataMap.get("phone").toString();
        branch = studentDataMap.get("branch").toString();
        gender = studentDataMap.get("gender").toString();
        dob = studentDataMap.get("dob").toString();
        address = studentDataMap.get("address").toString();
        TextView nameTextView = findViewById(R.id.studentName);
        TextView collegeTextView = findViewById(R.id.collegeName);
        TextView rollTextView = findViewById(R.id.roll);
        TextView emailTextView = findViewById(R.id.email);
        TextView phoneTextView = findViewById(R.id.phoneNumber);
        TextView dobTextView = findViewById(R.id.dob);
        TextView genderTextView = findViewById(R.id.gender);
        TextView branchTextView = findViewById(R.id.branch);
        TextView addressTextView = findViewById(R.id.address);
        Log.e("Phone", phone);
        nameTextView.setText("Name - " + name);
        collegeTextView.setText("College - " + college);
        rollTextView.setText("Roll - " + roll);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        dobTextView.setText("Date of Birth - " + dob);
        addressTextView.setText("Address - " + address);
        genderTextView.setText("Gender - " + gender);
        branchTextView.setText("Branch - " + branch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_student_registration);
        getStudentData();
    }
}
