package com.app.budbank.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.activities.InboxActivity;
import com.app.budbank.activities.QuizDispoActivity;
import com.app.budbank.adapters.NotificationAdapter;
import com.app.budbank.models.MarkReadModel;
import com.app.budbank.models.NotificationModel;
import com.app.budbank.models.ReadNotificationsResponseModel;
import com.app.budbank.models.ResponseModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.app.budbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnreadNotificationsFragment extends BaseFragment {

    @BindView(R.id.rv_unread_notifications)
    RecyclerView rvUnreadNotifications;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private boolean isAllLoaded = false;
    private boolean isLoading = false;
    private ArrayList<NotificationModel> unreadNotifications;
    public SwipeRefreshLayout.OnRefreshListener refreshListener;
    private NotificationAdapter notificationAdapter;
    private LocalBroadcast localBroadcast;

    public UnreadNotificationsFragment() {
        // Required empty public constructor
    }

    public UnreadNotificationsFragment(ArrayList<NotificationModel> unreadNotifications) {
        this.unreadNotifications=unreadNotifications;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unread_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
        registerReceiver();
        setupAdapter();
        getUnReadNotifications(page);
    }

    private void initViews() {
        setViewBasedOnListSize();
        rvUnreadNotifications.setOnScrollListener(onScrollListener);
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getUnReadNotifications(page);
                swipeRefreshLayout.setRefreshing(false);
                isAllLoaded =false;
            }
        };
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.red_delete,
                R.color.yellow_68);


        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        rvUnreadNotifications.setOnScrollListener(onScrollListener);
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
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
                    getUnReadNotifications(page);
                }
            }
        }
    };

    private void getUnReadNotifications(int page) {
        rvUnreadNotifications.removeOnScrollListener(onScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(rvUnreadNotifications, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            rvUnreadNotifications.setOnScrollListener(onScrollListener);
            return;
        }
        DialogUtils.showLoading(mContext);
        isLoading = true;
        APIController.getUnreadNotifications(sessionToken, userId ,page, pageSize,  new Callback<ReadNotificationsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ReadNotificationsResponseModel> call, @NonNull Response<ReadNotificationsResponseModel> response) {
                DialogUtils.stopLoading();
                isLoading = false;
                rvUnreadNotifications.setOnScrollListener(onScrollListener);
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvUnreadNotifications, getString(R.string.call_fail_error), mContext );
                    return;
                }
                ReadNotificationsResponseModel readNotificationsResponseModel = response.body();
                if (readNotificationsResponseModel != null) {
                    if(page ==1) {
                        unreadNotifications = readNotificationsResponseModel.getNotifications();
                        if (unreadNotifications != null && unreadNotifications.size() > 0) {
                            setViewBasedOnListSize();
                            setupAdapter();
                        }
                    } else {
                        int size = unreadNotifications.size();
                        unreadNotifications.addAll(readNotificationsResponseModel.getNotifications());
                        notificationAdapter.notifyItemRangeChanged(size, unreadNotifications.size());
                        if(readNotificationsResponseModel.getPageInfo().getCount() == unreadNotifications.size())
                            isAllLoaded = true;
                    }
                } else {
                    DialogUtils.showSnackBar(rvUnreadNotifications, readNotificationsResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadNotificationsResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                isLoading =false;
                rvUnreadNotifications.setOnScrollListener(onScrollListener);
                swipeRefreshLayout.setRefreshing(false);
                DialogUtils.showSnackBar(rvUnreadNotifications, getString(R.string.call_fail_error), mContext);
            }
        });
    }


    private void setViewBasedOnListSize() {
        if(unreadNotifications.size()==0) {
            rvUnreadNotifications.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else if (tvEmptyView.getVisibility() ==  View.VISIBLE){
            rvUnreadNotifications.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvUnreadNotifications.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(mContext, unreadNotifications, AppConstants.IsFrom.UNREAD_NOTIFICATION_FRAGMENT.getValue(), new InboxActivity.NotificationCallback() {
            @Override
            public void callback(NotificationModel notificationModel, int position) {
                markRead(notificationModel, position);
            }
        });
        rvUnreadNotifications.setAdapter(notificationAdapter);
    }

    private void markRead(NotificationModel notificationModel, int position) {
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        MarkReadModel model = new MarkReadModel();
        model.setUserId(Integer.parseInt(userId));
        model.setNotificationId(notificationModel.getNotificationId());
        int dispensaryId = notificationModel.getDispensaryId();
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showCustomToast(mContext, mContext.getString(R.string.no_internet_alert));
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(mContext);
        APIController.markRead( sessionToken, model,  new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull final Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                ResponseModel responseModel = response.body();
                if (responseModel != null) {
                    if (responseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        unreadNotifications.remove(notificationModel);
                        notificationAdapter.notifyItemRemoved(position);
                        if (unreadNotifications.size() == 0)
                            setViewBasedOnListSize();
                        goToquizDispo(dispensaryId);
                        broadcastAction(notificationModel);
                    }
                    else {
                        DialogUtils.showCustomToast(mContext, responseModel.getMessage());
                    }
                } else {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                DialogUtils.showErrorBasedOnType(mContext, t);
                Log.d("error", "onFailure:" + t.getMessage());
            }
        });
    }

    public void broadcastAction(NotificationModel notificationModel) {
        Intent intent = new Intent(AppConstants.Actions.READ_NOTIFICATION.getValue());
        intent.putExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue(), notificationModel);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void goToquizDispo(int dispensaryId) {
        Intent intent = new Intent(mContext, QuizDispoActivity.class);
        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_ID.getValue(), dispensaryId);
        intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.INBOX_ACTIVITY.getValue());
        mContext.startActivity(intent);
    }

    private void registerReceiver() {
        localBroadcast = new UnreadNotificationsFragment.LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.UNREAD_DELETE_NOTIFICATION.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if(action.equals(AppConstants.Actions.UNREAD_DELETE_NOTIFICATION.getValue())) {
                NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue());
                if (notificationModel == null)
                    return;
                unreadNotifications.remove(notificationModel);
                setViewBasedOnListSize();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcast);
    }

}
