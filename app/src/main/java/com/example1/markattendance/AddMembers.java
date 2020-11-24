package com.example1.markattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;

public class AddMembers extends AppCompatActivity {
    RecyclerView member_recyclerview;

    FloatingActionButton member_add_button,main_add_button,subject_list_button;
    Animation from_rotate,to_rotate,from_right,to_right,from_left,to_left;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;
    DocumentSnapshot number_of_class;
    public String document_name;
    Boolean onCLicked = false;

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
                startActivity(new Intent(AddMembers.this,Subjects_List.class));
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
        ArrayList<Model_Member> item_List = new ArrayList<>();
        db.collection(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            Model_Member model_member = new Model_Member(querySnapshot.getString("member_id"),
                                    querySnapshot.get("member_name").toString(),
                                    querySnapshot.getId(),
                                    document_name);
                            item_List.add(model_member);
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
}