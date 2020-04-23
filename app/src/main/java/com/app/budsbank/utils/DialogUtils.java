package com.app.budsbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.app.budsbank.R;
import com.app.budsbank.adapters.BottomSheetAdapter;
import com.app.budsbank.interfaces.AlertDialogListener;
import com.app.budsbank.interfaces.ConfirmationDialogListener;
import com.app.budsbank.models.BottomSheetModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.net.ConnectException;
import java.util.ArrayList;

public class DialogUtils {
    private static KProgressHUD progressDialog;
    private static AlertDialog alertDialog;
    private static AlertDialog confirmatinoDialog;
    private static Dialog loadingDialog;
    private static AnimationDrawable loadingAnimation;
    private static Dialog imageDialog;
    private static Dialog popupDialog;

    public static void showLoading(Context context) {
        if (progressDialog != null)
            progressDialog.dismiss();

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(context.getString(R.string.loading))
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.2f);
        progressDialog.show();
    }

    public static void showLoading(Context context, String message) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.2f);
        progressDialog.show();
    }

    public static void stopLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void showAlertDialog(Context context, String message,ConfirmationDialogListener callable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setCancelable(false);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialog = inflater.inflate(R.layout.lyt_alert_popup, null);
        builder.setView(dialog);
        alertDialog = builder.create();
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        tvMessage.setText(message, TextView.BufferType.SPANNABLE);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                alertDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if(callable!=null) {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        alertDialog.show();
    }

    public static void showConfirmationDialog(Context context, SpannableStringBuilder message, ConfirmationDialogListener callable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setCancelable(false);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialog = inflater.inflate(R.layout.lyt_confirmation_popup, null);
        builder.setView(dialog);
        confirmatinoDialog = builder.create();
        handleConfirmationPopupViews(dialog, confirmatinoDialog,message,  callable);
        confirmatinoDialog.show();
    }

    public static void showConfirmationDialog(Context context, String message, ConfirmationDialogListener callable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setCancelable(false);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialog = inflater.inflate(R.layout.lyt_confirmation_popup, null);
        builder.setView(dialog);
        confirmatinoDialog = builder.create();
        handleConfirmationPopupViews(dialog, confirmatinoDialog,message,  callable);
        confirmatinoDialog.show();
    }


    public static void showVerificationAlert(Context context, String message, AlertDialogListener callable){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setCancelable(false);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialog = inflater.inflate(R.layout.lyt_verification_popup, null);
        builder.setView(dialog);
        alertDialog = builder.create();
        handlePopupViews(context,dialog, alertDialog,message,  callable);
        alertDialog.show();
    }

    public static void showVerificationAlert(Context context, SpannableStringBuilder message, AlertDialogListener callable){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setCancelable(false);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialog = inflater.inflate(R.layout.lyt_verification_popup, null);
        builder.setView(dialog);
        alertDialog = builder.create();
        handlePopupViews(dialog, alertDialog,message,  callable);
        alertDialog.show();
    }

    private static void handleConfirmationPopupViews (View dialog, AlertDialog alert, String message, ConfirmationDialogListener callable) {
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        tvMessage.setText(message, TextView.BufferType.SPANNABLE);
        ivClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                alert.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                if(callable!=null) {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private static void handleConfirmationPopupViews (View dialog, AlertDialog alert, SpannableStringBuilder message, ConfirmationDialogListener callable) {
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        tvMessage.setText(message, TextView.BufferType.SPANNABLE);
        ivClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                alert.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                if(callable!=null) {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private static void handlePopupViews(Context mContext, View dialog, AlertDialog alert, String message, AlertDialogListener callable) {
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        EditText etCode = dialog.findViewById(R.id.et_code);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        tvMessage.setText(message);
        ivClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                alert.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCode.getText().toString())) {
                    etCode.setError(mContext.getString(R.string.enter_code));
                    return;
                }
                //TODO dismiss the alert dialogue before navigating to next screen
                if(callable!=null) {
                    try {
                        callable.call(etCode.getText().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private static void handlePopupViews(View dialog, AlertDialog alert, SpannableStringBuilder message, AlertDialogListener callable) {
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        EditText etCode = dialog.findViewById(R.id.et_code);
        etCode.setHint(R.string.enter_code);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        tvMessage.setText(message, TextView.BufferType.SPANNABLE);
        ivClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                alert.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callable!=null) {
                    try {
                        callable.call(etCode.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public static void showSnackBar(View view, String message, int duration, Context mContext) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String toAppend = message;
        Typeface font = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
        sb.append(message);
        sb.setSpan( new CustomTypeFace("", font),0,toAppend.length(),0);
        Snackbar snackbar = Snackbar.make(view, sb, duration).setAction("Action", null);
        snackbar.show();
    }

    public static void showSnackBar(View view, String message, Context mContext) {
        showSnackBar(view, message, Snackbar.LENGTH_SHORT, mContext);
    }

    public static void showAlertWithButtons(final Context context, String title, String message, String positiveBtn, String negativeBtn,
                                            DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener,
                                            DialogInterface.OnCancelListener cancelListener) {
        try {
            if(loadingDialog != null && loadingDialog.isShowing())
                loadingDialog.dismiss();
            if(alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {}



        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogThemeLight);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(positiveBtn, posListener)
                .setNegativeButton(negativeBtn, negListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showCustomToast(Context context, String msg) {
        //inflate the custom toast
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        // Inflate the Layout
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = (TextView) layout.findViewById(R.id.toast_text);

        // Set the Text to show in TextView
        text.setText(msg);

        Toast toast = new Toast(context.getApplicationContext());

        //Setting up toast position, similar to Snackbar
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showErrorBasedOnType(Context context, Throwable throwable) {
        if(throwable instanceof ConnectException){
            showCustomToast(context, context.getString(R.string.no_internet_alert));
        } else
            showCustomToast(context, context.getString(R.string.something_went_wrong));
    }

    public static void showErrorBasedOnType(Context context, View view, Throwable throwable) {
        if(throwable instanceof ConnectException){
            showSnackBar(view, context.getString(R.string.no_internet_alert), context);
        } else
            showSnackBar(view, context.getString(R.string.something_went_wrong), context);
    }

    public static void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (imageDialog != null) {
            imageDialog.dismiss();
        }
        if (confirmatinoDialog !=null && confirmatinoDialog.isShowing()) {
            confirmatinoDialog.dismiss();
        }
    }

    public static void showPhotoDialog(final Context mContext, final String url) {
        imageDialog = new Dialog(mContext, R.style.FullScreen_Dialog_Theme);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.dialog_photo);
        imageDialog.setCancelable(true);
        imageDialog.setCanceledOnTouchOutside(true);
        final ImageView mParentView             = imageDialog.findViewById(R.id.photo_msg);
        final ProgressBar mProgressBar          = imageDialog.findViewById(R.id.progressBar);
        ImageView close                         = imageDialog.findViewById(R.id.icon_dialog_close);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions messageOption = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_placeholder)
                .showImageOnFail(R.drawable.user_placeholder).cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.user_placeholder)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

            imageLoader.displayImage(url, mParentView, messageOption, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mParentView.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mProgressBar.setVisibility(View.GONE);
                    mParentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mProgressBar.setVisibility(View.GONE);
                    mParentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    mProgressBar.setVisibility(View.GONE);
                    mParentView.setVisibility(View.VISIBLE);
                }
            });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if(imageDialog.getWindow() != null) {
            lp.copyFrom(imageDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            imageDialog.getWindow().setAttributes(lp);
        }
        imageDialog.show();
    }

    public static void showBottomSheetDialog(Context context, String title, final ArrayList<BottomSheetModel> data, final AlertDialogListener callbackListener) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.lyt_bottom_sheet, null);
        ListView recyclerView = (ListView) view.findViewById(R.id.list_view);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        BottomSheetAdapter adapter = new BottomSheetAdapter(context, data);
        recyclerView.setAdapter(adapter);

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        tvTitle.setText(title);
        dialog.show();
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                BottomSheetModel model = data.get(position);
                callbackListener.call(model.getData());
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                callbackListener.call(AppConstants.CANCEL);
            }
        });
    }

    public static void dismissLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
