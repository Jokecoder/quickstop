package com.fighter.quickstop.publish;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fighter.quickstop.R;
import com.fighter.quickstop.distance.DistanceService;
import com.fighter.quickstop.utils.CustomDialog;

/**
 * 在此页面中选择地图中的一点作为车位的大致位置
 * 在进入此页面时会自动定位到当前的位置
 * 可以在搜索框中输入大致位置，然后点击搜索按钮确定，地图会定位到所选择的点
 * 也可以直接拖拽地图，在地图上选取响应的点
 * 点击确定按钮，定位到当前选择的位置，退出此界面，将大致位置和经纬度信息传到上一个界面
 */

public class PublishAddLocationActivity extends Activity implements View.OnClickListener{

    private MapView mapView;   //地图
    private  BaiduMap baiduMap;//百度地图对象
    private  EditText add_location_searchtext;  //查找地图内容的编辑框
    private  Button add_location_searchbtn;       //确认查找内容的按钮
    private Button add_location_makesurebtn;    //确认此地址的按钮

    private int isShowOverlay = 1;//是否显示覆盖物 1-显示 0-不显示
    private boolean isFirstLoc = true;    //是否首次定位
    private LocationClient mLocClient;//定位SDK的核心类
    private MyLocationConfiguration.LocationMode mCurrentMode;// 定位图层显示模式 (普通-跟随-罗盘)
    private BitmapDescriptor mCurrentMarker = null;//定位图标描述
    private BitmapDescriptor bitmap;

    GeoCoder geoCoder = null;  //搜索模块
    private double latitude;  //经度
    private double longitude; //纬度
    private String addlocation;  //位置信息

    int condition=0;   //条件

    //定位SDK监听函数
    public MyLocationListenner locListener = new MyLocationListenner();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_add_location);
//        distanceService.closeLocationTask();
//        distanceService.onUnbind(this.getIntent());
        mapView= (MapView) findViewById(R.id.add_location_mapView);
        add_location_searchtext= (EditText) findViewById(R.id.add_location_searchtext);
        add_location_searchbtn= (Button) findViewById(R.id.add_location_searchbtn);
        add_location_makesurebtn= (Button) findViewById(R.id.add_location_makesurebtn);

        add_location_searchbtn.setOnClickListener(this);   //为搜索添加点击监听
        add_location_makesurebtn.setOnClickListener(this);   //为确认添加点击监听

        initBaiduMap();
    }

    public void initBaiduMap()
    {
        // 隐藏百度地图自身携带的缩放控件
        int childCount = mapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);


        //设置地图缩放级别16 类型普通地图
        baiduMap = mapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        baiduMap.setMapStatus(msu);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        baiduMap.setMyLocationEnabled(true);//开启定位图层，定位当前位置
        //定位初始化
        //注意: 实例化定位服务 LocationClient类必须在主线程中声明 并注册定位监听接口
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(locListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);              //打开GPS
        option.setCoorType("bd09ll");        //设置坐标类型
        option.setScanSpan(1000);            //设置发起定位请求的间隔时间为1000ms
        option.setAddrType("all");         //设置使其可以获取具体的位置，把精度纬度换成具体地址
        mLocClient.setLocOption(option);     //设置定位参数
        mLocClient.start();                  //调用此方法开始定位

        addCircleOverlay();

//        mSearch=GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(listener);

        // 创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
//        addMyLocation();

        /**对地图的点击事件
         * 点击地图上一点之后会出现一个地点的覆盖物
         * 并在搜索框中显示点击的地点
         */
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                GeoCoder getCoder = GeoCoder.newInstance();
                ReverseGeoCodeOption reCodeOption = new ReverseGeoCodeOption();
                reCodeOption.location(latLng);
                getCoder.reverseGeoCode(reCodeOption);
                getCoder.setOnGetGeoCodeResultListener(listener);
            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }

        });
    }

    /**
     * 定位SDK监听器
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || baiduMap == null) {//mapview 销毁后不在处理新接收的位置
                return;
            }
            //MyLocationData.Builder定位数据建造器
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            //设置定位数据
            baiduMap.setMyLocationData(locData);
            mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
            //获取经纬度
            //第一次定位的时候，那地图中心点显示为定位到的位置
            if (isFirstLoc) {
                addMyLocation();
                isFirstLoc = false;
                //地理坐标基本数据结构
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
                //MapStatusUpdate描述地图将要发生的变化
                //MapStatusUpdateFactory生成地图将要反生的变化
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(loc);
                baiduMap.animateMapStatus(msu);
                addlocation=location.getAddrStr();
                Toast.makeText(getApplicationContext(),addlocation, Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 定位并添加标注
     */
    private void addMyLocation() {
        //更新
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        baiduMap.clear();
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        baiduMap.addOverlay(option); //在地图上添加Marker，并显示
    }

    /**
     * 添加圆形覆盖物
     */
    private void addCircleOverlay() {
        if(isShowOverlay == 1) {  //点击显示
//            baiduMap.clear();     //清除地图上的其他覆盖物
            isShowOverlay = 0;
            //DotOptions 圆点覆盖物
            LatLng pt = new LatLng(latitude, longitude);
            CircleOptions circleOptions = new CircleOptions();
            //circleOptions.center(new LatLng(latitude, longitude));
            circleOptions.center(pt);                          //设置圆心坐标
            circleOptions.fillColor(0xAAFFFF00);               //圆填充颜色
            circleOptions.radius(5000);                         //设置半径
            circleOptions.stroke(new Stroke(5, 0xAA00FF00));   // 设置边框
            baiduMap.addOverlay(circleOptions);
        }
        else {
            baiduMap.clear();
            isShowOverlay = 1;
        }
    }

//    关闭软键盘
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(add_location_searchtext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onClick(View view)   {     //点击的回调函数
        switch (view.getId())
        {
            case R.id.add_location_searchbtn:
                String addr = add_location_searchtext.getText().toString();    //获取编辑框中输入的地址信息
                closeInputMethod();
                if (addr.equals(""))
                    Toast.makeText(this, "您输入的信息是空的，\n请输入有效地址", Toast.LENGTH_LONG).show();
                else {
                    // Geo搜索
                    geoCoder.geocode(new GeoCodeOption().city("青岛").address(addr));
                    add_location_makesurebtn.setVisibility(View.VISIBLE);//点击搜索按钮后确定的按钮出现
                }
                break;
            case R.id.add_location_makesurebtn:
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setMessage("您确定要将\""+addlocation+"\"作为您车位的位置吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent mainIntent = new Intent(PublishAddLocationActivity.this, PublishAddActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("longitude", longitude);
                        bundle.putDouble("latitude",latitude);
                        bundle.putString("location",addlocation);
                        mainIntent.putExtras(bundle);
                        setResult(5, mainIntent);
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);   //界面切换动画
                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
                break;
            default:break;
        }
    }


    //
    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        // 反地理编码查询结果回调函数
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                Toast.makeText(PublishAddLocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            }
            baiduMap.clear();
            baiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));//为定位点设置覆盖物
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            //将获取到的位置信息赋给全局变量
            longitude=result.getLocation().longitude;
            latitude=result.getLocation().latitude;
            addlocation=result.getAddress();

            add_location_makesurebtn.setVisibility(View.VISIBLE);//点击地之后确定的按钮出现
            add_location_searchtext.setText(addlocation);
            Toast.makeText(PublishAddLocationActivity.this, "位置：" + addlocation, Toast.LENGTH_LONG).show();
        }
        // 地理编码查询结果回调函数
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                Toast.makeText(PublishAddLocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            }
            baiduMap.clear();
            baiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));//为定位点设置覆盖物
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            //将获取到的位置信息赋给全局变量
            longitude=result.getLocation().longitude;
            latitude=result.getLocation().latitude;
            addlocation=result.getAddress();

            add_location_makesurebtn.setVisibility(View.VISIBLE);//点击搜索按钮后确定的按钮出现

            String strInfo = String.format("纬度：%f 经度：%f \n地址：%s",latitude,longitude,addlocation);  //经纬度
            Toast.makeText(PublishAddLocationActivity.this, strInfo, Toast.LENGTH_LONG).show();//广播的形式出现经纬度
        }
    };






    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mLocClient.stop();                       //退出时销毁定位
//        baiduMap.setMyLocationEnabled(false);   //关闭定位图层
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
}
