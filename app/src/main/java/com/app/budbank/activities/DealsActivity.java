package com.app.budbank.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.adapters.ViewPagerAdapter;
import com.app.budbank.fragments.AllDealsFragment;
import com.app.budbank.fragments.FollowingDealsFragment;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.CustomViewpager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealsActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        mContext=this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer, mContext);
        ivBack.setOnClickListener(this);
        tvTabFollowing.setSelected(true);
        tvTabFollowing.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);
        initPagerAdapter();
    }

    private void initPagerAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
