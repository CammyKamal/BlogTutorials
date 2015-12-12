package com.example.kamalvaid.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by kamal.vaid on 02-04-2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {

      Context mContext;
      ArrayList<String>mListItems;
        public  RecyclerViewAdapter(Context mContext,ArrayList<String>mListItems){
            this.mContext=mContext;
            this.mListItems=mListItems;
        }


    @Override
    public RecyclerViewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview, viewGroup, false);
        return new RecyclerViewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewViewHolder recyclerViewViewHolder, int i) {

        recyclerViewViewHolder.txtView.setText(mListItems.get(i));
    }


    @Override
    public int getItemCount() {
        return mListItems.size();
    }
}
