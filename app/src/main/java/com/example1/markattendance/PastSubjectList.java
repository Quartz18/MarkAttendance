package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PastSubjectList extends AppCompatActivity {
    RecyclerView past_subject_list_recyclerview;
    ArrayList<Model_Member> item_List;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    String userID;
    public String document_name, class_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_subject_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        setUpFirebase();
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
                        if (error!=null){
                            return;
                        }
                        item_List.clear();
                        loadMembersFromFirestore();
                    }
                });

    }

    private void loadMembersFromFirestore() {
        db.collection("users").document(userID)
                .collection("Class_List").document(document_name)
                .collection("Subjects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int x = 1,found =1;
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            if (found ==4){
                                found = 1;
                            }
                            Model_Member model_member = new Model_Member(String.valueOf(x),
                                    querySnapshot.getString("Name"),
                                    document_name,
                                    querySnapshot.getId(),found);
                            x = x+1;
                            item_List.add(model_member);
                            found = found + 1;
                        }
                        past_subject_list_recyclerview.setAdapter(new PastSubjectListAdapter(item_List, PastSubjectList.this));
                    }
                });
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

    }
    private void setUpRecyclerview() {
        item_List = new ArrayList<>();
        past_subject_list_recyclerview = findViewById(R.id.past_subject_list_recyclerview);
        past_subject_list_recyclerview.setHasFixedSize(true);
        past_subject_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}