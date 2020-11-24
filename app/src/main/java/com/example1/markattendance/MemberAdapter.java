package com.example1.markattendance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    ArrayList<Model_Member> item_List;
    List<String> list_of_past_records=new ArrayList<>();
    AddMembers addMembers;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    String userID,document_name;
    String class_name;

    public MemberAdapter(ArrayList<Model_Member> item_List, AddMembers addMembers) {
        this.item_List = item_List;
        this.addMembers = addMembers;

    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_item,parent,false);
        MemberAdapter.ViewHolder viewHolder = new MemberAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberAdapter.ViewHolder holder, final int position) {

        setUpFirebase();

        holder.members_number.setText(String.valueOf(item_List.get(position).getMembers_number()));
        holder.members_name.setText(item_List.get(position).getMembers_name());
        holder.member_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display Menu Options
                PopupMenu popupMenu = new PopupMenu(addMembers.getBaseContext(),holder.member_option_menu);
                popupMenu.inflate(R.menu.member_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.edit_member:
                                Bundle args = new Bundle();
                                args.putString("id_member", item_List.get(position).getMembers_device());
                                args.putString("document_name", item_List.get(position).getDocument_name());
                                Edit_Member editMember = new Edit_Member();
                                editMember.setArguments(args);
                                editMember.show(addMembers.getSupportFragmentManager(),"addClassDialog");
                                break;
                            case R.id.delete_member:
//                                item_list.remove(position);
                                deleteSelectedRow(position);
//                                notifyDataSetChanged();
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

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    private void deleteSelectedRow(final int position){
        String id = item_List.get(position).getMembers_device();
        int x = item_List.get(position).document_name.indexOf(userID);
        class_name = item_List.get(position).document_name.substring(0,x-1);
        db.collection("users").document(userID).collection("Class_List").document(class_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String counter = document.getString("count_of_students");
                                int counting = Integer.valueOf(counter) - 1;
                                Map<String, Object> add_counter = new HashMap<>();
                                DocumentReference counter_list = db.collection("users").document(userID)
                                        .collection("Class_List").document(class_name);
                                add_counter.put("count_of_students",String.valueOf(counting));
                                counter_list.update(add_counter);
                                String past_records_string = document.getString("List");
                                String[] list1 = past_records_string.split("-");
                                list_of_past_records = Arrays.asList(list1);


                            } else {
                                Log.d("TAG", "No such document");
                            }
                        }
                    }
                });
        getpastlistdelete(position);

    }
    public void getpastlistdelete(int position){
        int q = 0;
        for (String tags: list_of_past_records){
            if (q == 0){
                q = 1;
                continue;
            }
            db.collection("Records").document(class_name)
                    .collection(tags).document(String.valueOf(item_List.get(position).getMembers_device()))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(addMembers.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        db.collection(addMembers.document_name).document(item_List.get(position).getMembers_device())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(addMembers.getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
//                            mbatch.loadDataFromFirebase();
                    }
                });
    }
    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView members_number;
        TextView members_name;
        public TextView member_option_menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            members_number = itemView.findViewById(R.id.members_number);
            members_name = itemView.findViewById(R.id.members_name);
            member_option_menu = itemView.findViewById(R.id.member_option_menu);
        }
    }
}
