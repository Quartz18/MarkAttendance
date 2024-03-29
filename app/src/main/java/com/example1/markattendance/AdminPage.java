package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminPage extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    RecyclerView admin_recyclerview;
    FloatingActionButton add_user_button;
    String userID;
    ArrayList<Model_Batch> item_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        setUpRecyclerview();
        setUpFirebase();
    }


    private void setUpFirebase(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    private  void setUpRecyclerview(){
        admin_recyclerview = findViewById(R.id.admin_recyclerview);
        admin_recyclerview.setHasFixedSize(true);
        admin_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        add_user_button = findViewById(R.id.add_user_button);
        add_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Nothing
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item:
                Intent intent = new Intent(AdminPage.this,Dashboard.class);
                startActivity(intent);
                break;
            default:
                //Nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserFromFirebase(){
        item_List = new ArrayList<>();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int found =1;
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            Log.d("Users", querySnapshot.getString("Name"));
                            if (found == 3){
                                found =1;
                            }
                            Model_Batch model_batch = new Model_Batch(querySnapshot.getString("Name"),
                                    String.valueOf(1),
                                    querySnapshot.getId(),found);
                            item_List.add(model_batch);
                            found = found +1;
                        }
                        Log.d("Users", item_List.toString());
                        admin_recyclerview.setAdapter(new AdminPageAdapter(item_List, AdminPage.this));
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        loadUserFromFirebase();
                    }
                });
    }
}