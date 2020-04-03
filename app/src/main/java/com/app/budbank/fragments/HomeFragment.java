package com.app.budbank.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.activities.DealsActivity;
import com.app.budbank.activities.ProfileActivity;
import com.app.budbank.adapters.DispensaryAdapter;
import com.app.budbank.adapters.VoucherAdapter;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.GetNotificationResponseModel;
import com.app.budbank.models.MarkReadModel;
import com.app.budbank.models.NotificationModel;
import com.app.budbank.models.Notifications;
import com.app.budbank.models.ResponseModel;
import com.app.budbank.models.UserModel;
import com.app.budbank.models.VoucherModel;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.budbank.R;
import com.app.budbank.activities.InboxActivity;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.app.budbank.utils.cacheUtils.MainStorageUtils;
import com.app.budbank.web.APIController;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    @BindView(R.id.iv_user_profile_image)
    ImageView ivProfileImage;
    @BindView(R.id.tv_user_name)
    TextView tvUsername;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.ll_empty_view_container)
    View emptyViewContainer;
    @BindView(R.id.rv_voucher_data_container)
    View voucherDataContainer;
    @BindView(R.id.rv_collections)
    RecyclerView rvCollections;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_coins_earned)
    TextView tvCoinsEarned;
    @BindView(R.id.rl_notification_count_container)
    RelativeLayout lytNotificationCountContainer;
    @BindView(R.id.tv_notification_count)
    TextView tvNotificationCount;
    @BindView(R.id.iv_inbox)
    ImageView ivInbox;

    private ArrayList<VoucherModel> collectionsList;
    private ArrayList<NotificationModel> readNotifications;
    private ArrayList<NotificationModel> unreadNotifications;
    private LocalBroadcast localBroadcast;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initViews(view);
    }

    private void initViews(View view) {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer,mContext);
        collectionsList = new ArrayList<>();
        readNotifications = new ArrayList<>();
        unreadNotifications = new ArrayList<>();
        populateUI();
        registerReceiver();
        getNotifications();
        btnConfirm.setOnClickListener(this);
        ivInbox.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
    }

    private void populateUI() {
        if (tvUsername == null)
            return;
        updateUserView();
        tvCoinsEarned.setText(StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), "0"));
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        collectionsList = mainStorageUtils.getAvailableVouchersList();
        if (collectionsList != null && collectionsList.size() > 0) {
            voucherDataContainer.setVisibility(View.VISIBLE);
            emptyViewContainer.setVisibility(View.GONE);
            setupAdapter();
        } else {
            voucherDataContainer.setVisibility(View.GONE);
            emptyViewContainer.setVisibility(View.VISIBLE);
        }
    }

    private void getNotifications() {
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(ivInbox, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            DialogUtils.stopLoading();
            return;
        }
        APIController.getNotifications(sessionToken, userId, new Callback<GetNotificationResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetNotificationResponseModel> call, @NonNull Response<GetNotificationResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(ivInbox, getString(R.string.call_fail_error), mContext);
                    return;
                }
                GetNotificationResponseModel getNotificationResponse = response.body();
                if (getNotificationResponse != null && getNotificationResponse.getStatus()==AppConstants.StatusCodes.SUCCESS.getValue()) {
                    populateLists(getNotificationResponse.getNotifications());
                    if(unreadNotifications.size() > 0) {
                        updateNotificationsCount(unreadNotifications.size());
                    }
                } else {
                    DialogUtils.showSnackBar(ivInbox, getNotificationResponse.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetNotificationResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(ivInbox, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void populateLists(Notifications model) {
        readNotifications.clear();
        unreadNotifications.clear();
        ArrayList<NotificationModel> readList = model.getSeenNotifications();
        ArrayList<NotificationModel> unreadList = model.getUnseenNotifications();
        if(readList!=null && readList.size()>0) {
            for (NotificationModel notificationModel : readList){
                readNotifications.add(notificationModel);
            }
        }
        if(unreadList!=null && unreadList.size()>0) {
            for (NotificationModel notificationModel : unreadList){
                unreadNotifications.add(notificationModel);
            }
        }
    }

    private void updateNotificationsCount(int count) {
        if(count > 0) {
            lytNotificationCountContainer.setVisibility(View.GONE);
            tvNotificationCount.setText(Integer.toString(count));
        } else {
            lytNotificationCountContainer.setVisibility(View.GONE);
            tvNotificationCount.setText("0");
        }

    }

    private void updateUserView() {
        UserModel userModel = StorageUtillity.getUserModel(mContext);
        String profileUrl = userModel.getProfileUrl();
        tvUsername.setText(userModel.getUsername());
        if (profileUrl != null && !TextUtils.isEmpty(profileUrl)) {
            if (BudsBankUtils.isValidContextForGlide(mContext)) {
                Glide.with(mContext)
                        .load(profileUrl)
                        .into(ivProfileImage);
            }
        } else {
            ivProfileImage.setImageResource(R.drawable.user_placeholder);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvCollections.setLayoutManager(linearLayoutManager);
        VoucherAdapter dispensaryAdapter = new VoucherAdapter(mContext, collectionsList, AppConstants.IsFrom.HOME_FRAGMENT.getValue());
        rvCollections.setAdapter(dispensaryAdapter);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_confirm:
                communicator.switchTab(2);
                break;
            case R.id.iv_inbox:
                //goToInbox();
                goToDeals();
                break;
            case R.id.iv_user_profile_image:
                startActivity(new Intent(mContext, ProfileActivity.class));
                break;
            case R.id.tv_user_name:
                startActivity(new Intent(mContext, ProfileActivity.class));
                break;
        }
    }

    private void goToDeals() {
        startActivity(new Intent(mContext, DealsActivity.class));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            populateUI();
        }
    }

    private void registerReceiver() {
        localBroadcast = new HomeFragment.LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.UPDATE_PROFILE.getValue());
        intentFilter.addAction(AppConstants.Actions.UPDATE_NOTIFICATIONS.getValue());
        intentFilter.addAction(AppConstants.Actions.READ_NOTIFICATION.getValue());
        intentFilter.addAction(AppConstants.Actions.UNREAD_DELETE_NOTIFICATION.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            if (action.equals(AppConstants.Actions.UPDATE_PROFILE.getValue())) {
                updateUserView();
            } else if (action.equals(AppConstants.Actions.UPDATE_NOTIFICATIONS.getValue())) {
                updateNotificationsCount(unreadNotifications.size());
            } else if (action.equals(AppConstants.Actions.READ_NOTIFICATION.getValue()) || action.equals(AppConstants.Actions.UNREAD_DELETE_NOTIFICATION.getValue())) {
                NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue());
                if (notificationModel == null)
                    return;
                int pos = getModelPosition(notificationModel);
                if (pos >= 0) {
                    unreadNotifications.remove(pos);
                }
                int count = unreadNotifications.size();
                updateNotificationsCount(count);
            }
        }
    }

    private int getModelPosition(NotificationModel model) {
        int id = model.getNotificationId();
        for(NotificationModel notificationModel : unreadNotifications) {
            if (notificationModel.getNotificationId() == id) {
                return unreadNotifications.indexOf(notificationModel);
            }
        }
        return -1;
    }

    private void goToInbox() {
        Intent intent = new Intent(mContext, InboxActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.IntentKeys.UNREAD_NOTIFICATIONS.getValue(), unreadNotifications);
        bundle.putSerializable(AppConstants.IntentKeys.READ_NOTIFICATIONS.getValue(), readNotifications);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcast);
    }

}
