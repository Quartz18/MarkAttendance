package com.example1.markattendance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SubjectAttendListAdapter extends RecyclerView.Adapter<SubjectAttendListAdapter.ViewHolder> {
    ArrayList<Model_Batch> item_List;
    SubjectAttendList subjectAttendList;
    FirebaseAuth mAuth;
    String userID;

    public SubjectAttendListAdapter(ArrayList<Model_Batch> item_List, SubjectAttendList subjectAttendList){
        this.subjectAttendList = subjectAttendList;
        this.item_List = item_List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_attend_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.attendance_subject_id.setText(item_List.get(position).getTotal_member());
        holder.attendance_subject_name_item.setText(item_List.get(position).getBatch_name());
        holder.attendance_subject_name_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(subjectAttendList.getApplicationContext(),TakingAttendance.class);
                intent.putExtra("document_name",item_List.get(position).getCount_of_subjects());
                intent.putExtra("subject_name",item_List.get(position).getBatch_name());
                openSubjectList(position);
            }
        });


    }

    private void openSubjectList(int position){
        setUpFirebase();
        Context context = subjectAttendList.getApplicationContext();
        String document_name = item_List.get(position).getCount_of_subjects()+" "+userID;
        String subject_name = item_List.get(position).getBatch_name();
        Intent new_intent = new Intent(context,TakingAttendance.class);
        new_intent.putExtra("document_name",document_name);
        new_intent.putExtra("subject_name",subject_name);
        subjectAttendList.startActivity(new_intent);

    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

    }

    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView attendance_subject_id;
        TextView attendance_subject_name_item;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            attendance_subject_id = itemView.findViewById(R.id.attendance_subject_id);
            attendance_subject_name_item = itemView.findViewById(R.id.attendance_subject_name_item);
        }
    }

}