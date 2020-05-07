package com.app.budsbank.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.budsbank.R;
import com.app.budsbank.interfaces.Communicator;
import com.app.budsbank.interfaces.PermissionListener;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.DialogUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Communicator {

    protected Context mContext;
    protected boolean isPhoneVerified = false;
    protected Location location;
    protected LinearLayoutManager linearLayoutManager;
    protected LocationRequest locationRequest;
    protected GoogleApiClient googleApiClient;
    private PermissionListener permissionGrantListener;

    @Override
    protected void onCreate(Bundle instance){
        super.onCreate(instance);
        Window w = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         w.setStatusBarColor(Color.TRANSPARENT);
         w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
         w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Fresco.initialize(this);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }

    public ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    protected void requestLocationPermissionsAndFetchLocation() {
        requestForPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onPermissionGranted(boolean granted) {
                if (granted)
                    getLastLocation();
                else
                    showPermissionDeniedError(mContext,getString(R.string.location_alert));
            }
        });
    }

    public void requestLocationPermission(Callable<Void> callable) {

        ArrayList<String> wantedPermissinos = new ArrayList<>();
        wantedPermissinos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        wantedPermissinos.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<String> permissionArray = permissionsToRequest(wantedPermissinos);

        if(permissionArray.size()>0) {
            requestForPermissions(permissionArray.toArray(new String[permissionArray.size()]), new PermissionListener() {
                @Override
                public void onPermissionGranted(boolean granted) {

                    if (granted) {
                        if(callable!=null) {
                            try {
                                callable.call();
                            }  catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        showPermissionDeniedError(mContext,getString(R.string.location_alert));
                    }
                }
            });
        } else {
            if(callable!=null) {
                try {
                    callable.call();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void requestForPermissions(String[] requiredPermissions, PermissionListener permissionListener){

        this.permissionGrantListener= permissionListener;

        if(isPermissionsGranted(requiredPermissions)){

            if(permissionListener !=null){
                permissionListener.onPermissionGranted(true);
            }
        }else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(requiredPermissions, AppConstants.RequestCodes.REQUEST_PERMISSION.getCode());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==AppConstants.RequestCodes.REQUEST_PERMISSION.getCode()){

            if(grantResults.length > 0){

                for(int i=0;i<grantResults.length;i++){

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED){

                        if(permissionGrantListener!=null){
                            permissionGrantListener.onPermissionGranted(false);
                            return;
                        }
                    }

                }
            }else {
                if(permissionGrantListener!=null){
                    permissionGrantListener.onPermissionGranted(false);
                    return;
                }

            }

            if(permissionGrantListener!=null){
                permissionGrantListener.onPermissionGranted(true);
            }
        }
    }


    public boolean isPermissionsGranted(String[] permissionsToCheck){


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){

            if(permissionsToCheck!=null&&permissionsToCheck.length>0){

                for(int i=0;i<permissionsToCheck.length;i++){

                    if(checkSelfPermission(permissionsToCheck[i])==PackageManager.PERMISSION_DENIED){

                        return false;
                    }
                }

                return true;
            }else return false;

        }else return true;
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


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void getLastLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastlocation) {
                        if (lastlocation != null) {
                            location = lastlocation;
                            locationFetched();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    public void showPermissionDeniedError(Context mContext, String message) {
        DialogUtils.showAlertWithButtons(mContext, mContext.getString(R.string.permission_denied), message, mContext.getString(R.string.ok),
                mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogUtils.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        myIntent.setData(uri);
                        startActivityForResult( myIntent, AppConstants.RequestCodes.REQUEST_SYSTEM_SETTINGS.getCode());
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

    }

    @Override
    public void switchTab(int position) {

    }
}

