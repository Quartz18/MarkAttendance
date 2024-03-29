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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class AddMembersDialog extends AppCompatDialogFragment {
    EditText member_name,member_id,device_name;
    Button connect_button;
    String counter,connect_data,ipAddress,macAddress, id,name,device,document_name;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    int counting;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_add_members, null);
        Bundle mArgs = getArguments();
        document_name = mArgs.getString("document_name");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String userId = mAuth.getCurrentUser().getUid();
        int x = document_name.indexOf(userId);
        final String class_name = document_name.substring(0,x-1);
        db.collection("users").document(userId)
                .collection("Class_List").document(class_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        counter = documentSnapshot.getString("count_of_students");
                        counting = Integer.parseInt(counter) + 1;
                    }
                });

        builder.setView(view)
                .setTitle("Add Student")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        id = member_id.getText().toString();
                        name = member_name.getText().toString();
                        device = device_name.getText().toString();
                        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(device) || TextUtils.isEmpty(id))
                        {
                            Toast.makeText(getContext(),"Enter all the details.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String, Object> add_counter = new HashMap<>();
                            DocumentReference counter_list = db.collection("users").document(userId)
                                    .collection("Class_List").document(class_name);
                            Map<String, Object> add_class_name = new HashMap<>();
                            DocumentReference class_list = db.collection(document_name).document(String.valueOf(counting));
                            add_counter.put("count_of_students",String.valueOf(counting));
                            add_class_name.put("member_id",id);
                            add_class_name.put("member_name",name);
                            add_class_name.put("device_name",device);
                            add_class_name.put("counting","0");
                            add_class_name.put("stats_list","");
                            class_list.set(add_class_name);
                            counter_list.update(add_counter);
                            Toast.makeText(getContext(),"Added!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        member_name = view.findViewById(R.id.member_name_dialog);
        member_id = view.findViewById(R.id.member_id_dialog);
        device_name = view.findViewById(R.id.device_name_dialog);
        connect_button = view.findViewById(R.id.connect);
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotspotConnection hotspotConnection = new HotspotConnection();
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
