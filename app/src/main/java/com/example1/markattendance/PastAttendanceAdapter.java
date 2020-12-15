package com.example1.markattendance;

import android.content.Intent;
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

public class PastAttendanceAdapter extends RecyclerView.Adapter<PastAttendanceAdapter.ViewHolder> {

    ArrayList<Model_Past_Records> item_List;
    ArrayList<String> selectedValues;
    Past_Attendance past_attendance;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    int colour =R.color.blue;
    String userID;

    public PastAttendanceAdapter(ArrayList<Model_Past_Records> item_List, Past_Attendance past_attendance) {
        this.item_List = item_List;
        this.past_attendance = past_attendance;

    }

    @NonNull
    @Override
    public PastAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_records_items,parent,false);
        PastAttendanceAdapter.ViewHolder viewHolder = new PastAttendanceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PastAttendanceAdapter.ViewHolder holder, final int position) {

        setUpFirebase();
        if (item_List.get(position).getFound() == 1){
            holder.past_records_go_to_details.setBackgroundResource(R.drawable.item_colour_1);
        }
        else if (item_List.get(position).getFound() == 2){
            holder.past_records_go_to_details.setBackgroundResource(R.drawable.item_colour_2);
        }
        else {
            holder.past_records_go_to_details.setBackgroundResource(R.drawable.item_colour_3);
        }
        selectedValues = new ArrayList<String>();
        holder.past_record_name.setText(item_List.get(position).getPast_record_name());
        holder.past_record_count.setText(item_List.get(position).getMember_id());
        holder.past_records_go_to_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item_List.get(position).getFound() == 1){
                    holder.past_records_go_to_details.setBackgroundColor(holder.past_records_go_to_details
                            .getContext().getResources()
                            .getColor(R.color.blue,null));
                }
                else if (item_List.get(position).getFound() == 2){
                    holder.past_records_go_to_details.setBackgroundColor(holder.past_records_go_to_details
                            .getContext().getResources()
                            .getColor(R.color.Green,null));
                }
                else {
                    holder.past_records_go_to_details.setBackgroundColor(holder.past_records_go_to_details
                            .getContext().getResources()
                            .getColor(R.color.yellow,null));
                }
                Intent intent_new = new Intent(past_attendance.getApplicationContext(), Past_Attendance_Details.class);
                intent_new.putExtra("document_name",item_List.get(position).getMember_name());
                intent_new.putExtra("record_name",item_List.get(position).getPast_record_name());
                past_attendance.startActivity(intent_new);
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

        TextView past_record_name,past_record_count;
        View past_records_go_to_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            past_records_go_to_details = itemView.findViewById(R.id.item_list6);
            past_record_count = itemView.findViewById(R.id.past_attendance_id);
            past_record_name = itemView.findViewById(R.id.past_attendance_name);

        }
    }
}