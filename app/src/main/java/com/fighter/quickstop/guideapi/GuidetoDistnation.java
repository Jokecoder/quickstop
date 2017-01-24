package com.fighter.quickstop.guideapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.fighter.quickstop.R;

public class GuidetoDistnation extends Activity {
	private static final String TAG = "GuidetoDistnation";

	//导航相关
	private boolean mIsEngineInitSuccess = false;
	//导航引擎初始化监听
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
	        public void engineInitSuccess() {
	            //导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，
	        	//为true时候才能发起导航
	        	mIsEngineInitSuccess=true;
	        }
	        //引擎初始化开始
	        public void engineInitStart() {

	        }
	        //引擎初始化失败
	        public void engineInitFail() {

	        }
	    };
	private String getSdcardDir() {
	        if (Environment.getExternalStorageState().equalsIgnoreCase(
	                Environment.MEDIA_MOUNTED)) {
	            return Environment.getExternalStorageDirectory().toString();
	        }
	        return null;
	    }

	private int drivintResultIndex=0;//驾车路线方案index
	private int totalLine;
	private MapView mMapView;// 地图视图
	private BaiduMap mBaiduMap;// 地图控制器 setMapStatus(mMapStatusUpdate);
	private MapStatus mMapStatus;// 地图当前状态
	private MapStatusUpdate mMapStatusUpdate;// 地图将要变化成的状态
	private Button btn_location;// 定位button
	private Button btn_plan;// 搜索button
	private Button btn_navi;//导航button

	public LocationClient mLocationClient = null;// 定位的核心类:LocationClient
	public BDLocationListener myLocationListener = new MyLocationListener();// 定位的回调接口
	private LatLng mCurrentLatLng;// 当前经纬度坐标
	private double latitude;//纬度
	private double longitude;//经度
	private double distnationa;//维度
	private double distnationo;//经度
	private RoutePlanSearch routePlanSearch;
	private PoiSearch mPoiSearch;// poi检索核心类
	private MySearchResultListener mySearchResultListener;// poi检索核心接口
	private Button nextLineBtn;
	int mark=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息,传入ApplicationContext
		// 该方法要在setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guidetodist);
		//setContentView(R.layout.activity_hello);
		Intent intent=getIntent();
		distnationa=intent.getDoubleExtra("latitude",0);//获取传来的经纬度
		distnationo=intent.getDoubleExtra("longitude",0);
		init();

		btn_navi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mIsEngineInitSuccess){
					navi();
				}
			}
		});

	}

	OnGetRoutePlanResultListener routePlanResultListener=new OnGetRoutePlanResultListener() {
		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
			//步行
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
			//公交
		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {//驾车
			if(mark==1){
				mBaiduMap.clear();
				if (drivingRouteResult == null
						|| drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				}
				if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
					return;
				}
				if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {

					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							mBaiduMap);
					drivingRouteOverlay.setData(drivingRouteResult.getRouteLines()
							.get(drivintResultIndex));// 设置一条驾车路线方案
					mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					totalLine = drivingRouteResult.getRouteLines().size();
					Toast.makeText(GuidetoDistnation.this,
							"共查询出" + totalLine + "条符合条件的线路",Toast.LENGTH_LONG).show();
					if (totalLine > 1) {
						nextLineBtn.setEnabled(true);
						nextLineBtn.setVisibility(View.VISIBLE);
					}else{
						nextLineBtn.setVisibility(View.GONE);
					}
					mark=0;
				}
			}
		}
	};

		private void init() {
			// 获得地图控件引用
			mMapView = (MapView) findViewById(R.id.bmapView);
			// 获得地图控制器
			nextLineBtn=(Button)findViewById(R.id.nextLineBtn);
			mBaiduMap = mMapView.getMap();// MapView与BaiduMap一一对应

			// 定位核心类
			mLocationClient = new LocationClient(getApplicationContext());

			// 定位回调接口
			myLocationListener = new MyLocationListener();
			mBaiduMap.setMyLocationEnabled(true);

			//poi搜索核心类
			mPoiSearch = PoiSearch.newInstance();
			//poi搜索回调接口
			mySearchResultListener = new MySearchResultListener();
			// 搜索按钮
			//btn_plan = (Button) findViewById(R.id.planline);

			//导航按钮
			btn_navi=(Button) findViewById(R.id.navi);
			//导航引擎初始化接口。调用该接口会触发SDK鉴权流程
			BaiduNaviManager.getInstance().initEngine(this, getSdcardDir()
					, mNaviEngineInitListener, null);


	}

	public void startPlan(int indext){//开始导航

		DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
		LatLng point=new LatLng(latitude,longitude);
		drivingOption.from(PlanNode.withLocation(point));
		 LatLng pointt=new LatLng(distnationa,distnationo);
		drivingOption.to(PlanNode.withLocation(pointt));
		routePlanSearch.drivingSearch(drivingOption);// 发起驾车路线规划
	}
	/**
	 * 定位
	 */
	private void location() {
		// 设置mLocationClient数据,如是否打开GPS,使用LocationClientOption类.
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setOpenGps(true);// 打开GPS
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(myLocationListener);
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// mapView 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 获取服务器回传的当前经纬度
			latitude=location.getLatitude();
			longitude=location.getLongitude();
			mCurrentLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())// 获取定位精度
					.latitude(location.getLatitude())// 获取纬度坐标
					.longitude(location.getLongitude())// 获取精度坐标
					.build();
			mBaiduMap.setMyLocationData(locData);// 设置定位数据
			routePlanSearch=RoutePlanSearch.newInstance();
			startPlan(5);
			routePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
		}
	}



	public class MySearchResultListener implements OnGetPoiSearchResultListener {

		@Override
		public void onGetPoiDetailResult(final PoiDetailResult poiDetailResult) {
			if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
				// 检索失败
			} else {
				// 点击marker showInfoWindow
				mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					private BitmapDescriptor descriptor;

					@Override
					public boolean onMarkerClick(Marker arg0) {
						// 设置弹窗 (View arg0, LatLng arg1, int arg2) y 偏移量 ，
						Button btn = new Button(getApplicationContext());
						btn.setBackgroundColor(0xAA00FF00);
						btn.setText(poiDetailResult.getName());
						// btn 变成 View 图片
						descriptor = BitmapDescriptorFactory.fromView(btn);

						InfoWindow mInfoWindow = new InfoWindow(descriptor,
								poiDetailResult.getLocation(), -60,
								new OnInfoWindowClickListener() {

									public void onInfoWindowClick() {
										// 点击infoWindow可以触发二次检索,跳转界面
										// 隐藏弹窗！
										mBaiduMap.hideInfoWindow();
									}
								});
						mBaiduMap.showInfoWindow(mInfoWindow);
						return false;
					}
				});

			}
		}

		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			// 首先判断检索结果是否有错,在判断检索结果是否为空
			if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
				if (poiResult != null) {
					mBaiduMap.clear();
					// 绑定Overlay
					MyPoiOverlay poiOverlay = new MyPoiOverlay(mBaiduMap);
					mBaiduMap.setOnMarkerClickListener(poiOverlay);
					// 设置数据到overlay
					poiOverlay.setData(poiResult);
					poiOverlay.addToMap();
					// 缩放地图，使所有Overlay都在合适的视野内 注： 该方法只对Marker类型的overlay有效
					poiOverlay.zoomToSpan();
				}

			} else {

			}
		}
	}

	// 自定义PoiOverlay
	class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			return super.onPoiClick(index);
		}

	}
	/**
	 * 导航
	 */
	protected void navi() {
		//通过指定导航的起终点启动导航

		BaiduNaviManager.getInstance().launchNavigator(this
				, latitude
				, longitude
				, "当前位置"
				, distnationa
				, distnationo
				, "目的地"
				, NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME//算路方式
				, true
				, BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY//强制离线优先
				, new OnStartNavigationListener(){
					//启动导航的监听器，由开发者实现跳转到基于SDK自定义的[离线数据下载页]
					@Override
					public void onJumpToDownloader() {
					}
					//启动导航的监听器，由开发者实现跳转到基于SDK自定义的[导航页]
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent=new Intent(GuidetoDistnation.this,BNavigatorActivity.class);

						intent.putExtras(configParams);
						startActivity(intent);
					}
		});

	}

	@Override
	protected void onStart() {
		location();
		mLocationClient.start();// 开启定位请求
		super.onStart();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mLocationClient.stop();// 停止定位
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();
		mMapView.onDestroy();
		super.onDestroy();
	}
}
