package com.example1.markattendance;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Subjects_List extends AppCompatActivity {
    RecyclerView subject_list_recyclerview;
    FloatingActionButton subject_add_button;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;
    public String document_name, class_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects__list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        setUpFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        int x = document_name.indexOf(userID);
        class_name = document_name.substring(0,x-1);
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects")
                .addSnapshotListener((value, error) -> {
                    if (error!=null){
                        return;
                    }
                    loadMembersFromFirestore();
                });
    }

    private void loadMembersFromFirestore() {
        ArrayList<Model_Batch> item_List = new ArrayList<>();
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects")
                .get()
                .addOnCompleteListener(task -> {
                    int found =1;
                    for (DocumentSnapshot querySnapshot: task.getResult()){
                        if (found == 4){
                            found =1;
                        }
                        Model_Batch model_batch = new Model_Batch(querySnapshot.getString("Name"),
                                querySnapshot.getId(),
                                class_name, found);
                        item_List.add(model_batch);
                        found = found + 1;
                    }
                    subject_list_recyclerview.setAdapter(new SubjectListAdapter(item_List, Subjects_List.this));
                });
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

    }
    private void setUpRecyclerview() {
        subject_list_recyclerview = findViewById(R.id.subject_list_recyclerview);
        subject_list_recyclerview.setHasFixedSize(true);
        subject_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        subject_add_button = findViewById(R.id.subject_add_button);
        subject_add_button.setOnClickListener(v -> openAddSubjects());

    }
    private void openAddSubjects() {
        Bundle args = new Bundle();
        args.putString("document_name", class_name);
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddSubjectDialog addSubjectDialog = new AddSubjectDialog();
        addSubjectDialog.setArguments(args);
        addSubjectDialog.show(fragmentManager, "Add Members");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}