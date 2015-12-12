package com.example.kamalvaid.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kamal.vaid on 02-04-2015.
 */
public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

    public TextView txtView;

    public RecyclerViewViewHolder(View itemView) {
        super(itemView);
        txtView = (TextView) itemView.findViewById(R.id.title);
    }
}