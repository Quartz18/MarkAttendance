package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Dashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID, user_name, user_mail,document_name,class_name;
    List<String> list_of_class;
    TextView current_user_name, current_user_mail;
    Button log_out, delete_account;
    List<String> list_of_past_records=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpFirebase();
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_name = documentSnapshot.getString("Name");
                        user_mail = documentSnapshot.getString("Email");
                        current_user_name.setText("Name: "+user_name);
                        current_user_mail.setText("Account: "+user_mail);
                        class_name = documentSnapshot.getString("List");
                        if (class_name.equals("")){
                            Log.d("usermail", user_mail);
                        }
                        else {
                            String[] list1 = class_name.split("-");
                            list_of_class = Arrays.asList(list1);
                        }

                    }
                });
        setUpWidgets();

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(Dashboard.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog();
            }
        });

    }

    private void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }
    private void setUpWidgets(){
        current_user_name = findViewById(R.id.current_user_name);
        current_user_mail = findViewById(R.id.current_user_mail);
        log_out = findViewById(R.id.current_user_log_out);
        delete_account = findViewById(R.id.current_user_delete);
    }

    private void getAlertDialog(){
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(Dashboard.this);
        alert_dialog.setTitle("Are you sure?");
        alert_dialog.setMessage("Deleting this account will result in completely removing the account from the system and the user "+
                user_name+" won't be able to access the application.");
        alert_dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (list_of_class.size()>1){
                    int q = 0;
                    for (String tags:list_of_class){
                        if(q==0){
                            q = q+1;
                            continue;
                        }
                        getListing(tags);
                    }
                    deleteUser();
                }
                else {
                    deleteUser();
                }
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

    private void deleteUser(){
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Dashboard.this,"Account Deleted!",
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Dashboard.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
    public void getListing(String tags){
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int count_of_class = Integer.parseInt(documentSnapshot.getString("count_of_class")) -1;
                        String list_of_class = documentSnapshot.getString("List");
                        String new_list_of_class = "-"+tags;
                        list_of_class = list_of_class.replaceAll(new_list_of_class,"");
                        Map<String, Object> counting_class = new HashMap<>();
                        DocumentReference class_list = db.collection("users").document(userID);
                        counting_class.put("count_of_class",String.valueOf(count_of_class));
                        counting_class.put("List",list_of_class);
                        class_list.update(counting_class);

                    }
                });
        db.collection("users").document(userID)
                .collection("Class_List").document(tags)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String past_records_string = documentSnapshot.getString("List");
                        if (past_records_string.equals("")){
                            deleteSelectedRow(tags);
                        }
                        else{
                            String[] list1 = past_records_string.split("-");
                            list_of_past_records = Arrays.asList(list1);
                            Log.d("Listing", past_records_string.toString());
                            deleteSelectedRow(tags);
                        }
                    }
                });


    }
    private void deleteSelectedRow(String tags){
        document_name =  tags+" "+ userID;
        db.collection(document_name)
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
                .collection("Class_List").document(tags)
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
                .collection("Class_List").document(tags)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Dashboard.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
        if (list_of_past_records.size()>1) {
            getpastlistdelete(tags);
        }
    }

    public void getpastlistdelete(String tags1){
        int q = 0;
        for (String tags: list_of_past_records){
            if (q == 0){
                q = 1;
                continue;
            }
            db.collection("Records").document(document_name).collection(tags)
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
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}