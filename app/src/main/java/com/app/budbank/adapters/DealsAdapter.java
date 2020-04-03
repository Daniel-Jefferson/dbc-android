package com.app.budbank.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budbank.R;
import com.app.budbank.models.FollowedDispensariesModel;
import com.app.budbank.utils.TextUtils;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;

public class DealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<FollowedDispensariesModel> followedDispensaries;

    public DealsAdapter(Context mContext, ArrayList<FollowedDispensariesModel> followedDispensaries) {
        this.mContext = mContext;
        this.followedDispensaries = followedDispensaries;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lyt_deals, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new DealsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        FollowedDispensariesModel model = followedDispensaries.get(position);
        if(!TextUtils.isEmpty(model.getProfileUrl())) {
            Glide.with(mContext)
                    .load(model.getProfileUrl())
                    .placeholder(R.drawable.ic_placeholder_black)
                    .centerCrop()
                    .into(mViewHolder.ivProfile);
        }
        mViewHolder.tvDispTitle.setText(model.getName());
        mViewHolder.expandableDeal.setText(model.getDeal());
        setExpandButtonVisibility(mViewHolder.expandableDeal, mViewHolder.tvExpand);
        mViewHolder.tvExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewHolder.expandableDeal.isExpanded())
                {
                    mViewHolder.expandableDeal.collapse();
                    mViewHolder.tvExpand.setText(R.string.expand);
                }
                else
                {
                    mViewHolder.expandableDeal.expand();
                    mViewHolder.tvExpand.setText(R.string.collapse);
                }
            }
        });
    }

    private void setExpandButtonVisibility(TextView tvDeal, TextView tvExpand) {
        ViewTreeObserver vto = tvDeal.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Layout l = tvDeal.getLayout();
                if ( l != null){
                    int lines = l.getLineCount();
                    if ( lines > 0) {
                        if (l.getEllipsisCount(lines-1) > 0) {
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
        return followedDispensaries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDispTitle, tvExpand;
        ImageView ivProfile;
        ExpandableTextView expandableDeal;


        ViewHolder(View itemView) {
            super(itemView);

            tvDispTitle = itemView.findViewById(R.id.tv_disp_name);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            expandableDeal = itemView.findViewById(R.id.tv_deal);
            tvExpand = itemView.findViewById(R.id.expand_collapse);

        }
    }
}
