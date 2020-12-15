package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    Animation frombtn, fromtop;
    EditText email;
    TextInputEditText paswrd;
    TextInputLayout passwd_layout;
    TextView textview,logged_in, forgot_psd;
    CardView google_cardview;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    SignInButton signInButton;
    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    Date date = new Date();
    String userID, username, user_email, dateToStr;
    Uri image_uri;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeScreen.class));
            finish();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpWidget();
        getAnimation();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("963358575455-h6ul90gluen8dom3ae1fksf8o5h7emf0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        logged_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                Pattern pat = Pattern.compile(emailRegex);
                String txtemail=email.getText().toString();
                String txtpassword=paswrd.getText().toString();
                if(TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword))
                {
                    Toast.makeText(LoginActivity.this,
                            "Fill all the entries!", Toast.LENGTH_SHORT).show();
                }
                else if (pat.matcher(txtemail).matches() == false){
                    Toast.makeText(LoginActivity.this,
                            "Please provide a Valid Address!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(txtemail, txtpassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginActivity.this,
                                            "Welcome Back!",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,
                                            e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
        forgot_psd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        if (TextUtils.isEmpty(mail)){
                            Toast.makeText(LoginActivity.this,"Enter a valid Email id!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this,"Reset Link Sent to Your Email.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"Error! Reset Link is not Sent. " +e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    getUpdatedValues(account);
                }
            });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_LONG).show();
        }
    }

    public void getUpdatedValues(GoogleSignInAccount account){

        userID = mAuth.getCurrentUser().getUid();
        username = account.getDisplayName();
        user_email = account.getEmail();
        image_uri = account.getPhotoUrl();
        dateToStr = DateFormat.getDateTimeInstance().format(date);
        db.collection("users").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        finish();
                    }
                    else {
                        DocumentReference documentReference = db
                                .collection("users").document(userID);
                        Map<String ,Object> user=new HashMap<>();
                        user.put("Name", username);
                        user.put("Email", user_email);
                        user.put("count_of_class","0");
                        user.put("List","");
                        user.put("Date",dateToStr);
                        documentReference.set(user);
                        firebaseUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(image_uri)
                                .setDisplayName(username)
                                .build();
                        firebaseUser.updateProfile(request)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("updateProfile", "onSuccess: Successfully");
                                    }
                                });
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        finish();
                    }
                }
            }
        });

    }
    public void getAnimation(){
        frombtn = AnimationUtils.loadAnimation(this, R.anim.btn_anim);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        login_btn.startAnimation(frombtn);
        google_cardview.startAnimation(frombtn);
        logged_in.startAnimation(frombtn);
        signInButton.startAnimation(frombtn);
        forgot_psd.startAnimation(frombtn);
        email.startAnimation(fromtop);
        textview.startAnimation(fromtop);
        passwd_layout.startAnimation(fromtop);
        paswrd.startAnimation(fromtop);

    }
    public void setUpWidget(){
        login_btn = findViewById(R.id.login_btn);
        passwd_layout = findViewById(R.id.login_password_layout);
        logged_in = findViewById(R.id.logged);
        email = findViewById(R.id.login_email);
        paswrd = findViewById(R.id.login_password);
        textview = findViewById(R.id.textView2);
        forgot_psd =findViewById(R.id.forgot_psd);
        google_cardview = findViewById(R.id.cardView);
        signInButton = findViewById(R.id.sign_in_button);
    }
}