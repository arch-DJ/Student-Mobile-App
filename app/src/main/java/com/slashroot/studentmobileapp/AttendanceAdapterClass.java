package com.slashroot.studentmobileapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harsh on 25/1/18.
 */

public class AttendanceAdapterClass extends RecyclerView.Adapter<AttendanceAdapterClass.ViewHolder> {
    private List<SubjectAttendance> subjectList;
    private Context context;

    public AttendanceAdapterClass(List<SubjectAttendance> subjectList, Context context) {
        this.subjectList = subjectList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_atendance_recyler_layout,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        SubjectAttendance listItem=subjectList.get(position);
        holder.subjectName.setText(listItem.getSubjectName());
    }

    @Override
    public int getItemCount()
    {
        return subjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
           public TextView subjectName;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectName=itemView.findViewById(R.id.subjectName);
        }
    }
}
