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
import android.widget.Toast;

import com.app.budbank.R;
import com.app.budbank.activities.BaseActivity;
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
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadNotificationFragment extends BaseFragment {

    @BindView(R.id.rv_read_notifications)
    RecyclerView rvReadNotifications;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int page =1;
    private boolean isLoading = false;
    private boolean isAllLoaded = false;
    public SwipeRefreshLayout.OnRefreshListener refreshListener;
    private ArrayList<NotificationModel> readNotifications;
    private LocalBroadcast localBroadcast;
    private NotificationAdapter notificationAdapter;

    public ReadNotificationFragment() {
        // Required empty public constructor
    }

    public ReadNotificationFragment(ArrayList<NotificationModel> readNotifications) {
        this.readNotifications = readNotifications;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initViews();
        setupAdapter();
        registerReceiver();
        getReadNotifications(page);
    }

    private void initViews() {
        rvReadNotifications.setOnScrollListener(onScrollListener);
        setViewBasedOnListSize();
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                getReadNotifications(page);
                swipeRefreshLayout.setRefreshing(false);
                isAllLoaded = false;
            }
        };
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.red_delete,
                R.color.yellow_68);


        swipeRefreshLayout.setOnRefreshListener(refreshListener);
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
                    getReadNotifications(page);
                }
            }
        }
    };

    private void getReadNotifications(int page) {
        int pageSize = AppConstants.PAGE_SIZE;
        rvReadNotifications.removeOnScrollListener(onScrollListener);
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvReadNotifications.setOnScrollListener(onScrollListener);
            DialogUtils.showSnackBar(rvReadNotifications, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        isLoading = true;
        DialogUtils.showLoading(mContext);
        APIController.getReadNotifications(sessionToken, userId , page, pageSize,new Callback<ReadNotificationsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ReadNotificationsResponseModel> call, @NonNull Response<ReadNotificationsResponseModel> response) {
                DialogUtils.stopLoading();
                swipeRefreshLayout.setRefreshing(false);
                rvReadNotifications.setOnScrollListener(onScrollListener);
                isLoading = false;
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvReadNotifications, getString(R.string.call_fail_error), mContext );
                    return;
                }
                ReadNotificationsResponseModel readNotificationsResponseModel = response.body();
                if (readNotificationsResponseModel != null) {
                    if(page == 1) {
                        readNotifications = readNotificationsResponseModel.getNotifications();
                        if (readNotifications != null && readNotifications.size() >0) {
                            setViewBasedOnListSize();
                            setupAdapter();
                        }
                    } else {
                        int size = readNotifications.size();
                        readNotifications.addAll(readNotificationsResponseModel.getNotifications());
                        notificationAdapter.notifyItemRangeChanged(size, readNotifications.size());
                        if (readNotificationsResponseModel.getPageInfo().getCount() == readNotifications.size())
                            isAllLoaded = true;
                    }
                } else {
                    DialogUtils.showSnackBar(rvReadNotifications, readNotificationsResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadNotificationsResponseModel> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                DialogUtils.dismiss();
                isLoading = false;
                rvReadNotifications.setOnScrollListener(onScrollListener);
                DialogUtils.showSnackBar(rvReadNotifications, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    protected void setViewBasedOnListSize() {
        if(readNotifications.size()==0) {
            rvReadNotifications.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else if (tvEmptyView.getVisibility() ==  View.VISIBLE){
            rvReadNotifications.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvReadNotifications.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(mContext, readNotifications, AppConstants.IsFrom.READ_NOTIFICATION_FRAGMENT.getValue(), new InboxActivity.NotificationCallback() {
            @Override
            public void callback(NotificationModel notificationModel, int position) {
                goToquizDispo(notificationModel.getDispensaryId());
            }
        });
        rvReadNotifications.setAdapter(notificationAdapter);
    }

    private void goToquizDispo(int dispensaryId) {
        Intent intent = new Intent(mContext, QuizDispoActivity.class);
        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_ID.getValue(), dispensaryId);
        intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.INBOX_ACTIVITY.getValue());
        mContext.startActivity(intent);
      //  ((BaseActivity)mContext).finish();
    }

    private void registerReceiver() {
        localBroadcast = new LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.READ_NOTIFICATION.getValue());
        intentFilter.addAction(AppConstants.Actions.READ_DELETE_NOTIFICATION.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if (action.equals(AppConstants.Actions.READ_NOTIFICATION.getValue())) {
                NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue());
                if (notificationModel == null)
                    return;
                if (readNotifications == null)
                    readNotifications = new ArrayList<>();
                if(readNotifications.size()==0){
                    readNotifications.add(notificationModel);
                    setupAdapter();
                    setViewBasedOnListSize();
                } else {
                    readNotifications.add(0, notificationModel);
                    notificationAdapter.notifyItemInserted(0);
                    setViewBasedOnListSize();
                }
            } else if(action.equals(AppConstants.Actions.READ_DELETE_NOTIFICATION.getValue())) {
                NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue());
                if (notificationModel == null)
                    return;
                readNotifications.remove(notificationModel);
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
