package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button register_btn;
    Animation frombtn, fromtop;
    EditText username, email, paswrd;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    TextView textview,title;
    String userID;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.textView);
        register_btn = findViewById(R.id.button);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email_login);
        paswrd = findViewById(R.id.editTextTextPassword);
        textview = findViewById(R.id.registered);
        frombtn = AnimationUtils.loadAnimation(this, R.anim.btn_anim);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        register_btn.startAnimation(frombtn);
        email.startAnimation(fromtop);
        title.startAnimation(fromtop);
        username.startAnimation(fromtop);
        textview.startAnimation(frombtn);
        paswrd.startAnimation(fromtop);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail = email.getText().toString();
                String txtpassword = paswrd.getText().toString();
                String txtusername = username.getText().toString();
                if (TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword) || TextUtils.isEmpty(txtusername)) {
                    Toast.makeText(MainActivity.this, "Fill all the Entries!", Toast.LENGTH_SHORT).show();
                } else if (txtpassword.length() < 6) {
                    paswrd.setError("Password should be more than 6 characters.");
                    return;
                } else {
                    mAuth.createUserWithEmailAndPassword(txtemail, txtpassword)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "User registered!", Toast.LENGTH_SHORT).show();
                                        userID = mAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String ,Object> user=new HashMap<>();
                                        user.put("Name", username.getText().toString());
                                        user.put("Email", email.getText().toString());
                                        user.put("count_of_class",0);
                                        user.put("List","");
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG","User Profile is created!");
                                            }
                                        });
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Log.w("Sign In", "Error", task.getException());
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }


}