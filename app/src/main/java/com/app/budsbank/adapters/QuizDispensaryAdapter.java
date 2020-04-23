package com.app.budsbank.adapters;

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

import com.app.budsbank.R;
import com.app.budsbank.activities.QuizDispoActivity;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.utils.AppConstants;

import java.util.ArrayList;

public class QuizDispensaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<DispensaryModel> data;
    private String isFrom;

    public QuizDispensaryAdapter() {

    }

    public QuizDispensaryAdapter(Context context, ArrayList<DispensaryModel> data, String isFrom) {
        super();
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
        holder = new QuizDispensaryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        DispensaryModel model = data.get(position);
        if (model.isFollowed()) {
            mViewHolder.ivHeart.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.ivHeart.setVisibility(View.GONE);
        }
        mViewHolder.tvName.setText(model.getName());
        mViewHolder.tvAddress.setText(model.getAddress());
        if (isFrom != null && !TextUtils.isEmpty(isFrom)) {
            if (isFrom.equals(AppConstants.IsFrom.DISPENSARY_FRAGMENT.getValue()) || isFrom.equals(AppConstants.IsFrom.SEARCH_ACTIVITY.getValue())
                    || isFrom.equals(AppConstants.IsFrom.HOME_FRAGMENT.getValue())) {
                mViewHolder.tvRedeem.setText(R.string.learn_more);
            } else if (isFrom.equals(AppConstants.IsFrom.QUIZ_FRAGMENT.getValue())) {
                mViewHolder.tvRedeem.setText(R.string.start_quiz);
            }
        }
        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, QuizDispoActivity.class);
                intent.putExtra(AppConstants.ISFROM, isFrom);
                intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), model);
                mContext.startActivity(intent);
            }
        });
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

