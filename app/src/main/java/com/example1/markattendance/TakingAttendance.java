package com.example1.markattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TakingAttendance extends AppCompatActivity {
    RecyclerView take_attendance_recyclerview;
    CheckBox select_all_button;
    ArrayList<String> selected_list;
    FloatingActionButton main_attendance_button;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TakingAttendanceAdapter takingAttendanceAdapter;
    String userID,dateToStr;
    public String document_name, class_name, subject_name;
    Date date = new Date();
    Boolean select_all_value=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_attendance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        setUpFirebase();
    }
    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        subject_name = getIntent().getStringExtra("subject_name");
        int x = document_name.indexOf(userID);
        class_name = document_name.substring(0,x-1);
        db.collection("users").document(userID).collection("Class_List")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        loadMembersFromFirestore();
                    }
                });
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

    }
    private void setUpRecyclerview() {

        take_attendance_recyclerview = findViewById(R.id.take_attendance_recyclerview);
        take_attendance_recyclerview.setHasFixedSize(true);
        take_attendance_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        main_attendance_button = findViewById(R.id.main_attendance_button);
        select_all_button = findViewById(R.id.select_all_check_box);
        select_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_all_value = select_all_data();
                loadMembersFromFirestore();

            }
        });
        main_attendance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();

            }
        });

    }

    private Boolean select_all_data(){
        if (select_all_button.isChecked()){

            return true;
        }
        return false;
    }
    private void loadMembersFromFirestore(){
        final ArrayList<Model_Attendance> item_List = new ArrayList<>();
        final ArrayList<String> id_list = new ArrayList<>();
        takingAttendanceAdapter = new TakingAttendanceAdapter(item_List, TakingAttendance.this);
        db.collection(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int found =1;
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            if (found==4){
                                found =1;
                            }
                            id_list.add(querySnapshot.getString("member_id"));
                            Model_Attendance model_attendance = new Model_Attendance(querySnapshot.getString("member_id"),
                                    querySnapshot.get("member_name").toString(),
                                    id_list,
                                    select_all_value,found);
                            item_List.add(model_attendance);
                            found =found+1;
                        }
                        take_attendance_recyclerview.setAdapter(takingAttendanceAdapter);
                    }
                });
    }
    public void getData(){
        selected_list = takingAttendanceAdapter.listOfSelectedValues();
        Log.d("Select All",selected_list.toString());
        dateToStr = DateFormat.getDateTimeInstance().format(date);
        select_all_value =false;
        select_all_button.setChecked(false);
        loadMembersFromFirestore();
        settingMemberRecord();

    }
    public void settingClassRecord(){
        db.collection("users").document(userID).collection("Class_List").document(class_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String count2 = documentSnapshot.getString("count_of_records");
                        int count_found2 = Integer.valueOf(count2) + 1;
                        Map<String, Object> add_subject_record_name = new HashMap<>();
                        Map<String, Object> add_past_record_name = new HashMap<>();
                        DocumentReference record_dr = db.collection("users").document(userID)
                                .collection("Class_List").document(class_name);
                        String past_records_string = documentSnapshot.getString("List");
                        past_records_string = past_records_string+"-"+ dateToStr;
                        add_past_record_name.put("List",past_records_string);
                        add_past_record_name.put("count_of_records",String.valueOf(count_found2));
                        record_dr.update(add_past_record_name);
                    }
                });
        if (subject_name.equals("None")){
            //settingClassRecord();
        }
        else{
            settingSubjectRecord();
        }
    }

    public void settingMemberRecord(){
        db.collection(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot documentSnapshot: task.getResult()){
                            int ind = selected_list.indexOf(documentSnapshot.getString("member_id"));
                            int count_found1 = Integer.valueOf(documentSnapshot.getString("counting"))+1;
                            String stats_list = documentSnapshot.getString("stats_list");
                            Map<String, Object> add_counter_attendance = new HashMap<>();
                            Map<String, Object> add_record_name = new HashMap<>();
                            DocumentReference counting_of_attendance_dr = db.collection(document_name).document(documentSnapshot.getId());
                            DocumentReference record_list = db.collection("Records").document(document_name)
                                    .collection(dateToStr).document(documentSnapshot.getId());
                            add_record_name.put("member_id",documentSnapshot.get("member_id"));
                            add_record_name.put("member_name",documentSnapshot.get("member_name"));
                            if (ind == -1){
                                add_record_name.put("member_attended","false");
                                add_counter_attendance.put("stats_list",stats_list+"-"+"0");
                            }else {
                                add_record_name.put("member_attended","true");
                                Log.d("Selected",String.valueOf(ind));
                                add_counter_attendance.put("stats_list",stats_list+"-"+"1");
                                add_counter_attendance.put("counting",String.valueOf(count_found1));
                            }
                            counting_of_attendance_dr.update(add_counter_attendance);
                            record_list.set(add_record_name);
                        }
                    }
                });
        settingClassRecord();

    }

    public void settingSubjectRecord() {
        db.collection("users").document(userID)
                .collection("Class_List").document(class_name)
                .collection("Subjects").document(subject_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String getcount3 = documentSnapshot.getString("count_of_records");
                        int count_found3 = Integer.valueOf(getcount3) + 1;
                        Map<String, Object> add_past_record_name = new HashMap<>();
                        DocumentReference record_dr = db.collection("users").document(userID)
                                .collection("Class_List").document(class_name)
                                .collection("Subjects").document(subject_name);
                        String past_records_string = documentSnapshot.getString("List");
                        past_records_string = past_records_string + "-" + dateToStr;
                        add_past_record_name.put("List", past_records_string);
                        add_past_record_name.put("count_of_records", String.valueOf(count_found3));
                        record_dr.update(add_past_record_name);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.past_records, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.past_attendance_opt:
                    Intent intent_new = new Intent(TakingAttendance.this,Past_Attendance.class);
                    intent_new.putExtra("document_name",document_name);
                    intent_new.putExtra("subject_name",subject_name);
                    startActivity(intent_new);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}