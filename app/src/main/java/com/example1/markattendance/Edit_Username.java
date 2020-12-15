package com.example1.markattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Edit_Username extends AppCompatDialogFragment {
    EditText edit_user_name;
    String userID,new_user_id;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_edit_username, null);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        edit_user_name.setText(documentSnapshot.getString("Name"));
                    }
                });
        builder.setView(view)
                .setTitle("Edit Class")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_user_id = edit_user_name.getText().toString();
                        if(TextUtils.isEmpty(new_user_id))
                        {
                            Toast.makeText(getContext(),"Enter the user name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DocumentReference change_user_name = db.collection("users").document(userID);
                            Map<String, Object> object_edit_name = new HashMap<>();
                            object_edit_name.put("Name",new_user_id);
                            change_user_name.update(object_edit_name);
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(new_user_id)
                                    .build();
                            firebaseUser.updateProfile(request)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("updateProfile", "onSuccess: Successfully");
                                        }
                                    });
                            Toast.makeText(getContext(),"Username is changed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        edit_user_name = view.findViewById(R.id.edit_user_name);
        return builder.create();
    }
}
