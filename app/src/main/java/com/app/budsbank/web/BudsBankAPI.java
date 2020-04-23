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
import com.app.budsbank.models.UpdateUserResponseModel;
import com.app.budsbank.models.VerifyCodeModel;
import com.app.budsbank.models.requestModel.ClaimVoucherRequestModel;
import com.app.budsbank.models.requestModel.FollowUnFollowRequestModel;
import com.app.budsbank.models.requestModel.SaveQuizRequestModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BudsBankAPI {

    @POST("forget-password")
    Call<ResponseModel> forgetPassword(@Body ForgetPasswordModel forgetPasswordModel);

    @POST("register")
    Call<SignupResponseModel> registerUser(@Body SignupModel signupModel);

    @POST("login")
    Call<LoginResponseModel> loginUser(@Body LoginModel loginModel,
                                       @Query("show_all") boolean showAll);

    @POST("api/v1/user/verify-code")
    Call<LoginResponseModel> verifyCode(@Header("x-access-token") String sessionToken,
                                        @Body VerifyCodeModel verifyCodeModel);
    @GET("api/v1/home-content")
    Call<LoginResponseModel> getHomeContent(@Header("x-access-token") String sessionToken,
                                            @Query("user_id") String userId,
                                            @Query("longitude") double longitude,
                                            @Query("latitude") double latitude,
                                            @Query("page_size") int pageSize,
                                            @Query("show_all") boolean showAll);

    @Multipart
    @POST("api/v1/user/update-profile")
    Call<UpdateUserResponseModel> updateUser(@Header("x-access-token") String sessionToken,
                                             @Part("user_id")RequestBody userId,
                                             @Part("username") RequestBody userName,
                                             @Part("first_name") RequestBody firstName,
                                             @Part("last_name") RequestBody lastName,
                                             @Part("phone") RequestBody phoneNumber,
                                             @Part MultipartBody.Part profileImage
    );

    @GET("api/v1/quiz/get-quiz")
    Call<QuizResponseModel> getQuiz(@Header("x-access-token") String sessionToken,
                                    @Query("user_id") String userId,
                                    @Query("dispensary_id") int dispensaryId);

    @POST("api/v1/quiz/save-quiz")
    Call<SaveQuizResponseModel> saveQuiz(@Header("x-access-token") String sessionToken,
                                         @Body SaveQuizRequestModel saveQuizRequestModel);

    @POST("api/v1/voucher/claim-voucher")
    Call<ClaimVoucherResponseModel> claimVoucher(@Header("x-access-token") String sessionToken,
                                                 @Body ClaimVoucherRequestModel claimVoucherRequestModel);


    @GET("api/v1/dispensary/search")
    Call<SearchResponseModel> search(@Header("x-access-token") String sessionToken,
                                     @Query("user_id") String userId,
                                     @Query("keyword") String keyword,
                                     @Query("current_page") int currentPage,
                                     @Query("page_size") int pageSize);

    @GET("api/v1/notification/all")
    Call<GetNotificationResponseModel> getNotifications(@Header("x-access-token") String sessionToken,
                                                        @Query("user_id") String userId);

    @GET("api/v1/dispensary/get-dispensary")
    Call<DispensaryResponseModel> getDispensaryById(@Header("x-access-token") String sessionToken,
                                                    @Query("dispensary_id") String dispensaryId,
                                                    @Query("user_id") String userId);

    @GET("api/v1/dispensaries/nearby-dispensaries")
    Call<SearchResponseModel> getNearbyDispensaries(@Header("x-access-token") String sessionToken,
                                                    @Query("user_id") String userId,
                                                    @Query("latitude") Double latitude,
                                                    @Query("longitude") Double longitude,
                                                    @Query("page_size") int pageSize,
                                                    @Query("current_page") int currentPage,
                                                    @Query("show_all") boolean showAll);

    @GET("api/v1/dispensaries/nearby-dispensaries")
    Call<SearchResponseModel> getNearbyDispensaries(@Header("x-access-token") String sessionToken,
                                                    @Query("user_id") String userId,
                                                    @Query("latitude") Double latitude,
                                                    @Query("longitude") Double longitude,
                                                    @Query("page_size") int pageSize,
                                                    @Query("current_page") int currentPage);

    @GET("api/v1/dispensary/completed-dispensaries")
    Call<CompletedDispensariesModel> getCompletedDispensaries(@Header("x-access-token") String sessionToken,
                                                              @Query("user_id") String userId,
                                                              @Query("page_size") int currentSize,
                                                              @Query("current_page") int currentPage);

    @GET("api/v1/voucher/redeemed-vouchers")
    Call<AvailableVoucherResponseModel> getRedeemedVouchers(@Header("x-access-token") String sessionToken,
                                                            @Query("user_id") String userId,
                                                            @Query("page_size") int pageSize,
                                                            @Query("current_page") int currentPage);

    @GET("api/v1/voucher/available-vouchers")
    Call<AvailableVoucherResponseModel> getAvailableVouchers(@Header("x-access-token") String sessionToken,
                                                             @Query("user_id") String userId,
                                                             @Query("page_size") int pageSize,
                                                             @Query("current_page") int currentPage);

    @GET("api/v1/notification/read-notifications")
    Call<ReadNotificationsResponseModel> getReadNotifications(@Header("x-access-token") String sessionToken,
                                                              @Query("user_id") String userId,
                                                              @Query("current_page") int currentPage,
                                                              @Query("page_size") int pageSize);

    @GET("api/v1/notification/unread-notifications")
    Call<ReadNotificationsResponseModel> getUnreadNotifications(@Header("x-access-token") String sessionToken,
                                                                @Query("user_id") String userId,
                                                                @Query("current_page") int currentPage,
                                                                @Query("page_size") int pageSize);

    @POST("api/v1/notification/mark-read")
    Call<ResponseModel> markRead(@Header("x-access-token") String sessionToken,
                                 @Body MarkReadModel markReadModel);


    @GET("api/v1/notification/settings")
    Call<NotificationSettingsResponseModel> getNotificationSettings(@Header("x-access-token") String sessionToken,
                                                                    @Query("user_id") String userId,
                                                                    @Query("current_page") int currentPage,
                                                                    @Query("page_size") int pageSize);

    @POST("api/v1/notification/enable-disable")
    Call<NotificationSettingsResponseModel> enableNotification(@Header("x-access-token") String sessionToken,
                                                               @Body SettingsModel settingsModel);

    @POST("api/v1/dispensary/follow-dispensary")
    Call<ResponseModel> followDispensary(@Header("x-access-token") String sessionToken,
                                         @Body FollowUnFollowRequestModel followModel);

    @POST("api/v1/dispensary/unfollow-dispensary")
    Call<ResponseModel> unFollowDispensary(@Header("x-access-token") String sessionToken,
                                           @Body FollowUnFollowRequestModel followModel);

    @POST("api/v1/notification/delete")
    Call<ResponseModel> deleteNotification(@Header("x-access-token") String sessionToken,
                                         @Body DeleteNotificationModel deleteNotificationModel);

    @POST("api/v1/fcm/update-token")
    Call<ResponseModel> updateFCMToken(@Header("x-access-token") String sessionToken,
                                       @Body FCMTokenModel fcmTokenModel);


}

