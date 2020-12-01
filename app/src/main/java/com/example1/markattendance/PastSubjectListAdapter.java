package com.example1.markattendance;

import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PastSubjectListAdapter extends RecyclerView.Adapter<PastSubjectListAdapter.ViewHolder> {

    ArrayList<Model_Member> item_List;
    ArrayList<String> selectedValues;
    PastSubjectList pastSubjectList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    String userID,document_name;

    public PastSubjectListAdapter(ArrayList<Model_Member> item_List, PastSubjectList pastSubjectList){
        this.item_List = item_List;
        this.pastSubjectList = pastSubjectList;

    }
    @NonNull
    @Override
    public PastSubjectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_subject_list_item,parent,false);
        PastSubjectListAdapter.ViewHolder viewHolder = new PastSubjectListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PastSubjectListAdapter.ViewHolder holder, final int position) {
        setUpFirebase();
        if (item_List.get(position).getFound() == 1){
            holder.past_subject_item.setBackgroundResource(R.drawable.item_colour_1);
        }
        else if (item_List.get(position).getFound() == 2){
            holder.past_subject_item.setBackgroundResource(R.drawable.item_colour_2);
        }
        else {
            holder.past_subject_item.setBackgroundResource(R.drawable.item_colour_3);
        }
        holder.past_subject_id.setText(item_List.get(position).getMembers_number());
        holder.past_subject_name_item.setText(item_List.get(position).getMembers_name());
        holder.past_subject_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pastSubjectList.getApplicationContext(), ShowingStatistics.class);
                String document_name = item_List.get(position).getMembers_device()+" "+userID;
                String subject_name = item_List.get(position).getDocument_name();
                intent.putExtra("document_name",document_name);
                intent.putExtra("subject_name",subject_name);
                pastSubjectList.startActivity(intent);
            }
        });

    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView past_subject_id,past_subject_name_item;
        View past_subject_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            past_subject_id = itemView.findViewById(R.id.past_subject_id);
            past_subject_name_item = itemView.findViewById(R.id.past_subject_name_item);
            past_subject_item = itemView.findViewById(R.id.past_subject_item);
        }
    }
}
