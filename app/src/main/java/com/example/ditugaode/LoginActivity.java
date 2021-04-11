package com.example.ditugaode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private DBOpenHelper mDBOpenHelper;
    private EditText username;
    private EditText user_password;
    private TextView login_submit;
    private ProgressBar progressBar;
    private MyViewModel myViewModel;

    Bundle bundle = new Bundle();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.user_logiin);
        mDBOpenHelper=new DBOpenHelper(this);
        setAndroidNativeLightStatusBar(this,true);
        init();

    }

    private void init(){
        myViewModel = new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        WatchChange watch = new WatchChange();
        username = findViewById(R.id.et_loginactivity_username);
        user_password = findViewById(R.id.et_loginactivity_password);
        login_submit = findViewById(R.id.login_submit);
        login_submit.setOnClickListener(this);
        findViewById(R.id.login_colse).setOnClickListener(this);
        findViewById(R.id.go_registered).setOnClickListener(this);
        username.addTextChangedListener(watch);
        user_password.addTextChangedListener(watch);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_colse:
                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                finish();
                break;
            case R.id.go_registered:
                startActivity(new Intent(LoginActivity.this, Registered.class));
                finish();
                break;
            case R.id.login_submit:
                progressBar.setVisibility(View.VISIBLE);
                String name = username.getText().toString().trim();
                String password = user_password.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    ArrayList<User> data = mDBOpenHelper.getAllData();
                    boolean match = false;
                    for (int i = 0; i < data.size(); i++) {
                        User user = data.get(i);
                        if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                            match = true;
                            break;
                        } else {
                            match = false;
                        }
                    }
                    if (match) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        String thisname = username.getText().toString().trim();
                        bundle.putString("key1",thisname);
                        Intent intent = new Intent(this,UserActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        myViewModel.username = name;
                        myViewModel.password = password;
                        finish();//销毁此Activity
                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:
                    login_submit.setEnabled(true);
                    login_submit.setBackgroundResource(R.drawable.login_buttom_press);
                    break;
                case 102:
                    login_submit.setEnabled(false);
                    login_submit.setBackgroundResource(R.drawable.login_buttom);
                    break;
            }
        }
    };



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    /**
     * 自定义监听EditText
     */
    class WatchChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(username.length() > 0 && user_password.length() > 0){
                Message msg = new Message();
                msg.what=101;
                handler.sendMessage(msg);
            }else {
                Message msg = new Message();
                msg.what=102;
                handler.sendMessage(msg);
            }
        }
    }
    //修改状态栏文字颜色
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
