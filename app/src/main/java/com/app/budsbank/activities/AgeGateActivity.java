package com.app.budsbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.budsbank.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgeGateActivity extends BaseActivity {

    @BindView(R.id.btn_agree)
    Button btnAccept;
    @BindView(R.id.btn_decline)
    Button btnDecline;
    @BindView(R.id.tv_terms)
    TextView tvTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_gate);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        String terms = String.format("<a href=http://budsbank.com/terms-of-use><b>%s</b></a>", mContext.getString(R.string.terms_of_use));
        String privacy = String.format("<a href=http://budsbank.com/privacy-policy><b>%s</b></a>", mContext.getString(R.string.privacy_policy));
        String termsOfUse = getString(R.string.age_confirmation);
        String formatedString = String.format(termsOfUse, terms, privacy);
        tvTerms.setText(Html.fromHtml(formatedString), TextView.BufferType.SPANNABLE);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(AgeGateActivity.this, OnBoardingHomeActivity.class));
               finish();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
