package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Past_Attendance extends AppCompatActivity {

    RecyclerView past_attendance_recyclerview;
    ArrayList<Model_Past_Records> item_List = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID,document_name,past_records_string,subject_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past__attendance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        past_attendance_recyclerview = findViewById(R.id.past_attendance_recyclerview);
        setUpRecyclerview();
        setUpFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        subject_name = getIntent().getStringExtra("subject_name");

        db.collection(document_name)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        item_List.clear();
                        if (subject_name.equals("None")){
                            loadDataFromFirebase();
                        }else {
                            loadSubjectDataFromFirebase();
                        }

                    }
                });
    }

    private void loadSubjectDataFromFirebase() {
        if (item_List.size()>0){
            item_List.clear();
        }
        int x = document_name.indexOf(userID);
        final String class_name = document_name.substring(0,x-1);
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects").document(subject_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        past_records_string = documentSnapshot.getString("List");
                        String[] list1 = past_records_string.split("-");
                        List<String> list_of_past_records = Arrays.asList(list1);
                        int q =0;
                        for (String tags : list_of_past_records){
                            if (q== 0){
                                q = 1;
                                continue;
                            }
                            Model_Past_Records model_past_records = new Model_Past_Records(tags,String.valueOf(q),document_name,"None");
                            item_List.add(model_past_records);
                            q = q+1;
                        }
                        past_attendance_recyclerview.setAdapter(new PastAttendanceAdapter(item_List, Past_Attendance.this));
                    }
                });
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    private void setUpRecyclerview()
    {
        past_attendance_recyclerview.setHasFixedSize(true);
        past_attendance_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void loadDataFromFirebase(){
        if (item_List.size()>0){
            item_List.clear();
        }
        int x = document_name.indexOf(userID);
        final String class_name = document_name.substring(0,x-1);
        db.collection("users").document(userID).collection("Class_List").document(class_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        past_records_string = documentSnapshot.getString("List");
                        String[] list1 = past_records_string.split("-");
                        List<String> list_of_past_records = Arrays.asList(list1);
                        int q =0;
                        for (String tags : list_of_past_records){
                            if (q== 0){
                                q = 1;
                                continue;
                            }
                            Model_Past_Records model_past_records = new Model_Past_Records(tags,String.valueOf(q),document_name,"None");
                            item_List.add(model_past_records);
                            q = q+1;
                        }
                        past_attendance_recyclerview.setAdapter(new PastAttendanceAdapter(item_List, Past_Attendance.this));
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}