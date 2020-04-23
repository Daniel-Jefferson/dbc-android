package com.app.budsbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budsbank.R;
import com.app.budsbank.models.DispensaryModel;

import java.util.ArrayList;

public class DisabledCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<DispensaryModel> data;

    public DisabledCollectionAdapter(Context context, ArrayList<DispensaryModel> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_disbaled_dispensary, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new DisabledCollectionAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DisabledCollectionAdapter.ViewHolder mViewHolder = (DisabledCollectionAdapter.ViewHolder) holder;
        DispensaryModel model = data.get(position);
        if(model.isFollowed()) {
            mViewHolder.ivHeart.setVisibility(View.VISIBLE);
        }
        mViewHolder.tvName.setText(model.getName());
        mViewHolder.tvAddress.setText(model.getAddress());
//        mViewHolder.tvRedeem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        mViewHolder.ivArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvReward, tvRedeem;
        ImageView ivArrow,ivHeart;

        ViewHolder(View itemView) {
            super(itemView);

            ivHeart = itemView.findViewById(R.id.iv_heart);
            tvName = itemView.findViewById(R.id.disp_name);
            tvAddress = itemView.findViewById(R.id.disp_address);
            tvRedeem = itemView.findViewById(R.id.tv_dis_redeem);
            ivArrow = itemView.findViewById(R.id.iv_dis_arrow);


        }
    }

}
