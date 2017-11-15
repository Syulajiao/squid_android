package com.get.vpn.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.get.vpn.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by istmedia-m1 on 2/18/17.
 */

public class AboutUsFragment extends Fragment {
     public static AboutUsFragment newInstance() {

        Bundle args = new Bundle();

        AboutUsFragment fragment = new AboutUsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Toolbar mToolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_aboutus,
                container,
                false);


        // toolbar
        mToolbar = (Toolbar) view.findViewById(R.id.id_toolbar_aboutus);
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //    ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_nav_back);


        initAction();
        return view;
    }

    private void initAction() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


}
