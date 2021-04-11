package com.example.ditugaode.view.map;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ditugaode.R;

public class MapHeaderView extends ConstraintLayout implements View.OnClickListener {
    private EditText editText;
    private ImageView user;
    private OnMapHeaderViewClickListener onMapHeaderViewClickListener;

    public MapHeaderView(Context context) {
        this(context, null);
    }

    public MapHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_map_header,this,true);

        initView();
        setListener();
    }

    public void initView(){
        editText = findViewById(R.id.tv_search);
        user = findViewById(R.id.imageView);
    }

    public void setListener(){
        View child = this.getChildAt(0);
        if(child instanceof ViewGroup){
            int count = ((ViewGroup)child).getChildCount();
            for(int i = 0; i < count; i++){
                ((ViewGroup) child).getChildAt(i).setOnClickListener(this);
            }
        }
    }

    public void setOnMapHeaderViewClickListener(OnMapHeaderViewClickListener listener) {
        if (listener != null) {
            this.onMapHeaderViewClickListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if(onMapHeaderViewClickListener == null){
            return;
        }
        if (v == user){
            onMapHeaderViewClickListener.onUserClick(v);
        }else if(v == editText){
            onMapHeaderViewClickListener.onSearchClick();
        }
    }

    public interface OnMapHeaderViewClickListener{
        /**
         * 点击用户
         */
        void onUserClick(View v);

        /**
         * 点击搜索
         */
        void onSearchClick();
    }
}
