package com.app.budbank.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.budbank.R;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.CustomTypeFace;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

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
        String terms = mContext.getString(R.string.terms_of_use);
        String privacy = mContext.getString(R.string.privacy_policy);
        String termsOfUse = getString(R.string.age_confirmation);
        SpannableStringBuilder formatedString = BudsBankUtils.formatSpannableString(mContext, termsOfUse, terms, privacy);
        tvTerms.setText(formatedString, TextView.BufferType.SPANNABLE);

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
