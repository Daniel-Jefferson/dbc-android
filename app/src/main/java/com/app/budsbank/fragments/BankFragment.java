package com.app.budsbank.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.adapters.ViewPagerAdapter;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.CustomViewpager;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankFragment extends BaseFragment {

    @BindView(R.id.tv_tab_available)
    TextView tvTabAvailable;
    @BindView(R.id.tv_tab_redeemed)
    TextView tvTabRedeemed;
    @BindView(R.id.tv_coins)
    TextView tvCoins;
    @BindView(R.id.view_pager)
    CustomViewpager viewPager;
    @BindView(R.id.ll_tabs_container)
    LinearLayout lytTabsContainer;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    private ViewPagerAdapter viewPagerAdapter;
    private LocalBroadcast localBroadcast;


    public BankFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer, mContext);
        updateCoins();
        registerReceiver();
        loadTabView();
    }
    
    private void updateCoins() {
        String coinStr = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), "0");
        tvCoins.setText(coinStr);
    }

    private void loadTabView() {
        lytTabsContainer.setVisibility(View.VISIBLE);
        tvTabAvailable.setSelected(true);
        tvTabAvailable.setOnClickListener(this);
        tvTabRedeemed.setOnClickListener(this);
        initPagerAdapter();
    }

    private void initPagerAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AvailableVoucherFragment());
        viewPagerAdapter.addFragment(new RedeemedVoucherFragment());
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    private void setSelectedTab(int position) {
        switch (position) {
            case 0:
                tvTabAvailable.setSelected(true);
                tvTabRedeemed.setSelected(false);
                viewPager.setCurrentItem(position);
                break;
            case 1:
                tvTabAvailable.setSelected(false);
                tvTabRedeemed.setSelected(true);
                viewPager.setCurrentItem(position);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_tab_available:
                setSelectedTab(0);
                break;
            case R.id.tv_tab_redeemed:
                setSelectedTab(1);
                break;
            case R.id.btn_confirm:
                communicator.switchTab(2);
                break;
        }
    }

    private void registerReceiver() {
        localBroadcast = new LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.UPDATE_COINS.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if (action.equals(AppConstants.Actions.UPDATE_COINS.getValue())) {
                updateCoins();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcast);
    }
}
