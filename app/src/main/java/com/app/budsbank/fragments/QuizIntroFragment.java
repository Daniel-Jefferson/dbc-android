package com.app.budsbank.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.activities.SearchActivity;
import com.app.budsbank.adapters.DisabledCollectionAdapter;
import com.app.budsbank.adapters.QuizDispensaryAdapter;
import com.app.budsbank.models.CompletedDispensariesModel;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.SearchResponseModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;
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
public class QuizIntroFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rv_collections)
    RecyclerView rvCollections;
    @BindView(R.id.rv_disabled_collections)
    RecyclerView rvDisabledCollections;
    @BindView(R.id.prog_bar)
    ProgressBar progressBar;
    @BindView(R.id.prog_bar_container)
    RelativeLayout lytProgBarContainer;
    @BindView(R.id.tv_no_dispensary)
    TextView tvNoDispensary;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.ll_available_dispensaries)
    View availableDispensariesContainer;
    @BindView(R.id.ll_completed_dispensaries_container)
    View completedDispensariesContainer;

    private ArrayList<DispensaryModel> collectionsList;
    private ArrayList<DispensaryModel> disabledCollectionsList;
    private LocalBroadcast localBroadcast;
    private QuizDispensaryAdapter dispensaryAdapter;
    private DisabledCollectionAdapter disabledCollectionAdapter;
    private LinearLayoutManager collectionLayoutManager;
    private LinearLayoutManager disabledLayoutManager;
    private int collectionPage = 1;
    private int disabledCollectionPage = 1;
    private Location location;
    private boolean isAllDisabledLoaded = false;
    private boolean isAllLoaded = false;
    private boolean isLoading = false;

    public QuizIntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initViews();
        registerReceiver();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer, mContext);
        disabledCollectionsList = new ArrayList<>();
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        disabledCollectionsList = mainStorageUtils.getCompletedDispensariesList();
        rvCollections.setOnScrollListener(onScrollListener);
        rvDisabledCollections.setOnScrollListener(onCompletedScrollListener);

        fetchLocationAndSendNearbyDispensaryCall(collectionPage);
        setupDisabledAdapter();

        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
        });
    }

    private void setAvailableDispensaryView() {
        progressBar.setVisibility(View.GONE);
        if (collectionsList != null && collectionsList.size() > 0) {
            lytProgBarContainer.setVisibility(View.GONE);
            tvNoDispensary.setVisibility(View.GONE);
            rvCollections.setVisibility(View.VISIBLE);
            availableDispensariesContainer.setVisibility(View.VISIBLE);
        } else {
            rvCollections.setVisibility(View.GONE);
            lytProgBarContainer.setVisibility(View.VISIBLE);
            tvNoDispensary.setVisibility(View.VISIBLE);
        }
    }

    private void setupAdapter() {
        collectionLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvCollections.setLayoutManager(collectionLayoutManager);
        dispensaryAdapter = new QuizDispensaryAdapter(mContext, collectionsList, AppConstants.IsFrom.QUIZ_FRAGMENT.getValue());
        rvCollections.setAdapter(dispensaryAdapter);
    }

    private void setupDisabledAdapter() {
        disabledLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvDisabledCollections.setLayoutManager(disabledLayoutManager);
        if (disabledCollectionsList != null && disabledCollectionsList.size() > 0) {
            completedDispensariesContainer.setVisibility(View.VISIBLE);
            disabledCollectionAdapter = new DisabledCollectionAdapter(mContext, disabledCollectionsList);
            rvDisabledCollections.setAdapter(disabledCollectionAdapter);
        } else
            completedDispensariesContainer.setVisibility(View.INVISIBLE);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = collectionLayoutManager.getChildCount();
            int totalItemCount = dispensaryAdapter.getItemCount();
            int firstVisibleItemPosition = collectionLayoutManager.findFirstVisibleItemPosition();
            if (!isAllLoaded && !isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 3) {
                    if(location!=null) {
                        collectionPage++;
                        getNearbyDispensaries(collectionPage);
                    }
                }
            }
        }
    };

    private RecyclerView.OnScrollListener onCompletedScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = disabledLayoutManager.getChildCount();
            int totalItemCount = disabledCollectionAdapter.getItemCount();
            int firstVisibleItemPosition = disabledLayoutManager.findFirstVisibleItemPosition();
            if (!isAllDisabledLoaded && !isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 3) {
                    disabledCollectionPage++;
                    getCompletedDispensaries(disabledCollectionPage);
                }
            }
        }
    };

    private void getCompletedDispensaries(int page) {
        rvDisabledCollections.removeOnScrollListener(onCompletedScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            rvDisabledCollections.setOnScrollListener(onCompletedScrollListener);
            DialogUtils.showSnackBar(rvDisabledCollections, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            return;
        }
        isLoading = true;
        APIController.getCompletedDispensaries(sessionToken, userId, pageSize, page, new Callback<CompletedDispensariesModel>() {
            @Override
            public void onResponse(@NonNull Call<CompletedDispensariesModel> call, @NonNull Response<CompletedDispensariesModel> response) {
                rvDisabledCollections.setOnScrollListener(onCompletedScrollListener);
                isLoading = false;
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvDisabledCollections, getString(R.string.call_fail_error), mContext);
                    return;
                }
                CompletedDispensariesModel searchResponseModel = response.body();
                if (searchResponseModel != null) {
                    if (page == 1) {
                        disabledCollectionsList = searchResponseModel.getDispensariesList();
                        setupDisabledAdapter();
                    } else {
                        int size = disabledCollectionsList.size();
                        disabledCollectionsList.addAll(searchResponseModel.getDispensariesList());
                        disabledCollectionAdapter.notifyItemRangeChanged(size, disabledCollectionsList.size());
                        if (searchResponseModel.getPageInfo().getCount() == disabledCollectionsList.size()) {
                            isAllDisabledLoaded = true;
                        }

                    }
                } else {
                    DialogUtils.showSnackBar(rvCollections, searchResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CompletedDispensariesModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                rvCollections.setOnScrollListener(onScrollListener);
                DialogUtils.showSnackBar(rvCollections, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void getNearbyDispensaries(int page) {
        rvCollections.removeOnScrollListener(onScrollListener);
        int pageSize = AppConstants.PAGE_SIZE;
        Double latitude = 0.0;
        Double longitude = 0.0;
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(rvCollections, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            return;
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else
            return;
        isLoading = true;
        APIController.getNearbyDispensaries(sessionToken, userId, latitude, longitude, pageSize, page, new Callback<SearchResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponseModel> call, @NonNull Response<SearchResponseModel> response) {
                rvCollections.setOnScrollListener(onScrollListener);
                isLoading = false;
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(rvCollections, getString(R.string.call_fail_error), mContext);
                    return;
                }
                SearchResponseModel searchResponseModel = response.body();
                if (searchResponseModel != null) {
                    if (page == 1) {
                        collectionsList = searchResponseModel.getDispensariesList();
                        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                        mainStorageUtils.setAvailableDispensariesList(collectionsList);
                        setAvailableDispensaryView();
                        setupAdapter();
                    } else {
                        int size = collectionsList.size();
                        collectionsList.addAll(searchResponseModel.getDispensariesList());
                        dispensaryAdapter.notifyItemRangeChanged(size, collectionsList.size());
                        if (searchResponseModel.getPageInfo().getCount() == collectionsList.size()) {
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
                rvCollections.setOnScrollListener(onScrollListener);
                isLoading = false;
                DialogUtils.showSnackBar(rvCollections, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void fetchLocationAndSendNearbyDispensaryCall(int page) {
        rvCollections.removeOnScrollListener(onScrollListener);
        isLoading = true;
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastLocation) {
                        location = lastLocation;
                        getNearbyDispensaries(page);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isLoading = false;
                rvCollections.setOnScrollListener(onScrollListener);
            }
        });

    }

    private void registerReceiver() {
        localBroadcast = new LocalBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Actions.COMPLETED_DISPENSARY.getValue());
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
            if (action.equals(AppConstants.Actions.COMPLETED_DISPENSARY.getValue())) {
                MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                collectionsList = mainStorageUtils.getAvailableDispensariesList();
                disabledCollectionsList = mainStorageUtils.getCompletedDispensariesList();
                collectionPage = 1;
                setupAdapter();
                setupDisabledAdapter();
            } else if (action.equals(AppConstants.Actions.FOLLOW_UNFOLLOW.getValue())) {
                DispensaryModel dispensaryModel = (DispensaryModel) intent.getSerializableExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue());
                if (dispensaryModel != null) {
                    int position = getPosition(collectionsList, dispensaryModel.getId());
                    if (position > -1) {
                        collectionsList.set(position, dispensaryModel);
                        dispensaryAdapter.notifyItemChanged(position);
                        return;
                    }
                    position = getPosition(disabledCollectionsList, dispensaryModel.getId());
                    if (position > -1) {
                        disabledCollectionsList.set(position, dispensaryModel);
                        disabledCollectionAdapter.notifyItemChanged(position);
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
