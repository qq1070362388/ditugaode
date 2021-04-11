package com.example.ditugaode.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ditugaode.R;

public class BaseActivity extends AppCompatActivity {
    private View mToastView;
    private TextView mTvToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        mToastView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null, false);
        mTvToast = mToastView.findViewById(R.id.tv_toast);
    }

    public void showToast(String text){
        if(mToastView == null){
            init();
        }
        Toast toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        mTvToast.setText(text);
        toast.setGravity(Gravity.CENTER,0,200);
        toast.show();
    }
}
