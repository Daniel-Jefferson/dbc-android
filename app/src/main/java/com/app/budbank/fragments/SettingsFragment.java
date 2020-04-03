package com.app.budbank.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.activities.BaseActivity;
import com.app.budbank.activities.LoginActivity;
import com.app.budbank.activities.NotificationSettingsActivity;
import com.app.budbank.activities.ProfileActivity;
import com.app.budbank.models.UserModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.connection.RealConnection;

public class SettingsFragment extends BaseFragment {

    @BindView(R.id.iv_user_profile)
    ImageView ivProfile;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.rl_profile)
    RelativeLayout lytProfile;
    @BindView(R.id.rl_notification)
    RelativeLayout lytNotification;
    @BindView(R.id.rl_terms)
    RelativeLayout lytTerms;
    @BindView(R.id.rl_privacy)
    RelativeLayout lytPrivacy;
    @BindView(R.id.rl_feedback)
    RelativeLayout lytFeedback;
    @BindView(R.id.tv_logout)
    TextView tvLogout;

    private LocalBroadcast localBroadcast;

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
        registerReceiver();
    }

    public void updateView() {
        UserModel userModel = StorageUtillity.getUserModel(mContext);
        String profileUrl = userModel.getProfileUrl();
        tvUserName.setText(userModel.getUsername());
        if(profileUrl!= null && !TextUtils.isEmpty(profileUrl)) {
            if(BudsBankUtils.isValidContextForGlide(mContext)) {
                Glide.with(mContext)
                        .load(profileUrl)
                        .placeholder(R.drawable.user_placeholder)
                        .into(ivProfile);
            }
        } else
            ivProfile.setImageResource(R.drawable.user_placeholder);
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer,mContext);
        updateView();
        lytProfile.setOnClickListener(this);
        lytNotification.setOnClickListener(this);
        lytTerms.setOnClickListener(this);
        lytPrivacy.setOnClickListener(this);
        lytFeedback.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
    }

    private void goToProfile() {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        mContext.startActivity(intent);
    }

    private void goToNotifcationSettings() {
        Intent intent = new Intent(mContext, NotificationSettingsActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_logout:
                actionLogout();
                break;
            case R.id.rl_profile:
                goToProfile();
                break;
            case R.id.rl_notification:
                goToNotifcationSettings();
                break;
            case R.id.rl_feedback:
                String email = "support@budsbank.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+email));
                startActivity(intent);
                break;
            case R.id.iv_user_profile:
                String profileUrl = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.PROFILE_IMAGE.getValue(), "");
                DialogUtils.showPhotoDialog(mContext,profileUrl );
        }
    }

    private void actionLogout() {
        StorageUtillity.clearAllPreferences(mContext);
        startActivity(new Intent(mContext, LoginActivity.class));
        ((BaseActivity)mContext).finish();
    }

    private void registerReceiver() {
        localBroadcast = new SettingsFragment.LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.UPDATE_PROFILE.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            if (action.equals(AppConstants.Actions.UPDATE_PROFILE.getValue())) {
                updateView();
            }
        }
    }

}
