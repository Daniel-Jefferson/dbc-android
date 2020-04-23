package com.app.budsbank.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.budsbank.R;
import com.app.budsbank.models.ForgetPasswordModel;
import com.app.budsbank.models.ResponseModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.web.APIController;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_email)
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        btnConfirm.setOnClickListener(this);
    }

    private void validate() {
        String email = etEmail.getText().toString();
        if(!TextUtils.isEmpty(email) && BudsBankUtils.isValidEmail(email)) {
            sendPassword(email);
        } else if (TextUtils.isEmpty(email)) {
            DialogUtils.showSnackBar(etEmail, getString(R.string.enter_email), Snackbar.LENGTH_LONG,mContext);
        } else if (!BudsBankUtils.isValidEmail(email)) {
            DialogUtils.showSnackBar(etEmail, getString(R.string.enter_valid_email), Snackbar.LENGTH_LONG,mContext);
        }
    }

    private void sendPassword(String email) {
        ForgetPasswordModel forgetPasswordModel = new ForgetPasswordModel(email);
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showSnackBar(etEmail, getString(R.string.no_internet_alert), Snackbar.LENGTH_LONG, mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(mContext);
        APIController.forgetPassword(forgetPasswordModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogUtils.dismiss();
                if (response != null) {
                    if (response.isSuccessful()) {
                        ResponseModel responseModel = response.body();
                        if (responseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                            DialogUtils.showSnackBar(etEmail, getString(R.string.link_sent_to_email), Snackbar.LENGTH_LONG,mContext);
                            btnConfirm.setEnabled(false);
                            btnConfirm.setBackgroundResource(R.drawable.ic_btn_white_selected);
                            new CountDownTimer(30000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    btnConfirm.setEnabled(true);
                                    btnConfirm.setBackgroundResource(R.drawable.all_buttons_selector);
                                }
                            }.start();
                            return;
                        } else {
                            DialogUtils.showSnackBar(etEmail,responseModel.getMessage(), Snackbar.LENGTH_LONG,mContext);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showErrorBasedOnType(mContext, etEmail, t);
                Log.d("error", "onFailure: "+t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_confirm:
                hideKeyboard(this);
                validate();
        }
    }
}
