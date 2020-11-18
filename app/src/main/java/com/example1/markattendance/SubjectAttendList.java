package com.example1.markattendance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectAttendList extends AppCompatActivity {
    String document_name,subject_name,userID;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    RecyclerView subject_attend_Recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_attend_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpFirebase();
        setUpRecyclerview();
    }

    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        db.collection("users").document(userID)
                .collection("Class_List").document(document_name)
                .collection("Subjects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null) {
                            return;
                        }
                        loadSubjectFromFirebase();
                    }
                });
    }

    private void loadSubjectFromFirebase(){
        ArrayList<Model_Batch> item_List = new ArrayList<>();
        db.collection("users").document(userID)
                .collection("Class_List").document(document_name)
                .collection("Subjects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int q = 1;
                        for (DocumentSnapshot documentSnapshot: task.getResult()){
                            Model_Batch model_batch = new Model_Batch(documentSnapshot.getId().toString(),
                                    String.valueOf(q),
                                    document_name);
                            q = q+1;
                            item_List.add(model_batch);
                        }
                        subject_attend_Recyclerview.setAdapter(new SubjectAttendListAdapter(item_List,SubjectAttendList.this));
                    }
                });
    }

    private void setUpRecyclerview(){
        subject_attend_Recyclerview = findViewById(R.id.subject_attend_recyclerview);
        subject_attend_Recyclerview.setHasFixedSize(true);
        subject_attend_Recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}