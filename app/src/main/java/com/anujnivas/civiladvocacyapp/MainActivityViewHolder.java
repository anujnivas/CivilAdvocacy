package com.anujnivas.civiladvocacyapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivityViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView name;
    public MainActivityViewHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.list_item_title_tv);
        name=itemView.findViewById(R.id.list_item_name_tv);
    }
}
