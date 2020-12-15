package com.example1.markattendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (item_List.get(position).getFound() == 1){
            holder.item_list2.setBackgroundResource(R.drawable.item_colour_1);
        }
        else if (item_List.get(position).getFound() == 2){
            holder.item_list2.setBackgroundResource(R.drawable.item_colour_2);
        }
        else {
            holder.item_list2.setBackgroundResource(R.drawable.item_colour_3);
        }

        holder.batch_name.setText(item_List.get(position).getBatch_name());
        holder.batch_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mbatch.startActivity(new Intent(mbatch.getContext(), AddMembers.class));
                if (item_List.get(position).getFound() == 1){
                    holder.item_list2.setBackgroundColor(holder.item_list2
                            .getContext().getResources()
                            .getColor(R.color.blue,null));
                }
                else if (item_List.get(position).getFound() == 2){
                    holder.item_list2.setBackgroundColor(holder.item_list2
                            .getContext().getResources()
                            .getColor(R.color.Green,null));
                }
                else {
                    holder.item_list2.setBackgroundColor(holder.item_list2
                            .getContext().getResources()
                            .getColor(R.color.yellow,null));
                }
                Intent intent = new Intent(class_fragment.getContext(),AddMembers.class);
                String document_name = item_List.get(position).getCount_of_subjects()+" "+userID;
                intent.putExtra("document_name",document_name);
                intent.putExtra("class_name",item_List.get(position).getBatch_name());
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
                            case R.id.edit_class:
                                Bundle args = new Bundle();
                                args.putString("class_id", item_List.get(position).getCount_of_subjects());
                                Edit_CLass editCLass = new Edit_CLass();
                                editCLass.setArguments(args);
                                editCLass.show(class_fragment.getFragmentManager(),"addClassDialog");
                                break;
                            case R.id.delete_class:
                                getAlertDialog(position);
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

    private void getAlertDialog(int position){
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(class_fragment.getContext());
        alert_dialog.setTitle("Are you sure?");
        alert_dialog.setMessage("Deleting "+item_List.get(position).getBatch_name()+" class will result in completely removing all the details of the class.");
        alert_dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getListing(position);
            }
        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert_dialog.create();
        alertDialog.show();
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }
    public void getListing(final int position){
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int count_of_class = Integer.parseInt(documentSnapshot.getString("count_of_class")) -1;
                        String list_of_class = documentSnapshot.getString("List");
                        String new_list_of_class = "-"+item_List.get(position).getCount_of_subjects();
                        list_of_class = list_of_class.replaceAll(new_list_of_class,"");
                        Map<String, Object> counting_class = new HashMap<>();
                        DocumentReference class_list = db.collection("users").document(userID);
                        counting_class.put("count_of_class",String.valueOf(count_of_class));
                        counting_class.put("List",list_of_class);
                        class_list.update(counting_class);

                    }
                });
        db.collection("users").document(userID)
                .collection("Class_List").document(item_List.get(position).getCount_of_subjects())
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
        class_name =  item_List.get(position).getCount_of_subjects()+" "+ userID;
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
                .collection("Class_List").document(item_List.get(position).getCount_of_subjects())
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
                .collection("Class_List").document(item_List.get(position).getCount_of_subjects())
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
        View item_list2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batch_name = itemView.findViewById(R.id.batch_name);
            option_menu = itemView.findViewById(R.id.option_menu);
            item_list2 = itemView.findViewById(R.id.item_list2);
        }
    }
}
