package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Attendance_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Attendance_Fragment extends Fragment {

    RecyclerView attendance_recyclerview;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Attendance_Fragment() {
        // Required empty public constructor
    }
    public static Statistics_Fragment newInstance(String param1, String param2) {
        Statistics_Fragment fragment = new Statistics_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_attendance_, container, false);
        attendance_recyclerview = view.findViewById(R.id.attendance_recyclerview);
        setUpRecyclerview();
        setUpFirebase();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        db.collection("users").document(userID).collection("Class_List")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        loadDataFromFirebase();


                    }
                });

    }
    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    private void setUpRecyclerview()
    {
        attendance_recyclerview.setHasFixedSize(true);
        attendance_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadDataFromFirebase(){
        ArrayList<Model_Member> item_List = new ArrayList<Model_Member>();
        db.collection("users").document(userID).collection("Class_List")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            Model_Member model_member = new Model_Member(querySnapshot.getString("Name"),
                                    querySnapshot.getString("count_of_students"),
                                    querySnapshot.getString("count_of_subjects"),
                                    querySnapshot.getId());
                            item_List.add(model_member);
                        }
                        attendance_recyclerview.setAdapter(new AttendanceAdapter(item_List, getContext()));
                    }
                });

    }
}