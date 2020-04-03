package com.app.budbank.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budbank.R;
import com.app.budbank.activities.InboxActivity;
import com.app.budbank.activities.QuizDispoActivity;
import com.app.budbank.fragments.ReadNotificationFragment;
import com.app.budbank.models.DeleteNotificationModel;
import com.app.budbank.models.MarkReadModel;
import com.app.budbank.models.NotificationModel;
import com.app.budbank.models.ResponseModel;
import com.app.budbank.models.SettingsModel;
import com.app.budbank.models.VoucherModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.web.APIController;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<NotificationModel> data;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private String isFrom;
    private InboxActivity.NotificationCallback callback;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> data, String isFrom, InboxActivity.NotificationCallback callback) {
        this.mContext = context;
        this.callback = callback;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.isFrom = isFrom;
        viewBinderHelper.setOpenOnlyOne(true);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.lyt_notification, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new NotificationAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder)holder;
        NotificationModel model = data.get(position);
        int notificationId = model.getNotificationId();
        viewBinderHelper.bind(mViewHolder.lytSwipe, Integer.toString(notificationId));
        mViewHolder.tvName.setText(model.getName());
        mViewHolder.tvTitle.setText(model.getTitle());
        mViewHolder.tvDescription.setText(model.getDescription());
        Glide.with(mContext)
                .load(model.getProfileUrl())
                .placeholder(R.drawable.ic_placeholder_black)
                .centerCrop()
                .into(mViewHolder.ivProfile);
        mViewHolder.lytNotificationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        callback.callback(model, position);
            }
        });
        mViewHolder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification(model, mViewHolder.getPosition());
            }
        });
    }

    private void deleteNotification(NotificationModel model, int position) {
        String sessionToken = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.SESSION_TOKEN.getValue(), "");
        String userId       = StorageUtillity.getDataFromPreferences(mContext, AppConstants.SharedPreferencesKeys.USER_ID.getValue(), "");
        DeleteNotificationModel deleteNotificationModel = new DeleteNotificationModel();
        deleteNotificationModel.setNotificationId(model.getNotificationId());
        deleteNotificationModel.setUserId(userId);
        if (!BudsBankUtils.isNetworkAvailable(mContext)) {
            DialogUtils.showCustomToast(mContext, mContext.getString(R.string.no_internet_alert));
            DialogUtils.stopLoading();
            return;
        }
        DialogUtils.showLoading(mContext);
        APIController.deleteNotification(sessionToken, deleteNotificationModel , new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                DialogUtils.stopLoading();
                if (!response.isSuccessful()) {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                    return;
                }
                ResponseModel responseModel = response.body();
                if (responseModel != null && responseModel.getStatus() == AppConstants.StatusCodes.SUCCESS.getValue()) {
                    DialogUtils.showCustomToast(mContext, responseModel.getMessage());
                    data.remove(model);
                    notifyItemRemoved(position);
                    sendBroadCast(model);
                } else {
                    DialogUtils.showCustomToast(mContext, mContext.getString(R.string.call_fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                DialogUtils.dismiss();
                DialogUtils.showErrorBasedOnType(mContext, t);
            }
        });
    }

    private void sendBroadCast(NotificationModel model) {
        Intent intent = null;
        if(isFrom.equals(AppConstants.IsFrom.UNREAD_NOTIFICATION_FRAGMENT.getValue())) {
            intent = new Intent(AppConstants.Actions.UNREAD_DELETE_NOTIFICATION.getValue());
        } else {
            intent = new Intent(AppConstants.Actions.READ_DELETE_NOTIFICATION.getValue());
        }
        intent.putExtra(AppConstants.IntentKeys.NOTIFICATION_MODEL.getValue(), model);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTitle, tvDescription;
        ImageView ivProfile;
        View  deleteContainer;
        SwipeRevealLayout lytSwipe;
        LinearLayout lytNotificationContainer;


        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_disp_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            lytSwipe = itemView.findViewById(R.id.lyt_swipe_reveal);
            deleteContainer = itemView.findViewById(R.id.delete_container);
            lytNotificationContainer = itemView.findViewById(R.id.ll_notification_container);

        }
    }

}
