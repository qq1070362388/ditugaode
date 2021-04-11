package com.example.ditugaode.view.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.ditugaode.R;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener,PoiListAdapter.ItemListner{

    public static final String FROM_TYPE="type_from";
    public static final int FROM_START=1;
    public static final int FROM_TARGET=2;
    public static final int FROM_HOME=3;
    public static final int FROM_COMPANY=4;

    private RecyclerView mRecyclerView = findViewById(R.id.rv_search);
    private EditText mEditText = findViewById(R.id.et_search_tip);

    private Context mContext;

    private PoiSearch mPoiSearch;
    private PoiSearch.Query mQuery;

    private PoiListAdapter mAdapter;
    private List<PoiItem> mListData=new ArrayList<PoiItem>();

    private int mFrom=FROM_START;

    public static void start(Context context, Bundle bundle, double laf, double lon, int from) {
        Intent starter = new Intent(context, PoiSearchActivity.class);
        starter.putExtra("data",bundle);
        starter.putExtra("lon",lon);
        starter.putExtra("laf",laf);
        starter.putExtra(FROM_TYPE,from);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void ItemOnclik(PoiItem item) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
