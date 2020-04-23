package com.app.budsbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.budsbank.R;
import com.app.budsbank.activities.NotificationSettingsActivity;
import com.app.budsbank.models.FollowedDispensariesModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NotificationSettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<FollowedDispensariesModel> data;
    private NotificationSettingsActivity.SettingsEnableCallback callback;

    public NotificationSettingsAdapter(Context context, ArrayList<FollowedDispensariesModel> data, NotificationSettingsActivity.SettingsEnableCallback callback) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.callback = callback;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lyt_notification_view, parent, false);
        RecyclerView.ViewHolder holder = null;
        holder = new NotificationSettingsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder) holder;
        FollowedDispensariesModel model = data.get(position);
        mViewHolder.tvName.setText(model.getName());
        if(model.isEnabled()) {
            mViewHolder.checkBox.setChecked(true);
        } else {
            mViewHolder.checkBox.setChecked(false);
        }
        Glide.with(mContext)
                .load(model.getProfileUrl())
                .placeholder(R.drawable.ic_placeholder_black)
                .centerCrop()
                .into(mViewHolder.ivProfile);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivProfile;
        CheckBox checkBox;


        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.disp_name);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                    callback.callback(Integer.toString(data.get(getAdapterPosition()).getDispensaryId()), isEnabled, getAdapterPosition());
                }
            });
        }
    }
}
