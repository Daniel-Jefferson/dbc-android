package com.app.budbank.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.budbank.R;
import com.app.budbank.activities.BaseActivity;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.web.HeaderIntercepter;
import com.app.budbank.web.WebConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.github.piasy.fresco.draweeview.shaped.ShapedDraweeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import okhttp3.OkHttpClient;

public class BudsBankUtils {

    public static boolean isNetworkAvailable(Context activity) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception ex) {
            notifyException(ex);
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static SpannableStringBuilder formatSpannableString(Context mContext, String parentString, String firstSubString, String secondSubString) {
       if(firstSubString == null)
           return null;
        SpannableStringBuilder sb = new SpannableStringBuilder();
        Typeface font = ResourcesCompat.getFont(mContext, R.font.montserrat_bold);
        if(firstSubString != null && secondSubString != null) {
            String toAppend = String.format(parentString,firstSubString, secondSubString);
            sb.append(toAppend);
            sb.setSpan( new CustomTypeFace("", font),toAppend.indexOf(firstSubString),toAppend.indexOf(firstSubString)+firstSubString.length(),0);
            sb.setSpan(new CustomTypeFace("", font),toAppend.indexOf(secondSubString),toAppend.indexOf(secondSubString)+secondSubString.length(),0);
        } else if (secondSubString == null) {
                String toAppend = String.format(parentString,firstSubString);
                sb.append(toAppend);
                sb.setSpan( new CustomTypeFace("", font),toAppend.indexOf(firstSubString),toAppend.indexOf(firstSubString)+firstSubString.length(),0);
        }
        return sb;
    }

    public static void notifyException(Exception e) {

        if (e != null) {
//            e.printStackTrace();
            Log.e("notifyException" , e.getMessage());
        }
    }

    public static void notifyException(Throwable e) {

        if (e != null) {
            e.printStackTrace();
            Log.e("notifyException" , e.getMessage());
        }
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    public static String genAuthenticationHeader() {

        String uname = WebConfig.BudsBankConfigurations.USERNAME;
        String pass = WebConfig.BudsBankConfigurations.PASSWORD;
        String secret = encodeBase64((uname + ':' + pass).getBytes());
        String authstr = "Basic " + secret;
        authstr = authstr.replace("\r\n", "");
        authstr = authstr.replace("\n", "");

        return authstr;

    }

    private static String encodeBase64(byte[] input) {
        return new String(Base64.encode(input, Base64.DEFAULT));
    }

    public static OkHttpClient getHttpClient() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.sslSocketFactory();
        OkHttpClient.Builder builder = okHttpClient.newBuilder().addInterceptor(new HeaderIntercepter())
                .connectTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(60 * 5, TimeUnit.SECONDS)
                .readTimeout(60 * 5, TimeUnit.SECONDS);
        return builder.build();
    }

    public static boolean checkPlayServices(Context mContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity)mContext, resultCode, AppConstants.RequestCodes.PLAY_SERVICES_RESOLUTION_REQUEST.getCode());
            } else {
                Log.d("errro", "checkPlayServices: Device Not Supported");
            }

            return false;
        }

        return true;
    }

    public static boolean isGpsEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                BudsBankUtils.notifyException(e);
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void showPermissionDeniedError(Context mContext, String message) {
        DialogUtils.showAlertWithButtons(mContext, mContext.getString(R.string.permission_denied), message, mContext.getString(R.string.ok),
                mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogUtils.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        myIntent.setData(uri);
                        mContext.startActivity(myIntent);
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

    public static void showLocationNotEnabledError(Context mContext, String message) {
        DialogUtils.showAlertWithButtons(mContext, mContext.getString(R.string.location_not_enabled), message, mContext.getString(R.string.ok),
                mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogUtils.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(myIntent);
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

    public static boolean isPhoneNumberValid(Context context, String region, String number) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(context);

        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(number, region);
            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (Exception e) {
            notifyException(e);
        }
        return false;

    }

    public static int getAPIVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
    public static void call(Context mContext, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        mContext.startActivity(intent);
    }

    public static void loadImageFromURL(ShapedDraweeView imageView, String url) {
        loadImageFromURL(imageView, url, R.drawable.bg);
    }

    public static void loadImageFromURL(ShapedDraweeView imageView, String url, int placeholder) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(placeholder)
                .showImageForEmptyUri(placeholder)
                .showImageOnFail(placeholder).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        if (!url.contains("no_image")) {
            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(
                    url)).build();
            imageView.setController(controller);
        } else {
            imageLoader.displayImage(url, imageView, options);
        }
    }

    public static void broadcastProfileUpdate(Context context) {
        Intent intent = new Intent(AppConstants.Actions.UPDATE_PROFILE.getValue());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastAction(Context context, String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastNotificationsUpdate(Context context) {
        Intent intent = new Intent(AppConstants.Actions.UPDATE_NOTIFICATIONS.getValue());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static boolean isValidContextForGlide(Context mContext) {
        if (mContext == null) {
            return false;
        }
        if (mContext instanceof Activity) {
            final Activity activity = (Activity) mContext;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void setViewUnderStatusBar(View view, Context mContext) {
        int statusBarHeight = BudsBankUtils.getStatusBarHeight(mContext);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMargins(marginLayoutParams.getMarginStart(), statusBarHeight, marginLayoutParams.getMarginEnd(), 0);
        view.requestLayout();
    }

    public static ArrayList<DispensaryModel> filterAvailableDispensaries(ArrayList<DispensaryModel> dispensaries) {
        ArrayList<DispensaryModel> availableDispensaries = new ArrayList<>();
        for (DispensaryModel dispensary: dispensaries) {
            if(dispensary.isAvailable()) {
                availableDispensaries.add(dispensary);
            }
        }
        return availableDispensaries;
    }
}
