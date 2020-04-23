package com.app.budsbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.budsbank.R;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.StorageUtillity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
    }

    private void initViews() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!StorageUtillity.getDataFromPreferences(SplashActivity.this, AppConstants.SharedPreferencesKeys.IS_LOGGED_IN.getValue(), false)) {
                    startActivity(new Intent(SplashActivity.this, AgeGateActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 1000);
    }
}
