package com.app.budsbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.interfaces.AlertDialogListener;
import com.app.budsbank.models.LoginResponseModel;
import com.app.budsbank.models.SignupModel;
import com.app.budsbank.models.SignupResponseModel;
import com.app.budsbank.models.VerifyCodeModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.et_phone_number)
    EditText etPhoneNumber;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_full_name)
    EditText etFullname;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.btn_signup)
    Button btnSignUp;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    private String isFrom, verificationCode;
    private boolean sendApi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.ISFROM);
        }

        ButterKnife.bind(this);
        initViews();
        getLastLocation();
    }

    private void initViews() {
        String terms = String.format("<a href=http://budsbank.com/terms-of-use><b>%s</b></a>", mContext.getString(R.string.terms_of_use));
        String privacy = String.format("<a href=http://budsbank.com/privacy-policy><b>%s</b></a>", mContext.getString(R.string.privacy_policy));
        String termsOfUse = getString(R.string.accept_disclaimer);
        String formatedString = String.format(termsOfUse, terms, privacy);
        requestLocationPermission( callable);
        setTextWatcher();
        tvTerms.setText(Html.fromHtml(formatedString), TextView.BufferType.SPANNABLE);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        tvLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void setTextWatcher() {
        final List<String> affineFormats = new ArrayList<>();
        affineFormats.add("([000]) [000]-[0000]");

        final MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                etPhoneNumber,
                "[]",
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue, @NonNull String formattedText) {
                        etPhoneNumber.setCursorVisible(true);
                    }
                }
        );
    }

    private String getPhoneNumber() {
        String phoneNumber = etPhoneNumber.getText().toString().replaceAll("[()-]", "").trim();
        phoneNumber = phoneNumber.replaceAll(" ","");
        return phoneNumber;
    }

    private boolean validateInput() {
        String phoneNumber = getPhoneNumber();
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            DialogUtils.showSnackBar(etPhoneNumber, getString(R.string.enter_phone), mContext);
            return false;
        } else if(phoneNumber.length()!=10){
            DialogUtils.showSnackBar(etFullname, getString(R.string.enter_valid_phone), Snackbar.LENGTH_LONG,mContext);
            return false;
        }

        if (TextUtils.isEmpty(etUsername.getText())) {
            DialogUtils.showSnackBar(etUsername, getString(R.string.fill_all_fields),mContext);
            return false;
        }

        if (TextUtils.isEmpty(etFullname.getText())) {
            DialogUtils.showSnackBar(etFullname, getString(R.string.fill_all_fields),mContext);
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText()) || etPassword.length() < 8) {
            DialogUtils.showSnackBar(etPassword, getString(R.string.enter_password),mContext);
            return false;
        } else if (!BudsBankUtils.isValidPassword(etPassword.getText().toString())) {
            DialogUtils.showSnackBar(etPassword, getString(R.string.enter_valid_password), Snackbar.LENGTH_LONG,mContext);
            return false;
        }

        if (TextUtils.isEmpty(etConfirmPassword.getText())) {
            DialogUtils.showSnackBar(etConfirmPassword, getString(R.string.fill_all_fields),mContext);
            return false;
        }

        if (!(etConfirmPassword.getText().toString()).equals(etPassword.getText().toString())) {
            DialogUtils.showSnackBar(etFullname, getString(R.string.password_mismatch),mContext);
            return false;
        } else {
            return true;
        }

    }

    private void registerUser() {
        if (validateInput()) {
            SignupModel signupModel = populateSignupModel();
            if (!BudsBankUtils.isNetworkAvailable(this)) {
                DialogUtils.showSnackBar(etEmail, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG,  mContext);
                DialogUtils.stopLoading();
                return;
            }
            DialogUtils.showLoading(this);
            APIController.registerUser(signupModel, new Callback<SignupResponseModel>() {
                @Override
                public void onResponse(Call<SignupResponseModel> call, final Response<SignupResponseModel> response) {
                    DialogUtils.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        SignupResponseModel signupResponseModel = response.body();
                        if (signupResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                            StorageUtillity.saveDataInPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), signupResponseModel.getUser().getSessionToken());
                            showAlert();
                        } else {
                            DialogUtils.showSnackBar(etEmail, signupResponseModel.getMessage(), Snackbar.LENGTH_LONG,mContext);
                        }
                    } else
                        DialogUtils.showSnackBar(etEmail, response.body().getMessage(), Snackbar.LENGTH_LONG,mContext);
                }

                @Override
                public void onFailure(Call<SignupResponseModel> call, Throwable t) {
                    DialogUtils.dismiss();
                    DialogUtils.showErrorBasedOnType(mContext,btnSignUp ,t);
                    Log.d("error", "onFailure:" + t.getMessage());
                }
            });
        }
    }

    private SignupModel populateSignupModel() {
        SignupModel model = new SignupModel();
        model.setUsername(etUsername.getText().toString());
        model.setEmail(etEmail.getText().toString());
        model.setFullName(etFullname.getText().toString());
        String number = getPhoneNumber();
        model.setPhone(number);
        model.setPassword(etPassword.getText().toString());

        return model;
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
        APIController.verifyCode(sessionToken, verifyCodeModel, new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, final Response<LoginResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseModel verifyCodeResponseModel = response.body();
                    if (verifyCodeResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        DialogUtils.dismiss();
                        isPhoneVerified = verifyCodeResponseModel.getUser().getPhoneVerified() == 1;
                        storeData(verifyCodeResponseModel);
                        goToHome();
                    } else {
                        DialogUtils.stopLoading();
                        DialogUtils.showCustomToast(mContext, verifyCodeResponseModel.getMessage());
                    }
                } else {
                    DialogUtils.stopLoading();
                    DialogUtils.showCustomToast(mContext, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                DialogUtils.stopLoading();
                DialogUtils.showErrorBasedOnType(mContext, t);
                Log.d("error", "onFailure:" + t.getMessage());
            }
        });
    }

    private void storeData(LoginResponseModel verifyCodeResponseModel) {
        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
        mainStorageUtils.setLists(verifyCodeResponseModel.getDispensaryList(), verifyCodeResponseModel.getCompletedDispensariesList(),verifyCodeResponseModel.getFeaturedDispensariesList(), verifyCodeResponseModel.getAvailableVouchersList(), verifyCodeResponseModel.getRedeemedVouchersList());
        StorageUtillity.saveDataInPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), verifyCodeResponseModel.getUser().getSessionToken());
        StorageUtillity.saveUserModel(SignUpActivity.this, verifyCodeResponseModel.getUser());
    }

    private void goToHome() {
        if (isPhoneVerified) {
            StorageUtillity.saveDataInPreferences(SignUpActivity.this, AppConstants.SharedPreferencesKeys.IS_LOGGED_IN.getValue(), true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            showAlert();
        }
    }

    private void showAlert() {
        DialogUtils.showVerificationAlert(mContext, String.format(getString(R.string.alert_sms_message), "+1" + getPhoneNumber()), new AlertDialogListener() {
            @Override
            public void call(String code) {
                verificationCode = code;
                sendApi =true;
                if(BudsBankUtils.isGpsEnabled(mContext)) {
                    requestLocationPermission( callable);
                }
                else {
                    BudsBankUtils.showLocationNotEnabledError(mContext, getString(R.string.gps_not_enabled));
                    return;
                }
            }
        });
    }

    protected void locationFetched() {
        if(sendApi) {
            populateVerifyCodeModel(verificationCode);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                hideKeyboard(this);
                registerUser();
                break;
            case R.id.tv_login:
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.SIGNUP_ACTIVTY.getValue());
                startActivity(intent);
                finish();
                break;
//            case R.id.tv_country_code:
//                showCountryPickerDialog();
//                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFrom != null) {
            if (isFrom.equals(AppConstants.IsFrom.BOARDING_ACTIVTY.getValue())) {
                startActivity(new Intent(mContext, OnBoardingHomeActivity.class));
                finish();
            } else if (isFrom.equals(AppConstants.IsFrom.LOGIN_ACTIVTY.getValue())) {
                startActivity(new Intent(mContext, LoginActivity.class));
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
            if(location==null) {
                getLastLocation();
            }
            else {
                populateVerifyCodeModel(verificationCode);
            }
            return null;
        }
    };
}
