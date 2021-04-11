package com.example.ditugaode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.Path;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.ditugaode.adapter.BusResultListAdapter;
import com.example.ditugaode.adapter.DrivePathAdapter;
import com.example.ditugaode.adapter.RideStepAdapter;
import com.example.ditugaode.adapter.WalkStepAdapter;
import com.example.ditugaode.behavior.NoAnchorBottomSheetBehavior;
import com.example.ditugaode.overlay.AMapUtil;
import com.example.ditugaode.overlay.BusRouteOverlay;
import com.example.ditugaode.overlay.DrivingRouteOverlay;
import com.example.ditugaode.overlay.RideRouteOverlay;
import com.example.ditugaode.overlay.WalkRouteOverlay;
import com.example.ditugaode.pickpoi.PoiItemEvent;
import com.example.ditugaode.pickpoi.PoiSearchActivity2;
import com.example.ditugaode.pickpoi.SelectedMyPoiEvent;
import com.example.ditugaode.view.base.InputMethodUtils;
import com.example.ditugaode.view.base.MapViewInterface;
import com.example.ditugaode.view.base.MyAMapUtils;
import com.example.ditugaode.view.base.OnItemClickListener;
import com.example.ditugaode.view.base.PoiSearchActivity;
import com.example.ditugaode.view.base.WalkRouteNaviAcitivity;
import com.example.ditugaode.view.map.AMapServicesUtil;
import com.example.ditugaode.view.map.GPSView;
import com.example.ditugaode.view.map.MapHeaderView;
import com.example.ditugaode.view.map.PoiDetailBottom;
import com.example.ditugaode.view.map.SupendViewContainer;
import com.example.ditugaode.view.map.TrafficView;
import com.example.ditugaode.view.map.ViewAnimUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements LocationSource, TextWatcher,AMapLocationListener, Inputtips.InputtipsListener, TrafficView.OnTrafficChangeListener, AMap.OnMapTouchListener, MapViewInterface,AMap.OnPOIClickListener,GPSView.OnGPSViewClickListener,View.OnClickListener, AMapGestureListener,MapHeaderView.OnMapHeaderViewClickListener,PoiDetailBottom.OnPoiDetailBottomClickListener,OnItemClickListener,RouteSearch.OnRouteSearchListener,BusResultListAdapter.BusListItemListner{
    public static final String CITY_CODE="CityCode";

    private MapView mapView = null;
    private AMap aMap;
    private AMapLocation aMapLocation;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private UiSettings uiSettings;
    private OnLocationChangedListener mListener;
    private TrafficView mTrafficView;
    boolean useMoveToLocationWithMapMode = true;
    private int mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;//地图状态类型
    private float mAccuracy;
    private GPSView gpsView;
    private int mZoomLevel = 16;//地图缩放级别，最大缩放级别为20
    private static long mAnimDuartion = 500L;//地图动效时长
    private LatLng mLatLng;//当前定位经纬度
    private LatLng mClickPointLatLng;//当前点击的poi经纬度
    private static boolean mFirstLocation = true;//第一次定位
    private int mCurrentGpsState = STATE_UNLOCKED;//当前定位状态
    private static final int STATE_UNLOCKED = 0;//未定位状态，默认状态
    private static final int STATE_LOCKED = 1;//定位状态
    private static final int STATE_ROTATE = 2;//根据地图方向旋转状态
    private SensorEventHelper mSensorHelper;
    //自定义定位小蓝点的Marker
    private Marker mLocMarker;//自定义小蓝点
    private boolean mMoveToCenter = true;//是否可以移动地图到定位点
    private Circle mCircle;
    private MyLocationStyle mLocationStyle;
    // 当前是否正在处理POI点击
    private boolean isPoiClick;
    private TextView mTvLocation;
    private String mPoiName;
    private LinearLayout mLLSearchContainer;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private MapHeaderView mMapHeaderView;
    private View mBottomSheet;
    private BottomSheetBehavior<View> mBehavior;
    private int mMaxPeekHeight;//最大高的
    private int mMinPeekHeight;//最小高度
    private int mScreenHeight;
    private int mScreenWidth;
    private TextView mTvLocTitle;
    private View mPoiColseView;
    private boolean slideDown;//向下滑动
    private int moveY;
    private int[] mBottomSheetLoc = new int[2];
    private int mPadding;
    private View mGspContainer;
    private boolean onScrolling;//正在滑动地图
    private PoiDetailBottom mPoiDetailTaxi;
    private TextView mTvRoute;
    private TextView mTvCall;

    private String mCity;
    private EditText mEtSearchTip;
    private ImageView mIvLeftSearch;
    private SupendViewContainer mSupendPartitionView;
    private LocationManager mLocMgr;
    private RecyclerView mRecycleViewSearch;
    private ProgressBar mSearchProgressBar;
    private SearchAdapter mSearchAdapter;
    // 搜索结果存储
    private List<Tip> mSearchData = new ArrayList<>();

    private SupendViewContainer supendViewContainer;
    private TextView mtvGo;
    private WalkStepAdapter mWalkStepAdapter;
    private DrivePathAdapter mDrivePathAdapter;
    private RideStepAdapter mRideStepAdapter;
    private BusResultListAdapter mBusResultAdapter;

    private WalkRouteResult mWalkRouteResult;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private RideRouteResult mRideRouteResult;

    private Context mContext;
    private RecyclerView mPathDetailRecView;
    private LinearLayout mPathLayout;
    private LinearLayout mPathLayout1;
    private LinearLayout mPathLayout2;
    private TextView mPathDurText;
    private TextView mPathDurText1;
    private TextView mPathDurText2;
    private TextView mPathDisText;
    private TextView mPathDisText1;
    private TextView mPathDisText2;
    private LinearLayout mSheetHeadLayout;
    private int mTopLayoutHeight=200;
    private RelativeLayout mTopLayout;
    private NoAnchorBottomSheetBehavior mBehavior2;
    private NestedScrollView mNesteScrollView;
    private TextView mNaviText;
    private Button mNaviBtn;
    private TextView mFloatBtn;
    private Poi mEndPoi;
    private Poi mStartPoi;
//    private int mSelectedType =TYPE_DRIVE;
    private static final int MSG_MOVE_CAMERA = 0x01;
    private final int TYPE_DRIVE=100;
    private final int TYPE_BUS=101;
    private final int TYPE_WALK=102;
    private final int TYPE_RIDE=103;
    private int mSelectedType =TYPE_DRIVE;

    private ConstraintLayout constraintLayoutAll;
    private RecyclerView mBusResultRview;
    private ImageView back;
    private TabLayout mTabLayout;
    private TextView mPathTipsText;
    private TextView mFromText;
    private TextView mTargetText;
    private Button startbtn;
    private TextView startbtn1;
    private ImageView change;
    private LinearLayout pathlayout;
    private LinearLayout pathlayout1;
    private LinearLayout pathlayout2;
    private Location mLocation;

    private boolean isTvGo;
    private TextView mPoiTitleText;
    private TextView mPoiDescText;
    private ImageView mgo;
    private ConstraintLayout mFrequentView;


    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view_supend_container for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        Log.d("create","onActivityCreated重新实例化");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onActivityCreated(savedInstanceState);

        EventBus.getDefault().register(this);//注册

        //沉浸式状态栏
        View decorview = requireActivity().getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);

        setAndroidNativeLightStatusBar(getActivity(),true);//修改状态栏文字颜色为黑色

        mContext = getActivity();
        supendViewContainer = requireActivity().findViewById(R.id.supend);
        mapView = requireActivity().findViewById(R.id.map);
        gpsView =  requireActivity().findViewById(R.id.gpsView);
        gpsView.setGpsState(mCurrentGpsState);
        mapView.onCreate(savedInstanceState);
        mTrafficView = requireActivity().findViewById(R.id.tv_toast);
        mMapHeaderView = (MapHeaderView)requireActivity().findViewById(R.id.mhv);
        mTvLocation = requireActivity().findViewById(R.id.tv_my_loc);
        mLLSearchContainer = requireActivity().findViewById(R.id.ll_search_container);
        mLLSearchContainer.getBackground().setAlpha(0);
        //底部弹出BottomSheet
        mBottomSheet =requireActivity().findViewById(R.id.poi_detail_bottom);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheet.setVisibility(View.GONE);
        mTvLocTitle = requireActivity().findViewById(R.id.tv_title);
        mPoiColseView = requireActivity().findViewById(R.id.iv_close);
        mPadding = getResources().getDimensionPixelSize(R.dimen.padding_size);
        mGspContainer = requireActivity().findViewById(R.id.all_gps);
        mPoiDetailTaxi =requireActivity().findViewById(R.id.poi_detail_taxi);
        mPoiDetailTaxi.clearAnimation();
        mPoiDetailTaxi.setVisibility(View.GONE);
        mTvRoute = requireActivity().findViewById(R.id.tv_route);
        mIvLeftSearch = requireActivity().findViewById(R.id.iv_search_left);
        mEtSearchTip = requireActivity().findViewById(R.id.et_search_tip);
        mRecycleViewSearch = requireView().findViewById(R.id.rv_search);
        mRecycleViewSearch.getBackground().setAlpha(230);
        mIvLeftSearch = (ImageView)requireActivity().findViewById(R.id.iv_search_left);
        mEtSearchTip = (EditText)requireActivity().findViewById(R.id.et_search_tip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycleViewSearch.setLayoutManager(layoutManager);
        mSearchProgressBar = (ProgressBar)requireActivity().findViewById(R.id.progressBar);
        mTvCall = requireActivity().findViewById(R.id.tv_call_taxi);
        mtvGo = requireActivity().findViewById(R.id.tv_go);
        mPathDetailRecView = requireActivity().findViewById(R.id.path_detail_recyclerView);
        constraintLayoutAll = requireActivity().findViewById(R.id.constraintLayout_all);
        back = requireActivity().findViewById(R.id.back);

        mPathLayout = requireActivity().findViewById(R.id.path_layout);
        mPathLayout1 = requireActivity().findViewById(R.id.path_layout1);
        mPathLayout2 = requireActivity().findViewById(R.id.path_layout2);
        mPathDurText = requireActivity().findViewById(R.id.path_general_time);
        mPathDurText1 = requireActivity().findViewById(R.id.path_general_time1);
        mPathDurText2 = requireActivity().findViewById(R.id.path_general_time2);
        mPathDisText = requireActivity().findViewById(R.id.path_general_distance);
        mPathDisText2 = requireActivity().findViewById(R.id.path_general_distance1);
        mPathDisText1 = requireActivity().findViewById(R.id.path_general_distance2);
        mSheetHeadLayout= requireActivity().findViewById(R.id.sheet_head_layout);
        mTopLayout = requireActivity().findViewById(R.id.topLayout);
        mTopLayout.setVisibility(View.GONE);
        mNesteScrollView = requireActivity().findViewById(R.id.bottom_sheet);

        mNesteScrollView.setVisibility(View.GONE);

        mNaviText = requireActivity().findViewById(R.id.navi_start_btn_1);
        mNaviBtn = requireActivity().findViewById(R.id.navi_start_btn);
        mFloatBtn = requireActivity().findViewById(R.id.tv_go);
        mBusResultRview = requireView().findViewById(R.id.bus_result_recyclerView);

        mTabLayout = requireActivity().findViewById(R.id.route_plan_tab_layout);
        mPathTipsText = requireActivity().findViewById(R.id.path_detail_traffic_light_text);
        mFromText = requireActivity().findViewById(R.id.route_plan_start_edit_layout);
        mTargetText = requireActivity().findViewById(R.id.route_plan_to_edit_layout);
        startbtn = requireActivity().findViewById(R.id.navi_start_btn);
        startbtn1 = requireActivity().findViewById(R.id.navi_start_btn_1);
        change = requireActivity().findViewById(R.id.route_plan_exchange_btn);
        pathlayout = requireActivity().findViewById(R.id.path_layout);
        pathlayout1 = requireActivity().findViewById(R.id.path_layout1);
        pathlayout2 = requireActivity().findViewById(R.id.path_layout2);
        mPoiTitleText = requireActivity().findViewById(R.id.route_plan_poi_title);
        mPoiDescText = requireActivity().findViewById(R.id.route_plan_poi_desc);
        mgo = requireActivity().findViewById(R.id.go);
        mFrequentView = requireActivity().findViewById(R.id.fv);
        initTabLayout();
        //初始化
        if(aMap == null) {
            Log.d("create","地图初始化");
            aMap = mapView.getMap();
            aMap.showIndoorMap(true);
            uiSettings = aMap.getUiSettings();
//            uiSettings.setCompassEnabled(true); //开启指南针
            uiSettings.setRotateGesturesEnabled(false);

            aMap.setLocationSource(this);//设置定位监听
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setMyLocationEnabled(true);//显示定位层并可触发定位

            aMap.setTrafficEnabled(true);//显示实时交通
            setBottomSheet();

            setListener();
            setLocationStyle();
        }
    }

    /**
     * 事件处理 监听
     */
    private void setListener(){
        gpsView.setOnGPSViewClickListener(this);
        mSensorHelper = new SensorEventHelper(getActivity());
        //地图手势事件
        aMap.setAMapGestureListener(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        if(mTrafficView != null){
            mTrafficView.setOnTrafficChangeListener(this);
        }
        if(null != mPoiColseView){
            mPoiColseView.setOnClickListener(this);
        }
        mMapHeaderView.setOnMapHeaderViewClickListener(this);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private float lastSlide;//上次slideOffset
            private float currSlide;//当前slideOffset

            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    //展开
                    case BottomSheetBehavior.STATE_EXPANDED:
                       Log.d("be","展开");
                        smoothSlideUpMap();
                        break;
                    //折叠
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d("be","折叠");
                        onPoiDetailCollapsed();
                        slideDown = false;
                        break;
                    //隐藏
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //拖拽
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //结束：释放
                        break;
                }
            }
            /**
             * BottomSheet滑动回调
             */

            @Override
            public void onSlide(@NonNull View view, float v) {
                currSlide = v;
//                Log.d("upBottom","滑动回调");
                if(v > 0){
                    mPoiColseView.setVisibility(View.GONE);
                    showBackToMapState();
                    if(v < 1){

                    }
                    mMoveToCenter =false;
                    if (currSlide - lastSlide > 0){
                        Log.i("slide",">>>>>向上滑动");
                        slideDown = false;
                        onPoiDetailExpanded();
//                        smoothSlideUpMap();
                    }else if(currSlide - lastSlide < 0){
                        Log.i("slide",">>>>>向下滑动");
                        if(!slideDown){
                            smoothSlideDownMap();
                        }
                    }
                }else if(v == 0){
                    //滑动到COLLAPSED状态
                    mPoiColseView.setVisibility(View.VISIBLE);
                    showPoiDetailState();
                }else if (v < 0) {
                    //从COLLAPSED向HIDDEN状态滑动，此处禁止BottomSheet隐藏
                    //setHideable(false)禁止Behavior执行：可以实现禁止向下滑动消失
                    mBehavior.setHideable(false);
                }
                lastSlide = currSlide;
            }
        });
        mPoiDetailTaxi.setOnPoiDetailBottomClickListener(this);
        aMap.setOnPOIClickListener(this);
        mTvRoute.setOnClickListener(this);
        mTvCall.setOnClickListener(this);
        mtvGo.setOnClickListener(this);
        mIvLeftSearch.setOnClickListener(this);
        // 搜索布局左侧返回箭头图标
        mIvLeftSearch.setOnClickListener(this);
        back.setOnClickListener(this);
        // 搜索结果RecyclerView
        mSearchAdapter = new SearchAdapter(mSearchData);
        mRecycleViewSearch.setAdapter(mSearchAdapter);
        // 搜索输入框
        mEtSearchTip.addTextChangedListener(this);
        mSearchAdapter.setOnItemClickListener(this);


     mTopLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
         @Override
         public void onGlobalLayout() {
             mTopLayoutHeight = mTopLayout.getHeight();
             mTopLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
         }
     });
        mBehavior2 = NoAnchorBottomSheetBehavior.from(mNesteScrollView);
        mBehavior2.setState(NoAnchorBottomSheetBehavior.STATE_COLLAPSED);
        mBehavior2.setPeekHeight(getSheetHeadHeight());
        mBehavior2.setBottomSheetCallback(new NoAnchorBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (mTopLayout.getVisibility()==View.VISIBLE && mPathDetailRecView.getVisibility()==View.VISIBLE){
                    if (slideOffset > 0.5){
                        mNaviText.setVisibility(View.GONE);
                        mNaviBtn.setVisibility(View.VISIBLE);
                    }else {
                        mNaviText.setVisibility(View.VISIBLE);
                        mNaviBtn.setVisibility(View.GONE);
                    }
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPathDetailRecView.setLayoutManager(linearLayoutManager);
        mFromText.setOnClickListener(this);
        mTargetText.setOnClickListener(this);
        change.setOnClickListener(this);
        pathlayout.setOnClickListener(this);
        pathlayout1.setOnClickListener(this);
        pathlayout2.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        startbtn1.setOnClickListener(this);
        mgo.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mBusResultRview.setLayoutManager(linearLayoutManager2);
        mFrequentView.setOnClickListener(this);
    }
    /**
     * 将GpsButton移动到poi detail上面
     */
    private void moveGspButtonAbove() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (gpsView.isAbovePoiDetail()) {
                    //已经在上面，不需要重复调用
                    return;
                }
                if (moveY == 0) {
                    //计算Y轴方向移动距离
                    moveY = mGspContainer.getTop() - mBottomSheet.getTop() + mGspContainer.getMeasuredHeight() + mPadding;
                    mBottomSheet.getLocationInWindow(mBottomSheetLoc);
                }
                if (moveY > 0) {
                    mGspContainer.setTranslationY(-moveY);
                    gpsView.setAbovePoiDetail(true);
                }
            }
        });



    }
    /**
     * 将GpsButton移动到原来位置
     */
    private void resetGpsButtonPosition() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!gpsView.isAbovePoiDetail()) {
                    //已经在下面，不需要重复调用
                    return;
                }
                //回到原来位置
                mGspContainer.setTranslationY(0);
                gpsView.setAbovePoiDetail(false);
            }
        });

    }


    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        /*if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = aMap.addMarker(markerOptions);
    }

    private void addRotateMarker(LatLng latlng) {
       /* if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        //3D效果
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_3d)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = aMap.addMarker(markerOptions);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("zh","onLocationChanged进来了");
        if(mListener != null && aMapLocation != null){
            if(aMapLocation.getErrorCode() == 0){
                Log.d("zh","定位成功了");
                Log.d("zh", String.valueOf(mFirstLocation));
                this.aMapLocation = aMapLocation;
                mLocation = aMapLocation;

               mLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                if(aMapLocation.getPoiName() != null && !aMapLocation.getPoiName().equals(mPoiName)){
                    if(!isPoiClick){
                        // 点击poi时,定位位置和点击位置不一定一样
                        mPoiName = aMapLocation.getPoiName();
                        showPoiNameText(String.format("在%s附近", mPoiName));
                    }
                }
                if(!aMapLocation.getCity().equals(mCity)){
                    mCity = aMapLocation.getCity();

                }
                //展示自定义定位小蓝点
                //首次定位成功才修改地图中心点，并移动
                mAccuracy = aMapLocation.getAccuracy();
                if(mFirstLocation){
                    mStartPoi = new Poi(getString(R.string.poi_search_my_location),mLatLng,"");
                    Log.d("zh","初次定位");
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel), new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mCurrentGpsState = STATE_LOCKED;
                            gpsView.setGpsState(mCurrentGpsState);
                            mMapType = MyLocationStyle.LOCATION_TYPE_LOCATE;
                            addCircle(mLatLng, mAccuracy);//添加定位精度圆
                            addMarker(mLatLng);//添加定位图标
                            mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                            mFirstLocation = false;
                        }

                        @Override
                        public void onCancel() {

                        }

                    });

                }else{
                    //BottomSheet顶上显示,地图缩小显示
                    mCircle.setCenter(mLatLng);
                    mCircle.setRadius(mAccuracy);
                    mLocMarker.setPosition(mLatLng);
                    if (mMoveToCenter) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel));
                    }
                }

            }else{
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }



    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            Log.d("zh","开始定位");
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mLocationOption.setLocationCacheEnable(true);//开启定位缓存
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.startLocation();
            }
        }

    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        Log.d("zh","停止定位");
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocMarker = null;
        mLocationClient = null;
    }

    /**
     * 设置底部POI详细BottomSheet
     */
    private void setBottomSheet(){
        mMinPeekHeight = mBehavior.getPeekHeight();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null == wm) {
            Log.d("zh","获取WindowManager失败");
            return;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        //屏幕高度3/5
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        //设置bottomsheet高度为屏幕 3/5
        int height = mScreenHeight * 3 / 5;
        mMaxPeekHeight = height;
        ViewGroup.LayoutParams params = mBottomSheet.getLayoutParams();
        params.height = height;
    }

    /**
     * 设置地图类型
     */
    private void setLocationStyle() {
        Log.d("zh","定位小蓝点");
        // 自定义系统定位蓝点
        if (null == mLocationStyle) {
            mLocationStyle = new MyLocationStyle();
            mLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            mLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明
        }
        //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
        aMap.setMyLocationStyle(mLocationStyle.myLocationType(mMapType));
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.i("amap","onTouch 关闭地图和小蓝点一起移动的模式");
        useMoveToLocationWithMapMode = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
        aMap = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        useMoveToLocationWithMapMode = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        useMoveToLocationWithMapMode = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onGPSClick() {

        Log.d("zh","点击GPs");
        CameraUpdate cameraUpdate = null;
        mMoveToCenter = true;
        isPoiClick = false;
        //修改定位图标状态
        switch (mCurrentGpsState){
            case STATE_LOCKED:
                mZoomLevel = 18;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_ROTATE;
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng,mZoomLevel,30,0));
                break;
            case STATE_UNLOCKED:
            case STATE_ROTATE:
                mZoomLevel = 16;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_LOCKED;
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng,mZoomLevel,30,0));
                break;
        }
        //显示底部POI详情
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail("我的位置", String.format("在%s附近", mPoiName));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText("我的位置");
            mTvLocation.setText(String.format("在%s附近", mPoiName));
        }

        aMap.setMyLocationEnabled(true);
        //改变定位图标状态
        gpsView.setGpsState(mCurrentGpsState);
        //执行拖动特性
        aMap.animateCamera(cameraUpdate,mAnimDuartion, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });

        setLocationStyle();
        resetLocationMarker2();
        mgo.setVisibility(View.GONE);
    }

    /**
     * 根据当前地图状态重置定位蓝点
     */
    public void resetLocationMarker(){
//        aMap.clear();
        mLocMarker = null;
        if(gpsView.getGpsState() == GPSView.STATE_ROTATE){
            Log.d("gps","运行了1");
            //ROTATE模式不需要方向传感器
            //mSensorHelper.unRegisterSensorListener();
            addRotateMarker(mLatLng);
        }else{
//            Log.d("gps","运行了2");
//            addMarker(mLatLng);
            if(null != mLocMarker){
                mSensorHelper.setCurrentMarker(mLocMarker);
            }
        }
//        addCircle(mLatLng,mAccuracy);
    }
    public void resetLocationMarker2(){
        aMap.clear();
        mLocMarker = null;
        if(gpsView.getGpsState() == GPSView.STATE_ROTATE){
            //ROTATE模式不需要方向传感器
            //mSensorHelper.unRegisterSensorListener();
            addRotateMarker(mLatLng);
        }else{
            addMarker(mLatLng);
            if(null != mLocMarker){
                mSensorHelper.setCurrentMarker(mLocMarker);
            }
        }
        addCircle(mLatLng,mAccuracy);
    }
    public void resetLocationMarker3(){
        aMap.clear();
        mLocMarker = null;
        if(gpsView.getGpsState() == GPSView.STATE_ROTATE){
            //ROTATE模式不需要方向传感器
            //mSensorHelper.unRegisterSensorListener();
            addRotateMarker(mLatLng);
        }else{
            addMarker(mLatLng);
            if(null != mLocMarker){
                mSensorHelper.setCurrentMarker(mLocMarker);
            }
        }
        addCircle(mLatLng,mAccuracy);
        if(mEndPoi != null){
            addPOIMarderAndShowDetail(mEndPoi.getCoordinate(),mEndPoi.getName());
        }

    }

    /**
     * 隐藏地图图层
     */
    private void hideMapView(){
//        mapView.setVisibility(View.GONE);
        mMapHeaderView.setVisibility(View.GONE);
        mTrafficView.setVisibility(View.GONE);
        gpsView.setVisibility(View.GONE);
        mgo.setVisibility(View.GONE);
        mBehavior.setHideable(true);
        resetGpsButtonPosition();
        hidePoiDetail();
        uiSettings.setAllGesturesEnabled(false);
    }
    private void hideMapView2(){
//        mapView.setVisibility(View.GONE);
        mMapHeaderView.setVisibility(View.GONE);
        mTrafficView.setVisibility(View.GONE);
        gpsView.setVisibility(View.GONE);
        mBehavior.setHideable(true);
        resetGpsButtonPosition();
        hidePoiDetail();
        mgo.setVisibility(View.GONE);
//        uiSettings.setAllGesturesEnabled(false);
    }

    /**
     * 显示地图图层
     */
    private void showMapView(){
        mapView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        mTrafficView.setVisibility(View.VISIBLE);
        gpsView.setVisibility(View.VISIBLE);
//        mgo.setVisibility(View.VISIBLE);
        mLLSearchContainer.setVisibility(View.GONE);
        uiSettings.setAllGesturesEnabled(true);
    }

    /**
     * 显示当前所在poi点信息
     */
    private void showPoiNameText(String locInfo) {
        mTvLocation.setText(locInfo);
    }


    @Override
    public void onClick(View v) {
        if (v == null){
            return;
        }
        // 点击关闭POI detail
        if(v == mPoiColseView){
            mBehavior.setHideable(true);
            resetGpsButtonPosition();
            hidePoiDetail();
            mgo.setVisibility(View.VISIBLE);
            return;
        }

        if (v == mgo){
            isTvGo = true;
            hideMapView2();
            mTopLayout.setVisibility(View.VISIBLE);
        }

//        进入导航页面
        if(v == mTvRoute){
            Log.d("mTvRoute","进入导航界面");
            if(mLatLng == null){
                Toast.makeText(getActivity(),"导航失败",Toast.LENGTH_SHORT).show();
                return;
            }
            if(mClickPointLatLng == null){
                Toast.makeText(getActivity(),"请选择目的地",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(),WalkRouteNaviAcitivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("startLatLng", mLatLng);
            bundle.putParcelable("stopLatLng", mClickPointLatLng);

            intent.putExtra("params", bundle);
            startActivity(intent);
            return;
        }

        //滴滴打车界面
        if(v == mTvCall){
            if(checkAppInstalled(getActivity(),"com.sdu.didi.psnger")){
                Intent intent = requireActivity().getPackageManager().getLaunchIntentForPackage("com.sdu.didi.psnger");
                startActivity(intent);
            }else{
                Uri uri = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=com.sdu.didi.psnger&fromcase=10000&g_f=1113784&bd_vid=11288422589755026385");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(getActivity(),"未安装滴滴出行，即将跳到下载页面",Toast.LENGTH_SHORT).show();
            }
        }
        //到这去路径规划
        if(v == mtvGo){
            if(mEndPoi == null){
                return;
            }
            isTvGo = true;
            routeSearch(mStartPoi,mEndPoi,TYPE_DRIVE);
//            hideAll();

        }


        //点击返回箭头
        if(v == mIvLeftSearch){
            hideSearchTipView();
            showMapView();
            return;
        }
        if(v == back){
            hideRouted();
            showMapView();
            isTvGo = false;
            resetLocationMarker3();
            mBusResultRview.setVisibility(View.GONE);
            aMap.getUiSettings().setRotateGesturesEnabled(false);
        }

        if(v == mFromText){
            Location location1=aMap.getMyLocation();
            PoiSearchActivity2.start(mContext,mLocation.getExtras(),mLocation.getLatitude(),mLocation.getLongitude(),PoiSearchActivity2.FROM_START);
            routeSearch(mStartPoi,mEndPoi,TYPE_DRIVE);
        }
        if(v == mTargetText){

            Location location2=aMap.getMyLocation();
            PoiSearchActivity2.start(mContext,mLocation.getExtras(),mLocation.getLatitude(),mLocation.getLongitude(),PoiSearchActivity2.FROM_TARGET);
            routeSearch(mStartPoi,mEndPoi,TYPE_DRIVE);
        }
        if(v == change){
            Poi temp=mStartPoi;
            mStartPoi=mEndPoi;
            mEndPoi=temp;
            updateEditUI();
            routeSearch(mStartPoi,mEndPoi, mSelectedType);
        }
        if(v == startbtn || v == startbtn1){
            Log.d("mTvRoute","进入导航界面");
            if(mLatLng == null){
                Toast.makeText(getActivity(),"导航失败",Toast.LENGTH_SHORT).show();
                return;
            }
            if(mClickPointLatLng == null){
                Toast.makeText(getActivity(),"请选择目的地",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(),WalkRouteNaviAcitivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("startLatLng", mLatLng);
            bundle.putParcelable("stopLatLng", mClickPointLatLng);

            intent.putExtra("params", bundle);
            startActivity(intent);
        }
        if(v == pathlayout ){
            onPathClick(0);
        }
        if(v == pathlayout1 ){
            onPathClick(1);
        }
        if(v == pathlayout2 ){
            onPathClick(2);
        }
//搜索附近
        if(v==mFrequentView){
            Log.d("mFrequentView","mFrequentView进来了");
            Intent intent = new Intent(getActivity(),SearchActivity.class);
            Bundle bundle=new Bundle();
            startActivity(intent);
            return;
        }

    }
    private int getSheetHeadHeight(){
        mSheetHeadLayout.measure(0,0);
        Log.d("czh",mSheetHeadLayout.getMeasuredHeight()+"height");
        return mSheetHeadLayout.getMeasuredHeight();
    }

    private int getTopLayoutHeight(){
//        RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) mTopLayout.getLayoutParams();
        Log.d("czh",mTopLayoutHeight+"top height");
        return mTopLayout.getHeight();
    }

    //路径规划
    private void routeSearch(Poi startPoi,Poi targetPoi, int type){
        if(startPoi == null || targetPoi == null){
            return;
        }
        LatLng start = startPoi.getCoordinate();
        LatLng target = targetPoi.getCoordinate();

        RouteSearch routeSearch = new RouteSearch(getActivity());
        routeSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapServicesUtil.convertToLatLonPoint(start),AMapServicesUtil.convertToLatLonPoint(target));
        switch (type){
            case TYPE_DRIVE:
                RouteSearch.DriveRouteQuery dquery=new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST,null,null,"");
                routeSearch.calculateDriveRouteAsyn(dquery);
                break;
            case TYPE_BUS:
                RouteSearch.BusRouteQuery bquery=new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT,
                        mCity,0);
                routeSearch.calculateBusRouteAsyn(bquery);
                break;
            case TYPE_WALK:
                RouteSearch.WalkRouteQuery wquery=new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WALK_DEFAULT );
                routeSearch.calculateWalkRouteAsyn(wquery);
                break;
            case TYPE_RIDE:
                RouteSearch.RideRouteQuery rquery=new RouteSearch.RideRouteQuery(fromAndTo, RouteSearch.RIDING_DEFAULT );
                routeSearch.calculateRideRouteAsyn(rquery);
                break;
            default:
                break;
        }
    }

    //跳转用户
    @Override
    public void onUserClick(View v) {
//        aMap = null;
//        NavController controller = Navigation.findNavController(v);
//        controller.navigate(R.id.action_homeFragment_to_userFragment);
        startActivity(new Intent(getActivity(), UserActivity.class));
    }

    @Override
    public void onSearchClick() {
// 显示搜索layout,隐藏地图图层,并设置当前地图操作模式
        showSearchTipView();
        hideMapView();
//        mMapMode = MapMode.SEARCH;
    }
    /**
     * 展示搜索layout
     */
    private void showSearchTipView(){
        mgo.setVisibility(View.GONE);
        mLLSearchContainer.setVisibility(View.VISIBLE);
        InputMethodUtils.showInput(getActivity(),mEtSearchTip);

    }

    @Override
    public void onTrafficChanged(boolean selected) {
        aMap.setTrafficEnabled(selected);
    }

    private void addPOIMarker(LatLng latLng) {
        aMap.clear();
        addMarker(mLatLng);
        mSensorHelper.setCurrentMarker(mLocMarker);
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(latLng);
        markOptiopns.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_mark));
        aMap.addMarker(markOptiopns);
    }

    /**
     * 隐藏搜索提示布局
     */
    private void hideSearchTipView(){
        InputMethodUtils.hideInput(getActivity());
        mgo.setVisibility(View.VISIBLE);
        mLLSearchContainer.setVisibility(View.GONE);
        mEtSearchTip.setVisibility(View.VISIBLE);
        mEtSearchTip.setFocusable(true);
        mEtSearchTip.setFocusableInTouchMode(true);
        mSearchData.clear();
        mSearchAdapter.notifyDataSetChanged();
        mEtSearchTip.setText("");
    }


    /**
     * 显示poi点击底部BottomSheet
     */
    private void showClickPoiDetail(LatLng latLng, String poiName) {
        mPoiName = poiName;
        mTvLocTitle.setText(poiName);
        String distanceStr = MyAMapUtils.calculateDistanceStr(mLatLng, latLng);
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail(poiName, String.format("距离您%s", distanceStr));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText(poiName);
            mTvLocation.setText(String.format("距离您%s", distanceStr));
        }
    }


    /**
     * 移动地图中心点到指定位置
     * @param latLng
     */
    private void animMap(LatLng latLng){
        if(latLng != null){
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));
        }
    }

    /**
     * 添加POImarker
     */
    private void addPOIMarderAndShowDetail(LatLng latLng,String poiName){
        animMap(latLng);
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        //当前没有正在定位才能修改状态
        if (!mFirstLocation) {
            gpsView.setGpsState(mCurrentGpsState);
        }
        mMoveToCenter = false;
        // 添加marker标记
        addPOIMarker(latLng);
        showClickPoiDetail(latLng, poiName);
    }

    @Override
    public void onPOIClick(Poi poi) {
        Log.d("poi","poi点击");
        if(poi == null || poi.getCoordinate() == null || TextUtils.isEmpty(poi.getName())){
            return;
        }

        mEndPoi = poi;
        mClickPointLatLng = poi.getCoordinate();// 当前点击坐标
        isPoiClick = true;// 当前正在处理poi点击
        mTargetText.setText(poi.getName());
        if(isTvGo == true){
            routeSearch(mStartPoi,mEndPoi,TYPE_DRIVE);
        }else{
            addPOIMarderAndShowDetail(poi.getCoordinate(), poi.getName());
        }


        Log.d("position3", String.valueOf(poi.getName()));
        Log.d("position3", String.valueOf(poi.getCoordinate()));
        mgo.setVisibility(View.GONE);

    }


    @Override
    public void showPoiDetail(String locTitle, String locInfo) {
        gpsView.setVisibility(View.VISIBLE);

        mBottomSheet.setVisibility(View.VISIBLE);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mPoiDetailTaxi.setVisibility(View.VISIBLE);
        //我的位置
        mTvLocTitle.setText(locTitle);
        mTvLocation.setText(locInfo);
        int poiTaxiHeight = getResources().getDimensionPixelSize(R.dimen.setting_item_large_height);

        mBehavior.setHideable(true);
        mBehavior.setPeekHeight(mMinPeekHeight + poiTaxiHeight);
        mgo.setVisibility(View.GONE);
    }

    /**
     * 隐藏底部POI详情
     */
    @Override
    public void hidePoiDetail() {
        mBottomSheet.setVisibility(View.GONE);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mPoiDetailTaxi.setVisibility(View.GONE);
    }

    @Override
    public void showBackToMapState() {
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottom.STATE_MAP);
    }

    @Override
    public void showPoiDetailState() {
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottom.STATE_DETAIL);
    }

    @Override
    public void minMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == mMaxPeekHeight) {
            return;
        }
        lp.bottomMargin = mMaxPeekHeight;
        mapView.setLayoutParams(lp);
    }

    @Override
    public void maxMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == 0) {
            return;
        }
        lp.bottomMargin = 0;
       mapView.setLayoutParams(lp);
    }

    @Override
    public void onPoiDetailCollapsed() {
//BottomSheet折叠：显示头部搜索、隐藏反馈、显示右边侧边栏
//        mgo.setVisibility(View.VISIBLE);
        mPoiColseView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        gpsView.setVisibility(View.VISIBLE);
        mTrafficView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPoiDetailExpanded() {
//BottomSheet展开：隐藏头部搜索、显示反馈、隐藏右边侧边栏
        mgo.setVisibility(View.GONE);
        mMapHeaderView.setVisibility(View.GONE);
        gpsView.setVisibility(View.GONE);
        mTrafficView.setVisibility(View.GONE);
    }
    /**
     * 地图平滑上移，重置新的marker
     */

    @Override
    public void smoothSlideUpMap() {
        Log.d("upBottom","地图平滑上移");
        switch (gpsView.getGpsState()){
            case GPSView.STATE_ROTATE:
                if(!isPoiClick){
                    mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                }
                break;
                case GPSView.STATE_UNLOCKED:
                case GPSView.STATE_LOCKED:
                    if(!isPoiClick){
                        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                    }
                    break;
        }
        setLocationStyle();
        aMap.getUiSettings().setAllGesturesEnabled(false);
        if(!isPoiClick){
            mMoveToCenter = true;
        }else{
            mMoveToCenter = false;
        }
        ViewGroup.LayoutParams lp = mapView.getLayoutParams();
        lp.height = mScreenHeight * 2 / 5;
        mapView.setLayoutParams(lp);
        Log.d("upBottom","地图设为2/5");
    }

    @Override
    public void smoothSlideDownMap() {
        Log.d("upBottom","地图滑下来了");
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mMoveToCenter = false;
        slideDown = true;
        ViewGroup.LayoutParams lp = mapView.getLayoutParams();
        lp.height = mScreenHeight;
        mapView.setLayoutParams(lp);
        //启用手势操作
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        switch (gpsView.getGpsState()) {
            case GPSView.STATE_ROTATE:
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                break;
            case GPSView.STATE_UNLOCKED:
            case GPSView.STATE_LOCKED:
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                break;
        }
        setLocationStyle();
//        resetLocationMarker();
        mMoveToCenter = false;
    }

    /**
     * 地图手势事件回调：单指双击
     *
     * @param v
     * @param v1
     */
    @Override
    public void onDoubleTap(float v, float v1) {
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        gpsView.setGpsState(mCurrentGpsState);
        setLocationStyle();
        resetLocationMarker();
        mMoveToCenter = false;
    }

    @Override
    public void onSingleTap(float v, float v1) {

    }

    @Override
    public void onFling(float v, float v1) {

    }

    /**
     * 地图手势事件回调：单指滑动
     *
     * @param v
     * @param v1
     */
    @Override
    public void onScroll(float v, float v1) {
        //避免重复调用闪屏，当手指up才重置为false
        if (!onScrolling) {
            onScrolling = true;
            //旋转不移动到中心点
            mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
            mCurrentGpsState = STATE_UNLOCKED;
            //当前没有正在定位才能修改状态
            if (!mFirstLocation) {
                gpsView.setGpsState(mCurrentGpsState);
            }
            mMoveToCenter = false;
            setLocationStyle();
            resetLocationMarker();
        }
    }

    @Override
    public void onLongPress(float v, float v1) {

    }

    @Override
    public void onDown(float v, float v1) {

    }

    @Override
    public void onUp(float v, float v1) {
        onScrolling = false;
    }

    @Override
    public void onMapStable() {

    }

    @Override
    public void onDetailClick() {
        int state = mPoiDetailTaxi.getPoiDetailState();
        switch (state) {
            case PoiDetailBottom.STATE_DETAIL:
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //minMapView();
                break;
            case PoiDetailBottom.STATE_MAP:
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Log.d("position2",mCity);
        Log.d("position2","条目点击");

        if(mSearchData != null && mSearchData.size() > 0){
            Tip tip = mSearchData.get(position);
            if(tip == null){
                return;
            }
            Log.d("position2", String.valueOf(tip));
            Log.d("position2", String.valueOf(tip.getPoint()));

            hideSearchTipView();
            showMapView();
            mMoveToCenter = false;
            isPoiClick = true;
            LatLonPoint point = tip.getPoint();
            if( point!= null){
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                addPOIMarderAndShowDetail(latLng, tip.getName());
                showClickPoiDetail(latLng, tip.getName());
                mClickPointLatLng = latLng;
                mEndPoi = new Poi(tip.getName(),latLng,"");
                mTargetText.setText(mEndPoi.getName());
                Log.d("poi", mEndPoi.getName());
                Log.d("position2", "到这里了");
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s == null || TextUtils.isEmpty(s.toString())){
            mSearchProgressBar.setVisibility(View.GONE);
            return;
        }
        String content = s.toString();
        if(!TextUtils.isEmpty(content) && ! TextUtils.isEmpty(mCity)){
            // 调用高德地图搜索提示api
            InputtipsQuery inputquery = new InputtipsQuery(content, mCity);
            inputquery.setCityLimit(false);
            Inputtips inputTips = new Inputtips(getActivity(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
            mSearchProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        mSearchProgressBar.setVisibility(View.GONE);
        if(list == null || list.size() == 0){
            return;
        }
        mSearchData.clear();
        mSearchData.addAll(list);
        // 刷新RecycleView
        mSearchAdapter.notifyDataSetChanged();

    }

    private void initTabLayout(){
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.route_plan_drive));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.route_plan_ride));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.route_plan_walk));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.route_plan_bus));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                mTabLayout.selectTab(mTabLayout.getTabAt(0));
                if (getString(R.string.route_plan_walk).equals(tab.getText())){
                    Log.d("tab","进入步行");
                    mSelectedType =TYPE_WALK;
                    if (mEndPoi==null){
                        return;
                    }
                    Location location=aMap.getMyLocation();
                    routeSearch(mStartPoi,mEndPoi,TYPE_WALK);
                }
                else if (getString(R.string.route_plan_drive).equals(tab.getText())){
                    Log.d("tab","进入驾车");
                    mSelectedType =TYPE_DRIVE;
                    if (mEndPoi==null){
                        return;
                    }
                    Location myLocation=aMap.getMyLocation();
                    routeSearch(mStartPoi,mEndPoi,TYPE_DRIVE);
                }
                else if (getString(R.string.route_plan_ride).equals(tab.getText())) {
                    Log.d("tab","进入骑行");
                    mSelectedType = TYPE_RIDE;
                    if (mEndPoi == null) {
                        return;
                    }
                    Location location=aMap.getMyLocation();
                    routeSearch(mStartPoi,mEndPoi,TYPE_RIDE);
                }else if (getString(R.string.route_plan_bus).equals(tab.getText())){
                    Log.d("tab","进入公交");
                        mSelectedType =TYPE_BUS;
                        if (mEndPoi==null){
                            return;
                        }
                        routeSearch(mStartPoi,mEndPoi,TYPE_BUS);
                    }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        if (i==1000){
            Log.d("select","进入bus界面");
            if (busRouteResult != null && busRouteResult.getPaths() != null){
                if (busRouteResult.getPaths().size() > 0){
                    if (mTopLayout.getVisibility()!=View.VISIBLE){
                        ViewAnimUtils.dropDownWithInterpolator(mTopLayout, new ViewAnimUtils.AnimEndListener() {
                            @Override
                            public void onAnimEnd() {
                                mTopLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    mNesteScrollView.setVisibility(View.GONE);

                    mBusRouteResult = busRouteResult;
                    mBusResultAdapter=new BusResultListAdapter(mContext,busRouteResult);
                    mBusResultRview.setAdapter(mBusResultAdapter);
                    ViewAnimUtils.popupinWithInterpolator(mBusResultRview, new ViewAnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimEnd() {
                            mBusResultRview.setVisibility(View.VISIBLE);
                        }
                    });
//                    drawBusRoutes(mBusRouteResult,mBusRouteResult.getPaths().get(0));
                }else if (busRouteResult != null && busRouteResult.getPaths() == null) {
                    Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext,R.string.poi_search_error,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i==1000){
            Log.d("select","进入drive界面");
            if (driveRouteResult != null && driveRouteResult.getPaths() != null){
                if (driveRouteResult.getPaths().size() > 0){
                    updateUiAfterRouted();
                    mDriveRouteResult=driveRouteResult;

                    DrivePath path=mDriveRouteResult.getPaths().get(0);
                    mDrivePathAdapter=new DrivePathAdapter(getActivity(),path.getSteps());
                    Log.d("step", String.valueOf(mDrivePathAdapter));
                    mPathDetailRecView.setAdapter(mDrivePathAdapter);
                    Log.d("step", String.valueOf(mPathDetailRecView));
                    mPathDetailRecView.setVisibility(View.VISIBLE);

                    mPathTipsText.setText(getString(R.string.route_plan_path_traffic_lights,path.getTotalTrafficlights()+""));
                    mPathTipsText.setVisibility(View.VISIBLE);
                    drawDriveRoutes(mDriveRouteResult,path);

                    for (int j=0;j<mDriveRouteResult.getPaths().size();j++){
                        updatePathGeneral(mDriveRouteResult.getPaths().get(j),j);
                    }

                    mBehavior2.setPeekHeight(getSheetHeadHeight());
                }else if (driveRouteResult != null && driveRouteResult.getPaths() == null) {
                    Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext,R.string.poi_search_error,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        Log.d("Search", String.valueOf(i));
        if (i==1000){
            if (walkRouteResult != null && walkRouteResult.getPaths() != null){
                if (walkRouteResult.getPaths().size() > 0){
                    updateUiAfterRouted();

                    mWalkRouteResult = walkRouteResult;
                    WalkPath path=mWalkRouteResult.getPaths().get(0);
                    mWalkStepAdapter=new WalkStepAdapter(mContext,path.getSteps());
                    mPathDetailRecView.setAdapter(mWalkStepAdapter);
//                    mPathDetailRecView.setVisibility(View.VISIBLE);
                    drawWalkRoutes(mWalkRouteResult,mWalkRouteResult.getPaths().get(0));

                    for (int j=0;j<mWalkRouteResult.getPaths().size();j++){
                        updatePathGeneral(mWalkRouteResult.getPaths().get(j),j);
                    }

                    mBehavior2.setPeekHeight(getSheetHeadHeight());
                }else if (walkRouteResult.getPaths() == null) {
                    Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext,"距离过长，不支持步行导航，建议驾车、骑行、公交出行",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        if (i==1000){
            if (rideRouteResult != null && rideRouteResult.getPaths() != null){
                if (rideRouteResult.getPaths().size() > 0){
                    updateUiAfterRouted();
                    mRideRouteResult = rideRouteResult;

                    RidePath path=mRideRouteResult.getPaths().get(0);
                    mRideStepAdapter=new RideStepAdapter(mContext,path.getSteps());
                    mPathDetailRecView.setVisibility(View.VISIBLE);
                    drawRideRoutes(mRideRouteResult,mRideRouteResult.getPaths().get(0));

                    for (int j=0;j<mRideRouteResult.getPaths().size();j++){
                        updatePathGeneral(mRideRouteResult.getPaths().get(j),j);
                    }

                    mBehavior2.setPeekHeight(getSheetHeadHeight());
                }else if (rideRouteResult != null && rideRouteResult.getPaths() == null) {
                    Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(mContext,R.string.no_result,Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext,R.string.poi_search_error,Toast.LENGTH_LONG).show();
        }
    }

    private void hideRouted(){
        mTopLayout.setVisibility(View.GONE);
        mNesteScrollView.setVisibility(View.GONE);
    }

    private void onPathClick(int i){
        switch (mSelectedType){
            case TYPE_DRIVE:
                mPathTipsText.setText(getString(R.string.route_plan_path_traffic_lights,mDriveRouteResult.getPaths().get(i).getTotalTrafficlights()+""));
                mPathDetailRecView.setAdapter(new DrivePathAdapter(mContext,mDriveRouteResult.getPaths().get(i).getSteps()));
                drawDriveRoutes(mDriveRouteResult,mDriveRouteResult.getPaths().get(i));
                break;
            case TYPE_WALK:
                mPathDetailRecView.setAdapter(new WalkStepAdapter(mContext,mWalkRouteResult.getPaths().get(i).getSteps()));
                drawWalkRoutes(mWalkRouteResult,mWalkRouteResult.getPaths().get(i));
                break;
            case TYPE_RIDE:
                mPathDetailRecView.setAdapter(new RideStepAdapter(mContext,mRideRouteResult.getPaths().get(i).getSteps()));
                drawRideRoutes(mRideRouteResult,mRideRouteResult.getPaths().get(i));
                break;
            default:
                break;
        }
    }

    private void updateEditUI(){
        if (mStartPoi==null){
            mFromText.setText("");
//            mFromText.setText(getString(R.string.poi_search_my_location));
        }else {
            mFromText.setText(mStartPoi.getName());
        }
        if (mEndPoi==null){
            mTargetText.setText("");
        }else {
            mTargetText.setText(mEndPoi.getName());
        }
    }

    private void updateUiAfterRouted(){
        if (mTopLayout.getVisibility()!=View.VISIBLE){
            ViewAnimUtils.dropDownWithInterpolator(mTopLayout, new ViewAnimUtils.AnimEndListener() {
                @Override
                public void onAnimEnd() {
                    mTopLayout.setVisibility(View.VISIBLE);
                }
            });
        }
        if (mBusResultRview.getVisibility()==View.VISIBLE){
            ViewAnimUtils.popupoutWithInterpolator(mBusResultRview, new ViewAnimUtils.AnimEndListener() {
                @Override
                public void onAnimEnd() {
                    mBusResultRview.setVisibility(View.GONE);

                }
            });
        }
        hideMapView2();
        mNaviText.setVisibility(View.VISIBLE);
        mNesteScrollView.setVisibility(View.VISIBLE);
    }
    //路线基类
    private void drawDriveRoutes(DriveRouteResult driveRouteResult, DrivePath path){
        aMap.clear();
        final DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                mContext, aMap, path,
                driveRouteResult.getStartPos(),driveRouteResult.getTargetPos(),
                null);
        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
        drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap();
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomWithPadding(getTopLayoutHeight(),getSheetHeadHeight());
    }

    private void drawBusRoutes(BusRouteResult busRouteResult, BusPath path){
        aMap.clear();
        BusRouteOverlay busRouteOverlay = new BusRouteOverlay(
                mContext, aMap,path,busRouteResult.getStartPos(),
                busRouteResult.getTargetPos());
        busRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
        busRouteOverlay.removeFromMap();
        busRouteOverlay.addToMap();
        busRouteOverlay.zoomWithPadding(getTopLayoutHeight(),getSheetHeadHeight());
    }

    private void drawWalkRoutes(WalkRouteResult walkRouteResult, WalkPath path){
        aMap.clear();
        WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                mContext, aMap,path,walkRouteResult.getStartPos(),
                walkRouteResult.getTargetPos());
        walkRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
        walkRouteOverlay.removeFromMap();
        walkRouteOverlay.addToMap();
        walkRouteOverlay.zoomWithPadding(getTopLayoutHeight(),getSheetHeadHeight());
    }

    private void drawRideRoutes(RideRouteResult rideRouteResult, RidePath path){
        aMap.clear();
        RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                mContext, aMap,path,rideRouteResult.getStartPos(),
                rideRouteResult.getTargetPos());
        rideRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
        rideRouteOverlay.removeFromMap();
        rideRouteOverlay.addToMap();
        rideRouteOverlay.zoomWithPadding(getTopLayoutHeight(),getSheetHeadHeight());
    }

    private void updatePathGeneral(Path path, int i){
        String dur = AMapUtil.getFriendlyTime((int) path.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) path.getDistance());
        if (i==0){
            mPathDurText.setText(dur);
            mPathDisText.setText(dis);
            mPathLayout.setVisibility(View.VISIBLE);
            mPathLayout1.setVisibility(View.GONE);
            mPathLayout2.setVisibility(View.GONE);
        }else if (i==1){
            mPathDurText1.setText(dur);
            mPathDisText1.setText(dis);
            mPathLayout.setVisibility(View.VISIBLE);
            mPathLayout1.setVisibility(View.VISIBLE);
            mPathLayout2.setVisibility(View.GONE);

        }else if (i==2){
            mPathDurText2.setText(dur);
            mPathDisText2.setText(dis);
            mPathLayout.setVisibility(View.VISIBLE);
            mPathLayout1.setVisibility(View.VISIBLE);
            mPathLayout2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(BusPath busPath) {
        drawBusRoutes(mBusRouteResult,busPath);
    }


    /**
     * 地图模式
     */
    private enum MapMode{
        /**
         * 普通模式:显示地图图层
         */
        NORMAL,

        /**
         * 搜索模式:显示搜索提示和搜索结果
         */
        SEARCH
    }

    /**
     * 搜索ViewHolder
     */
    private static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchTitle;
        TextView tvSearchLoc;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchLoc = itemView.findViewById(R.id.tv_search_loc);
        }
    }


    /**
     * 搜索Adapter
     */
    private  class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements View.OnClickListener{

        private List<Tip> mData;
        private OnItemClickListener mListener;


        public SearchAdapter(List<Tip> data){
            this.mData = data;
        }


        /**
         * 设置RecycleView条目点击
         * @param listener
         */
        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }


        @Override
        public void onClick(View v) {

        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = ((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_tip_recycle, parent, false);
            itemView.setTag(viewType);
            itemView.setOnClickListener(this);
            return new SearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {
//            Log.d("position", String.valueOf(position));
            Tip tip = mData.get(position);
            holder.tvSearchTitle.setText(tip.getName());
            holder.tvSearchLoc.setText(tip.getAddress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mListener.onItemClick(view,position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mData != null && mData.size() > 0){
                return mData.size();
            }
            return 0;
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
    /**
     * 选点返回处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectPoiEvent(PoiItemEvent event){
        PoiItem item=event.getItem();
        LatLonPoint point=item.getLatLonPoint();
        LatLng latLng=new LatLng(point.getLatitude(),point.getLongitude());
        if (event.getFrom()== PoiSearchActivity.FROM_START){
            mStartPoi=new Poi(item.getTitle(),latLng,item.getAdName());
        }else if (event.getFrom()==PoiSearchActivity.FROM_TARGET){
            mEndPoi=new Poi(item.getTitle(),latLng,item.getAdName());
        }
        updateEditUI();
//        goToPlaceAndMark(item);

        mPoiTitleText.setText(item.getTitle());
        mPoiDescText.setText(item.getAdName()+"    "+item.getSnippet());

        if (mStartPoi==null || mEndPoi==null){
            return;
        }
        routeSearch(mStartPoi,mEndPoi,mSelectedType);
    }



    /**
     * 点击我的位置处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectedMyPoiEvent(SelectedMyPoiEvent event){
        Location location=aMap.getMyLocation();
        if (event.getFrom()==PoiSearchActivity.FROM_START){
            mStartPoi=new Poi(getString(R.string.poi_search_my_location),
                    new LatLng(location.getLatitude(),location.getLongitude()),"");
        }else if (event.getFrom()==PoiSearchActivity.FROM_TARGET){
            mEndPoi=new Poi(getString(R.string.poi_search_my_location),
                    new LatLng(location.getLatitude(),location.getLongitude()),"");
        }
        updateEditUI();
    }

    //检查软件是否安装
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
}