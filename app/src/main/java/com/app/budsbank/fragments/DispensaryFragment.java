package com.app.budsbank.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.activities.QuizDispoActivity;
import com.app.budsbank.activities.SearchActivity;
import com.app.budsbank.adapters.DispensaryAdapter;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.SearchResponseModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;
import com.github.piasy.fresco.draweeview.shaped.ShapedDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class DispensaryFragment extends BaseFragment {
    @BindView(R.id.tv_no_featured_shop)
    TextView tvNoFeaturedShop;
    @BindView(R.id.ll_featured_shop_container)
    LinearLayout featuredShopContainer;
    @BindView(R.id.rv_collections)
    RecyclerView rvCollections;
    @BindView(R.id.et_container)
    LinearLayout lytSearchContainer;
    @BindView(R.id.rl_main_container)
    LinearLayout lytMainContainer;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_featured_disp_name)
    TextView tvFeaturedDispName;
    @BindView(R.id.tv_featured_disp_address)
    TextView tvFeaturedDispAddress;
    @BindView(R.id.iv_featured_disp_bg)
    ShapedDraweeView ivFeaturedDispBg;
    @BindView(R.id.ll_learn_more)
    LinearLayout lytLearnMore;
    @BindView(R.id.collection_container)
    View collectionsContainer;

    private ArrayList<DispensaryModel> dispensaryModels;
    private ArrayList<DispensaryModel> featuredModelList;
    private LocalBroadcast localBroadcast;
    private DispensaryAdapter dispensaryAdapter;
    private int page = 1;
    private Location location;
    private boolean isAllLoaded=false;
    private boolean isLoading = false;

    public DispensaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dispensary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initViews(view);
    }

    private void initViews(View view) {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer,mContext);
        fetchLocation();
        TextView tvLetUsKnow = view.findViewById(R.id.tv_let_us_know);
        tvLetUsKnow.setOnClickListener(this);
        dispensaryModels = new ArrayList<>();
        featuredModelList = new ArrayList<>();
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        dispensaryModels = mainStorageUtils.getDispensariesList();
        featuredModelList = mainStorageUtils.getFeaturedDispensariesList();
        setFeaturedDispensary();
        lytLearnMore.setOnClickListener(this);
        lytSearchContainer.setOnClickListener(this);
        etSearch.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvCollections.setLayoutManager(linearLayoutManager);
        rvCollections.setOnScrollListener(onScrollListener);
        setupAdapter();
        registerReceiver();

    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = dispensaryAdapter.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if(!isAllLoaded && !isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 3) {
                    if(location!=null)
                    page++;
                    getNearbyDispensaries(page);
                }
            }
        }
    };

    private void getNearbyDispensaries(int page) {
        int pageSize = AppConstants.PAGE_SIZE;
        rvCollections.removeOnScrollListener(onScrollListener);
        double latitude = 0.0;
        double longitude= 0.0;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvCollections.setOnScrollListener(onScrollListener);
            DialogUtils.showSnackBar(rvCollections, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            return;
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else
            return;
        isLoading = true;
        APIController.getNearbyDispensaries(sessionToken, userId, latitude, longitude,pageSize,  page,true,  new Callback<SearchResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponseModel> call, @NonNull Response<SearchResponseModel> response) {
                rvCollections.setOnScrollListener(onScrollListener);
                isLoading =false;
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvCollections, getString(R.string.call_fail_error), mContext);
                    return;
                }
                SearchResponseModel searchResponseModel = response.body();
                if (searchResponseModel != null) {
                    if (page == 1) {
                        dispensaryModels = searchResponseModel.getDispensariesList();
                        setupAdapter();
                    } else {
                        int size = dispensaryModels.size();
                        dispensaryModels.addAll(searchResponseModel.getDispensariesList());
                        dispensaryAdapter.notifyItemRangeChanged(size, dispensaryModels.size());
                        if(searchResponseModel.getPageInfo().getCount() == dispensaryModels.size()) {
                            isAllLoaded = true;
                        }

                    }
                } else {
                    DialogUtils.showSnackBar(rvCollections, searchResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                isLoading = false;
                rvCollections.setOnScrollListener(onScrollListener);
                DialogUtils.showSnackBar(rvCollections, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void fetchLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastLocation) {
                        location = lastLocation;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                rvCollections.setOnScrollListener(onScrollListener);
                isLoading = false;
            }
        });
    }

    private void setFeaturedDispensary() {
        if (featuredModelList != null && featuredModelList.size() > 0) {
            tvNoFeaturedShop.setVisibility(View.INVISIBLE);
            featuredShopContainer.setVisibility(View.VISIBLE);
            DispensaryModel featuredDispensary = featuredModelList.get(0);
            tvFeaturedDispName.setText(featuredDispensary.getName());
            tvFeaturedDispAddress.setText(featuredDispensary.getAddress());
            String profileUrl = featuredDispensary.getProfileUrl();
            if (profileUrl != null && !TextUtils.isEmpty(profileUrl)) {
                BudsBankUtils.loadImageFromURL(ivFeaturedDispBg, profileUrl);
            }
        } else {
            tvNoFeaturedShop.setVisibility(View.VISIBLE);
            featuredShopContainer.setVisibility(View.INVISIBLE);
            ivFeaturedDispBg.setImageResource(R.drawable.bg_blue);
        }
    }

    private void setupAdapter() {
        if (dispensaryModels != null && dispensaryModels.size() > 0) {
            collectionsContainer.setVisibility(View.VISIBLE);
            dispensaryAdapter = new DispensaryAdapter(mContext, dispensaryModels, AppConstants.IsFrom.DISPENSARY_FRAGMENT.getValue());
            rvCollections.setAdapter(dispensaryAdapter);
        } else {
            collectionsContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void goToQuizDispo(DispensaryModel model) {
        Intent intent = new Intent(mContext, QuizDispoActivity.class);
        intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.DISPENSARY_FRAGMENT.getValue());
        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), model);
        mContext.startActivity(intent);
    }

    private void goToSearch() {
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.DISPENSARY_FRAGMENT.getValue());
        mContext.startActivity(intent);
        ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_learn_more:
                if (featuredModelList != null && featuredModelList.size() > 0) {
                    DispensaryModel featuredDispensary = featuredModelList.get(0);
                    goToQuizDispo(featuredDispensary);
                }
                break;
            case R.id.et_container:
                goToSearch();
                break;
            case R.id.et_search:
                goToSearch();
                break;
            case R.id.tv_let_us_know:
                String email = "info@budsbank.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+email));
                startActivity(intent);
                break;
        }
    }

    private void registerReceiver() {
        localBroadcast = new LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.FOLLOW_UNFOLLOW.getValue());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(localBroadcast, intentFilter);
    }

    private class LocalBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            final String eventResponse = intent.getStringExtra(AppConstants.IntentKeys.EVENT_RESPONSE.getValue());
            if (action.equals(AppConstants.Actions.FOLLOW_UNFOLLOW.getValue())) {
                DispensaryModel dispensaryModel = (DispensaryModel) intent.getSerializableExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue());
                if (dispensaryModel != null) {
                    int position = getPosition(dispensaryModels, dispensaryModel.getId());
                    if (position > -1) {
                        dispensaryModels.set(position, dispensaryModel);
                        dispensaryAdapter.notifyItemChanged(position);
                        return;
                    }
                    position = getPosition(featuredModelList, dispensaryModel.getId());
                    if (position > -1) {
                        featuredModelList.set(position, dispensaryModel);
                        setFeaturedDispensary();
                    }
                }
            }
        }
    }

    private int getPosition(ArrayList<DispensaryModel> dispensaryModels, int dispensaryId) {
        if (dispensaryModels == null)
            return -1;
        for (int i = 0; i < dispensaryModels.size(); i++) {
            DispensaryModel dispensaryModel = dispensaryModels.get(i);
            if (dispensaryModel.getId() == dispensaryId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcast);
    }
}
