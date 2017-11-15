package com.get.vpn.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.get.vpn.R;
import com.get.vpn.core.LocalVpnService;
import com.get.vpn.model.UserModel;
import com.get.vpn.model.VpnModel;
import com.get.vpn.otto.BusProvider;
import com.get.vpn.otto.ChangeVpnChannelEvent;
import com.get.vpn.restful.VpnRestful;
import com.get.vpn.service.VpnService;
import com.get.vpn.utils.ServerLogoUpdater;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by istmedia-m1 on 2/16/17.
 */

public class ServerListFragment extends Fragment {
    private static final String TAG="ServerListFragment";

    //UI
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView    mServerRecyclerView;
    private ServerListAdapter   mServerListAdapter;
    private Toolbar mToolbar;
    private Integer             mSelectedItem;

    //data
    private Handler mHandler = new Handler();
    private UserModel mUserModel;
    private boolean mRequesting = false;
    private boolean mLoginSilentlyEver = false;
    private List<VpnModel> mVpnModels = new ArrayList<>();
    private String mCurrentDefaultVpnName = "";

    public static ServerListFragment newInstance() {

        Bundle args = new Bundle();

        ServerListFragment fragment = new ServerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        mSelectedItem = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    private void initData() {
        mUserModel = VpnService.getLoginUserModel(getActivity().getApplicationContext());
        ArrayList<VpnModel> models = (ArrayList<VpnModel>) getActivity().getIntent().getSerializableExtra("VpnModels");
        if (models != null && !models.isEmpty()) {
            mVpnModels.clear();
            mVpnModels.addAll(models);
        }

        mCurrentDefaultVpnName = getActivity().getIntent().getStringExtra("CurrentDefaultVpnName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_list,
                container,
                false);

        mServerRecyclerView = (RecyclerView)view.findViewById(R.id.id_recycler_list_server);
        mServerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // toolbar
        mToolbar = (Toolbar) view.findViewById(R.id.id_toolbar_serverlist);
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //    ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_nav_back);

       //swipe refresh layout
       mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestVpn();
            }
        });

        initAction();
        updateUI();
        initCurrentStatus();

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

    private void initCurrentStatus() {
        if (mVpnModels.isEmpty()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    requestVpn();
                }
            }, 500);
        }
    }

    private void requestVpn() {
        if (mRequesting)
            return;

        mRequesting = true;
        VpnRestful.queryServers(mUserModel.getEmail(), mUserModel.getToken(), this.getActivity().getApplicationContext(),
                new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body;
                    body = VpnRestful.DecryptResponse(response);
                    if (null == body) {
                         if (mLoginSilentlyEver) {
                            goToLogin();
                        } else {
                            mLoginSilentlyEver = true;
                            loginSilently();
                        }
                        return;
                    }

                    JSONObject jsonObject = JSONObject.parseObject(body);
                    if (jsonObject.getIntValue("code") == -9975
                         || jsonObject.getIntValue("code") == -9983) {
                        if (mLoginSilentlyEver) {
                            goToLogin();
                        } else {
                            mLoginSilentlyEver = true;
                            loginSilently();
                        }
                        return;
                    }

                    List<VpnModel> models = VpnModel.parseVpnModels(body);
                    if (models != null) {
                        mVpnModels.clear();
                        mVpnModels.addAll(models);
                    }

                    updateUI();
                    doneRefresh();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                doneRefresh();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("force_login", true);
        startActivity(intent);
        getActivity().finish();
    }

    private void loginSilently() {
        VpnRestful.login(mUserModel.getEmail(), VpnService.getLoginUserPwd(this.getActivity().getApplicationContext()), mUserModel.getToken(),
                this.getActivity().getApplicationContext(),
                 new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = null;
                body = VpnRestful.DecryptResponse(response);
                if (null == body) {
                    goToLogin();
                    return;
                }

                UserModel userModel = UserModel.parseUserModel(body);
                if (userModel != null) {
                    VpnService.setLoginUserInfo(getActivity().getApplicationContext(), body);
                    if (userModel.getCode() != 0) {
                        goToLogin();
                        return;
                    }
                    requestVpn();
                } else {
                    goToLogin();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                goToLogin();
            }
        });
    }

    private void doneRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRequesting = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 800);
    }

    private void updateUI() {
        if (mServerListAdapter == null) {
            mServerListAdapter = new ServerListAdapter();
            mServerRecyclerView.setAdapter(mServerListAdapter);
        }
        else {
            mServerListAdapter.notifyDataSetChanged();
        }
    }

    private class ServerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private VpnModel        mVpnServer;
        private ImageView mImgGeo;
        private TextView mTextDes;
        private ImageView       mImgSig;
        private LinearLayout mItemLayout;

        public ServerHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mImgGeo = (ImageView)itemView.findViewById(R.id.id_item_geo);
            mTextDes = (TextView)itemView.findViewById(R.id.id_item_des);
            mImgSig = (ImageView)itemView.findViewById(R.id.id_item_sig);
            mItemLayout = (LinearLayout)itemView.findViewById(R.id.id_listitem_server);
        }

        public void bindServer(VpnModel vpn) {
            mVpnServer = vpn;

            String strBuf = mVpnServer.getDesc();
            mTextDes.setText(strBuf);

            mImgGeo.setImageResource(ServerLogoUpdater.GetServerLogoRid(mVpnServer));

            String currentVpnDesc = LocalVpnService.RunningVpnModel == null ? "" : LocalVpnService.RunningVpnModel.getDesc();
            if (TextUtils.isEmpty(currentVpnDesc)) {
                currentVpnDesc = mCurrentDefaultVpnName;
            }
            if (currentVpnDesc.equals(mVpnServer.getDesc())) {
                mItemLayout.setBackgroundColor(Color.parseColor("#53BAE5"));
            } else {
                mItemLayout.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public void onClick(View v) {
            ChangeVpnChannelEvent changeVpnChannelEvent = new ChangeVpnChannelEvent(mVpnServer);
            BusProvider.getInstance().post(changeVpnChannelEvent);
            getActivity().finish();
        }
    }

    private class ServerListAdapter extends RecyclerView.Adapter<ServerHolder> {


        public  ServerListAdapter() {

       }

        @Override
        public ServerHolder onCreateViewHolder(ViewGroup parent,
                                               int ViewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_server, parent, false);

            return new ServerHolder(view);
        }

        @Override
        public void onBindViewHolder(ServerHolder holder, int position) {
            holder.bindServer(mVpnModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mVpnModels.size();
        }
    }


}
