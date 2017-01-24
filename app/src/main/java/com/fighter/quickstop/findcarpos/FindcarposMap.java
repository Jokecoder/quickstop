package com.fighter.quickstop.findcarpos;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.fighter.quickstop.R;
import com.fighter.quickstop.utils.CustomDialog;

import java.util.ArrayList;

/**
 * Created by zhuch on 2016/2/13.
 */
public class FindcarposMap extends Fragment {
    private BitmapDescriptor mIconMaker;
    MapView mapView = null;
    BaiduMap baiduMap = null;
    LocationClient locationClient = null;
    boolean isFirstLoc = true;
    RelativeLayout mMarkerInfoLy=null;
    View view=null;
    PopupWindow infopop;
    TextView locationText;
    TextView timeText;
    ImageView orderimage;
    TextView costPerHourText;
    Dialog dia;//
    TextView descriptionText;
    ArrayList<FindcarposObject> allpi;//所有发布了的信息
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.findcarpos_map, null);
       initDatas();
        initwidget();
        return view;
    }
    void initwidget(){//初始化控件
        mapView=(MapView)view.findViewById(R.id.findcarmapview);

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


        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        locationClient = new LocationClient(view.getContext());
        locationClient.registerLocationListener(myListener);
        this.setLocationOption();
        locationClient.start();
        addInfOverlay();//添加所有覆盖物
    }
void initDatas(){
    allpi=(ArrayList<FindcarposObject>)getArguments().getSerializable(Findcarpos.FCP_MAIN);//上个界面传递过来的所有的值
}
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(10).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
//                    mMarkerInfoLy.setVisibility(View.GONE);
                    if(dia!=null&&dia.isShowing()){
                        dia.cancel();
                    }
//                    baiduMap.hideInfoWindow();
                }
                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });
            baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    final FindcarposObject findcarposObject = (FindcarposObject) marker.getExtraInfo().get("fcppi");
                    View contents = View.inflate(getActivity(), R.layout.carpospoplayout, null);
                    locationText=(TextView)contents.findViewById(R.id.locationText);
                    locationText=(TextView)contents.findViewById(R.id.locationText);
                    locationText.setText(findcarposObject.getLocation());
                    timeText=(TextView)contents.findViewById(R.id.timeText);
                    timeText.setText(findcarposObject.getStarttime()+"到"+findcarposObject.getEndtime());
                    costPerHourText=(TextView)contents.findViewById(R.id.costPerHourText);
                    costPerHourText.setText(findcarposObject.getPrice()+"元/小时");
                    descriptionText=(TextView)contents.findViewById(R.id.descriptionText);
                    descriptionText.setText(findcarposObject.getDescription());

                    CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                    builder.setTitle("车位具体信息")
                            .setContentView(contents)
                            .setPositiveButton("预定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent intent = new Intent();
                                    intent.putExtra("username",findcarposObject.getUsername());
                                    intent.putExtra("createdtime",findcarposObject.getCreatedTime());
                                    intent.setClass(getActivity(), FindcarOrder.class);
                                    startActivity(intent);


                                }
                            });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dia.dismiss();
                        }
                    });
                    dia=builder.create();
                    dia.show();
                    //builder.create().show();

                    return false;
                }
            });
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
                baiduMap.animateMapStatus(u);
            }
        }
    };

    public void onDestroy() {
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    void setbackAlpha(float bgAlpha){
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha =bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.onPause();
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        locationClient.setLocOption(option);
    }
    public void addInfOverlay()
    {
        baiduMap.clear();
        LatLng latLng = null;
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        for (FindcarposObject findcarposObject :allpi)
        {
            // 位置
            latLng = new LatLng(findcarposObject.getLatitude(), findcarposObject.getLongitude());
            // 图标
            overlayOptions = new MarkerOptions().position(latLng)
                    .icon(mIconMaker).zIndex(5);
            marker = (Marker) (baiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("fcppi", findcarposObject);//传递对象
            marker.setExtraInfo(bundle);
        }
        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.setMapStatus(u);
    }

}
