package com.example1.markattendance;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Class_Adapter extends RecyclerView.Adapter<Class_Adapter.ViewHolder> {

    List<Model_Batch> item_List;
    Class_Fragment class_fragment;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    List<String> list_of_past_records=new ArrayList<>();
    int f = 0;
    String userID, class_name;

    public Class_Adapter(List<Model_Batch> item_List, Class_Fragment class_fragment) {
        this.item_List = item_List;
        this.class_fragment = class_fragment;

    }

    @NonNull
    @Override
    public Class_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Class_Adapter.ViewHolder holder, final int position) {

        setUpFirebase();
        holder.batch_name.setText(item_List.get(position).getBatch_name());
        holder.batch_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mbatch.startActivity(new Intent(mbatch.getContext(), AddMembers.class));
                Intent intent = new Intent(class_fragment.getContext(),AddMembers.class);
                String document_name = item_List.get(position).getBatch_name()+" "+item_List.get(position).getTotal_member();
                intent.putExtra("document_name",document_name);
                class_fragment.startActivity(intent);
            }
        });
        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display Menu Options
                PopupMenu popupMenu = new PopupMenu(class_fragment.getContext(),holder.option_menu);
                popupMenu.inflate(R.menu.batch_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.delete_class:
//                                item_list.remove(position);
                                getListing(position);
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
    public void getListing(final int position){
        db.collection("users").document(userID)
                .collection("Class_List").document(item_List.get(position).getBatch_name())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String past_records_string = documentSnapshot.getString("List");
                        String[] list1 = past_records_string.split("-");
                        list_of_past_records = Arrays.asList(list1);
                        Log.d("Listing", past_records_string.toString());
                        deleteSelectedRow(position);
                    }
                });

    }
    private void deleteSelectedRow(int position){
        class_name =  item_List.get(position).getBatch_name()+" "+ userID;
        db.collection(class_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot: snapshotList){
                            writeBatch.delete(documentSnapshot.getReference());
                        }
                        writeBatch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Deletion","SuccessFull");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Deletion",e.toString());
                                    }
                                });
                    }
                });
        db.collection("users").document(userID)
                .collection("Class_List").document(item_List.get(position).getBatch_name())
                .collection("Subjects")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot: snapshotList){
                            writeBatch.delete(documentSnapshot.getReference());
                        }
                        writeBatch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Deletion","SuccessFull");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Deletion",e.toString());
                                    }
                                });
                    }
                });
        db.collection("users").document(userID)
                .collection("Class_List").document(item_List.get(position).getBatch_name())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(class_fragment.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
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
            db.collection("Records").document(class_name).collection(tags)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot: snapshotList){
                                writeBatch.delete(documentSnapshot.getReference());
                            }
                            writeBatch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Deletion","SuccessFull");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Deletion",e.toString());
                                        }
                                    });
                        }
                    });
        }
    }
    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView batch_name;
        public TextView option_menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batch_name = itemView.findViewById(R.id.batch_name);
            option_menu = itemView.findViewById(R.id.option_menu);
        }
    }
}
