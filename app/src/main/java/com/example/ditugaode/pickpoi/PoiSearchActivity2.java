package com.example.ditugaode.pickpoi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.ditugaode.R;
import com.example.ditugaode.Util.RecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class PoiSearchActivity2 extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, PoiListAdapter2.ItemListner {
    public static final String FROM_TYPE="type_from";
    public static final int FROM_START=1;
    public static final int FROM_TARGET=2;
    public static final int FROM_HOME=3;
    public static final int FROM_COMPANY=4;


    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Context mContext;

    private PoiSearch mPoiSearch;
    private PoiSearch.Query mQuery;

    private PoiListAdapter2 mAdapter;
    private List<PoiItem> mListData=new ArrayList<PoiItem>();

    private int mFrom=FROM_START;
    private ImageView poiReturn;
    private LinearLayout myLocLayout;
    private LinearLayout collectLayout;
    private LinearLayout mapLayout;



    public static void start(Context context, Bundle bundle, double laf, double lon, int from) {
        Intent starter = new Intent(context, PoiSearchActivity2.class);
        starter.putExtra("data",bundle);
        starter.putExtra("lon",lon);
        starter.putExtra("laf",laf);
        starter.putExtra(FROM_TYPE,from);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setAndroidNativeLightStatusBar(this,true);

        setContentView(R.layout.activity_poi_search);
        mContext=this;
//        getSupportActionBar().hide();
        mRecyclerView = findViewById(R.id.poiListRv);
        mEditText = findViewById(R.id.poi_search_poi_edit);
        init();
        initView();
    }

    private void init(){
        if (getIntent().hasExtra(FROM_TYPE)){
            mFrom=getIntent().getIntExtra(FROM_TYPE,FROM_START);
        }
        poiReturn = findViewById(R.id.poi_search_return_btn);
        myLocLayout = findViewById(R.id.choose_poi_myloc_layout);
        collectLayout = findViewById(R.id.choose_poi_collect_layout);
        mapLayout = findViewById(R.id.choose_poi_map_layout);
        poiReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"keyword can not be empty",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        myLocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectedMyPoiEvent(mFrom));
                finish();
            }
        });

        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,SelectPoiFromMapActivity.class).putExtra("from",mFrom));
            }
        });

        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initView(){
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH){
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(PoiSearchActivity2.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    searchOnclick();
                    return true;
                }else if (event.getKeyCode()==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(PoiSearchActivity2.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    searchOnclick();
                    return true;
                }
                return false;
            }
        });


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
    }

    @OnClick(R.id.poi_search_poi_btn)
    public void searchOnclick(){
        String keyword=mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)){
            Toast.makeText(mContext,"keyword can not be empty",Toast.LENGTH_LONG).show();
            return;
        }
        mQuery=new PoiSearch.Query(keyword,"",getCityCode());
        mQuery.setPageSize(10);
        mQuery.setPageNum(0);

        mPoiSearch=new PoiSearch(mContext,mQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        mPoiSearch.searchPOIAsyn();
    }

    private String getCityCode(){
        if (getIntent().hasExtra("data")){
            Bundle bundle=getIntent().getBundleExtra("data");
            return  bundle.getString("CityCode");
        }else {
            return "";
        }
    }

    private LatLng getLatLng(){
        if (getIntent().hasExtra("laf")){
            return  new LatLng(getIntent().getDoubleExtra("laf",0),getIntent().getDoubleExtra("lon",0));
        }else {
            return null;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(mQuery)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    PoiItem item=poiItems.get(0);
                    mListData.clear();
                    mListData.addAll(poiItems);
                    if (mAdapter==null){
                        mAdapter=new PoiListAdapter2(mContext,mListData,getLatLng());
                        mAdapter.setItenmListner(this);
                        mRecyclerView.setAdapter(mAdapter);
                    }else {
                        mAdapter.notifyDataSetChanged();
                    }
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                }
            } else {
                Toast.makeText(mContext,"no data",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext,"unknow error",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void ItemOnclik(PoiItem item) {
        EventBus.getDefault().post(new PoiItemEvent(item,mFrom));
        finish();
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
