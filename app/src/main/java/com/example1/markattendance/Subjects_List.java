package com.example1.markattendance;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Subjects_List extends AppCompatActivity {
    RecyclerView subject_list_recyclerview;
    ArrayList<Model_Batch> item_List;
    FloatingActionButton subject_add_button;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
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
                .collection("Class_List").document(class_name)
                .collection("Subjects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            Model_Batch model_batch = new Model_Batch(querySnapshot.getString("Name"),querySnapshot.getId(),class_name);
                            item_List.add(model_batch);
                        }
                        subject_list_recyclerview.setAdapter(new SubjectListAdapter(item_List, Subjects_List.this));
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
        subject_list_recyclerview = findViewById(R.id.subject_list_recyclerview);
        subject_list_recyclerview.setHasFixedSize(true);
        subject_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        subject_add_button = findViewById(R.id.subject_add_button);
        subject_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSubjects();
            }
        });

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