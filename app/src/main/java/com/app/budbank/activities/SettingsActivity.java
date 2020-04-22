package com.app.budbank.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.budbank.R;
import com.app.budbank.models.UserModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity  extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_user_profile)
    ImageView ivProfile;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.rl_profile)
    RelativeLayout lytProfile;
//    @BindView(R.id.rl_notification)
//    RelativeLayout lytNotification;
    @BindView(R.id.rl_terms)
    RelativeLayout lytTerms;
    @BindView(R.id.rl_privacy)
    RelativeLayout lytPrivacy;
    @BindView(R.id.rl_feedback)
    RelativeLayout lytFeedback;
    @BindView(R.id.tv_logout)
    TextView tvLogout;

    private SettingsActivity.LocalBroadcast localBroadcast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // apply bindings
        ButterKnife.bind(this);

        // set context
        mContext = this;

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
        ivBack.setOnClickListener(this);
        lytProfile.setOnClickListener(this);
//        lytNotification.setOnClickListener(this);
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
            //case R.id.rl_notification:
            //    goToNotifcationSettings();
            //    break;

            case R.id.rl_terms:
                Uri termsUri = Uri.parse("http://budsbank.com/terms-of-use");
                Intent termsIntent = new Intent(Intent.ACTION_VIEW, termsUri);
                startActivity(termsIntent);
                break;
            case R.id.rl_privacy:
                Uri privacyUri = Uri.parse("http://budsbank.com/privacy-policy");
                Intent privacyIntent = new Intent(Intent.ACTION_VIEW, privacyUri);
                startActivity(privacyIntent);
                break;
            case R.id.rl_feedback:
                String email = "support@budsbank.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+email));
                startActivity(intent);
                break;
            case R.id.iv_user_profile:
                String profileUrl = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.PROFILE_IMAGE.getValue(), "");
                DialogUtils.showPhotoDialog(mContext,profileUrl );
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void actionLogout() {
        StorageUtillity.clearAllPreferences(mContext);
        startActivity(new Intent(mContext, LoginActivity.class));
        ((BaseActivity)mContext).finish();
    }

    private void registerReceiver() {
        localBroadcast = new SettingsActivity.LocalBroadcast();
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
