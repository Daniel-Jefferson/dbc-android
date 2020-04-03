package com.app.budbank.adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budbank.R;
import com.app.budbank.fragments.AvailableVoucherFragment;
import com.app.budbank.interfaces.AlertDialogListener;
import com.app.budbank.interfaces.ConfirmationDialogListener;
import com.app.budbank.models.ClaimVoucherResponseModel;
import com.app.budbank.models.VoucherModel;
import com.app.budbank.models.requestModel.ClaimVoucherRequestModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.BudsBankUtils;
import com.app.budbank.utils.DialogUtils;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.utils.TextUtils;
import com.app.budbank.utils.cacheUtils.MainStorageUtils;
import com.app.budbank.web.APIController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableVoucherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<VoucherModel> data;
    private String isFrom;
    private AvailableVoucherFragment.ClaimVoucher claimVoucher;

    public AvailableVoucherAdapter(Context context, ArrayList<VoucherModel> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setClaimVoucherCallback(AvailableVoucherFragment.ClaimVoucher claimVoucher) {
        this.claimVoucher = claimVoucher;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lyt_available_voucher, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new AvailableVoucherAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        VoucherModel model = data.get(position);
        mViewHolder.tvName.setText(model.getDispensaryName());
        mViewHolder.tvAddress.setText(model.getDispensaryAddress());
        mViewHolder.tvExpiryDate.setText(getExpiryDate(model.getExpiry()));
        mViewHolder.tvClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewHolder.getAdapterPosition();
                VoucherModel model = data.get(position);
                showConfirmation(model.getReward(), model.getDispensaryName(), model.getVoucherId(), position);
            }
        });
    }

    private String getExpiryDate(String expiry) {
        Calendar mCalendar = Calendar.getInstance();
        try {
            Date date = mCalendar.getTime();
            SimpleDateFormat inSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outSdf = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            inSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = inSdf.parse(expiry);
            return outSdf.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private void showConfirmation(String reward, String dispensaryName, int voucherId, int position) {
        String rewardString = reward + " " +mContext.getString(R.string.coins);
        String parentString = String.format(mContext.getString(R.string.alert_claim_coins), rewardString, dispensaryName);
        SpannableStringBuilder message = BudsBankUtils.formatSpannableString(mContext, parentString, rewardString, null);
        showAlert(message, voucherId, position);
    }

    private void showAlert(SpannableStringBuilder message, final int voucherId, final int position) {
        DialogUtils.showVerificationAlert(mContext, message, new AlertDialogListener() {
            @Override
            public void call(String code) {
                if (claimVoucher != null)
                    claimVoucher.claim(voucherId, code, position);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvClaim, tvExpiryDate;


        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.disp_name);
            tvAddress = itemView.findViewById(R.id.disp_address);
            tvClaim = itemView.findViewById(R.id.tv_claim);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);

        }
    }
}
