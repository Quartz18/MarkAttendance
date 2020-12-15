package com.example1.markattendance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMembers extends AppCompatActivity {
    RecyclerView member_recyclerview;
    FloatingActionButton member_add_button,main_add_button,subject_list_button;
    Animation from_rotate,to_rotate,from_right,to_right,from_left,to_left;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID,counter;
    int counting;
    private static final int PERMISSION_REQUEST_STORAGE =1000;
    private static final int READ_REQUEST_CODE =42;
    public String document_name, class_name;
    Boolean onCLicked = false;
    ArrayList<Model_Member> item_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerview();
        animate();
        setUpFirebase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        document_name = getIntent().getStringExtra("document_name");
        class_name = getIntent().getStringExtra("class_name");
        db.collection(document_name)
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
        member_recyclerview = findViewById(R.id.add_members_recyclerview);
        member_recyclerview.setHasFixedSize(true);
        member_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        main_add_button = findViewById(R.id.main_add_button);
        member_add_button = findViewById(R.id.member_add_button);
        subject_list_button = findViewById(R.id.subject_list_button);
        main_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMainAddButtonClicked();
            }
        });
        member_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openaddmembers();
            }
        });
        subject_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubjectList();
            }
        });

    }

    private void openSubjectList() {
        Intent intent = new Intent(AddMembers.this,Subjects_List.class);
        intent.putExtra("document_name",document_name);
        startActivity(intent);
    }

    private void onMainAddButtonClicked() {
        setVisibillity(onCLicked);
        setAnimation(onCLicked);
        onCLicked = !onCLicked;
    }

    private void setAnimation(Boolean onCLicked) {
        if (!onCLicked){
            main_add_button.startAnimation(from_rotate);
            member_add_button.startAnimation(from_left);
            subject_list_button.startAnimation(from_right);
        }else {
            main_add_button.startAnimation(to_rotate);
            member_add_button.startAnimation(to_left);
            subject_list_button.startAnimation(to_right);
        }
    }

    private void setVisibillity(Boolean onCLicked) {
        if (!onCLicked){
            member_add_button.setVisibility(View.VISIBLE);
            subject_list_button.setVisibility(View.VISIBLE);
            member_add_button.setClickable(true);
            subject_list_button.setClickable(true);

        }else{
            member_add_button.setVisibility(View.INVISIBLE);
            subject_list_button.setVisibility(View.INVISIBLE);
            member_add_button.setClickable(false);
            subject_list_button.setClickable(false);
        }
    }

    private void openaddmembers() {
        Bundle args = new Bundle();
        args.putString("document_name", document_name);
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddMembersDialog addMembersDialog = new AddMembersDialog();
        addMembersDialog.setArguments(args);
        addMembersDialog.show(fragmentManager, "Add Members");

    }
    private void loadMembersFromFirestore(){
        item_List = new ArrayList<>();
        db.collection(document_name)
                .orderBy("member_id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int found = 1;
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            if (found == 4){
                                found = 1;
                            }
                            Model_Member model_member = new Model_Member(querySnapshot.getString("member_id"),
                                    querySnapshot.get("member_name").toString(),
                                    querySnapshot.getId(),
                                    document_name,found);
                            item_List.add(model_member);
                            found = found + 1;
                        }
                        member_recyclerview.setAdapter(new MemberAdapter(item_List, AddMembers.this));
                    }
                });
    }
    public void animate(){
        from_rotate = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        to_rotate = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        from_right = AnimationUtils.loadAnimation(this,R.anim.from_right);
        to_right = AnimationUtils.loadAnimation(this,R.anim.to_right);
        from_left = AnimationUtils.loadAnimation(this,R.anim.from_left);
        to_left = AnimationUtils.loadAnimation(this,R.anim.to_left);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_member_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.copyCSV:
                readCSVfile(class_name);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleCSVfile(){
        Intent csv_intent = new Intent(Intent.ACTION_GET_CONTENT);
        csv_intent.setType("text/*");
        startActivityForResult(Intent.createChooser(csv_intent, "Select CSV file from here...."),READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case READ_REQUEST_CODE:
                if (resultCode==Activity.RESULT_OK){
                    String path = data.getData().toString();
                    writeCSVfile(path);
                }
                break;
            default:
                break;
        }
    }

    private void writeCSVfile(String path){


        try {
            StringBuilder stringBuilder = new StringBuilder();
            FileInputStream fileInputStream = openFileInput(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = bufferedReader.readLine())!= null) {
                //String[] strings = line.split(",", 3);
                stringBuilder.append(line + "\n");
                Log.d("okayhua", stringBuilder.toString());
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readCSVfile(String title){
        StringBuilder data = new StringBuilder();
        data.append("ID"+","+"Name");
        for (int i=0;i<item_List.size();i++){
            data.append("\n"+item_List.get(i).getMembers_number()+","+item_List.get(i).getMembers_name());
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