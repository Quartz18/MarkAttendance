package com.example1.markattendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowingStatisticsAdapter extends RecyclerView.Adapter<ShowingStatisticsAdapter.ViewHolder> {

    ArrayList<Model_Statistics> item_List;
    ShowingStatistics showingStatistics;

    public ShowingStatisticsAdapter(ArrayList<Model_Statistics> item_List, ShowingStatistics showingStatistics) {
        this.item_List = item_List;
        this.showingStatistics = showingStatistics;

    }

    @NonNull
    @Override
    public ShowingStatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showing_statistics_items,parent,false);
        ShowingStatisticsAdapter.ViewHolder viewHolder = new ShowingStatisticsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowingStatisticsAdapter.ViewHolder holder, final int position) {

        holder.statistics_member_id.setText(String.valueOf(item_List.get(position).getMember_id()));
        holder.statistics_member_name.setText(item_List.get(position).getMember_name());
        holder.statistics_percentage.setText(item_List.get(position).getMember_ratio());
        holder.statistics_member_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("document_name",item_List.get(position).getDocument_name());
                args.putString("class_name",item_List.get(position).getClass_name());
                args.putString("subject_name",item_List.get(position).getSubject_name());
                args.putString("member_name",item_List.get(position).getMember_name());
                GraphBottomSheet graphBottomSheet = new GraphBottomSheet();
                graphBottomSheet.setArguments(args);
                graphBottomSheet.show(showingStatistics.getSupportFragmentManager(),"graphsheet");
//                Intent new_intent = new Intent(showingStatistics.getApplicationContext(),GraphBottomSheet.class);
//                new_intent.putExtra("document_name",item_List.get(position).getDocument_name());
//                new_intent.putExtra("class_name",item_List.get(position).getClass_name());
//                new_intent.putExtra("member_id",item_List.get(position).getMember_id());
//                new_intent.putExtra("member_name",item_List.get(position).getMember_name());
//                showingStatistics.getApplicationContext().startActivity(new_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView statistics_member_id,statistics_member_name,statistics_percentage;
        View statistics_member_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statistics_member_id = itemView.findViewById(R.id.statistics_member_id);
            statistics_member_name = itemView.findViewById(R.id.statistics_member_name);
            statistics_percentage = itemView.findViewById(R.id.statistics_percentage);
            statistics_member_item = itemView.findViewById(R.id.statistics_member_item_view);
        }
    }
}
