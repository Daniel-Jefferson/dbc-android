package com.app.budsbank.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.budsbank.R;
import com.app.budsbank.adapters.NotificationSettingsAdapter;
import com.app.budsbank.models.FollowedDispensariesModel;
import com.app.budsbank.models.NotificationSettingsResponseModel;
import com.app.budsbank.models.SettingsModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSettingsActivity extends BaseActivity {

    @BindView(R.id.rv_notification)
    RecyclerView rvNotification;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    private ArrayList<FollowedDispensariesModel> followedDispensaryList;
    private  NotificationSettingsAdapter adapter;
    private int page = 1;
    private boolean isAllLoaded=false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        followedDispensaryList = new ArrayList<>();
        ivBack.setOnClickListener(this);
        rvNotification.setOnScrollListener(onScrollListener);
        getNotificationSettings(page);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if(!isAllLoaded && !isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= AppConstants.PAGE_SIZE-1) {
                    page++;
                    getNotificationSettings(page);
                }
            }
        }
    };

    private void getNotificationSettings(int page) {
        int pageSize = AppConstants.PAGE_SIZE;
        rvNotification.removeOnScrollListener(onScrollListener);
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(rvNotification, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        isLoading = true;
        DialogUtils.showLoading(mContext);
        APIController.getNotificationSettings(sessionToken, userId , page, pageSize, new Callback<NotificationSettingsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Response<NotificationSettingsResponseModel> response) {
                DialogUtils.stopLoading();
                isLoading = false;
                rvNotification.setOnScrollListener(onScrollListener);
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvNotification, getString(R.string.call_fail_error), mContext );
                    return;
                }
                NotificationSettingsResponseModel notificationSettingsResponseModel = response.body();
                if (notificationSettingsResponseModel != null) {
                    if(page ==1) {
                        followedDispensaryList = notificationSettingsResponseModel.getSettings();
                        if (followedDispensaryList != null && followedDispensaryList.size() > 0) {
                            setupAdapter();
                        }
                    } else {
                        int size = followedDispensaryList.size();
                        followedDispensaryList.addAll(notificationSettingsResponseModel.getSettings());
                        adapter.notifyItemRangeChanged(size, followedDispensaryList.size());
                        if(notificationSettingsResponseModel.getPageInfo().getCount() == followedDispensaryList.size())
                            isAllLoaded = true;
                    }
                } else {
                    DialogUtils.showSnackBar(rvNotification, notificationSettingsResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(rvNotification, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void enableNotification(String dispensaryId, boolean enable,int position) {
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.setDispensaryId(dispensaryId);
        settingsModel.setUserId(userId);
        if(enable)
            settingsModel.setEnabled(getString(R.string.truee));
        else
            settingsModel.setEnabled(getString(R.string.falsee));
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvNotification.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemChanged(position);
                }
            });

            DialogUtils.showSnackBar(rvNotification, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }

        APIController.enableNotification(sessionToken, settingsModel , new Callback<NotificationSettingsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Response<NotificationSettingsResponseModel> response) {

                if (!response.isSuccessful()) {
                    rvNotification.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                    DialogUtils.showSnackBar(rvNotification, getString(R.string.call_fail_error), mContext );
                    return;
                }
                NotificationSettingsResponseModel notificationSettingsResponseModel = response.body();
                if (notificationSettingsResponseModel != null) {

                } else {
                    DialogUtils.showSnackBar(rvNotification, getString(R.string.call_fail_error), mContext);
                    rvNotification.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Throwable t) {

                rvNotification.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemChanged(position);
                    }
                });
                DialogUtils.showSnackBar(rvNotification, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvNotification.setLayoutManager(linearLayoutManager);
       adapter = new NotificationSettingsAdapter(mContext,followedDispensaryList, new SettingsEnableCallback() {
            public void callback(String dispensaryId, boolean enable, int position) {
                enableNotification(dispensaryId,enable, position);
            }
        });
        rvNotification.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            finish();
            break;
        }
    }

    public interface SettingsEnableCallback {
        void callback(String dispensaryId, boolean enable, int position);
    }
}
