package com.app.budsbank.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budsbank.R;
import com.app.budsbank.activities.QuizDispoActivity;
import com.app.budsbank.models.DispensaryModel;
import com.app.budsbank.utils.AppConstants;
import com.app.budsbank.utils.TextUtils;
import com.app.budsbank.utils.cacheUtils.MainStorageUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;

public class AllDealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<DispensaryModel> allDispensaries;
    private String isFrom;

    public AllDealsAdapter(Context mContext, ArrayList<DispensaryModel> allDispensaries, String isFrom) {
        this.mContext = mContext;
        this.allDispensaries = allDispensaries;
        inflater = LayoutInflater.from(mContext);
        this.isFrom = isFrom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lyt_deals, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new AllDealsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        DispensaryModel model = allDispensaries.get(position);
        if (!TextUtils.isEmpty(model.getProfileUrl())) {
            Glide.with(mContext)
                    .load(model.getProfileUrl())
                    .placeholder(R.drawable.ic_placeholder_black)
                    .centerCrop()
                    .into(mViewHolder.ivProfile);
        }
        mViewHolder.tvDispTitle.setText(model.getName());
        mViewHolder.expandableDeal.setText(model.getDeal());
        setExpandButtonVisibility(mViewHolder.expandableDeal, mViewHolder.tvExpand);
        mViewHolder.rlProfileView.setVisibility(View.GONE);
        mViewHolder.tvExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewHolder.expandableDeal.expand();
                mViewHolder.tvExpand.setText(R.string.collapse);
                mViewHolder.tvExpand.setVisibility(View.GONE);
                mViewHolder.rlProfileView.setVisibility(View.VISIBLE);
            }
        });
        mViewHolder.lytDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.expandableDeal.collapse();
                mViewHolder.tvExpand.setText(R.string.expand);
                mViewHolder.tvExpand.setVisibility(View.VISIBLE);
                mViewHolder.rlProfileView.setVisibility(View.GONE);
            }
        });

        mViewHolder.rlProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, QuizDispoActivity.class);
                intent.putExtra(AppConstants.ISFROM, isFrom);
                intent.putExtra(AppConstants.IntentKeys.DISPENSARY_MODEL.getValue(), model);
                mContext.startActivity(intent);
            }
        });
    }

    private void setExpandButtonVisibility(TextView tvDeal, TextView tvExpand) {
        final ViewTreeObserver vto = tvDeal.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Layout l = tvDeal.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) > 0) {
                            tvExpand.setVisibility(View.VISIBLE);
                            tvDeal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            tvExpand.setVisibility(View.GONE);
                            tvDeal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allDispensaries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDispTitle, tvExpand;
        ImageView ivProfile;
        ExpandableTextView expandableDeal;
        RelativeLayout rlProfileView;
        LinearLayout lytDeal;


        ViewHolder(View itemView) {
            super(itemView);
            lytDeal = itemView.findViewById(R.id.lyt_deal);
            tvDispTitle = itemView.findViewById(R.id.tv_disp_name);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            expandableDeal = itemView.findViewById(R.id.tv_deal);
            tvExpand = itemView.findViewById(R.id.expand_collapse);
            rlProfileView = itemView.findViewById(R.id.rl_profile_view);
        }
    }
}

