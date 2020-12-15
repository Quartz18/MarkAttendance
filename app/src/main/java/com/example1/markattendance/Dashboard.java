package com.example1.markattendance;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    GoogleSignInClient mGoogleSignInClient;
    String userID, user_name, user_mail,class_name;
    List<String> list_of_class;
    TextView current_user_name, current_user_mail;
    Button log_out;
    FirebaseStorage storage;
    int TAKE_IMAGE_CODE = 22;
    ImageView profileImageView, show_off;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpFirebase();
        setUpWidgets();
        if (firebaseUser.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .error(R.drawable.profile_bg)
                    .into(profileImageView);
        }
        storageReference.child("profileImages/"+userID+".jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Dashboard.this)
                        .load(uri)
                        .error(R.drawable.profile_bg)
                        .into(profileImageView);
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("users").document(userID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error !=null){
                            return;
                        }
                        setUpValues();
                    }
                });
    }

    private void setUpValues(){
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_name = documentSnapshot.getString("Name");
                        user_mail = documentSnapshot.getString("Email");
                        current_user_name.setText(user_name);
                        current_user_mail.setText(user_mail);
                        class_name = documentSnapshot.getString("List");
                        if (class_name.equals("")){
                            Log.d("usermail", user_mail);
                        }
                        else {
                            String[] list1 = class_name.split("-");
                            list_of_class = Arrays.asList(list1);
                        }

                    }
                });
    }

    private void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("963358575455-h6ul90gluen8dom3ae1fksf8o5h7emf0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Dashboard.this, gso);
    }
    private void setUpWidgets(){
        current_user_name = findViewById(R.id.current_user_name);
        current_user_mail = findViewById(R.id.current_user_mail);
        profileImageView = findViewById(R.id.profileimage);
        log_out = findViewById(R.id.current_user_log_out);
        show_off = findViewById(R.id.show_off);
    }

    private void getAlertDialog(){
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(Dashboard.this);
        alert_dialog.setTitle("Log Out");
        alert_dialog.setMessage("Are you sure you want to log out?");
        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Dashboard.this);
                if (account !=null){
                    mGoogleSignInClient.signOut()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(Dashboard.this,LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                }
                Intent i = new Intent(Dashboard.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert_dialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.user_edit_photo:
                handleImageclick();
                break;
            case R.id.user_edit_name:
                handleUsername();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private  void handleImageclick(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select profile picture...."),TAKE_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK && data!=null){
            image_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(),image_uri);
                profileImageView.setImageBitmap(bitmap);
                handleUpload(image_uri);
            }catch (Exception exception){
                exception.printStackTrace();
            }

        }
    }

    private void handleUpload(Uri image_uri) {
        if (image_uri != null){
            StorageReference reference = storageReference.child("profileImages/"+userID+".jpg");
            reference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Dashboard.this,"Profile is updated!",Toast.LENGTH_SHORT).show();
                        }
                    });
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(image_uri)
                    .build();
            firebaseUser.updateProfile(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("updatePro","Hua");
                        }
                    });
        }

    }

    private void handleUsername(){
        Edit_Username edit_username = new Edit_Username();
        edit_username.show(getSupportFragmentManager(),"EditUsername");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}