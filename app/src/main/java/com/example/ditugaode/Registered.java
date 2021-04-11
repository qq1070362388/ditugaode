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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class Registered extends AppCompatActivity implements View.OnClickListener{
    private EditText  registered_username;
    private EditText registered_password;
    private EditText registered_password2;
    private TextView registered_submit;
    private DBOpenHelper mDBOpenHelper;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.user_registered);
        setAndroidNativeLightStatusBar(this,true);
        init();
        mDBOpenHelper=new DBOpenHelper(this);
    }

    private void init(){
        myViewModel = new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        WatchChange watchChange = new WatchChange();
        registered_submit = findViewById(R.id.registered_submit);
        registered_username = findViewById(R.id.registered_username);
        registered_password = findViewById(R.id.registered_password);
        registered_password2 = findViewById(R.id.registered_password2);
        findViewById(R.id.go_Login).setOnClickListener(this);
        findViewById(R.id.registered_colse).setOnClickListener(this);
        findViewById(R.id.registered_submit).setOnClickListener(this);
        registered_username.addTextChangedListener(watchChange);
        registered_password.addTextChangedListener(watchChange);
        registered_password2.addTextChangedListener(watchChange);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_Login:
                startActivity(new Intent(Registered.this,LoginActivity.class));
                finish();
                break;
            case R.id.registered_colse:
                startActivity(new Intent(Registered.this,UserActivity.class));
                finish();
                break;
            case R.id.registered_submit:
//                注册按钮
                //获取用户输入的用户名、密码、验证码
                String username = registered_username.getText().toString().trim();
                String password = registered_password.getText().toString().trim();
                String password2 = registered_password2.getText().toString().trim();

                //注册验证
                if(password.equals(password2)){
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)  ) {

                        //将用户名和密码加入到数据库中
                        mDBOpenHelper.add(username, password);
                        Intent intent2 = new Intent(this, LoginActivity.class);
                        startActivity(intent2);
                        finish();
                        Toast.makeText(this,  "验证通过，注册成功,请重新登录", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "两次密码不一致请重新输入", Toast.LENGTH_SHORT).show();
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
                    registered_submit.setEnabled(true);
                    registered_submit.setBackgroundResource(R.drawable.login_buttom_press);
                    break;
                case 102:
                    registered_submit.setEnabled(false);
                    registered_submit.setBackgroundResource(R.drawable.login_buttom);
                    break;
            }
        }
    };
    /**
     * 自定义监听EditText
     */
    class WatchChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(registered_username.length() > 0 && registered_password.length() > 0 && registered_password2.length() > 0){
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
