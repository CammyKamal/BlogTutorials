package com.toolbardemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kamal on 10/11/2015.
 */
public class ToolBarFragment extends Fragment {

    private Toolbar toolbar;
    private TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolBar();
    }

    private void setToolBar() {
        toolbar=(Toolbar)getView().findViewById(R.id.toolbar);
        tvTitle=(TextView)getView().findViewById(R.id.tv_title);
        tvTitle.setText("Toolbar Title");
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                ((MainActivity)getActivity()).finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }}
