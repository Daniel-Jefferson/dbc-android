package com.app.budsbank.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.adapters.DealsAdapter;
import com.app.budsbank.models.FollowedDispensariesModel;
import com.app.budsbank.models.NotificationSettingsResponseModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingDealsFragment extends BaseFragment {

    @BindView(R.id.rv_following_deals)
    RecyclerView rvFollowingDeals;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private boolean isLoading = false;
    private boolean isAllLoaded = false;
    public SwipeRefreshLayout.OnRefreshListener refreshListener;
    private ArrayList<FollowedDispensariesModel> followedDispensaries;
    private DealsAdapter dealsAdapter;

    public FollowingDealsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following_deals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
        getFollowedDispensaries(page);
    }

    private void initViews() {
        rvFollowingDeals.setOnScrollListener(onScrollListener);
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getFollowedDispensaries(page);
                swipeRefreshLayout.setRefreshing(false);
                isAllLoaded = false;
            }
        };
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.red_delete,
                R.color.yellow_68);


        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        rvFollowingDeals.setOnScrollListener(onScrollListener);
    }

    private void setViewBasedOnListSize() {
        if (followedDispensaries.size() == 0) {
            rvFollowingDeals.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else if (tvEmptyView.getVisibility() == View.VISIBLE) {
            rvFollowingDeals.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvFollowingDeals.setLayoutManager(linearLayoutManager);
        dealsAdapter = new DealsAdapter(mContext, followedDispensaries);
        rvFollowingDeals.setAdapter(dealsAdapter);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (!isAllLoaded && !isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= AppConstants.PAGE_SIZE - 1) {
                    page++;
                    getFollowedDispensaries(page);
                }
            }
        }
    };

    private void getFollowedDispensaries(int page) {
        rvFollowingDeals.removeOnScrollListener(onScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(rvFollowingDeals, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            DialogUtils.stopLoading();
            return;
        }
        isLoading = true;
        DialogUtils.showLoading(mContext);
        isLoading = true;
        APIController.getNotificationSettings(sessionToken, userId, page, pageSize, new Callback<NotificationSettingsResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Response<NotificationSettingsResponseModel> response) {
                DialogUtils.stopLoading();
                isLoading = false;
                rvFollowingDeals.setOnScrollListener(onScrollListener);
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvFollowingDeals, getString(R.string.call_fail_error), mContext);
                    return;
                }
                NotificationSettingsResponseModel notificationSettingsResponseModel = response.body();
                if (notificationSettingsResponseModel != null) {
                    if (page == 1) {
                        followedDispensaries = notificationSettingsResponseModel.getSettings();
                        if (followedDispensaries != null && followedDispensaries.size() > 0) {
                            removeEmptyDeals(followedDispensaries);
                            setViewBasedOnListSize();
                            setupAdapter();
                        }
                    } else {
                        removeEmptyDeals(followedDispensaries);
                        int size = followedDispensaries.size();
                        followedDispensaries.addAll(notificationSettingsResponseModel.getSettings());
                        dealsAdapter.notifyItemRangeChanged(size, followedDispensaries.size());
                        if (notificationSettingsResponseModel.getPageInfo().getCount() == followedDispensaries.size())
                            isAllLoaded = true;
                    }
                } else {
                    DialogUtils.showSnackBar(rvFollowingDeals, notificationSettingsResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationSettingsResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                isLoading = false;
                rvFollowingDeals.setOnScrollListener(onScrollListener);
                swipeRefreshLayout.setRefreshing(false);
                DialogUtils.showSnackBar(rvFollowingDeals, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void removeEmptyDeals(ArrayList<FollowedDispensariesModel> allDispensaries) {
        Iterator<FollowedDispensariesModel> it = allDispensaries.iterator();
        while (it.hasNext()) {
            FollowedDispensariesModel dispensaryModel = it.next();
            if (TextUtils.isEmpty(dispensaryModel.getDeal())) {
                it.remove();
            }
        }
    }
}
