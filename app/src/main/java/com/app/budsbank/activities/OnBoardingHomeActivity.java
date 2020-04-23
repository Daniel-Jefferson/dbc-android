package com.app.budsbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.budsbank.R;
import com.app.budsbank.utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnBoardingHomeActivity extends BaseActivity {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_signup)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_home);

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnBoardingHomeActivity.this, LoginActivity.class);
                intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.BOARDING_ACTIVTY.getValue());
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnBoardingHomeActivity.this, SignUpActivity.class);
                intent.putExtra(AppConstants.ISFROM, AppConstants.IsFrom.BOARDING_ACTIVTY.getValue());
                startActivity(intent);
                finish();
            }
        });
    }
}
