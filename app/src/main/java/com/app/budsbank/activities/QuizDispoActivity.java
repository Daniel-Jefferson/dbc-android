package com.app.budsbank.activities;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.DispensaryResponseModel;
import com.app.budsbank.models.OpenCloseTimeModel;
import com.app.budsbank.models.QuizQuestionsModel;
import com.app.budsbank.models.QuizResponseModel;
import com.app.budsbank.models.ResponseModel;
import com.app.budsbank.models.TimeDataModel;
import com.app.budsbank.models.requestModel.FollowUnFollowRequestModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.github.piasy.fresco.draweeview.shaped.ShapedDraweeView;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDispoActivity extends BaseActivity {
    @BindView(R.id.parent_view)
    View parentView;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.tv_go_back)
    TextView tvGoBack;
    @BindView(R.id.tv_choose_dispensary)
    TextView tvChooseDispensary;
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.btn_piggy_bank)
    Button btnPiggyBank;
    @BindView(R.id.rl_call_now)
    RelativeLayout layoutCallNow;
    @BindView(R.id.rl_directions)
    RelativeLayout layoutDirections;
    @BindView(R.id.rl_open_now)
    RelativeLayout layoutOpenNow;
    @BindView(R.id.iv_dispensary_bg)
    ShapedDraweeView ivDispensaryBg;
    @BindView(R.id.ll_dispo_contact)
    LinearLayout layoutDispoContact;
    @BindView(R.id.tv_dispensary_name)
    TextView tvDispensaryName;
    @BindView(R.id.tv_dispensary_address)
    TextView tvDispensaryAddress;
    @BindView(R.id.tv_coins_earned)
    TextView tvCoinsEarned;
    @BindView(R.id.tv_open_close)
    TextView tvOpenClose;
    private DispensaryModel dispensaryModel;
    private int dispensaryId = 0;
    private String isFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dispo);

        Bundle bundle = getIntent().getExtras();
        if(bundle  != null) {
            isFrom = bundle.getString(AppConstants.ISFROM);
            dispensaryModel = (DispensaryModel) bundle.getSerializable(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue());
            dispensaryId = bundle.getInt(AppConstants.IntentKeys.DISPENSARY_ID.getValue());
        }
        mContext = this;
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(scrollView,mContext);
        if(dispensaryModel!=null) {
            populateUIBasedOnDispensaryModel();
        } else  {
            String dispId  = Integer.toString(dispensaryId);
            fetchDispensaryById(dispId);
        }
        populateUIBasedOnFrom();
        tvFollow.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
    }

    private void fetchDispensaryById(String dispensaryId) {
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(btnPlay, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(this);
        APIController.getDispensaryById(sessionToken, dispensaryId,  userId , new Callback<DispensaryResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<DispensaryResponseModel> call, @NonNull Response<DispensaryResponseModel> response) {
                DialogUtils.stopLoading();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizDispoActivity.this );
                    return;
                }
                DispensaryResponseModel dispensaryResponseModel = response.body();
                if (dispensaryResponseModel != null) {
                    dispensaryModel = dispensaryResponseModel.getDispensaryModel();
                    if (dispensaryModel != null) {
                        populateUIBasedOnDispensaryModel();
                    }
                } else {
                    DialogUtils.showSnackBar(parentView, dispensaryResponseModel.getMessage(), QuizDispoActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DispensaryResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizDispoActivity.this);
            }
        });
    }

    private void populateUIBasedOnDispensaryModel() {
        if (dispensaryModel == null)
            return;
        String profileUrl = dispensaryModel.getProfileUrl();
        if (profileUrl != null && !TextUtils.isEmpty(profileUrl)) {
            BudsBankUtils.loadImageFromURL(ivDispensaryBg, profileUrl);
        }
        tvDispensaryName.setText(dispensaryModel.getName());
        tvDispensaryAddress.setText(dispensaryModel.getAddress());
        tvCoinsEarned.setText(StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), "0"));
        String followingText = dispensaryModel.isFollowed() ? getString(R.string.following) : getString(R.string.follow);
        tvFollow.setText(followingText);
        if(!dispensaryModel.isAvailable()) {
            btnPlay.setVisibility(View.GONE);
        }
        if(!dispensaryModel.isFollowed()) {
            enablePlayButton(false);
        } else {
            tvFollow.setBackgroundResource(R.drawable.following_bg_selecter);
        }
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        boolean isAvailable = mainStorageUtils.isDispensaryAvailable(dispensaryModel.getId());
        if (!isAvailable) {
            btnPlay.setVisibility(View.GONE);
        }
        OpenCloseTimeModel openCloseModel = dispensaryModel.getOpenCloseTimeModel();
        if (openCloseModel != null) {
            boolean isOpen = isDispensaryOpen(openCloseModel);
            if (isOpen)
                tvOpenClose.setText(getString(R.string.open_now));
            else
                tvOpenClose.setText(getString(R.string.closed_now));
        } else
            tvOpenClose.setText(getString(R.string.closed_now));
    }

    private void enablePlayButton(boolean enable) {
        if(enable) {
            btnPlay.setEnabled(true);
            btnPlay.setBackgroundResource(R.drawable.alert_btn_selecter);
            btnPlay.setText(R.string.start_quiz);
        } else {
            btnPlay.setEnabled(false);
            btnPlay.setBackgroundResource(R.drawable.alert_btn_disabled);
            btnPlay.setText(R.string.follow_to_play);
        }
    }

    private boolean isDispensaryOpen(OpenCloseTimeModel openCloseModel) {
        String openDay = openCloseModel.getOpenDay();
        String closeDay = openCloseModel.getCloseDay();
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        int openDayOfWeek = getDayOfWeek(openDay);
        int closeDayOfWeek = getDayOfWeek(closeDay);
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (todayDayOfWeek >= openDayOfWeek && todayDayOfWeek <= closeDayOfWeek || openDayOfWeek > closeDayOfWeek) {

            for (int i = 0; i < openCloseModel.getTimeDataModelArray().size(); i++) {
                TimeDataModel timeDataModel = openCloseModel.getTimeDataModelArray().get(i);
                String openTime = timeDataModel.getOpenTime();
                String closeTime = timeDataModel.getCloseTime();
                if (timeDataModel.getWeekday() == todayDayOfWeek) {
                    Calendar now = Calendar.getInstance();
//            now.setTimeZone(TimeZone.getTimeZone("UTC"));
                    int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
                    int minute = now.get(Calendar.MINUTE);
                    Date date = parseDate(hour + ":" + minute+":00");
                    Date dateCompareOne = parseDate(openTime);
                    Date dateCompareTwo = parseDate(closeTime);
                    if (dateCompareOne.before( date ) && dateCompareTwo.after(date)) {
                        return true;
                    }
                }

            }



        }

        return false;
    }

    private int getDayOfWeek(String dayStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(dayStr);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar.get(Calendar.DAY_OF_WEEK);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Date getHoursParsed(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    private Date parseDate(String date) {

        final String inputFormat = "hh:mm:ss";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat);
//        inputParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void populateUIBasedOnFrom() {
        if(isFrom != null && !TextUtils.isEmpty(isFrom)) {
            if(!isFrom.equals(AppConstants.IsFrom.QUIZ_FRAGMENT.getValue())) {
                layoutDispoContact.setVisibility(View.VISIBLE);
                btnPiggyBank.setVisibility(View.VISIBLE);
                tvGoBack.setText(R.string.close);
                tvChooseDispensary.setText(R.string.shops);
                btnPiggyBank.setOnClickListener(this);
                layoutCallNow.setOnClickListener(this);
                layoutDirections.setOnClickListener(this);
                layoutOpenNow.setOnClickListener(this);
            }
        }

        tvFollow.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
    }

    private void followDispensary() {
        if (!dispensaryModel.isFollowed()) {
            tvFollow.setText(R.string.following);
            tvFollow.setBackgroundResource(R.drawable.following_bg_selecter);
            follow();
        } else {
            tvFollow.setText(R.string.follow);
            tvFollow.setBackgroundResource(R.drawable.follow_bg_selecter);
            enablePlayButton(false);
            unFollow();
        }
    }

    private void follow() {
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        FollowUnFollowRequestModel followUnFollowRequestModel = new FollowUnFollowRequestModel(userId, dispensaryModel.getId());
        APIController.followDispensary(sessionToken, followUnFollowRequestModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                ResponseModel responseModel = response.body();
                if (responseModel != null && responseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                    dispensaryModel.setFollowed(true);
                    enablePlayButton(true);
                    MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                    mainStorageUtils.updateDispensary(dispensaryModel);
                    broadcastAction();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {

            }
        });
    }
    private void unFollow() {
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        FollowUnFollowRequestModel followUnFollowRequestModel = new FollowUnFollowRequestModel(userId, dispensaryModel.getId());
        APIController.unFollowDispensary(sessionToken, followUnFollowRequestModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    enablePlayButton(true);
                    return;
                }
                ResponseModel responseModel = response.body();
                if (responseModel != null && responseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                    dispensaryModel.setFollowed(false);
                    MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                    mainStorageUtils.updateDispensary(dispensaryModel);
                    broadcastAction();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                enablePlayButton(true);
            }
        });
    }

    public void broadcastAction() {
        if (dispensaryModel == null)
            return;
        Intent intent = new Intent(AppConstants.Actions.FOLLOW_UNFOLLOW.getValue());
        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), dispensaryModel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_go_back:
                finish();
                break;
            case R.id.btn_play:
                actionBtnPlay();
                break;
            case R.id.tv_follow:
                followDispensary();
                break;
            case R.id.btn_piggy_bank:
                actionPiggyBank();
                break;
            case R.id.rl_call_now:
                actionCallNow();
                break;
            case R.id.rl_directions:
                actionDirections();
                break;
        }
    }

    private void actionPiggyBank() {
        Intent intent  = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(AppConstants.IntentKeys.FRAGMENT_TO_OPEN.getValue(), 0);
        startActivity(intent);
        finishAffinity();
    }

    private void actionBtnPlay() {
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(btnPlay, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(this, getString(R.string.initializing_quiz));
        APIController.getQuiz(sessionToken, userId, 1, new Callback<QuizResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponseModel> call, @NonNull Response<QuizResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizDispoActivity.this );
                    return;
                }
                QuizResponseModel quizResponseModel = response.body();
                if (quizResponseModel != null) {
                    ArrayList<QuizQuestionsModel> questions = quizResponseModel.getQuestions();
                    if (questions != null && questions.size() > 0) {
                        Intent intent = new Intent(QuizDispoActivity.this, QuizActivity.class);
                        intent.putExtra(AppConstants.IntentKeys.QUIZ_QUESTIONS.getValue(), questions);
                        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), dispensaryModel);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                DialogUtils.showSnackBar(parentView, getString(R.string.no_questions_available), QuizDispoActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizDispoActivity.this);
            }
        });
    }

    private void actionDirections() {
        String map = "http://maps.google.co.in/maps?q=" + dispensaryModel.getAddress();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        mContext.startActivity(intent);
    }

    private void actionCallNow() {
        if (dispensaryModel == null || TextUtils.isEmpty(dispensaryModel.getPhone()))
            return;
        BudsBankUtils.call(this, dispensaryModel.getPhone());
    }
}
