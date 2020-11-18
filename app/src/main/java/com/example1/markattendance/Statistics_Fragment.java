package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Statistics_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Statistics_Fragment extends Fragment {

    RecyclerView statistics_recyclerview;
    ArrayList<Model_Batch> item_List = new ArrayList<Model_Batch>();
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    String userID;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Statistics_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Statistics_Fragment.
     */
    // TODO: Rename and change types and number of parameters
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
        View view =  inflater.inflate(R.layout.fragment_statistics_, container, false);
        statistics_recyclerview = view.findViewById(R.id.statistics_recyclerview);
        setUpRecyclerview();
        setUpFirebase();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (item_List.size() > 0){
            item_List.clear();
        }
        db.collection("users").document(userID).collection("Class_List")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot: task.getResult()){
                            //Model_Batch model_batch = new Model_Batch(querySnapshot.getId(),"70");
                            item_List.add(new Model_Batch(querySnapshot.getId(),
                                    querySnapshot.getString("Counter"),
                                    querySnapshot.getString("count_of_subjects")));
                        }
                        statistics_recyclerview.setAdapter(new StatisticsAdapter(item_List, getContext()));
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
        statistics_recyclerview.setHasFixedSize(true);
        statistics_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}