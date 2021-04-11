package com.example.ditugaode.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ditugaode.R;
/**
 * poi详情View，该View固定在地图底部
 */
public class PoiDetailBottom extends ConstraintLayout implements View.OnClickListener{

    private TextView mTvPoiDetail;
    private TextView mTvRoute;
    private TextView call;
    private OnPoiDetailBottomClickListener mListener;
    private int poiDetailState;//显示状态
    private ImageView mTVPoiDetailImg;
    //查看详情
    public static final int STATE_DETAIL = 0;
    //显示地图
    public static final int STATE_MAP = 1;

    public PoiDetailBottom(Context context) {
        this(context,null);
    }

    public PoiDetailBottom(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PoiDetailBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.poi_fixl_bottom,this,true);
        mTvPoiDetail = findViewById(R.id.left);
        mTVPoiDetailImg = findViewById(R.id.tv_detail_img);
        mTvPoiDetail.setOnClickListener(this);
        mTvRoute = findViewById(R.id.tv_route);
        call = findViewById(R.id.tv_call_taxi);
//        Drawable drawable = getResources().getDrawable(R.drawable.poi_indicator_route);
//        Drawable drawable2 = getResources().getDrawable(R.drawable.poi_indicator_call_taxi);
//            drawable.setBounds(0,0,60,60);
//        mTvRoute.setCompoundDrawables(drawable,null,null,null);
//        drawable2.setBounds(0,0,60,60);
//        call.setCompoundDrawables(drawable2,null,null,null);
    }

    @Override
    public void onClick(View v) {
        if(null != mListener){
            if(v == mTvPoiDetail){
                mListener.onDetailClick();
                Log.d("show","进行了点击");
            }
        }
    }
    public void setOnPoiDetailBottomClickListener(OnPoiDetailBottomClickListener listener) {
        this.mListener = listener;
    }
    /**
     * PoiDetailBottomView点击监听
     */
    public interface OnPoiDetailBottomClickListener {
        /**
         * 点击查看详情
         */
        void onDetailClick();


    }

    /**
     * 显示查看详情状态
     */
    private void showPoiDetailState(){
        if(null == mTvPoiDetail){
            return;
        }
        mTVPoiDetailImg.setImageDrawable(getResources().getDrawable(R.drawable.poi_indicator_details,null));
        mTvPoiDetail.setText("查看详情");
    }

    /**
     * 显示返回地图状态
     */
    private void showBackMapState() {
        if (null == mTvPoiDetail) {
            return;
        }
        mTVPoiDetailImg.setImageDrawable(getResources().getDrawable(R.drawable.poi_indicator_map_selector,null));
        mTvPoiDetail.setText("显示地图");
    }
    /**
     * 设置poi detail显示状态
     *
     * @param state @see {@link #STATE_DETAIL}{@link #STATE_MAP}
     *
     */
    public void setPoiDetailState(int state) {
        this.poiDetailState = state;
        switch (state) {
            case STATE_DETAIL:
                showPoiDetailState();
                break;
            case STATE_MAP:
                showBackMapState();
                break;
        }
    }

    /**
     * 返回poi detail显示状态
     *
     * @return
     */
    public int getPoiDetailState() {
        return poiDetailState;
    }
}
