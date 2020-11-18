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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddClassDialog extends AppCompatDialogFragment {
    EditText class_name_text;
    Date date = new Date();
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_add_class, null);
        final String dateToStr = DateFormat.getDateTimeInstance().format(date);
        builder.setView(view)
                .setTitle("Add Class")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = class_name_text.getText().toString();
                        if(TextUtils.isEmpty(name))
                        {
                            Toast.makeText(getContext(),"Enter class name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            final String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference acct = db.collection("users").document(userID);
                            Map<String, Object> add_class_name = new HashMap<>();
                            List<String> past_list = new ArrayList<>();
                            DocumentReference class_list = db.collection("users").document(userID).collection("Class_List").document(name);
                            add_class_name.put("Counter","0");
                            add_class_name.put("List","");
                            add_class_name.put("count_of_records","0");
                            add_class_name.put("count_of_subjects","0");
                            add_class_name.put("Date",dateToStr);
                            class_list.set(add_class_name);
                            Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        class_name_text = view.findViewById(R.id.class_name);
        return builder.create();
    }
}
