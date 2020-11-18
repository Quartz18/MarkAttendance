package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowingStatistics extends AppCompatActivity {

    RecyclerView showing_statistics_recyclerview;
    ArrayList<Model_Statistics> item_List;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ShowingStatisticsAdapter showingStatisticsAdapter;
    int count_of_records,difference;
    String document_name, subject_name, userID,class_name,record_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        setUpFirebase();
    }
    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }
    private void setUpRecyclerview() {
        item_List = new ArrayList<>();
        showing_statistics_recyclerview = findViewById(R.id.showing_statistics_recyclerview);
        showing_statistics_recyclerview.setHasFixedSize(true);
        showing_statistics_recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
    private void loadMembersFromFirestore(){
        db.collection(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            float counting = Float.valueOf(querySnapshot.getString("counting"))/Float.valueOf(count_of_records)*100;
                            Model_Statistics model_statistics = new Model_Statistics(querySnapshot.getId(),
                                    querySnapshot.get("member_name").toString(),
                                    String.format("%.02f",counting)+"%\n"+querySnapshot.getString("counting")+"/"+count_of_records,
                                    document_name,
                                    record_list,
                                    querySnapshot.getString("stats_list"));
                            item_List.add(model_statistics);
                        }
                        showing_statistics_recyclerview.setAdapter(new ShowingStatisticsAdapter(item_List, ShowingStatistics.this));
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        subject_name = getIntent().getStringExtra("subject_name");
        int x = document_name.indexOf(userID);
        class_name = document_name.substring(0,x-1);
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        count_of_records = Integer.valueOf(value.getString("count_of_records"));
                        record_list = value.getString("List");
                        item_List.clear();
                        if (subject_name.equals("None")){
                            loadMembersFromFirestore();
                        }else {
                            loadSubjectDataFromFirebase();
                        }

                    }
                });
    }

    private void loadSubjectDataFromFirebase() {
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects").document(subject_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        difference = count_of_records - Integer.valueOf(documentSnapshot.getString("count_of_records"));;
                        count_of_records = Integer.valueOf(documentSnapshot.getString("count_of_records"));
                        record_list = documentSnapshot.getString("List");
                    }
                });
        db.collection(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            int count_of_member = Integer.valueOf(querySnapshot.getString("counting")) - difference;
                            float counting = Float.valueOf(count_of_member)/Float.valueOf(count_of_records)*100;
                            Model_Statistics model_statistics = new Model_Statistics(querySnapshot.getId(),
                                    querySnapshot.get("member_name").toString(),
                                    String.format("%.02f",counting)+"%\n"+String.valueOf(count_of_member)+"/"+count_of_records,
                                    document_name,
                                    record_list,
                                    querySnapshot.getString("stats_list"));
                            item_List.add(model_statistics);
                        }
                        showing_statistics_recyclerview.setAdapter(new ShowingStatisticsAdapter(item_List, ShowingStatistics.this));
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}