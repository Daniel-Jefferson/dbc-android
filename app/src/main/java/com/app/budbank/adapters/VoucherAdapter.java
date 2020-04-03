package com.app.budbank.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budbank.R;
import com.app.budbank.activities.QuizDispoActivity;
import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.VoucherModel;
import com.app.budbank.utils.AppConstants;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<VoucherModel> data;
    private String isFrom;

    public VoucherAdapter(Context context, ArrayList<VoucherModel> data, String isFrom) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.isFrom = isFrom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_dispensary, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new VoucherAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        VoucherModel model = data.get(position);
        mViewHolder.tvName.setText(model.getDispensaryName());
        mViewHolder.tvAddress.setText(model.getDispensaryAddress());
        if(isFrom!=null && !TextUtils.isEmpty(isFrom)) {
            if(isFrom.equals(AppConstants.IsFrom.DISPENSARY_FRAGMENT.getValue())) {
                mViewHolder.tvRedeem.setText(R.string.learn_more);
            } else if(isFrom.equals(AppConstants.IsFrom.QUIZ_FRAGMENT.getValue())) {
                mViewHolder.tvRedeem.setText(R.string.start_quiz);
            } else if(isFrom.equals(AppConstants.IsFrom.HOME_FRAGMENT.getValue())) {
                mViewHolder.tvRedeem.setText(R.string.learn_more);
            }
        }

        mViewHolder.tvRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mViewHolder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvReward, tvRedeem;
        ImageView ivArrow, ivHeart;


        ViewHolder(View itemView) {
            super(itemView);

            ivHeart = itemView.findViewById(R.id.iv_heart);
            tvName = itemView.findViewById(R.id.disp_name);
            tvAddress = itemView.findViewById(R.id.disp_address);
            tvRedeem = itemView.findViewById(R.id.tv_redeem);
            ivArrow = itemView.findViewById(R.id.iv_arrow);

        }
    }
}
