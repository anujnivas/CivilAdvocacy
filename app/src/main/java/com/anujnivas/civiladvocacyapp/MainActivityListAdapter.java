package com.anujnivas.civiladvocacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivityListAdapter extends RecyclerView.Adapter<MainActivityViewHolder> {
    private ArrayList<Government_Official> list;
    private final MainActivity mainActivity;
    @NonNull
    @Override
    public MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        itemView.setOnClickListener(mainActivity);
        return new MainActivityViewHolder(itemView);
    }

    MainActivityListAdapter(MainActivity mainActivity,ArrayList<Government_Official> government_officials){
        this.mainActivity=mainActivity;
        list=government_officials;
    }
    @Override
    public void onBindViewHolder(@NonNull MainActivityViewHolder holder, int position) {
            holder.title.setText(list.get(position).getTitle());
            holder.name.setText(list.get(position).getName() + " (" + list.get(position).getParty()+")   ");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
