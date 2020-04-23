package com.app.budsbank.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.budsbank.R;
import com.app.budsbank.adapters.ViewPagerAdapter;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.CustomViewpager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealsFragment extends BaseFragment {


    public DealsFragment() {
    }


    @BindView(R.id.tv_tab_following)
    TextView tvTabFollowing;
    @BindView(R.id.tv_tab_all)
    TextView tvTabAll;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.view_pager)
    CustomViewpager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer, mContext);
        tvTabFollowing.setSelected(true);
        tvTabFollowing.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);
        initPagerAdapter();
    }

    private void initPagerAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new FollowingDealsFragment());
        viewPagerAdapter.addFragment(new AllDealsFragment());
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    private void setSelectedTab(int position) {
        switch (position) {
            case 0:
                tvTabFollowing.setSelected(true);
                tvTabAll.setSelected(false);
                viewPager.setCurrentItem(position);
                break;
            case 1:
                tvTabFollowing.setSelected(false);
                tvTabAll.setSelected(true);
                viewPager.setCurrentItem(position);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_tab_following:
                setSelectedTab(0);
                break;
            case R.id.tv_tab_all:
                setSelectedTab(1);
                break;
        }
    }
}
