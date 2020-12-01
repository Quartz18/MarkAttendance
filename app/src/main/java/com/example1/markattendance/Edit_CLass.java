package com.example1.markattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class Edit_CLass extends AppCompatDialogFragment {
    EditText class_name_text;
    String class_id,userID,new_class_list;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_add_class, null);
        Bundle mArgs = getArguments();
        class_id = mArgs.getString("class_id");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userID)
                .collection("Class_List").document(class_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        class_name_text.setText(documentSnapshot.getString("Name"));
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
                        String name = class_name_text.getText().toString();
                        if(TextUtils.isEmpty(name))
                        {
                            Toast.makeText(getContext(),"Enter the class name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String, Object> add_class_name = new HashMap<>();
                            DocumentReference class_list = db.collection("users").document(userID)
                                    .collection("Class_List").document(class_id);
                            add_class_name.put("Name",name);
                            class_list.update(add_class_name);
                            Toast.makeText(getContext(),"Changes Applied!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        class_name_text = view.findViewById(R.id.class_name);
        return builder.create();
    }
}
