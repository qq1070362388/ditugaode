package com.example.ditugaode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.route.DriveStep;
import com.example.ditugaode.R;
import com.example.ditugaode.overlay.AMapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrivePathAdapter extends RecyclerView.Adapter {
    private Context mContext;
    List<DriveStep> mItemList = new ArrayList<DriveStep>();

    public DrivePathAdapter(Context context, List<DriveStep> list) {
        mContext = context;
        mItemList.add(new DriveStep());
        mItemList.addAll(list);
        mItemList.add(new DriveStep());
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
        ViewHolder viewHolder=(ViewHolder)holder;

        final DriveStep item = mItemList.get(position);
        if (position == 0) {
            viewHolder.driveDirIcon.setImageResource(R.drawable.qi);
            viewHolder.driveLineName.setText("出发");
            viewHolder.splitLine.setVisibility(View.GONE);
        } else if (position == mItemList.size() - 1) {
            viewHolder.driveDirIcon.setImageResource(R.drawable.end);
            viewHolder.driveLineName.setText("到达终点");
            viewHolder.splitLine.setVisibility(View.VISIBLE);
        } else {
            String actionName = item.getAction();
            int resID = AMapUtil.getDriveActionID(actionName);
            viewHolder.driveDirIcon.setImageResource(resID);
            viewHolder.driveLineName.setText(item.getInstruction());
            viewHolder.splitLine.setVisibility(View.VISIBLE);
        }
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
