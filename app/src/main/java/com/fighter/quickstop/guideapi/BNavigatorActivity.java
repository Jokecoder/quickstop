package com.fighter.quickstop.guideapi;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.ui.widget.RoutePlanObserver.IJumpToDownloadListener;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

public class BNavigatorActivity extends Activity {
	
	//导航视图监听器
	private IBNavigatorListener mBNavigatorListener=new IBNavigatorListener() {
		
		@Override
		public void onYawingRequestSuccess() {
			// TODO 偏航请求成功			
		}
		
		@Override
		public void onYawingRequestStart() {
			// TODO 开始偏航请求			
		}
		
		@Override
		public void onPageJump(int jumpTiming, Object arg1) {
			// TODO 页面跳转回调
			if(IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END==jumpTiming){
				finish();
			}else if(IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL==jumpTiming){
				finish();
			}			
		}
		
		@Override
		public void notifyViewModeChanged(int arg0) {
			
		}
		
		@Override
		public void notifyStartNav() {
			
		}
		
		@Override
		public void notifySensorData(SensorData arg0) {
			
		}
		
		@Override
		public void notifyNmeaData(String arg0) {
			
		}
		
		@Override
		public void notifyLoacteData(LocData arg0) {
			
		}
		
		@Override
		public void notifyGPSStatusData(int arg0) {
			
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//创建NMapView
		MapGLSurfaceView nMapView = BaiduNaviManager.getInstance().createNMapView(this);
		//设置支持卫星视图
		nMapView.setSatellite(true);
		//创建导航视图
		View navigatorView = BNavigator.getInstance().init(BNavigatorActivity.this, getIntent().getExtras(), nMapView);
		//填充视图
		setContentView(navigatorView);
		
		BNavigator.getInstance().setListener(mBNavigatorListener);
		//初始化就绪后发起导航，分为算路后导航(百度地图)与直接发起导航(百度导航) 两种模式 
		BNavigator.getInstance().startNav();
		
		//初始化TTS
		BNTTSPlayer.initPlayer();
		//设置TTS回调
		BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener() {
			
			@Override
			public int playTTSText(String arg0, int arg1) {
				// TODO Auto-generated method stub
				return BNTTSPlayer.playTTSText(arg0, arg1);
			}
			
			@Override
			public void phoneHangUp() {
				// 手机挂断
			}
			
			@Override
			public void phoneCalling() {
				// 通话中
			}
			
			@Override
			public int getTTSState() {
				return BNTTSPlayer.getTTSState();
			}
		});
		
		
		
		BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this,
				new IJumpToDownloadListener() {
					
					@Override
					public void onJumpToDownloadOfflineData() {
						
					}
				}));
		
	};
	
	
	@Override  
    public void onResume() {  
        BNavigator.getInstance().resume();  
        super.onResume();  
        BNMapController.getInstance().onResume();  
    };  
 
    @Override  
    public void onPause() {  
        BNavigator.getInstance().pause();  
        super.onPause();  
        BNMapController.getInstance().onPause();  
    }  
 
    @Override  
    public void onConfigurationChanged(Configuration newConfig) {  
        BNavigator.getInstance().onConfigurationChanged(newConfig);  
        super.onConfigurationChanged(newConfig);  
    }  
 
    public void onBackPressed(){  
        BNavigator.getInstance().onBackPressed();  
    }  
 
    @Override  
    public void onDestroy(){  
        BNavigator.destory();  
        BNRoutePlaner.getInstance().setObserver(null);  
        super.onDestroy();  
    }

}
