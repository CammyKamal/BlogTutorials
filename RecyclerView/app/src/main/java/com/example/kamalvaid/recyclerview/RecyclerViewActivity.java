package com.example.kamalvaid.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class RecyclerViewActivity extends Activity {

    ArrayList<String>mListitems;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiView();
    }

    private void initiView(){

        mListitems=new ArrayList<String>();

        for(int i=0;i<125;i++){
            mListitems.add("List Item "+ i);
        }

        mRecyclerView=(RecyclerView) findViewById(R.id.recyclervew);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

       //The LinearLayoutManager is currently the only default implementation of LayoutManager. You can use this class to create either vertical or horizontal lists.
        LinearLayoutManager layoutManager = new LinearLayoutManager(RecyclerViewActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //setting up the adapter
        RecyclerViewAdapter mAdapter=new RecyclerViewAdapter(RecyclerViewActivity.this,mListitems);
        mRecyclerView.setAdapter(mAdapter);

    }





}
