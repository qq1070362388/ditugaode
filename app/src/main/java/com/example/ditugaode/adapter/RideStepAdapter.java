package com.example.ditugaode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.route.RideStep;
import com.example.ditugaode.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RideStepAdapter extends RecyclerView.Adapter {
    private Context mContext;
    List<RideStep> mItemList = new ArrayList<RideStep>();

    public RideStepAdapter(Context context, List<RideStep> list) {
        mContext = context;
        mItemList.add(new RideStep());
        mItemList.addAll(list);
        mItemList.add(new RideStep());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(mContext).inflate(R.layout.item_bus_segment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.bus_line_name)
        TextView driveLineName;
        @BindView(R.id.bus_dir_icon)
        ImageView driveDirIcon;
        @BindView(R.id.bus_seg_split_line)
        ImageView splitLine;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
