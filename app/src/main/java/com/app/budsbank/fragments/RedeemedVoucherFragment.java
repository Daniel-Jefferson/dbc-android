package com.app.budsbank.fragments;


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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.budsbank.R;
import com.app.budsbank.adapters.RedeemedVoucherAdapter;
import com.app.budsbank.models.AvailableVoucherResponseModel;
import com.app.budsbank.models.VoucherModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;
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
public class RedeemedVoucherFragment extends BaseFragment {

    @BindView(R.id.rv_redeemed_vouchers)
    RecyclerView rvRedeemedVouchers;
    private ArrayList<VoucherModel> redeemedVouchersList;
    private RedeemedVoucherAdapter availableVoucherAdapter;
    @BindView(R.id.ll_empty_view_container)
    LinearLayout lytEmptyContainer;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private int page = 1;
    private boolean isAllLoaded = false;
    private boolean isLoading = false;
    private LocalBroadcast localBroadcast;

    public RedeemedVoucherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeemed_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
    }

    private void initViews() {
        redeemedVouchersList = new ArrayList<>();
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        redeemedVouchersList = mainStorageUtils.getRedeemedVouchersList();
        btnConfirm.setOnClickListener(this);
        setViewsBasedOnSizeOfList();
        registerReceiver();
        rvRedeemedVouchers.setOnScrollListener(onScrollListener);
    }

    private void setViewsBasedOnSizeOfList() {
        if (rvRedeemedVouchers == null || lytEmptyContainer  == null)
            return;
        if(redeemedVouchersList != null && redeemedVouchersList.size() > 0 ) {
            rvRedeemedVouchers.setVisibility(View.VISIBLE);
            lytEmptyContainer.setVisibility(View.GONE);
            setupAdapter();
        } else {
            rvRedeemedVouchers.setVisibility(View.GONE);
            lytEmptyContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvRedeemedVouchers.setLayoutManager(linearLayoutManager);
        availableVoucherAdapter = new RedeemedVoucherAdapter(mContext, redeemedVouchersList );
        rvRedeemedVouchers.setAdapter(availableVoucherAdapter);
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
                    getRedeemedVouchers(page);
                }
            }
        }
    };

    private void getRedeemedVouchers(int page) {
        rvRedeemedVouchers.removeOnScrollListener(onScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        DialogUtils.showLoading(mContext);
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvRedeemedVouchers.setOnScrollListener(onScrollListener);
            DialogUtils.showSnackBar(rvRedeemedVouchers, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            return;
        }
        isLoading = true;
        APIController.getRedeemedVouchers(sessionToken, userId,pageSize, page, new Callback<AvailableVoucherResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AvailableVoucherResponseModel> call, @NonNull Response<AvailableVoucherResponseModel> response) {
                rvRedeemedVouchers.setOnScrollListener(onScrollListener);
                isLoading = false;
                DialogUtils.dismissLoading();
                if (!response.isSuccessful()) {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                    return;
                }
                AvailableVoucherResponseModel availableVoucherResponse = response.body();
                if (availableVoucherResponse != null) {
                    if (availableVoucherResponse.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        DialogUtils.dismiss();
                        int size = redeemedVouchersList.size();
                        redeemedVouchersList.addAll(availableVoucherResponse.getVouchers());
                        availableVoucherAdapter.notifyItemRangeChanged(size, redeemedVouchersList.size());
                        if(availableVoucherResponse.getPageInfo().getCount() == redeemedVouchersList.size())
                            isAllLoaded = true;

                    } else {
                        DialogUtils.showCustomToast(mContext, availableVoucherResponse.getMessage());
                        return;
                    }
                } else {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AvailableVoucherResponseModel> call, @NonNull Throwable t) {
                rvRedeemedVouchers.setOnScrollListener(onScrollListener);
                isLoading = false;
                DialogUtils.dismissLoading();
                DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_confirm:
                communicator.switchTab(2);
                break;
        }
    }

    private void registerReceiver() {
        localBroadcast = new LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.REDEEM_DISPENSARY.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if (action.equals(AppConstants.Actions.REDEEM_DISPENSARY.getValue())) {
                MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                ArrayList<VoucherModel> list = mainStorageUtils.getRedeemedVouchersList();
                setViewsBasedOnSizeOfList();
                if (list != null && list.size() > 0) {
                    redeemedVouchersList = list;
                    setViewsBasedOnSizeOfList();
                    setupAdapter();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcast);
    }
}
