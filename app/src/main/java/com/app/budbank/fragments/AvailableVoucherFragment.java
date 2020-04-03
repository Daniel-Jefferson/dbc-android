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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.budbank.R;
import com.app.budbank.adapters.AvailableVoucherAdapter;
import com.app.budbank.models.AvailableVoucherResponseModel;
import com.app.budbank.models.ClaimVoucherResponseModel;
import com.app.budbank.models.VoucherModel;
import com.app.budbank.models.requestModel.ClaimVoucherRequestModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.app.budbank.utils.cacheUtils.MainStorageUtils;
import com.app.budbank.web.APIController;
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
public class AvailableVoucherFragment extends BaseFragment {

    @BindView(R.id.rv_available_vouchers)
    RecyclerView rvAvailableVouchers;
    private ArrayList<VoucherModel> availableVouchersList;
    private AvailableVoucherAdapter availableVoucherAdapter;
    @BindView(R.id.ll_empty_view_container)
    LinearLayout lytEmptyContainer;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private int page = 1;
    private boolean isAllLoaded = false;
    private boolean isLoading = false;
    private LocalBroadcast localBroadcast;

    public AvailableVoucherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_available_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initViews();
        setupAdapter();
    }

    private void initViews() {
        availableVouchersList = new ArrayList<>();
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        availableVouchersList = mainStorageUtils.getAvailableVouchersList();
        btnConfirm.setOnClickListener(this);
        rvAvailableVouchers.setOnScrollListener(onScrollListener);
        setViewsBasedOnSizeOfList();
        setupAdapter();
        registerReceiver();
    }

    private void setViewsBasedOnSizeOfList() {
        if (rvAvailableVouchers == null || lytEmptyContainer  == null)
            return;
        if(availableVouchersList != null && availableVouchersList.size() > 0 ) {
            rvAvailableVouchers.setVisibility(View.VISIBLE);
            lytEmptyContainer.setVisibility(View.GONE);
        } else {
            rvAvailableVouchers.setVisibility(View.GONE);
            lytEmptyContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvAvailableVouchers.setLayoutManager(linearLayoutManager);
        availableVoucherAdapter = new AvailableVoucherAdapter(mContext, availableVouchersList );
        availableVoucherAdapter.setClaimVoucherCallback(new ClaimVoucher() {
            @Override
            public void claim(int voucherId, String code, int position) {
                claimVoucher(voucherId, code, position);
            }
        });
        rvAvailableVouchers.setAdapter(availableVoucherAdapter);
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
                    getAvailableVouchers(page);
                }
            }
        }
    };

    private void getAvailableVouchers(int page) {
        rvAvailableVouchers.removeOnScrollListener(onScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        DialogUtils.showLoading(mContext);
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvAvailableVouchers.setOnScrollListener(onScrollListener);
            DialogUtils.showSnackBar(rvAvailableVouchers, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            return;
        }
        isLoading = true;
        APIController.getAvailableVouchers(sessionToken, userId,pageSize, page, new Callback<AvailableVoucherResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AvailableVoucherResponseModel> call, @NonNull Response<AvailableVoucherResponseModel> response) {
                rvAvailableVouchers.setOnScrollListener(onScrollListener);
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
                        int size = availableVouchersList.size();
                        availableVouchersList.addAll(availableVoucherResponse.getVouchers());
                        availableVoucherAdapter.notifyItemRangeChanged(size, availableVouchersList.size());
                        if(availableVoucherResponse.getPageInfo().getCount() == availableVouchersList.size())
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
                rvAvailableVouchers.setOnScrollListener(onScrollListener);
                isLoading = false;
                DialogUtils.dismissLoading();
                DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
            }
        });
    }

    private void claimVoucher(int voucherId, String code, int position) {
        DialogUtils.showLoading(mContext);
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        ClaimVoucherRequestModel claimVoucherRequestModel = new ClaimVoucherRequestModel(userId, voucherId, code);
        APIController.claimVoucher(sessionToken, claimVoucherRequestModel, new Callback<ClaimVoucherResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ClaimVoucherResponseModel> call, @NonNull Response<ClaimVoucherResponseModel> response) {
                DialogUtils.dismissLoading();
                if (!response.isSuccessful()) {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                    return;
                }
                ClaimVoucherResponseModel claimVoucherResponseModel = response.body();
                if (claimVoucherResponseModel != null) {
                    if (claimVoucherResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        DialogUtils.dismiss();
                        VoucherModel voucherModel = claimVoucherResponseModel.getVoucher();
                        updateCoin(voucherModel);
                        availableVouchersList.remove(position);
                        availableVoucherAdapter.notifyItemRemoved(position);
                        BudsBankUtils.broadcastAction(mContext, AppConstants.Actions.REDEEM_DISPENSARY.getValue());
                        setViewsBasedOnSizeOfList();
                        return;
                    }
                    DialogUtils.showCustomToast(mContext, claimVoucherResponseModel.getMessage());
                    return;
                }
                DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
            }

            @Override
            public void onFailure(@NonNull Call<ClaimVoucherResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismissLoading();
                DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
            }
        });
    }

    private void updateCoin(VoucherModel voucherModel) {
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        mainStorageUtils.addRedeemedVoucher(voucherModel);
        String coinStr = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), "0");
        int coin = TextUtils.getIntValue(coinStr);
        coin -= 5;
        StorageUtillity.saveDataInPreferences(mContext, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), String.valueOf(coin));
        BudsBankUtils.broadcastAction(mContext, AppConstants.Actions.UPDATE_COINS.getValue());
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
        intentFilter.addAction(AppConstants.Actions.AVAILABLE_VOUCHER.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if (action.equals(AppConstants.Actions.AVAILABLE_VOUCHER.getValue())) {
                MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                ArrayList<VoucherModel> list = mainStorageUtils.getAvailableVouchersList();
                setViewsBasedOnSizeOfList();
                if (list != null && list.size() > 0  ) {
                    availableVouchersList = list;
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

    public interface ClaimVoucher {
        void claim(int voucherId, String code, int position);
    }
}
