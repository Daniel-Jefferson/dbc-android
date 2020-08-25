package com.app.budsbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.budsbank.R;
import com.app.budsbank.models.ProductModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private ArrayList<ProductModel> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ProductAdapter(Context context, ArrayList<ProductModel> data) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
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

            view = layoutInflater.inflate(R.layout.lyt_product_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.tvProductName);
            viewHolder.icon = (ImageView) view.findViewById(R.id.product_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtTitle.setText(data.get(position).getProductName());

        if (!data.get(position).getProductImg().equals("")) {
            Glide.with(this.context)
                    .load(data.get(position).getProductImg())
                    .placeholder(R.drawable.user_placeholder)
                    .into(viewHolder.icon);
        }
        return view;
    }

    private static class ViewHolder {
        TextView txtTitle;
        ImageView icon;
    }
}