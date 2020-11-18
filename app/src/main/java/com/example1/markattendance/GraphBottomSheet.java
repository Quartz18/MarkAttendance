package com.example1.markattendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBottomSheet extends BottomSheetDialogFragment {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference dr;
    String userID,class_name, document_name,subject_name,member_name;
    int q=0,y_Value =0;
    List<String> list_of_past_records;
    List<Integer> x_list=new ArrayList<>(),y_list=new ArrayList<>();
    LineGraphSeries series;
    List<DataPoint> datapoints = new ArrayList();
    TextView name_of_data;
    GraphView graph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        document_name = mArgs.getString("document_name");
        class_name = mArgs.getString("class_name");
        subject_name = mArgs.getString("subject_name");
        member_name = mArgs.getString("member_name");
        return inflater.inflate(R.layout.graph_bottom_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFirebase();
        dr = db.collection("Records").document(document_name);
        graph = view.findViewById(R.id.line_graph_layout);
        name_of_data = view.findViewById(R.id.name_of_student);
        name_of_data.setText(member_name);
        String[] list1 = subject_name.split("-");
        list_of_past_records = Arrays.asList(list1);
        getGraphData();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints.toArray(new DataPoint[datapoints.size()]));
        series.setColor(R.color.dark_red);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true);

        graph.addSeries(series);
        graph.setVisibility(View.VISIBLE);

    }
    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    public void getGraphData() {
        q =0; y_Value =0;
        for (String tags: list_of_past_records){
            if (q==0){
                q = q+1;
                datapoints.add(new DataPoint(q, 0));
                continue;
            }
            if (Integer.valueOf(tags)==1){
                y_Value = y_Value +1;
            }
            datapoints.add(new DataPoint(q, (y_Value*100)/q));
            q = q+1;
        }
    }

}
