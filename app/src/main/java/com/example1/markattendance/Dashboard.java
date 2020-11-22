package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class Dashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID, user_name, user_mail,document_name,class_name;
    TextView current_user_name, current_user_mail;
    Button log_out, delete_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpFirebase();
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_name = documentSnapshot.getString("Name");
                        user_mail = documentSnapshot.getString("Email");
                        current_user_name.setText("Name: "+user_name);
                        current_user_mail.setText("Account: "+user_mail);
                        Log.d("usermail", user_mail);
                    }
                });
        setUpWidgets();

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(Dashboard.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog();
            }
        });

    }

    private void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }
    private void setUpWidgets(){
        current_user_name = findViewById(R.id.current_user_name);
        current_user_mail = findViewById(R.id.current_user_mail);
        log_out = findViewById(R.id.current_user_log_out);
        delete_account = findViewById(R.id.current_user_delete);
    }

    private void getAlertDialog(){
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(Dashboard.this);
        alert_dialog.setTitle("Are you sure?");
        alert_dialog.setMessage("Deleting this account will result in completely removing the account from the system and the user "+
                user_name+" won't be able to access the application.");
        alert_dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Dashboard.this,"Account Deleted!",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Dashboard.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert_dialog.create();
        alertDialog.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}