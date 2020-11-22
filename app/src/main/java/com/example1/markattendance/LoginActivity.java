package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    Animation frombtn, fromtop;
    EditText email, paswrd;
    TextView textview,logged_in, forgot_psd,orview;
    CardView google_cardview;
    View line1,line2;
    private FirebaseAuth mAuth;
    SignInButton signInButton;
    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = findViewById(R.id.login_btn);
        logged_in = findViewById(R.id.logged);
        email = findViewById(R.id.login_email);
        paswrd = findViewById(R.id.login_password);
        textview = findViewById(R.id.textView2);
        forgot_psd =findViewById(R.id.forgot_psd);
        google_cardview = findViewById(R.id.cardView);

        mAuth = FirebaseAuth.getInstance();
        frombtn = AnimationUtils.loadAnimation(this, R.anim.btn_anim);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        login_btn.startAnimation(frombtn);
        google_cardview.startAnimation(frombtn);
        signInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("786275489232-hjgaetbs472v9htvv417fj3tqvp8av8l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        logged_in.startAnimation(frombtn);
        signInButton.startAnimation(frombtn);
        forgot_psd.startAnimation(frombtn);
        logged_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        email.startAnimation(fromtop);
        textview.startAnimation(fromtop);
        paswrd.startAnimation(fromtop);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail=email.getText().toString();
                String txtpassword=paswrd.getText().toString();
                if(TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword))
                {
                    Toast.makeText(LoginActivity.this, "Fill all the entries!", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    mAuth.signInWithEmailAndPassword(txtemail, txtpassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        if(mAuth.getCurrentUser().getUid().equals("8JRTM9p8XfWfbG4LhKZW6ypOUj92")){
                                            startActivity(new Intent(LoginActivity.this, AdminPage.class));
                                            finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "User logged in!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                            finish();
                                        }
                                    }
                                    else
                                    {
                                        Log.w("Log In", "Error",task.getException());
                                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
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
            String userID = account.getId();
            String username = account.getDisplayName();
            String email = account.getEmail();
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("users").document(userID);
            Map<String ,Object> user=new HashMap<>();
            user.put("Name", username);
            user.put("Email", email);
            user.put("count_of_class",0);
            user.put("List","");
            documentReference.set(user);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                }
            });
//
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_LONG).show();
        }
    }
    private void updateUI(GoogleSignInAccount account) {
        Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (mAuth.getCurrentUser().getUid().equals("8JRTM9p8XfWfbG4LhKZW6ypOUj92")){
                startActivity(new Intent(LoginActivity.this, AdminPage.class));
                finish();
            }
            else{
                startActivity(new Intent(LoginActivity.this, HomeScreen.class));
                finish();
            }
        }
        else{
            //Nothing
        }
    }
}