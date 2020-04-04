package com.app.budbank.activities;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.adapters.ViewPagerAdapter;
import com.app.budbank.fragments.BankFragment;
import com.app.budbank.fragments.DealsFragment;
import com.app.budbank.fragments.HomeFragment;
import com.app.budbank.fragments.DispensaryFragment;
import com.app.budbank.fragments.QuizIntroFragment;
import com.app.budbank.interfaces.Communicator;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.LoginResponseModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.cacheUtils.MainStorageUtils;
import com.app.budbank.web.APIController;

import java.util.ArrayList;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements Communicator {

    @BindView(R.id.rl_bank)
    RelativeLayout lytBank;
    @BindView(R.id.rl_dispensary)
    RelativeLayout lytDispensary;
    @BindView(R.id.rl_quiz)
    RelativeLayout lytQuiz;
    @BindView(R.id.rl_home)
    RelativeLayout lytHome;
    @BindView(R.id.rl_deals)
    RelativeLayout lytMore;
    @BindView(R.id.ll_ads_view)
    LinearLayout ll_ads_view;
    @BindView(R.id.bank_indicator)
    View bankIndicator;
    @BindView(R.id.disp_indicator)
    View dispIndicator;
    @BindView(R.id.quiz_indicator)
    View quizIndicator;
    @BindView(R.id.home_indicator)
    View homeIndicator;
    @BindView(R.id.more_indicator)
    View moreIndicator;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.tv_dispensaries)
    TextView tvDisp;
    @BindView(R.id.tv_quiz)
    TextView tvQuiz;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_deals)
    TextView tvMore;
    @BindView(R.id.view_ads)
    View viewAds;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;
    private View selectedView ;
    private TextView selectedTextView;
    private View selectedTab;
    private String DEVICE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        fetchFCMToken();
        DEVICE_ID = BudsBankUtils.getDeviceId(mContext);
        ll_ads_view.setVisibility(View.INVISIBLE);
        setClickListeneres();
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        ArrayList<DispensaryModel> list = mainStorageUtils.getDispensariesList();
        if (list != null && list.size() > 0) {
            initViewPager();
        } else if(BudsBankUtils.isGpsEnabled(mContext)) {
            requestLocationPermissionsAndFetchLocation();
        } else {
            BudsBankUtils.showLocationNotEnabledError(mContext,getString(R.string.gps_not_enabled));
        }
    }

    private void fetchFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.d("msg", msg);

                    }
                });
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode,data);
        if(reqCode == AppConstants.RequestCodes.REQUEST_SYSTEM_SETTINGS.getCode() || reqCode == AppConstants.RequestCodes.REQUEST_SOURCE_LOCATION.getCode()) {
            if(BudsBankUtils.isGpsEnabled(mContext)) {
                requestLocationPermissionsAndFetchLocation();
            } else {
                showLocationNotEnabledError(mContext,getString(R.string.gps_not_enabled));
            }
        }
    }

    private void showLocationNotEnabledError(Context mContext, String message) {
        DialogUtils.showAlertWithButtons(mContext, mContext.getString(R.string.location_not_enabled), message, mContext.getString(R.string.ok),
                mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogUtils.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, AppConstants.RequestCodes.REQUEST_SOURCE_LOCATION.getCode());
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogUtils.dismiss();

                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        DialogUtils.dismiss();

                    }
                });
    }


    protected void locationFetched() {
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(tvBank, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(this);
        fetchHomeContentDataFromServer();
    }

    private void fetchHomeContentDataFromServer() {
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        APIController.getHomeContent(sessionToken, userId,location.getLongitude(), location.getLatitude(), AppConstants.PAGE_SIZE, true,  new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull Response<LoginResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    return;
                }
                LoginResponseModel loginResponseModel = response.body();
                if (loginResponseModel != null) {
                    if (loginResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        storeData(loginResponseModel);
                        initViewPager();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
            }
        });
    }

    private void storeData(LoginResponseModel loginResponseModel) {
        StorageUtillity.saveUserModel(MainActivity.this, loginResponseModel.getUser());
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        mainStorageUtils.setLists(loginResponseModel.getDispensaryList(), loginResponseModel.getCompletedDispensariesList(),loginResponseModel.getFeaturedDispensariesList(), loginResponseModel.getAvailableVouchersList(), loginResponseModel.getRedeemedVouchersList());
    }

    private void setClickListeneres() {
        lytBank.setOnClickListener(this);
        lytDispensary.setOnClickListener(this);
        lytQuiz.setOnClickListener(this);
        lytHome.setOnClickListener(this);
        lytMore.setOnClickListener(this);
    }

    private void initViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new BankFragment());
        viewPagerAdapter.addFragment(new DispensaryFragment());
        viewPagerAdapter.addFragment(new QuizIntroFragment());
        viewPagerAdapter.addFragment(new HomeFragment());
        viewPagerAdapter.addFragment(new DealsFragment());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(3);
        selectedView = lytHome;
        selectedTextView = tvHome;
        selectedTab = homeIndicator;
        unSelectOtherViews(lytHome, tvHome, homeIndicator);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setActiveTabViews(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.rl_bank:
                viewPager.setCurrentItem(0);
                unSelectOtherViews(lytBank,tvBank, bankIndicator);
                break;
            case R.id.rl_dispensary:
                viewPager.setCurrentItem(1);
                unSelectOtherViews(lytDispensary,tvDisp, dispIndicator);
                break;
            case R.id.rl_quiz:
                viewPager.setCurrentItem(2);
                unSelectOtherViews(lytQuiz,tvQuiz, quizIndicator);
                break;
            case R.id.rl_home:
                viewPager.setCurrentItem(3);
                unSelectOtherViews(lytHome,tvHome, homeIndicator);
                break;
            case R.id.rl_deals:
                viewPager.setCurrentItem(4);
                unSelectOtherViews(lytMore,tvMore, moreIndicator);
                break;
        }
    }

    private void setActiveTabViews(int position) {
        switch (position) {
            case 0:
                unSelectOtherViews(lytBank, tvBank, bankIndicator);
                break;
            case 1:
                unSelectOtherViews(lytDispensary, tvDisp, dispIndicator);
                break;
            case 2:
                unSelectOtherViews(lytQuiz,tvQuiz, quizIndicator);
                break;
            case 3:
                unSelectOtherViews(lytHome, tvHome, homeIndicator);
                break;
            case 4:
                unSelectOtherViews(lytMore, tvMore, moreIndicator);
                break;
        }
    }

    private void unSelectOtherViews(View view, TextView textView, View indicator) {
        if (selectedView == null)
            return;
        selectedTab.setVisibility(View.INVISIBLE);
        selectedView.setSelected(false);
        Typeface unSelectedFont = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
        selectedTextView.setTypeface(unSelectedFont);
        view.setSelected(true);
        Typeface font = ResourcesCompat.getFont(mContext, R.font.montserrat_bold);
        textView.setTypeface(font);
        selectedView = view;
        selectedTextView = textView;
        selectedTab = indicator;
        indicator.setVisibility(View.VISIBLE);
        if(view.getId()== R.id.rl_deals) {
            viewAds.setVisibility(View.GONE);
        } else if(viewAds.getVisibility()!= View.VISIBLE){
            viewAds.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void switchTab(int position) {
        if (viewPager != null)
            viewPager.setCurrentItem(position, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int index = intent.getIntExtra(AppConstants.IntentKeys.FRAGMENT_TO_OPEN.getValue(), 3);
            if (viewPager != null)
                viewPager.setCurrentItem(index, false);
        }
    }
}
