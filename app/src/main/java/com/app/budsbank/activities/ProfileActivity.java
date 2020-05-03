package com.app.budsbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.interfaces.AlertDialogListener;
import com.app.budsbank.interfaces.PermissionListener;
import com.app.budsbank.models.BottomSheetModel;
import com.app.budsbank.models.UpdateUserModel;
import com.app.budsbank.models.UpdateUserResponseModel;
import com.app.budsbank.models.UserModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.MediaUtillity;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.web.APIController;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_phone_number)
    EditText etPhoneNumber;
    @BindView(R.id.et_full_name)
    EditText etFullname;
    @BindView(R.id.et_username)
    EditText etUserName;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.iv_user_profile)
    RoundedImageView ivUserProfile;
    private String profileUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        populateUserData();
        ivUserProfile.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        setTextWatchers();
    }

    private void setTextWatchers() {
        final List<String> affineFormats = new ArrayList<>();
        affineFormats.add("[000]-[000]-[0000]");

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

    private void populateUserData() {
        UserModel user = StorageUtillity.getUserModel(mContext);
        String profileUrl = user.getProfileUrl();
        if(profileUrl!= null && !TextUtils.isEmpty(profileUrl)) {
            Glide.with(mContext)
                    .load(profileUrl)
                    .placeholder(R.drawable.user_placeholder)
                    .into(ivUserProfile);
        } else
            ivUserProfile.setImageResource(R.drawable.user_placeholder);
        String phoneNumber = user.getPhoneNumber();
        String number = phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        etPhoneNumber.setText(number);
        etPhoneNumber.setSelection(etPhoneNumber.getText().length());
        etFullname.setText(user.getFullName());
        etFullname.setSelection(etFullname.getText().length());
        etUserName.setText(user.getUsername());
        etUserName.setSelection(etUserName.getText().length());
        tvUserName.setText(user.getUsername());
    }

    private void updateUserProfile(String sessionToken, UpdateUserModel updateUserModel) {
        if (!BudsBankUtils.isNetworkAvailable(this)) {
            DialogUtils.showSnackBar(etFullname, getString(R.string.no_internet_alert), mContext);
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(mContext);
        APIController.updateUser( sessionToken, updateUserModel, new Callback<UpdateUserResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<UpdateUserResponseModel> call, @NonNull final Response<UpdateUserResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(etFullname, getString(R.string.call_fail_error), mContext);
                    return;
                }
                UpdateUserResponseModel updateUserResponseModel = response.body();
                if (updateUserResponseModel != null) {
                    if (updateUserResponseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                        StorageUtillity.saveUserModel(mContext, updateUserResponseModel.getUser());
                        populateUserData();
                        DialogUtils.showSnackBar(etFullname, updateUserResponseModel.getMessage(), Snackbar.LENGTH_LONG,mContext);
                        BudsBankUtils.broadcastProfileUpdate(mContext);
                    } else {
                        DialogUtils.showSnackBar(etFullname, updateUserResponseModel.getMessage(), Snackbar.LENGTH_LONG,mContext);
                    }
                    return;
                }
                DialogUtils.showSnackBar(etFullname, getString(R.string.call_fail_error), mContext);
            }
            @Override
            public void onFailure(@NonNull Call<UpdateUserResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                populateUserData();
                DialogUtils.showErrorBasedOnType(mContext, etFullname, t);
                Log.d("error", "onFailure:" + t.getMessage());
            }
        });
    }

    private void update() {
        if(validate()) {
            String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(),"");
            UpdateUserModel updateUserModel = new UpdateUserModel();
            updateUserModel.setUserId(StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), ""));
            updateUserModel.setUserName(etUserName.getText().toString());
            updateUserModel.setFullName(etFullname.getText().toString());
            String phoneNumber = etPhoneNumber.getText().toString();
            phoneNumber = phoneNumber.replaceAll("-", "").trim();
            updateUserModel.setPhoneNumber(phoneNumber);
            updateUserModel.setProfileUrl(profileUrl);
            updateUserProfile(sessionToken, updateUserModel);
        }
    }

    private boolean validate() {
        UserModel userModel = StorageUtillity.getUserModel(mContext);
        String phonenumber = etPhoneNumber.getText().toString().replaceAll("-", "");
        if(TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(etFullname.getText().toString()) || TextUtils.isEmpty(etUserName.getText().toString())) {
            DialogUtils.showSnackBar(etFullname, getString(R.string.fill_all_fields), Snackbar.LENGTH_LONG,mContext);
            return false;
        } else if (phonenumber.length()!=10){
            DialogUtils.showSnackBar(etFullname, getString(R.string.enter_valid_phone), Snackbar.LENGTH_LONG,mContext);
            return false;
        }
        if (!etFullname.getText().toString().equals(userModel.getFullName())
                || !etUserName.getText().toString().equals(userModel.getUsername()) || !phonenumber.equals(userModel.getPhoneNumber())
                || (profileUrl!=null && !profileUrl.equals(userModel.getProfileUrl()))) {
            return true;
        } else {
            DialogUtils.showSnackBar(etFullname, getString(R.string.no_value_changed), Snackbar.LENGTH_LONG,mContext);
            return false;
        }
    }

    private void showBottomSheetDialog() {
        final ArrayList<BottomSheetModel> data = new ArrayList<>();
        data.add(new BottomSheetModel(getString(R.string.from_camera), R.drawable.ic_camera));
        data.add(new BottomSheetModel(getString(R.string.from_gallery), R.drawable.ic_gallery));
        DialogUtils.showBottomSheetDialog(mContext, "", data, new AlertDialogListener() {
            @Override
            public void call(String result) {
                BottomSheetModel model = data.get(0);
                if (result.equals(model.getData())) {
                    MediaUtillity.startCameraIntent((AppCompatActivity) mContext);
                } else if(result.equals(getString(R.string.from_gallery))){
                    MediaUtillity.startGalleryByIntent(mContext);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.RequestCodes.REQUEST_GALLERY.getCode()) {
            if (data == null)
                return;
            Uri selectedImage = data.getData();
            String picturePath = MediaUtillity.getPath(this,selectedImage);
            if(picturePath != null && !picturePath.equals("")) {
                profileUrl = picturePath;
                Glide.with(mContext)
                        .load(profileUrl)
                        .into(ivUserProfile);
            }
        } else if (requestCode == AppConstants.RequestCodes.REQUEST_CAMERA.getCode()) {
            Uri imageUri = Uri.fromFile(new File(MediaUtillity.getCurrentPhotoPath()));

            if(imageUri == null) {
                DialogUtils.showSnackBar(ivUserProfile, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG, mContext);
                return;
            }
            Bitmap bitmap = null;
            try {
                bitmap = MediaUtillity.handleSamplingAndRotationBitmap(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap == null)
                return;



            Uri uri = MediaUtillity.getImageUri(this, bitmap);
            profileUrl = MediaUtillity.getPath(this, uri);
            Glide.with(mContext)
                    .load(profileUrl)
                    .into(ivUserProfile);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                hideKeyboard(this);
                finish();
                break;
            case R.id.iv_user_profile:
                requestForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void onPermissionGranted(boolean granted) {
                        if(granted)
                            showBottomSheetDialog();
                        else
                            BudsBankUtils.showPermissionDeniedError(mContext,getString(R.string.required_permissions_denied_error));
                    }
                });
                break;
            case R.id.btn_update:
                hideKeyboard(this);
                update();
                break;
        }
    }
}
