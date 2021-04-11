package com.example.ditugaode.busresult;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.example.ditugaode.R;
import com.example.ditugaode.adapter.BusPathAdapter;
import com.example.ditugaode.adapter.BusSegmentListAdapter;
import com.example.ditugaode.behavior.AnchorBottomSheetBehavior;
import com.example.ditugaode.overlay.AMapUtil;
import com.example.ditugaode.overlay.BusRouteOverlay;

import java.util.ArrayList;
import java.util.List;

public class BusRouteResultActivity extends AppCompatActivity {

    private RecyclerView mStepsList;
    private ViewPager mViewPager;
    private NestedScrollView mNestScrollView;
    private MapView mMapView;

    private AnchorBottomSheetBehavior mBehavior;

    private AMap mAmap;
    private BusRouteOverlay mCurrentOverlay;
    private PathsAdapter mPathAdapter;
    private BusPathAdapter mBusPathAdapter;
    private BusSegmentListAdapter mBusSegmentListAdapter;
    private List<BusStep> mListData=new ArrayList<BusStep>();
    private int mHeight=150;
    private float mOffset=0.0f;
    private int mSheetHeight=120;

    private Context mContext;
    private BusRouteResult mRouteResult;
    private ImageView back;


    public static void start(Context context, BusRouteResult result,int pos) {
        Intent starter = new Intent(context, BusRouteResultActivity.class);
        starter.putExtra("result",result);
        starter.putExtra("position",pos);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route_result);
        mStepsList = findViewById(R.id.bus_segment_list);
        mViewPager = findViewById(R.id.bus_paths_viewpage);
        mNestScrollView = findViewById(R.id.bus_paths_bottom_sheet);
        mMapView = findViewById(R.id.route_plan_map);
        mContext=this;

        init();
        initMap(savedInstanceState);
        initSheet();
    }

    private void init(){
        back = findViewById(R.id.return_img);
        if(getIntent().hasExtra("result")){
            mRouteResult = getIntent().getParcelableExtra("result");
        }else {
            finish();
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        mHeight = dm.heightPixels;
        mSheetHeight = getResources().getDimensionPixelSize(R.dimen.sheet_peakHeight);
        mBehavior = AnchorBottomSheetBehavior.from(mNestScrollView);
        mBehavior.addBottomSheetCallback(new AnchorBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case AnchorBottomSheetBehavior.STATE_COLLAPSED:
                        mCurrentOverlay.zoomToBusSpan(getSheetPadding());
                        break;
                    case AnchorBottomSheetBehavior.STATE_DRAGGING:
                        Log.d("bottomsheet-", "STATE_DRAGGING");
                        break;
                    case AnchorBottomSheetBehavior.STATE_EXPANDED:
                        Log.d("bottomsheet-", "STATE_EXPANDED");
                        break;
                    case AnchorBottomSheetBehavior.STATE_ANCHOR_POINT:
                        Log.d("bottomsheet-", "STATE_ANCHOR_POINT");
                        mCurrentOverlay.zoomToBusSpan(getSheetPadding());
                        break;
                    case AnchorBottomSheetBehavior.STATE_HIDDEN:
                        Log.d("bottomsheet-", "STATE_HIDDEN");
                        break;
                    default:
                        Log.d("bottomsheet-", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mOffset=slideOffset;
            }
        });
        mBehavior.setState(AnchorBottomSheetBehavior.STATE_COLLAPSED);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initSheet(){
        int position=getIntent().getIntExtra("position",0);
        List<View> list=new ArrayList<View>();
        for (BusPath path:mRouteResult.getPaths()){
            View view= LayoutInflater.from(mContext).inflate(R.layout.layout_page_item,null);
            TextView title=(TextView)view.findViewById(R.id.bus_path_title);
            TextView content=(TextView)view.findViewById(R.id.path_content);
            title.setText(AMapUtil.getBusPathTitle(path));
            content.setText(AMapUtil.getBusPathDes(path));
            list.add(view);
        }
        mPathAdapter=new PathsAdapter(list);
        mViewPager.setAdapter(mPathAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                drawBusRoutes(mRouteResult,mRouteResult.getPaths().get(position));
//                mListData.clear();
//                mListData.addAll(mRouteResult.getPaths().get(position).getSteps());
                mStepsList.setAdapter(new BusPathAdapter(mContext, mRouteResult.getPaths().get(position).getSteps()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(position);
        mListData=mRouteResult.getPaths().get(position).getSteps();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mStepsList.setLayoutManager(linearLayoutManager);
        mStepsList.setAdapter(new BusPathAdapter(mContext,mListData));
    }

    private void initMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mAmap=mMapView.getMap();

        /**   基本设置   **/
        mAmap.setTrafficEnabled(true);
        mAmap.showIndoorMap(true);
        mAmap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        /**   定位模式   **/
        MyLocationStyle locationStyle=new MyLocationStyle();
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mAmap.setMyLocationStyle(locationStyle);
        mAmap.setMyLocationEnabled(true);

        mAmap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                drawBusRoutes(mRouteResult,mRouteResult.getPaths().get(0));
            }
        });

        /**监听**/
        mAmap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });
    }
    private void drawBusRoutes(BusRouteResult busRouteResult,BusPath path){
        mAmap.clear();
        mCurrentOverlay = new BusRouteOverlay(
                mContext, mAmap,path,busRouteResult.getStartPos(),
                busRouteResult.getTargetPos());
        mCurrentOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
        mCurrentOverlay.removeFromMap();
        mCurrentOverlay.addToMap();
        mCurrentOverlay.zoomToBusSpan(getSheetPadding());
    }

    private int getSheetPadding(){
        return (int)((mHeight-mSheetHeight)*mOffset+mSheetHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    class PathsAdapter extends PagerAdapter{
        private List<View> mViewList;

        public PathsAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViewList.get(position));
        }
    }
}
