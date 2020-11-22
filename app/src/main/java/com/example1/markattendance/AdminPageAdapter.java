package com.example1.markattendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminPageAdapter extends RecyclerView.Adapter<AdminPageAdapter.ViewHolder> {

    ArrayList<Model_Batch> item_List;
    AdminPage adminPage;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    String userID;

    public AdminPageAdapter(ArrayList<Model_Batch> item_List, AdminPage adminPage) {
        this.item_List = item_List;
        this.adminPage = adminPage;
    }

    @NonNull
    @Override
    public AdminPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
        AdminPageAdapter.ViewHolder viewHolder = new AdminPageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPageAdapter.ViewHolder holder, int position) {
        holder.user_number.setText(item_List.get(position).getTotal_member());
        holder.user_name.setText(item_List.get(position).getBatch_name());
        holder.user_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(adminPage.getApplicationContext(),holder.user_option_menu);
                popupMenu.inflate(R.menu.member_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.delete_member:
//                                item_list.remove(position);
                                deleteSelectedRow(position);
//
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void deleteSelectedRow(int position) {
        AlertDialog.Builder dailog = new AlertDialog.Builder(adminPage);
        dailog.setTitle("Are you sure?");
        dailog.setMessage("Deleting this account will result in completely removing the account from the system and the user "+
                item_List.get(position).getBatch_name()+" and the user won't be able to access the application.");
        dailog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
    }

    private void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
    }

    @Override
    public int getItemCount() {
        return item_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_number;
        TextView user_name;
        TextView user_option_menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_number = itemView.findViewById(R.id.user_number_item);
            user_name = itemView.findViewById(R.id.user_name_item);
            user_option_menu = itemView.findViewById(R.id.user_option_menu);
        }
    }
}
