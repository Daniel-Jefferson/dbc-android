package com.app.budbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.adapters.DispensaryAdapter;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.SearchResponseModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.app.budbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.ll_results_container)
    LinearLayout lytResultContainer;
    @BindView(R.id.rl_main_container)
    RelativeLayout lytMainContainer;
    @BindView(R.id.rv_results)
    RecyclerView rvResults;
    private int page = 1;
    private boolean isAllLoaded = false;
    private boolean isLoading = false;
    private String keyword ;
    private  ArrayList<DispensaryModel> dispensariesList;
    private DispensaryAdapter dispensaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(lytMainContainer,mContext);
        dispensariesList = new ArrayList<>();
        lytResultContainer.setVisibility(View.GONE);
        etSearch.requestFocus();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                page = 1;
                if(charSequence.length() == 0) {
                    lytResultContainer.setVisibility(View.GONE);
                    dispensariesList.clear();
                } else {
                    keyword = charSequence.toString();
                    search(keyword, page);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard((Activity) mContext);
                    return true;
                }

                return false;
            }
        });
        ivClear.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvResults.setLayoutManager(linearLayoutManager);
        dispensaryAdapter = new DispensaryAdapter(mContext, dispensariesList, AppConstants.IsFrom.SEARCH_ACTIVITY.getValue());
        rvResults.setAdapter(dispensaryAdapter);
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
                    search(keyword,page);
                }
            }
        }
    };

    private void search(String keyword, int page) {
        int pageSize = AppConstants.PAGE_SIZE;
        rvResults.removeOnScrollListener(onScrollListener);
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(etSearch, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        isLoading = true;
        APIController.search(sessionToken, userId, keyword, page, pageSize, new Callback<SearchResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponseModel> call, @NonNull Response<SearchResponseModel> response) {
                rvResults.setOnScrollListener(onScrollListener);
                isLoading =false;
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(etSearch, getString(R.string.call_fail_error), mContext );
                    return;
                }
                SearchResponseModel searchResponseModel = response.body();
                if (searchResponseModel != null) {
                    if(page == 1) {
                        if(!TextUtils.isEmpty(etSearch)) {
                            populateList(searchResponseModel.getDispensariesList());
                            if (dispensariesList != null && dispensariesList.size() > 0) {
                                if(dispensaryAdapter==null) {
                                    setupAdapter();
                                } else {
                                    dispensaryAdapter.notifyDataSetChanged();
                                }
                                lytResultContainer.setVisibility(View.VISIBLE);
                            } else {
                                lytResultContainer.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        int size = dispensariesList.size();
                        dispensariesList.addAll(searchResponseModel.getDispensariesList());
                        dispensaryAdapter.notifyItemRangeChanged(size, dispensariesList.size());
                        if(searchResponseModel.getPageInfo().getCount() == dispensariesList.size())
                            isAllLoaded = true;
                    }
                } else {
                    DialogUtils.showSnackBar(etSearch, searchResponseModel.getMessage(), mContext);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponseModel> call, @NonNull Throwable t) {
                rvResults.setOnScrollListener(onScrollListener);
                isLoading =false;
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(etSearch, getString(R.string.call_fail_error), mContext);
            }
        });
    }

    private void populateList(ArrayList<DispensaryModel> dispensaries) {
        dispensariesList.clear();
        if(dispensaries!=null && dispensaries.size()>0) {
            for(DispensaryModel model : dispensaries) {
                dispensariesList.add(model);
            }
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(this);
        finish();
        ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_clear:
                if(etSearch.getText().length() >0)
                    etSearch.setText("");
                else
                    hideKeyboard(this);
                break;
            case R.id.iv_back:
                hideKeyboard(this);
                finish();
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                break;
        }
    }
}
