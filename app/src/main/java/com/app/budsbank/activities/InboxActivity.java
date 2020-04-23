package com.app.budsbank.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.adapters.ViewPagerAdapter;
import com.app.budsbank.fragments.ReadNotificationFragment;
import com.app.budsbank.fragments.UnreadNotificationsFragment;
import com.app.budsbank.models.NotificationModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.CustomViewpager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InboxActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_tab_read)
    TextView tvTabRead;
    @BindView(R.id.tv_tab_unread)
    TextView tvTabUnread;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.view_pager)
    CustomViewpager viewPager;
    private ArrayList<NotificationModel> readNotifications= new ArrayList<>();
    private ArrayList<NotificationModel> unreadNotifications= new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        mContext=this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer, mContext);
        Bundle bundle =getIntent().getExtras();
        if(bundle != null) {
            readNotifications = (ArrayList<NotificationModel>) bundle.getSerializable(AppConstants.IntentKeys.READ_NOTIFICATIONS.getValue());
            unreadNotifications = (ArrayList<NotificationModel>) bundle.getSerializable(AppConstants.IntentKeys.UNREAD_NOTIFICATIONS.getValue());
        }

        ivBack.setOnClickListener(this);
        tvTabUnread.setSelected(true);
        tvTabUnread.setOnClickListener(this);
        tvTabRead.setOnClickListener(this);
        initPagerAdapter();
    }

    private void initPagerAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new UnreadNotificationsFragment(unreadNotifications));
        viewPagerAdapter.addFragment(new ReadNotificationFragment(readNotifications));
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    private void setSelectedTab(int position) {
        switch (position) {
            case 0:
                tvTabUnread.setSelected(true);
                tvTabRead.setSelected(false);
                viewPager.setCurrentItem(position);
                break;
            case 1:
                tvTabUnread.setSelected(false);
                tvTabRead.setSelected(true);
                viewPager.setCurrentItem(position);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_tab_unread:
                setSelectedTab(0);
                break;
            case R.id.tv_tab_read:
                setSelectedTab(1);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public interface NotificationCallback {
        void callback(NotificationModel notificationModel, int position);
    }
}
