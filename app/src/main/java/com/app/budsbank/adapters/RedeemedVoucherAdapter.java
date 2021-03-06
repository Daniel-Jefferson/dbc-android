package com.app.budsbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budsbank.R;
import com.app.budsbank.models.VoucherModel;

import java.util.ArrayList;

public class RedeemedVoucherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<VoucherModel> data;

    public RedeemedVoucherAdapter(Context context, ArrayList<VoucherModel> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lyt_available_voucher, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new RedeemedVoucherAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RedeemedVoucherAdapter.ViewHolder mViewHolder = (RedeemedVoucherAdapter.ViewHolder) holder;
        VoucherModel model = data.get(position);
        mViewHolder.tvName.setText(model.getDispensaryName());
        mViewHolder.tvAddress.setText(model.getDispensaryAddress());
        mViewHolder.tvCoinAmount.setText(model.getReward());

        mViewHolder.tvClaim.setVisibility(View.INVISIBLE);
        mViewHolder.lytExpiryDate.setVisibility(View.GONE);
        mViewHolder.tvClaim.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvClaim, tvExpiryDate, tvCoinAmount;
        View lytExpiryDate;


        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.disp_name);
            tvAddress = itemView.findViewById(R.id.disp_address);
            tvClaim = itemView.findViewById(R.id.tv_claim);
            lytExpiryDate = itemView.findViewById(R.id.ll_expiry_date);
            tvCoinAmount = itemView.findViewById(R.id.tv_coins_amount);

        }
    }
}
