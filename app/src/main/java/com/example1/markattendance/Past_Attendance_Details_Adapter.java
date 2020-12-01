package com.example1.markattendance;

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

public class Past_Attendance_Details_Adapter extends RecyclerView.Adapter<Past_Attendance_Details_Adapter.ViewHolder> {

    ArrayList<Model_Past_Records> item_List;
    ArrayList<String> selectedValues;
    Past_Attendance_Details past_attendance_details;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;

    public Past_Attendance_Details_Adapter(ArrayList<Model_Past_Records> item_List, Past_Attendance_Details past_attendance_details) {
        this.item_List = item_List;
        this.past_attendance_details = past_attendance_details;

    }

    @NonNull
    @Override
    public Past_Attendance_Details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taking_attendance_item,parent,false);
        Past_Attendance_Details_Adapter.ViewHolder viewHolder = new Past_Attendance_Details_Adapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Past_Attendance_Details_Adapter.ViewHolder holder, final int position) {

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
        final int member_id = Integer.valueOf(item_List.get(position).getMember_id());
        holder.attendance_member_id.setText(String.valueOf(item_List.get(position).getMember_id()));
        holder.getAttendance_member_name.setText(item_List.get(position).getMember_name());
        if (item_List.get(position).getMember_attended().equals("true")){
            holder.attendance_member_checkbox.setChecked(true);
        }
        else {
            holder.attendance_member_checkbox.setChecked(false);
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