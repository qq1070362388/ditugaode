
package com.example.ditugaode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.ditugaode.databinding.ActivityUserBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private MyViewModel myViewModel;
    private ActivityUserBinding binding;
    private ImageView didi;
    private TextView username;
    private TextView tip;
    private TextView login_or_clear;
    private String change;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏透明
        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

//        setContentView(R.layout.activity_user);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user);//binding绑定
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.user_title);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleText);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        toolBarLayout.setTitle(getTitle());
        init();

    }
    private void init(){
        tip = findViewById(R.id.user_tip);
        username = findViewById(R.id.my_username);
        login_or_clear = findViewById(R.id.user_login);
        intent = getIntent();
        String name = intent.getStringExtra("key1");
        if(name == null){
            username.setText("未登录");
            tip.setText("登录体验更多功能");
            login_or_clear.setText("登录/注册");
        }else {
            username.setText(name);
            tip.setText("欢迎回来"+name);
            login_or_clear.setText("注销");
        }

        didi = findViewById(R.id.user_detail_didi);
        didi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        myViewModel = new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        findViewById(R.id.user_login).setOnClickListener(this);
        findViewById(R.id.travel1).setOnClickListener(this);
        findViewById(R.id.travel2).setOnClickListener(this);
        findViewById(R.id.travel3).setOnClickListener(this);
        findViewById(R.id.travel4).setOnClickListener(this);
        findViewById(R.id.shopping1).setOnClickListener(this);
        findViewById(R.id.shopping2).setOnClickListener(this);
        findViewById(R.id.shopping3).setOnClickListener(this);
        findViewById(R.id.shopping4).setOnClickListener(this);
        findViewById(R.id.user_Detail_12306).setOnClickListener(this);
        findViewById(R.id.running).setOnClickListener(this);
        findViewById(R.id.haluo).setOnClickListener(this);
        findViewById(R.id.liandongyun).setOnClickListener(this);
        findViewById(R.id.cdb).setOnClickListener(this);
        findViewById(R.id.shuidi).setOnClickListener(this);
        findViewById(R.id.news).setOnClickListener(this);
        findViewById(R.id.user_detail_didi).setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("back","界面销毁");
                finish();
            }
        });
        binding.setData(myViewModel);
        binding.setLifecycleOwner(this);
    }
    private boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty())
            return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_login:
                if(login_or_clear.getText().equals("登录/注册")){
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    finish();
                }else{
                    Toast.makeText(this,"您已注销",Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

            case R.id.travel1:
            case R.id.travel2:
            case R.id.travel3:
            case R.id.travel4:
                if(checkAppInstalled(this,"ctrip.android.view")){
                Intent intent = getPackageManager().getLaunchIntentForPackage("ctrip.android.view");
                startActivity(intent);
            }else{
                    Uri uri = Uri.parse("https://m.ctrip.com/html5/?sourceid=497&allianceid=4897&sid=182042&sepopup=150");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                    Toast.makeText(this,"未安装携程旅行，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shopping1:
            case R.id.shopping2:
            case R.id.shopping3:
            case R.id.shopping4:
                if(checkAppInstalled(this,"com.sankuai.meituan")){
                    Intent intent2 = getPackageManager().getLaunchIntentForPackage("com.sankuai.meituan");
                    startActivity(intent2);
                }else {
                    Uri uri22 = Uri.parse("http://i.meituan.com/?utm_campaign=m.baidu&utm_medium=organic&utm_source=m.baidu&utm_content=100001&utm_term=&pageId=100001&city=guilin");
                    Intent intent22 = new Intent(Intent.ACTION_VIEW);
                    intent22.setData(uri22);
                    startActivity(intent22);
                    Toast.makeText(this,"未安装美团，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.user_Detail_12306:
                if(checkAppInstalled(this,"com.MobileTicket")){
                    Intent intent3 = getPackageManager().getLaunchIntentForPackage("com.MobileTicket");
                    startActivity(intent3);
                }else{
                    Uri uri33 = Uri.parse("https://www.12306.cn/index/");
                    Intent intent33 = new Intent(Intent.ACTION_VIEW);
                    intent33.setData(uri33);
                    startActivity(intent33);
                    Toast.makeText(this,"未安装铁路12306，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.running:
                if(checkAppInstalled(this,"com.gotokeep.keep")){
                    Intent intent4 = getPackageManager().getLaunchIntentForPackage("com.gotokeep.keep");
                    startActivity(intent4);
                }
                else {
                    Uri uri44 = Uri.parse("https://www.gotokeep.com/");
                    Intent intent44 = new Intent(Intent.ACTION_VIEW);
                    intent44.setData(uri44);
                    startActivity(intent44);
                    Toast.makeText(this,"未安装keep，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.haluo:
                if(checkAppInstalled(this,"com.jingyao.easybike")){
                    Intent intent5 = getPackageManager().getLaunchIntentForPackage("com.jingyao.easybike");
                    startActivity(intent5);
                }else {
                    Uri uri55 = Uri.parse("http://www.helloglobal.com/");
                    Intent intent55 = new Intent(Intent.ACTION_VIEW);
                    intent55.setData(uri55);
                    startActivity(intent55);
                    Toast.makeText(this,"未安装哈啰单车，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.liandongyun:
                if(checkAppInstalled(this,"com.ldygo.qhzc")){
                    Intent intent6 = getPackageManager().getLaunchIntentForPackage("com.ldygo.qhzc");
                    startActivity(intent6);
                }else {
                    Uri uri66 = Uri.parse("http://www.ldygo.com/");
                    Intent intent66 = new Intent(Intent.ACTION_VIEW);
                    intent66.setData(uri66);
                    startActivity(intent66);
                    Toast.makeText(this,"未安装联动云，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cdb:
                Uri uri = Uri.parse("https://www.enmonster.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.news:
                if(checkAppInstalled(this," com.ss.android.article.news")){
                    Intent intent7 = getPackageManager().getLaunchIntentForPackage(" com.ss.android.article.news");
                    startActivity(intent7);
                }else {
                    Uri uri77 = Uri.parse("https://m.toutiao.com/?");
                    Intent intent77 = new Intent(Intent.ACTION_VIEW);
                    intent77.setData(uri77);
                    startActivity(intent77);
                    Toast.makeText(this,"未安装今日头条，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shuidi:
                if(checkAppInstalled(this," com.shuidihuzhu.aixinchou")){
                    Intent intent8 = getPackageManager().getLaunchIntentForPackage(" com.shuidihuzhu.aixinchou");
                    startActivity(intent8);
                }else {
                    Uri uri88 = Uri.parse("https://www.shuidichou.com/landing?channel=ad_search_sem&source=baidu_pc_brand&account=%E7%99%BE%E5%BA%A6PC%E5%93%81%E4%B8%93&keyword=%E6%A0%87%E9%A2%98");
                    Intent intent88 = new Intent(Intent.ACTION_VIEW);
                    intent88.setData(uri88);
                    startActivity(intent88);
                    Toast.makeText(this,"未安装水滴筹，即将跳到网页版",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.user_detail_didi:
                Uri uri99 = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=com.sdu.didi.psnger&fromcase=10000&g_f=1113784&bd_vid=11288422589755026385");
                Intent intent99 = new Intent(Intent.ACTION_VIEW);
                intent99.setData(uri99);
                startActivity(intent99);
                break;
        }
    }
}