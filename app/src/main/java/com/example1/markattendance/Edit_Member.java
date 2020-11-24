package com.example1.markattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Edit_Member extends AppCompatDialogFragment {
    EditText member_name,member_id,device_name;
    Button connect_button;
    String connect_data,ipAddress,macAddress,id_member,userID,document_name;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_add_members, null);
        Bundle mArgs = getArguments();
        document_name = mArgs.getString("document_name");
        id_member = mArgs.getString("id_member");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        final String userId = mAuth.getCurrentUser().getUid();
        int x = document_name.indexOf(userId);
        final String class_name = document_name.substring(0,x-1);
        db.collection(document_name).document(id_member)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        member_name.setText(documentSnapshot.getString("member_name"));
                        member_id.setText(documentSnapshot.getString("member_id"));
                        device_name.setText(documentSnapshot.getString("device_name"));

                    }
                });
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
                        String name = member_name.getText().toString();
                        String id = member_id.getText().toString();
                        String device = device_name.getText().toString();
                        if(TextUtils.isEmpty(name))
                        {
                            Toast.makeText(getContext(),"Enter class name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String, Object> add_class_name = new HashMap<>();
                            DocumentReference class_list = db.collection(document_name).document(id_member);
                            add_class_name.put("member_id",id);
                            add_class_name.put("member_name",name);
                            add_class_name.put("device_name",device);
                            class_list.update(add_class_name);
                            Toast.makeText(getContext(),"Changes Applied!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        member_name = view.findViewById(R.id.member_name_dialog);
        member_id = view.findViewById(R.id.member_id_dialog);
        device_name = view.findViewById(R.id.device_name_dialog);
        connect_button = view.findViewById(R.id.connect);
        getListOfConnectedDevice();
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect_data = getListOfConnectedDevice();
                device_name.setText(connect_data);
            }
        });
        return builder.create();
    }
    public String getListOfConnectedDevice() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                BufferedReader br = null;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] splitted = line.split(" +");

                        if (splitted != null && splitted.length >= 4) {

                            ipAddress = splitted[0];
                            macAddress = splitted[3];

                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);  // this is network call so we cant do that on UI thread, so i take background thread.
                            if (isReachable) {
                                Log.d("Device Information", ipAddress + " : " + macAddress);
                                return ;
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        return ipAddress + " "+macAddress;
    }
}
