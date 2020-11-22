package com.example1.markattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link Class_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Class_Fragment extends Fragment {
    RecyclerView batch_recyclerview;
    FloatingActionButton add_class_btn;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<Model_Batch> item_List;
    String userID,number_of_class;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Class_Fragment() {
        // Required empty public constructor
    }
    public static Class_Fragment newInstance(String param1, String param2) {
        Class_Fragment fragment = new Class_Fragment();
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
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(),mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
        db.collection("users").document(userID).collection("Class_List")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Log.d("TAG","Error:"+error.getMessage());
                        }
                        else {
                            item_List = new ArrayList<>();
                            for (DocumentSnapshot querySnapshot : value) {
                                number_of_class = querySnapshot.getString("count_of_class");
                                Model_Batch model_batch = new Model_Batch(querySnapshot.getString("Name"),
                                        userID,
                                        querySnapshot.getId());
                                item_List.add(model_batch);
                            }
                            batch_recyclerview.setAdapter(new Class_Adapter(item_List, Class_Fragment.this));
                        }
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_class_, container, false);
        batch_recyclerview = view.findViewById(R.id.batch_recyclerview);
        setUpRecyclerview();
        setUpFirebase();
        add_class_btn = view.findViewById(R.id.add_button);
        add_class_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        return view;
    }
    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }
    private void setUpRecyclerview()
    {

        batch_recyclerview.setHasFixedSize(true);
        batch_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    public void openDialog(){
        Bundle args = new Bundle();
        args.putString("document_name", number_of_class);
        AddClassDialog addClassDialog = new AddClassDialog();
        addClassDialog.setArguments(args);
        addClassDialog.show(getChildFragmentManager(),"addClassDialog");


    }
}