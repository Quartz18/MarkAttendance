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

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    List<Model_Batch> itemlist1;
    Context mContext;
    String userID;
    FirebaseAuth mAuth;

    public StatisticsAdapter(List<Model_Batch> itemlist, Context mContext) {
        this.itemlist1 = itemlist;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public StatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabs_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.ViewHolder holder, final int position) {

        holder.batch_name.setText(itemlist1.get(position).getBatch_name());
        holder.total_member.setText(itemlist1.get(position).getTotal_member());
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        holder.go_to_tabs_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int count_of_subjects = Integer.valueOf(itemlist1.get(position).getCount_of_subjects());
                if (count_of_subjects>0){
                    openSubjectList(position);
                }else {

                    openStudentList(position);
                }
            }
        });
    }

    private void openSubjectList(int position){
        Intent intent = new Intent(mContext,PastSubjectList.class);
        String document_name = itemlist1.get(position).getBatch_name();
        intent.putExtra("document_name",document_name);
        mContext.startActivity(intent);

    }
    private void openStudentList(int position){
        Intent intent = new Intent(mContext,ShowingStatistics.class);
        String document_name = itemlist1.get(position).getBatch_name()+" "+userID;
        String subject_name = "None";
        intent.putExtra("document_name",document_name);
        intent.putExtra("subject_name",subject_name);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return itemlist1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView batch_name;
        TextView total_member;
        View go_to_tabs_items;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batch_name = itemView.findViewById(R.id.batch_name);
            total_member = itemView.findViewById(R.id.total_member);
            go_to_tabs_items = itemView.findViewById(R.id.go_to_tabs_items);
        }
    }
}
