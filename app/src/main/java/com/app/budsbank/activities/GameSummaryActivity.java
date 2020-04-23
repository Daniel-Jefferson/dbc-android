package com.app.budsbank.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.VoucherModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameSummaryActivity extends BaseActivity {
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.tv_redeem)
    TextView tvRedeem;
    @BindView(R.id.tv_play_again)
    TextView tvPlayAgain;
    @BindView(R.id.iv_user_profile_image)
    ImageView ivUserProfileImage;
    @BindView(R.id.tv_user_name)
    TextView tvUsername;
    @BindView(R.id.tv_result_desc)
    TextView tvResultDesc;
    @BindView(R.id.tv_coins_earned)
    TextView tvCoinsEarned;
    @BindView(R.id.ll_voucher_container)
    View voucherContainer;
    @BindView(R.id.disp_name)
    TextView tvDispensaryName;
    @BindView(R.id.disp_address)
    TextView tvDispensaryAddress;
    private VoucherModel voucherModel;
    private int questionAnswered;
    private int remainCount;
    private DispensaryModel dispensaryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sumarry);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    public void initViews() {
        BudsBankUtils.setViewUnderStatusBar(scrollView, mContext);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            voucherModel = (VoucherModel) bundle.getSerializable(AppConstants.IntentKeys.VOUCHER_MODEL.getValue());
            dispensaryModel = (DispensaryModel) bundle.get(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue());
            questionAnswered = bundle.getInt(AppConstants.IntentKeys.CORRECT_ANSWERS_COUNT.getValue(), 0);
            remainCount = bundle.getInt(AppConstants.IntentKeys.USER_DISABLED_DISPENSARIES.getValue(), 0);
        }
        tvRedeem.setText(R.string.learn_more);
        tvPlayAgain.setOnClickListener(this);
        ivCross.setOnClickListener(this);
        populateUI();
    }

    private void populateUI() {
        tvResultDesc.setText(String.format(getString(R.string.result), questionAnswered));
        tvUsername.setText(StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USERNAME.getValue(), ""));
        String profileUrl = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.PROFILE_IMAGE.getValue(), "");
        if(profileUrl!=null && !TextUtils.isEmpty(profileUrl)) {
            if(BudsBankUtils.isValidContextForGlide(this)) {
                Glide.with(this)
                        .load(profileUrl)
                        .placeholder(R.drawable.user_placeholder)
                        .into(ivUserProfileImage);
            }
        }
        if (voucherModel == null) {
            tvCoinsEarned.setText("0");
            voucherContainer.setVisibility(View.GONE);
        } else {
            String coinStr = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), "0");
            int coin = TextUtils.getIntValue(coinStr);
            coin += 5;
            StorageUtillity.saveDataInPreferences(this, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), String.valueOf(coin));
            BudsBankUtils.broadcastAction(this, AppConstants.Actions.UPDATE_COINS.getValue());
            tvCoinsEarned.setText("5");

            tvDispensaryName.setText(voucherModel.getDispensaryName());
            tvDispensaryAddress.setText(voucherModel.getDispensaryAddress());
            voucherContainer.setVisibility(View.VISIBLE);
        }

        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        if (voucherModel != null)
            mainStorageUtils.addCompletedVoucher(voucherModel);

        if (remainCount == 0) {
            mainStorageUtils.addCompletedDispensary(dispensaryModel);
        }
        BudsBankUtils.broadcastAction(this, AppConstants.Actions.AVAILABLE_VOUCHER.getValue());
        BudsBankUtils.broadcastAction(this, AppConstants.Actions.COMPLETED_DISPENSARY.getValue());
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_cross:
                finish();
                break;
            case R.id.tv_play_again:
                finish();
                break;
        }
    }
}
