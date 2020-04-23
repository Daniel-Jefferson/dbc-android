package com.app.budsbank.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.adapters.AllDealsAdapter;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.SearchResponseModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.web.APIController;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class AllDealsFragment extends BaseFragment {

    @BindView(R.id.rv_all_deals)
    RecyclerView rvAllDeals;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private boolean isLoading = false;
    private boolean isAllLoaded = false;
    private Location location;
    public SwipeRefreshLayout.OnRefreshListener refreshListener;
    private ArrayList<DispensaryModel> allDispensaries;
    private ArrayList<DispensaryModel> allDeals;
    private AllDealsAdapter allDealsAdapter;

    public AllDealsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_deals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        initViews();
        fetchLocationAndGetDispensaries();
    }

    private void initViews() {
        allDeals = new ArrayList<>();
        allDispensaries = new ArrayList<>();
        rvAllDeals.setOnScrollListener(onScrollListener);
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getAllDispensaries(page);
                swipeRefreshLayout.setRefreshing(false);
                isAllLoaded = false;
            }
        };
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.red_delete,
                R.color.yellow_68);


        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        rvAllDeals.setOnScrollListener(onScrollListener);
    }

    private void setViewBasedOnListSize() {
        if (allDispensaries.size() == 0) {
            rvAllDeals.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else if (tvEmptyView.getVisibility() == View.VISIBLE) {
            rvAllDeals.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvAllDeals.setLayoutManager(linearLayoutManager);
        allDealsAdapter = new AllDealsAdapter(mContext, allDispensaries);
        rvAllDeals.setAdapter(allDealsAdapter);
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
                    getAllDispensaries(page);
                }
            }
        }
    };

    private void getAllDispensaries(int page) {
        int pageSize = AppConstants.PAGE_SIZE;
        rvAllDeals.removeOnScrollListener(onScrollListener);
        double latitude = 0.0;
        double longitude = 0.0;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvAllDeals.setOnScrollListener(onScrollListener);
            DialogUtils.showSnackBar(rvAllDeals, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            return;
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else
            return;
        isLoading = true;
        APIController.getNearbyDispensaries(sessionToken, userId, latitude, longitude, pageSize, page, true, new Callback<SearchResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponseModel> call, @NonNull Response<SearchResponseModel> response) {
                rvAllDeals.setOnScrollListener(onScrollListener);
                isLoading = false;
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvAllDeals, getString(R.string.call_fail_error), mContext);
                    return;
                }
                SearchResponseModel searchResponseModel = response.body();
                if (searchResponseModel != null) {
                    if (page == 1) {
                        allDispensaries = searchResponseModel.getDispensariesList();
                        removeEmptyDeals(allDispensaries);
                        setViewBasedOnListSize();
                        setupAdapter();
                    } else {
                        removeEmptyDeals(allDispensaries);
                        int size = allDispensaries.size();
                        allDispensaries.addAll(searchResponseModel.getDispensariesList());
                        allDealsAdapter.notifyItemRangeChanged(size, allDispensaries.size());
                        if (searchResponseModel.getPageInfo().getCount() == allDispensaries.size()) {
                            isAllLoaded = true;
                        }

                    }
                } else {
                    DialogUtils.showSnackBar(rvAllDeals, searchResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                isLoading = false;
                rvAllDeals.setOnScrollListener(onScrollListener);
                DialogUtils.showSnackBar(rvAllDeals, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void fetchLocationAndGetDispensaries() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastLocation) {
                        location = lastLocation;
                        getAllDispensaries(page);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                rvAllDeals.setOnScrollListener(onScrollListener);
                isLoading = false;
            }
        });
    }

    private void removeEmptyDeals(ArrayList<DispensaryModel> allDispensaries) {
        Iterator<DispensaryModel> it = allDispensaries.iterator();
        while (it.hasNext()) {
            DispensaryModel dispensaryModel = it.next();
            if (TextUtils.isEmpty(dispensaryModel.getDeal())) {
                it.remove();
            }
        }
    }
}
