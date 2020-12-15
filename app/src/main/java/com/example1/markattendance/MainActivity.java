package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button register_btn;
    Animation frombtn, fromtop;
    EditText username, email;
    TextInputEditText paswrd;
    TextInputLayout passwrd_layout;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    TextView textview,title;
    String userID;
    Date date = new Date();
    FirebaseStorage storage;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.textView);
        register_btn = findViewById(R.id.button);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email_login);
        passwrd_layout = findViewById(R.id.editTextTextPassword_layout);
        paswrd = findViewById(R.id.editTextTextPassword);
        textview = findViewById(R.id.registered);
        frombtn = AnimationUtils.loadAnimation(this, R.anim.btn_anim);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        register_btn.startAnimation(frombtn);
        email.startAnimation(fromtop);
        title.startAnimation(fromtop);
        username.startAnimation(fromtop);
        textview.startAnimation(frombtn);
        passwrd_layout.startAnimation(fromtop);
        paswrd.startAnimation(fromtop);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                String passwordRegex = "^(?=.*[0-9])"
                        + "(?=.*[a-z])(?=.*[A-Z])"
                        + "(?=\\S+$).{7,20}$";
                String nameRegex = "^[A-Za-z]\\w{4,29}$";
                Pattern pat = Pattern.compile(emailRegex);
                Pattern pat2 = Pattern.compile(passwordRegex);
                Pattern pat3 = Pattern.compile(nameRegex);
                String txtemail = email.getText().toString();
                String txtpassword = paswrd.getText().toString();
                String txtusername = username.getText().toString();
                final String dateToStr = DateFormat.getDateTimeInstance().format(date);
                if (TextUtils.isEmpty(txtemail)) {
                    email.setError("Enter email Id");
                }
                else if (TextUtils.isEmpty(txtpassword)){
                    paswrd.setError("Enter Password");
                }
                else if (TextUtils.isEmpty(txtusername)){
                    username.setError("Enter your Name");

                }
                else if (!pat.matcher(txtemail).matches()){
                    email.setError("Provide valid email id");
                }
                else if (txtpassword.length() < 7) {
                    paswrd.setError("Provide at least 6 characters.");
                    return;
                }
                else if (txtusername.length() < 3){
                    username.setError("Provide at least 4 characters");
                }
                else if (!pat3.matcher(txtusername).matches()){
                    username.setError("Provide valid Name");
                }
                else if (!pat2.matcher(txtpassword).matches()){
                    Toast.makeText(MainActivity.this,
                            "Password should contain at least one digit and a capital letter",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(txtemail, txtpassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(MainActivity.this,
                                            "Welcome "+txtusername, Toast.LENGTH_SHORT).show();
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    Map<String ,Object> user=new HashMap<>();
                                    user.put("Name", username.getText().toString());
                                    user.put("Email", email.getText().toString());
                                    user.put("count_of_class","0");
                                    user.put("List","");
                                    user.put("Date",dateToStr);
                                    user.put("Profile_pic","0");
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username.getText().toString())
                                                    .build();
                                            firebaseUser.updateProfile(request)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("TAG","User Profile is created!");
                                                        }
                                                    });
                                        }
                                    });
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this,
                                            e.getMessage(), Toast.LENGTH_LONG).show();
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