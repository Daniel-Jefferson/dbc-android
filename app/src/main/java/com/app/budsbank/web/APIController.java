package com.app.budsbank.web;

import com.app.budsbank.models.AvailableVoucherResponseModel;
import com.app.budsbank.models.ClaimVoucherResponseModel;
import com.app.budsbank.models.CompletedDispensariesModel;
import com.app.budsbank.models.DeleteNotificationModel;
import com.app.budsbank.models.DispensaryResponseModel;
import com.app.budsbank.models.FCMTokenModel;
import com.app.budsbank.models.ForgetPasswordModel;
import com.app.budsbank.models.GetNotificationResponseModel;
import com.app.budsbank.models.LoginModel;
import com.app.budsbank.models.LoginResponseModel;
import com.app.budsbank.models.MarkReadModel;
import com.app.budsbank.models.NotificationSettingsResponseModel;
import com.app.budsbank.models.QuizResponseModel;
import com.app.budsbank.models.ReadNotificationsResponseModel;
import com.app.budsbank.models.ResponseModel;
import com.app.budsbank.models.SaveQuizResponseModel;
import com.app.budsbank.models.SearchResponseModel;
import com.app.budsbank.models.SettingsModel;
import com.app.budsbank.models.SignupModel;
import com.app.budsbank.models.SignupResponseModel;
import com.app.budsbank.models.UpdateUserModel;
import com.app.budsbank.models.UpdateUserResponseModel;
import com.app.budsbank.models.VerifyCodeModel;
import com.app.budsbank.models.requestModel.ClaimVoucherRequestModel;
import com.app.budsbank.models.requestModel.FollowUnFollowRequestModel;
import com.app.budsbank.models.requestModel.SaveQuizRequestModel;
import com.app.budsbank.utils.BudsBankUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIController {

    private static BudsBankAPI apiInstance;

    private static void init() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebConfig.API_URL)
                    .client(BudsBankUtils.getHttpClient())
                    .build();


            apiInstance = retrofit.create(BudsBankAPI.class);
        } catch (Throwable e) {
            BudsBankUtils.notifyException(e);
        }
    }

    public static BudsBankAPI getApiInstance() {
        if(apiInstance == null) {
            init();
        }
        return apiInstance;
    }

    public static void forgetPassword(ForgetPasswordModel forgetPasswordModel, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> updateRequestStatus = apiInstance.forgetPassword(
                forgetPasswordModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void registerUser(SignupModel signupModel, Callback<SignupResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<SignupResponseModel> updateRequestStatus = apiInstance.registerUser(
                signupModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void loginUser(LoginModel loginModel, boolean showAll, Callback<LoginResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<LoginResponseModel> updateRequestStatus = apiInstance.loginUser(
                loginModel,
                showAll
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void verifyCode(String sessionToken, VerifyCodeModel verifyCodeModel,  Callback<LoginResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<LoginResponseModel> updateRequestStatus = apiInstance.verifyCode(
                sessionToken, verifyCodeModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getHomeContent(String sessionToken, String userId,double longitude, double latitude,int pageSize, boolean showAll, Callback<LoginResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<LoginResponseModel> updateRequestStatus = apiInstance.getHomeContent(
                sessionToken,
                userId,
                longitude,
                latitude,
                pageSize,
                showAll

        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getNearbyDispensaries(String sessionToken, String userId, double latitude, double longitude, int pageSize, int currentPage,boolean showAll , Callback<SearchResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<SearchResponseModel> updateRequestStatus = apiInstance.getNearbyDispensaries(
                sessionToken,
                userId,
                latitude,
                longitude,
                pageSize,
                currentPage,
                showAll
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getNearbyDispensaries(String sessionToken, String userId, double latitude, double longitude, int pageSize, int currentPage, Callback<SearchResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<SearchResponseModel> updateRequestStatus = apiInstance.getNearbyDispensaries(
                sessionToken,
                userId,
                latitude,
                longitude,
                pageSize,
                currentPage
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getCompletedDispensaries(String sessionToken, String userId, int pageSize, int currentPage, Callback<CompletedDispensariesModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<CompletedDispensariesModel> updateRequestStatus = apiInstance.getCompletedDispensaries(
                sessionToken,
                userId,
                pageSize,
                currentPage
        );

        updateRequestStatus.enqueue(callback);
    }
    public static void getRedeemedVouchers(String sessionToken, String userId,int pageSize, int currentPage, Callback<AvailableVoucherResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<AvailableVoucherResponseModel> updateRequestStatus = apiInstance.getRedeemedVouchers(
                sessionToken,
                userId,
                pageSize,
                currentPage
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getAvailableVouchers(String sessionToken, String userId,int pageSize, int currentPage, Callback<AvailableVoucherResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<AvailableVoucherResponseModel> updateRequestStatus = apiInstance.getAvailableVouchers(
                sessionToken,
                userId,
                pageSize,
                currentPage
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void updateUser(String sessionToken, UpdateUserModel updateUserModel, Callback<UpdateUserResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        MultipartBody.Part body = null;
        File file=null;
        String profileImage = updateUserModel.getProfileUrl();
        if(profileImage != null && !profileImage.equals("")) {
            file = new File(profileImage);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        
        Call<UpdateUserResponseModel> updateRequestStatus = apiInstance.updateUser(
                sessionToken,
                RequestBody.create(MultipartBody.FORM, updateUserModel.getUserId()),
                RequestBody.create(MultipartBody.FORM, updateUserModel.getUserName()),
                RequestBody.create(MultipartBody.FORM, updateUserModel.getFirstname()),
                RequestBody.create(MultipartBody.FORM, updateUserModel.getLastName()),
                RequestBody.create(MultipartBody.FORM, updateUserModel.getPhoneNumber()),
                body
        );
        updateRequestStatus.enqueue(callback);
    }

    public static void getQuiz(String sessionToken, String userId, int dispensaryId, Callback<QuizResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<QuizResponseModel> updateRequestStatus = apiInstance.getQuiz(
                sessionToken,
                userId,
                dispensaryId
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void saveQuiz(String sessionToken, SaveQuizRequestModel saveQuizRequestModel, Callback<SaveQuizResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<SaveQuizResponseModel> updateRequestStatus = apiInstance.saveQuiz(
                sessionToken,
                saveQuizRequestModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void claimVoucher(String sessionToken, ClaimVoucherRequestModel claimVoucherRequestModel, Callback<ClaimVoucherResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ClaimVoucherResponseModel> updateRequestStatus = apiInstance.claimVoucher(
                sessionToken,
                claimVoucherRequestModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void search(String sessionToken, String userId, String keyword,int currentPage,int pageSize, Callback<SearchResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<SearchResponseModel> updateRequestStatus = apiInstance.search(
                sessionToken,
                userId,
                keyword,
                currentPage,
                pageSize
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getNotifications(String sessionToken, String userId, Callback<GetNotificationResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<GetNotificationResponseModel> updateRequestStatus = apiInstance.getNotifications(
                sessionToken,
                userId
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getDispensaryById(String sessionToken,String dispensaryId, String userId, Callback<DispensaryResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<DispensaryResponseModel> updateRequestStatus = apiInstance.getDispensaryById(
                sessionToken,
                dispensaryId,
                userId
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getReadNotifications(String sessionToken, String userId, int page,int pageSize, Callback<ReadNotificationsResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ReadNotificationsResponseModel> updateRequestStatus = apiInstance.getReadNotifications(
                sessionToken,
                userId,
                page,
                pageSize
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getUnreadNotifications(String sessionToken, String userId, int page,int pageSize, Callback<ReadNotificationsResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ReadNotificationsResponseModel> updateRequestStatus = apiInstance.getUnreadNotifications(
                sessionToken,
                userId,
                page,
                pageSize
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void markRead(String sessionToken, MarkReadModel markReadModel, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> updateRequestStatus = apiInstance.markRead(
                sessionToken,
                markReadModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void getNotificationSettings(String sessionToken, String userId,int currentPage, int pageSize, Callback<NotificationSettingsResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<NotificationSettingsResponseModel> updateRequestStatus = apiInstance.getNotificationSettings(
                sessionToken,
                userId,
                currentPage,
                pageSize
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void enableNotification(String sessionToken, SettingsModel settingsModel, Callback<NotificationSettingsResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<NotificationSettingsResponseModel> updateRequestStatus = apiInstance.enableNotification(
                sessionToken,
                settingsModel
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void followDispensary(String sessionToken, FollowUnFollowRequestModel model, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> updateRequestStatus = apiInstance.followDispensary(
                sessionToken,
                model
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void unFollowDispensary(String sessionToken, FollowUnFollowRequestModel model, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> updateRequestStatus = apiInstance.unFollowDispensary(
                sessionToken,
                model
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void deleteNotification(String sessionToken, DeleteNotificationModel model, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> updateRequestStatus = apiInstance.deleteNotification(
                sessionToken,
                model
        );

        updateRequestStatus.enqueue(callback);
    }

    public static void updateFCMToken(String sessionToken, FCMTokenModel model, Callback<ResponseModel> callback) {
        if (apiInstance == null)
            apiInstance = getApiInstance();
        Call<ResponseModel> fcmUpdateStatus = apiInstance.updateFCMToken(
                sessionToken,
                model
        );

        fcmUpdateStatus.enqueue(callback);
    }


}
