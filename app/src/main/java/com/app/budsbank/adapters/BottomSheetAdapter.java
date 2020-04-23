package com.app.budsbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.models.BottomSheetModel;

import java.util.ArrayList;

public class BottomSheetAdapter extends BaseAdapter {
    private ArrayList<BottomSheetModel> data;
    private LayoutInflater layoutInflater;

    public BottomSheetAdapter(Context context, ArrayList<BottomSheetModel> data) {
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.lyt_bottom_sheet_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.tvBottomSheet);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon_bottom_sheet);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int resource = data.get(position).getResourceId();
        if (resource == 0)
            viewHolder.icon.setVisibility(View.GONE);
        else {
            viewHolder.icon.setImageResource(resource);
            viewHolder.icon.setVisibility(View.VISIBLE);
        }

        viewHolder.txtTitle.setText(data.get(position).getData());

        return view;
    }

    private static class ViewHolder {
        TextView txtTitle;
        ImageView icon;
    }
}

