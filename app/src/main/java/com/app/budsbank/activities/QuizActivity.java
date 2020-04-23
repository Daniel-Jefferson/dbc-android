package com.app.budsbank.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.interfaces.ConfirmationDialogListener;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.models.QuizOptionModel;
import com.app.budsbank.models.QuizQuestionsModel;
import com.app.budsbank.models.SaveQuizResponseModel;
import com.app.budsbank.models.requestModel.SaveQuizRequestModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.BudsBankUtils;
import com.app.budsbank.utils.DialogUtils;
import com.app.budsbank.utils.StorageUtillity;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.app.budsbank.web.APIController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends BaseActivity {
    @BindView(R.id.parent_view)
    View parentView;
    @BindView(R.id.rl_option_one)
    RelativeLayout optionOne;
    @BindView(R.id.rl_option_two)
    RelativeLayout optionTwo;
    @BindView(R.id.rl_option_three)
    RelativeLayout optionThree;
    @BindView(R.id.rl_option_four)
    RelativeLayout optionFour;
    @BindView(R.id.tv_option_one)
    TextView tvOptionOne;
    @BindView(R.id.tv_option_two)
    TextView tvOptionTwo;
    @BindView(R.id.tv_option_three)
    TextView tvOptionThree;
    @BindView(R.id.tv_option_four)
    TextView tvOptionFour;
    @BindView(R.id.iv_brain)
    ImageView ivBrain;
    @BindView(R.id.iv_progbar)
    ImageView ivProgBar;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.tv_qns_no)
    TextView tvQuestionNumber;
    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.icon_option1)
    ImageView iconOption1;
    @BindView(R.id.icon_option2)
    ImageView iconOption2;
    @BindView(R.id.icon_option3)
    ImageView iconOption3;
    @BindView(R.id.icon_option4)
    ImageView iconOption4;
    private CountDownTimer mCountDownTimer;
    ArrayList<QuizQuestionsModel> questionsModels;
    private int currentQuestion = 0;
    private String isSuccess = "true";
    private DispensaryModel dispensaryModel;
    private boolean isActivityRunning = true;
    private boolean quitQuiz = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mContext = this;

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        BudsBankUtils.setViewUnderStatusBar(parentView, mContext);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            questionsModels = (ArrayList<QuizQuestionsModel>) bundle.getSerializable(AppConstants.IntentKeys.QUIZ_QUESTIONS.getValue());
            dispensaryModel = (DispensaryModel) bundle.getSerializable(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue());
        }

        optionOne.setOnClickListener(this);
        optionTwo.setOnClickListener(this);
        optionThree.setOnClickListener(this);
        optionFour.setOnClickListener(this);
        ivCross.setOnClickListener(this);

        populateQuestion();
    }

    private void populateQuestion() {
        if (currentQuestion >= questionsModels.size() || !isActivityRunning) {
            saveQuizDetailsToServer();
            return;
        }
        int questionNo = currentQuestion + 1;
        tvQuestionNumber.setText(String.format(getString(R.string.question_number), questionNo));
        setProgBar(questionNo);
        QuizQuestionsModel quizQuestionsModel = questionsModels.get(currentQuestion);
        ArrayList<QuizOptionModel> options = quizQuestionsModel.getOptions();
        tvQuestion.setText(quizQuestionsModel.getQuestion());
        if (options != null && options.size() >= 4) {
            setOption(tvOptionOne, options.get(0));
            setOption(tvOptionTwo, options.get(1));
            setOption(tvOptionThree, options.get(2));
            setOption(tvOptionFour, options.get(3));
        }
        setAnimationOnQuestion();
    }

    private void setProgBar(int questionNo) {
        switch (questionNo) {
            case 1:
                ivProgBar.setBackgroundResource(R.drawable.progbar_full);
                break;
            case 2:
                ivProgBar.setBackgroundResource(R.drawable.progbar_three_quarter);
                break;
            case 3:
                ivProgBar.setBackgroundResource(R.drawable.progbar_half);
                break;
            case 4:
                ivProgBar.setBackgroundResource(R.drawable.progbar_quarter);
                break;
            case 5:
                ivProgBar.setBackgroundResource(R.drawable.progbar_empty);
                break;
        }
    }

    private void setAnimationOnQuestion() {
        Animation fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvQuestion.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setAnimationOnOptions();
            }
        });
        if(tvQuestion != null)
            tvQuestion.startAnimation(fadeIn);

    }
    private void setAnimationOnOptions() {
        if(tvOptionOne == null || tvOptionTwo == null || tvOptionThree == null || tvOptionFour == null)
            return;

        setOptionsEnabled(false);
        setAndStartAnimationOnOptions(1);
    }

    private void setAndStartAnimationOnOptions(final int index){
        View optionContainer = null;
        switch (index){
            case 1:
                optionContainer = optionOne;
                break;
            case 2:
                optionContainer = optionTwo;
                break;
            case 3:
                optionContainer = optionThree;
                break;
            case 4:
                optionContainer = optionFour;
                break;
            default:
                setOptionsEnabled(true);
                startTimer();
                return;
        }

        final View finalOptionParent = optionContainer;
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoomIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                finalOptionParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setAndStartAnimationOnOptions(index + 1);
            }
        });

        if(finalOptionParent != null)
            finalOptionParent.startAnimation(zoomIn);
    }

    private void setOptionsEnabled(boolean isEnabled){
        optionOne.setEnabled(isEnabled);
        optionTwo.setEnabled(isEnabled);
        optionThree.setEnabled(isEnabled);
        optionFour.setEnabled(isEnabled);
    }

    private void setOption(TextView option, QuizOptionModel optionModel) {
        option.setText(optionModel.getOptionValue());
        option.setTag(optionModel.isAnswer());
    }

    private void startTimer() {
        ((AnimationDrawable)ivBrain.getBackground()).start();
        initTimer();
    }

    private void initTimer() {
        mCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                isSuccess = "false";
                saveQuizDetailsToServer();
            }
        };
        mCountDownTimer.start();
    }

    private void resetScreen() {
        mCountDownTimer.cancel();
        ((AnimationDrawable)ivBrain.getBackground()).setVisible(true, true);

        iconOption1.setVisibility(View.GONE);
        iconOption2.setVisibility(View.GONE);
        iconOption3.setVisibility(View.GONE);
        iconOption4.setVisibility(View.GONE);

        if(tvQuestion != null) {
            tvQuestion.clearAnimation();
            if (tvQuestion.getAnimation() != null)
                tvQuestion.getAnimation().reset();
            tvQuestion.setVisibility(View.INVISIBLE);
        }
        if(optionOne != null && optionOne.getAnimation() != null) {
            optionOne.getAnimation().reset();
            optionOne.clearAnimation();
        }
        if(optionTwo != null && optionTwo.getAnimation() != null) {
            optionTwo.getAnimation().reset();
            optionTwo.clearAnimation();
        }
        if(optionThree != null && optionThree.getAnimation() != null) {
            optionThree.getAnimation().reset();
            optionThree.clearAnimation();
        }
        if(optionFour != null && optionFour.getAnimation() != null) {
            optionFour.getAnimation().reset();
            optionFour.clearAnimation();
        }
        if(optionOne != null) {
            optionOne.setVisibility(View.INVISIBLE);
        }
        if(optionTwo != null) {
            optionTwo.setVisibility(View.INVISIBLE);
        }
        if(optionThree != null) {
            optionThree.setVisibility(View.INVISIBLE);
        }
        if(optionFour != null) {
            optionFour.setVisibility(View.INVISIBLE);
        }
        currentQuestion++;
        populateQuestion();
    }

    private void saveQuizDetailsToServer() {
        DialogUtils.showLoading(this, getString(R.string.saving_details));
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        String sessionToken = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(this, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        SaveQuizRequestModel saveQuizRequestModel = new SaveQuizRequestModel(userId, dispensaryModel.getId(), isSuccess);
        APIController.saveQuiz(sessionToken, saveQuizRequestModel, new Callback<SaveQuizResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<SaveQuizResponseModel> call, @NonNull Response<SaveQuizResponseModel> response) {
                DialogUtils.dismiss();
                if (!response.isSuccessful()) {
                    DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizActivity.this);
                    return;
                }
                SaveQuizResponseModel saveQuiz = response.body();
                if (saveQuiz != null) {
                    isActivityRunning = false;
                    if(quitQuiz) {
                        MainStorageUtils mainStorageUtils = MainStorageUtils.getInstance();
                        if (response.body().getRemainCount() == 0) {
                            mainStorageUtils.addCompletedDispensary(dispensaryModel);
                        }
                        BudsBankUtils.broadcastAction(mContext, AppConstants.Actions.COMPLETED_DISPENSARY.getValue());
                        finish();
                    } else {
                        Intent intent = new Intent(QuizActivity.this, GameSummaryActivity.class);
                        intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), dispensaryModel);
                        intent.putExtra(AppConstants.IntentKeys.VOUCHER_MODEL.getValue(), saveQuiz.getVoucher());
                        intent.putExtra(AppConstants.IntentKeys.CORRECT_ANSWERS_COUNT.getValue(), currentQuestion);
                        intent.putExtra(AppConstants.IntentKeys.USER_DISABLED_DISPENSARIES.getValue(), response.body().getRemainCount());
                        startActivity(intent);
                        finish();
                    }
                    return;
                }
                DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<SaveQuizResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showSnackBar(parentView, getString(R.string.call_fail_error), QuizActivity.this);
            }
        });
    }

    private void checkAnswer(TextView textView, ImageView optionIcon, View layout) {
        ((AnimationDrawable)ivBrain.getBackground()).stop();

        boolean isAnswer = textView.getTag() != null && (boolean) textView.getTag();
        optionIcon.setVisibility(View.VISIBLE);
        layout.setBackgroundResource(R.drawable.options_correct_bg);

        if(isAnswer) {
            layout.setBackgroundResource(R.drawable.options_correct_bg);
            optionIcon.setImageResource(R.drawable.ic_tick);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setBackgroundResource(R.drawable.options_bg_selecter);
                    resetScreen();
                }
            }, 1000);
        } else {
            layout.setBackgroundResource(R.drawable.options_wrong_bg);
            optionIcon.setImageResource(R.drawable.cross);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSuccess = "false";
                    saveQuizDetailsToServer();
                }
            }, 500);
        }
    }

    private void showAlertMessage() {
        DialogUtils.showAlertDialog(mContext, getString(R.string.cancel_quiz_alert), new ConfirmationDialogListener() {
            @Override
            public void call() {
                quitQuiz = true;
                isSuccess="false";
                saveQuizDetailsToServer();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_option_one:
                setOptionsEnabled(false);
                checkAnswer(tvOptionOne, iconOption1, view);
                break;
            case R.id.rl_option_two:
                setOptionsEnabled(false);
                checkAnswer(tvOptionTwo, iconOption2, view);
                break;
            case R.id.rl_option_three:
                setOptionsEnabled(false);
                checkAnswer(tvOptionThree, iconOption3, view);
                break;
            case R.id.rl_option_four:
                setOptionsEnabled(false);
                checkAnswer(tvOptionFour, iconOption4, view);
                break;
            case R.id.iv_cross:
                showAlertMessage();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        showAlertMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        mCountDownTimer = null;
    }
}
