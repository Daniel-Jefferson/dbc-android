package com.app.budbank.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.app.budbank.R;
import com.app.budbank.interfaces.AlertDialogListener;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.LoginModel;
import com.app.budbank.models.LoginResponseModel;
import com.app.budbank.models.VerifyCodeModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.cacheUtils.MainStorageUtils;
import com.app.budbank.web.APIController;
import com.app.budbank.web.WebConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.parent_view)
    View parentView;
    @BindView(R.id.et_email)
    TextView etEmail;
    @BindView(R.id.et_password)
    TextView etPassword;
    @BindView(R.id.tv_signup)
    TextView tvSignup;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    @BindView(R.id.tv_space)
    TextView tvSpace;
    private String isFrom;
    private boolean sendApi= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.ISFROM);
        }

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        isEmailVerified =false;
        String terms = mContext.getString(R.string.terms_of_use);
        String privacy = mContext.getString(R.string.privacy_policy);
        String termsOfUse = getString(R.string.accept_disclaimer);
        SpannableStringBuilder formatedString = BudsBankUtils.formatSpannableString(mContext, termsOfUse, terms, privacy);
        requestLocationPermission( callable);
        tvTerms.setText(formatedString, TextView.BufferType.SPANNABLE);
        btnLogin.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
        tvSpace.setOnClickListener(this);
    }

    protected void locationFetched() {
        if(sendApi) {
            LoginModel loginModel = populateLoginModel(location);
            login(loginModel);
        }
    }


    private boolean validateInput() {
        if (TextUtils.isEmpty(etEmail.getText()) ) {
            DialogUtils.showSnackBar(etEmail, getString(R.string.enter_email),mContext);
            return false;
        } else if(!BudsBankUtils.isValidEmail(etEmail.getText().toString())){
            DialogUtils.showSnackBar(etEmail, getString(R.string.enter_valid_email),mContext);
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText()) || etPassword.length() < 8) {
            DialogUtils.showSnackBar(etPassword, getString(R.string.enter_password),mContext);
            return false;
        } else {
            return true;
        }
    }

    private void validate() {
        if(validateInput()) {
            if(BudsBankUtils.isGpsEnabled(mContext))
                requestLocationPermission( callable);
            else
                BudsBankUtils.showLocationNotEnabledError(mContext, getString(R.string.gps_not_enabled));
        }
    }



    private LoginModel populateLoginModel(Location location) {
        LoginModel model = new LoginModel();
        model.setValue(etEmail.getText().toString());
        model.setPassword(etPassword.getText().toString());
        model.setLatitude((float)location.getLatitude());
        model.setLogitude((float)location.getLongitude());

        return model;
    }

    private void login(LoginModel loginModel) {
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(etEmail, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(this);
        APIController.loginUser( loginModel, true, new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull final Response<LoginResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), LoginActivity.this);
                    return;
                }
                LoginResponseModel loginResponseModel = response.body();
                if (loginResponseModel != null) {
                    if (loginResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        String emailVerifiedAt = loginResponseModel.getUser().getEmailVerifiedAt();
                        if (emailVerifiedAt != null)
                            isEmailVerified = true;
                        storeData(loginResponseModel);
                        goToHome();
                    } else {
                        DialogUtils.showSnackBar(parentView, loginResponseModel.getMessage(), Snackbar.LENGTH_LONG,mContext);
                    }
                    return;
                }
                DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), LoginActivity.this);
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showErrorBasedOnType(mContext, parentView, t);
                Log.d("error", "onFailure:" + t.getMessage());
            }
        });
    }

    private void storeData(LoginResponseModel loginResponseModel) {
        StorageUtillity.saveDataInPreferences(mContext,AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(),loginResponseModel.getUser().getSessionToken());
        StorageUtillity.saveUserModel(mContext, loginResponseModel.getUser());
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        mainStorageUtils.setLists(loginResponseModel.getDispensaryList(), loginResponseModel.getCompletedDispensariesList(),loginResponseModel.getFeaturedDispensariesList(), loginResponseModel.getAvailableVouchersList(), loginResponseModel.getRedeemedVouchersList());
    }

    private void populateVerifyCodeModel(String code) {
        VerifyCodeModel verifyCodeModel = new VerifyCodeModel();
        verifyCodeModel.setVerificationCode(code);
        verifyCodeModel.setLatitude(location.getLatitude());
        verifyCodeModel.setLongitude(location.getLongitude());
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        verifyCode(sessionToken, verifyCodeModel);
    }

    private void verifyCode(String sessionToken, VerifyCodeModel verifyCodeModel) {
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showCustomToast(mContext, getString(R.string.no_internet_alert));
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(this);
        APIController.verifyCode( sessionToken, verifyCodeModel,  new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull final Response<LoginResponseModel> response) {
                DialogUtils.stopLoading();
                if (!response.isSuccessful()) {
                    DialogUtils.showCustomToast(LoginActivity.this, getString(R.string.call_fail_error));
                    return;
                }
                LoginResponseModel verifyCodeResponseModel = response.body();
                if (verifyCodeResponseModel != null) {
                    if (verifyCodeResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        DialogUtils.dismiss();
                        String emailVerifiedAt = verifyCodeResponseModel.getUser().getEmailVerifiedAt();
                        if (emailVerifiedAt != null)
                            isEmailVerified = true;
                        storeData(verifyCodeResponseModel);
                        goToHome();
                    } else {
                        DialogUtils.stopLoading();
                        DialogUtils.showCustomToast(mContext, verifyCodeResponseModel.getMessage());
                    }
                    return;
                }
                DialogUtils.stopLoading();
                DialogUtils.showCustomToast(LoginActivity.this, getString(R.string.call_fail_error));
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                DialogUtils.stopLoading();
                DialogUtils.showErrorBasedOnType(mContext,t);
                Log.d("error", "onFailure:" + t.getMessage());
            }
        });
    }


    private void goToHome() {
        if (isEmailVerified) {
            StorageUtillity.saveDataInPreferences(LoginActivity.this, AppConstants.SharedPreferencesKeys.IS_LOGGED_IN.getValue(), true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            DialogUtils.showVerificationAlert(mContext, getString(R.string.email_not_verified), new AlertDialogListener() {
                @Override
                public void call(String code) {
                    populateVerifyCodeModel(code);
                }
            });
        }
    }


    private void goToSignUp() {
        Intent intent = new Intent(mContext, SignUpActivity.class);
        intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.LOGIN_ACTIVTY.getValue());
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                hideKeyboard(this);
                sendApi =true;
                validate();
                break;
            case R.id.tv_signup:
                goToSignUp();
                break;
            case R.id.tv_space:
                startActivity(new Intent(mContext, ForgotPasswordActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BudsBankUtils.checkPlayServices(mContext);
    }



    @Override
    public void onBackPressed() {
        if (isFrom != null) {
            if (isFrom.equals(AppConstants.IsFrom.BOARDING_ACTIVTY.getValue())) {
                startActivity(new Intent(mContext, OnBoardingHomeActivity.class));
                finish();
            } else if (isFrom.equals(AppConstants.IsFrom.SIGNUP_ACTIVTY.getValue())) {
                startActivity(new Intent(mContext, SignUpActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(mContext, OnBoardingHomeActivity.class));
            finish();
        }
    }

    private Callable<Void> callable = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            if(location==null)
                getLastLocation();
            else {
                LoginModel loginModel = populateLoginModel(location);
                login(loginModel);
            }
            return null;
        }
    };
}
