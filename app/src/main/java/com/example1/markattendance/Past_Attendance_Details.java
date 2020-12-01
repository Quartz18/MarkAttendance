package com.example1.markattendance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Past_Attendance_Details extends AppCompatActivity {
    RecyclerView past_attendance_details_recyclerview;
    Past_Attendance_Details_Adapter past_attendance_details_adapter;
    ArrayList<Model_Past_Records> item_List;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;
    public String document_name,record_name,class_name;
    FloatingActionButton download_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past__attendance__details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        setUpFirebase();
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createCSV(class_name+record_name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        past_attendance_details_recyclerview = findViewById(R.id.past_attendance_details_recyclerview);
        download_button = findViewById(R.id.downloading_button);
        past_attendance_details_recyclerview.setHasFixedSize(true);
        past_attendance_details_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        past_attendance_details_adapter = new Past_Attendance_Details_Adapter(item_List, Past_Attendance_Details.this);

    }
    private void loadMembersFromFirestore(){


        db.collection("Records").document(document_name).collection(record_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int found =1;
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            if (found == 4){
                                found = 1;
                            }
                            Model_Past_Records model_past_records = new Model_Past_Records(record_name,querySnapshot.getString("member_id"),
                                    querySnapshot.get("member_name").toString(),
                                    querySnapshot.get("member_attended").toString(),found);
                            item_List.add(model_past_records);
                            found = found + 1;
                        }
                        past_attendance_details_recyclerview.setAdapter(past_attendance_details_adapter);
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        int x = document_name.indexOf(userID);
        class_name = document_name.substring(0,x-1);
        record_name = getIntent().getStringExtra("record_name");
        db.collection("Records").document(document_name).collection(record_name)
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void createCSV(String title){
        StringBuilder data = new StringBuilder();
        data.append("ID"+","+"Name"+","+"Mark");
        for (int i=0;i<item_List.size();i++){
            data.append("\n"+item_List.get(i).member_id+","+item_List.get(i).member_name+","+item_List.get(i).member_attended);
        }
        try {
            FileOutputStream outputStream = openFileOutput(title+".csv", Context.MODE_PRIVATE);
            outputStream.write((data.toString()).getBytes());
            outputStream.close();

            Context context =getApplicationContext();
            File file = new File(getFilesDir(),title+".csv");
            Uri path = FileProvider.getUriForFile(context,"com.example1.markattendance.fileprovider",file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_SUBJECT,title);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(intent,"Send mail"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}