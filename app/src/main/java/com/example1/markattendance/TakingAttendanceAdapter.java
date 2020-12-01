package com.example1.markattendance;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TakingAttendanceAdapter extends RecyclerView.Adapter<TakingAttendanceAdapter.ViewHolder> {

    ArrayList<Model_Attendance> item_List;
    ArrayList<String> selectedValues;
    TakingAttendance takingAttendance;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    String userID,document_name;

    public TakingAttendanceAdapter(ArrayList<Model_Attendance> item_List, TakingAttendance takingAttendance) {
        this.item_List = item_List;
        this.takingAttendance = takingAttendance;

    }

    @NonNull
    @Override
    public TakingAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taking_attendance_item,parent,false);
        TakingAttendanceAdapter.ViewHolder viewHolder = new TakingAttendanceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TakingAttendanceAdapter.ViewHolder holder, final int position) {

        setUpFirebase();
        if (item_List.get(position).getFound() == 1){
            holder.item_list5.setBackgroundResource(R.drawable.item_colour_1);
        }
        else if (item_List.get(position).getFound() == 2){
            holder.item_list5.setBackgroundResource(R.drawable.item_colour_2);
        }
        else {
            holder.item_list5.setBackgroundResource(R.drawable.item_colour_3);
        }
        selectedValues = new ArrayList<String>();
        holder.attendance_member_id.setText(String.valueOf(item_List.get(position).getAttendee_id()));
        holder.getAttendance_member_name.setText(item_List.get(position).getAttendee_name());
        if (item_List.get(position).getAttendance_select_all()){
            select_all_saved(holder,position);
        }
        holder.attendance_member_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.attendance_member_checkbox.isChecked()){
                    selectedValues.add(String.valueOf(item_List.get(position).getAttendee_id()));
                }
                else {
                    selectedValues.remove(String.valueOf(item_List.get(position).getAttendee_id()));
                }
                Log.d("Selection",selectedValues.toString());
            }
        });

    }

    private void select_all_saved(TakingAttendanceAdapter.ViewHolder holder, int position){
        holder.attendance_member_checkbox.setChecked(true);
        if (holder.attendance_member_checkbox.isChecked()){
            selectedValues.clear();
            selectedValues.addAll(item_List.get(position).getAttendee_details());
        }
        else {
            selectedValues.clear();
        }
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

    public ArrayList<String> listOfSelectedValues(){
        return selectedValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView attendance_member_id,getAttendance_member_name;
        CheckBox attendance_member_checkbox;
        View item_list5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attendance_member_id = itemView.findViewById(R.id.attendance_member_id);
            getAttendance_member_name = itemView.findViewById(R.id.attendance_member_name);
            attendance_member_checkbox = itemView.findViewById(R.id.attendance_member_checkbox);
            item_list5 = itemView.findViewById(R.id.item_list5);

        }
    }
}