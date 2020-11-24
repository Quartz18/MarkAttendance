package com.example1.markattendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolder>{
    List<Model_Batch> item_List;
    Subjects_List subjects_list;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    int f = 0,counting;
    String userID, class_name,subject_name;

    public SubjectListAdapter(List<Model_Batch> item_List, Subjects_List subjects_list) {
        this.item_List = item_List;
        this.subjects_list = subjects_list;

    }

    @NonNull
    @Override
    public SubjectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectListAdapter.ViewHolder holder, final int position) {
        setUpFirebase();
        holder.subject_name_item.setText(item_List.get(position).getBatch_name());
        holder.option_menu_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(subjects_list.getApplicationContext(),holder.option_menu_subject);
                popupMenu.inflate(R.menu.subject_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.edit_subject:
                                Bundle args = new Bundle();
                                args.putString("subject_name", item_List.get(position).getTotal_member());
                                args.putString("class_name", item_List.get(position).getCount_of_subjects());
                                Edit_Subject editSubject = new Edit_Subject();
                                editSubject.setArguments(args);
                                editSubject.show(subjects_list.getSupportFragmentManager(),"addClassDialog");
                                break;
                            case R.id.delete_subject:
                                deleteSelectedRow(position);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void deleteSelectedRow(final int position) {
        subject_name = item_List.get(position).getTotal_member();
        class_name = item_List.get(position).getCount_of_subjects();
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        counting = Integer.valueOf(documentSnapshot.getString("count_of_subjects")) - 1;
                        Map<String, Object> count_of_subjects_map = new HashMap<>();
                        DocumentReference count_of_subjects_dr = db.collection("users").document(userID)
                                .collection("Class_List").document(class_name);
                        count_of_subjects_map.put("count_of_subjects",String.valueOf(counting));
                        count_of_subjects_dr.update(count_of_subjects_map);
                    }
                });
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects").document(subject_name)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(subjects_list.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
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

        TextView subject_name_item;
        public TextView option_menu_subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subject_name_item = itemView.findViewById(R.id.suject_name_item);
            option_menu_subject = itemView.findViewById(R.id.option_menu_subject);
        }
    }
}
