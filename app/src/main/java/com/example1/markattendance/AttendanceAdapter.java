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
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    List<Model_Member> item_List;
    Context mContext;
    FirebaseAuth mAuth;
    String userID;

    public AttendanceAdapter(List<Model_Member> item_List, Context mContext) {
        this.item_List = item_List;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabs_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, final int position) {


        if (item_List.get(position).getFound() == 1){
            holder.go_to_tabs_items.setBackgroundResource(R.drawable.item_colour_1);

        }
        else if (item_List.get(position).getFound() == 2){
            holder.go_to_tabs_items.setBackgroundResource(R.drawable.item_colour_2);
        }
        else {
            holder.go_to_tabs_items.setBackgroundResource(R.drawable.item_colour_3);
        }
        holder.tab_name.setText(item_List.get(position).getMembers_number());
        holder.total_member.setText(item_List.get(position).getMembers_name());
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        holder.go_to_tabs_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item_List.get(position).getFound() == 1){
                    holder.go_to_tabs_items.setBackgroundColor(holder.go_to_tabs_items
                            .getContext().getResources()
                            .getColor(R.color.blue,null));
                }
                else if (item_List.get(position).getFound() == 2){
                    holder.go_to_tabs_items.setBackgroundColor(holder.go_to_tabs_items
                            .getContext().getResources()
                            .getColor(R.color.Green,null));
                }
                else {
                    holder.go_to_tabs_items.setBackgroundColor(holder.go_to_tabs_items
                            .getContext().getResources()
                            .getColor(R.color.yellow,null));
                }
                final int count_of_subjects = Integer.valueOf(item_List.get(position).getMembers_device());
                if (count_of_subjects>0){
                    openSubjectList(position);
                }else {

                    openStudentList(position);
                }
            }
        });

    }

    private void openSubjectList(int position){
        Intent intent = new Intent(mContext,SubjectAttendList.class);
        String document_name = item_List.get(position).getDocument_name();
        intent.putExtra("document_name",document_name);
        mContext.startActivity(intent);

    }
    private void openStudentList(int position){
        Intent intent = new Intent(mContext,TakingAttendance.class);
        String document_name = item_List.get(position).getDocument_name()+" "+userID;
        String subject_name = "None";
        intent.putExtra("document_name",document_name);
        intent.putExtra("subject_name",subject_name);
        mContext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tab_name;
        TextView total_member;
        View go_to_tabs_items;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tab_name = itemView.findViewById(R.id.tab_name);
            total_member = itemView.findViewById(R.id.total_member);
            go_to_tabs_items = itemView.findViewById(R.id.go_to_tabs_items);
        }
    }
}

