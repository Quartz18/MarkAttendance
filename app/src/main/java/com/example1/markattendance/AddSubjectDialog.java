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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddSubjectDialog extends AppCompatDialogFragment {
    EditText subject_name_text;
    String counter,document_name,dateToStr,userID;
    int counting;
    Date date = new Date();
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_add_subject, null);
        Bundle mArgs = getArguments();
        document_name = mArgs.getString("document_name");
        dateToStr = DateFormat.getDateTimeInstance().format(date);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userID)
                .collection("Class_List").document(document_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        counter = documentSnapshot.getString("count_of_subjects");
                        counting = Integer.valueOf(counter) + 1;
                    }
                });
        builder.setView(view)
                .setTitle("Add Subject")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = subject_name_text.getText().toString();
                        if(TextUtils.isEmpty(name))
                        {
                            Toast.makeText(getContext(),"Enter the Subject name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String, Object> add_counter = new HashMap<>();
                            DocumentReference counter_list = db.collection("users").document(userID)
                                    .collection("Class_List").document(document_name);
                            add_counter.put("count_of_subjects",String.valueOf(counting));
                            counter_list.update(add_counter);
                            Map<String, Object> add_class_name = new HashMap<>();
                            DocumentReference subject_list = db.collection("users").document(userID)
                                    .collection("Class_List").document(document_name)
                                    .collection("Subjects").document(String.valueOf(counting));
                            add_class_name.put("Name",name);
                            add_class_name.put("List","");
                            add_class_name.put("count_of_records","0");
                            add_class_name.put("Date",dateToStr);
                            subject_list.set(add_class_name);
                            Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        subject_name_text = view.findViewById(R.id.subject_name);
        return builder.create();
    }
}
