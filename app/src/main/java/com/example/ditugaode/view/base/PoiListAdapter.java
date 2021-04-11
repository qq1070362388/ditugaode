package com.example.ditugaode.view.base;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.example.ditugaode.R;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PoiListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<PoiItem> mPoiList;
    private Location mLocation;
    private LatLng mLatLng;
    private TextView search_loc;
    private TextView search_title;

    public PoiListAdapter(Context context,List<PoiItem> list,LatLng latLng){
        mContext=context;
        mPoiList=list;
        mLatLng=latLng;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(mContext).inflate(R.layout.search_tip_recycle,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder=(ViewHolder)holder;
        final PoiItem model=mPoiList.get(position);
        viewHolder.itemName.setText(model.getTitle());
        viewHolder.itemAddress.setText(model.getSnippet());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListner!=null){
                    mListner.ItemOnclik(model);
                }
            }
        });
    }

//    public String meterTokm(double dis) {
//        String back = "";
//        if (dis >= 100) {
//            dis = dis / 1000;
//            String parten = "#.##";
//            DecimalFormat decimal = new DecimalFormat(parten);
//            back = decimal.format(dis) + "km";
//        } else {
//            back = String.format("%dm", dis);
//        }
//        return back;
//    }

    private LatLng getLat(PoiItem item){
        LatLonPoint point=item.getLatLonPoint();
        return new LatLng(point.getLatitude(),point.getLongitude());
    }

    @Override
    public int getItemCount() {
        return mPoiList.size();
    }
    public void setItenmListner(ItemListner listner){
        mListner=listner;
    }

    private ItemListner mListner;
    public interface ItemListner{
        public void ItemOnclik(PoiItem item);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_search_title)
        TextView itemName;
        @BindView(R.id.tv_search_loc)
        TextView itemAddress;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
