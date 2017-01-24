
package com.fighter.quickstop.distance;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.fighter.quickstop.R;
import com.fighter.quickstop.myorderlist.Myorderlist;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.Tools;

//定时器定位类
public class DistanceService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getLocalinfo();
		openLocationTask();
		return super.onStartCommand(intent, flags, startId);
	}

	public IBinder onBind(Intent intent) {
		return null;
	}
	ArrayList<String> data=new ArrayList<String>();
	//错误标记
	private  static String TAG ="locationApplicationBeanError";
	//时间间隔
	String url="reach";
	String netdata;//
	String sstarttime;
	private String message;//从服务器返回的消息
	String createdtime;
	String reachtime;//到达时间
	private static int myTime = 5*1000 ;
	private LocationClient mLocationClient = null;  //定位类
	private String mData;  //获取的数据
	//定位时间间隔
	private int myLocationTime = 5*1000;
	MyLocationData locData;//我的位置信息
	//是否启动了定位API
	private boolean isOpenLocation = false;
	//是否启动了定位线程
	private boolean isOpenLocationTask = false;
	//经纬度
	private double jingweidu[] = new double[2];
	private double dislat;
	private double dislong;
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient(this);//初始化
		getLocalinfo();
		super.onCreate();
	}
	private void getLocalinfo(){
		SharedPreferences sp=getApplicationContext().getSharedPreferences("Locinfo",MODE_PRIVATE);
		int r=0;
		r= Tools.getArray(sp,"Locinfo",data);
		if(r==1){//读取正确
			try {
				//将获取到的信息转换成该要的格式
				dislat=Double.valueOf(data.get(0));//维度
				dislong=Double.valueOf(data.get(1));//经度
				sstarttime=data.get(2);
				createdtime=data.get(3);
				if(dislat==0){//本地数据为空
					closeLocationTask();
					stopSelf();
				}
			} catch (NumberFormatException e)
			{
				dislat=0;
				dislong=0;
				Log.i(TAG, "空值异常"+e.toString());
			} catch(Exception e)
			{
				Log.i(TAG, "获取异常"+e.toString());
			}
		}else{
			Log.i(TAG, "读取本地信息错误");
		}

	}

	private void clearLocalinf(){//清空本地的inf
		SharedPreferences sp=getApplicationContext().getSharedPreferences("Locinfo",MODE_PRIVATE);
		ArrayList<String> data=new ArrayList<String>();

		data.add(0+"");
		data.add(0+"");
		data.add(0+"");
		data.add(0+"");
		Tools.saveArray(sp,"Locinfo",data);
	}

	/**
	 * start定位
	 */
	private void startLocation()
	{
		try {
			if(!isOpenLocation)  //如果没有打开
			{
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);
				option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
				option.setCoorType("bd09ll");
				option.setScanSpan(5000);
				option.setIsNeedAddress(true);
				option.setNeedDeviceDirect(true);
				mLocationClient.setLocOption(option);//设置配置
				mLocationClient.registerLocationListener(myListener);
				mLocationClient.start();  //打开定位
				isOpenLocation = true;  //标识为已经打开了定位
			}
		} catch (Exception e) {
			Log.i(TAG, "打开定位异常"+e.toString());
		}
	}
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation == null)
				return;
			locData = new MyLocationData.Builder()
					.accuracy(bdLocation.getRadius())
					.direction(10).latitude(bdLocation.getLatitude())
					.longitude(bdLocation.getLongitude()).build();
			Log.i(TAG, "得到了数据");
		}
	};
	/**
	 * end 定位
	 */
	private void closeLocation()
	{
		try {
			mLocationClient.stop();  //结束定位
			isOpenLocation = false;  //标识为已经结束了定位
		} catch (Exception e) {
			Log.i(TAG, "结束定位异常"+e.toString());
		}
	}

	/***
	 * 定时器的回调函数
	 */
	private  Handler handler = new Handler() {
		//更新的操作
		@Override
		public void handleMessage(Message msg) {
			if(locData!=null){
				Log.i(TAG,"这是一个好东西 啊啊啊啊啊"+locData.longitude+"    "+locData.latitude);
			}
			checktime();
			Log.i(TAG,"调用了获取经纬度方法");
			super.handleMessage(msg);
		}
	};

	//检查位置距离
	private boolean checktime(){
		LatLng my=new LatLng(locData.latitude,locData.longitude);
		//LatLng my=new LatLng(39.7172520000,109.6021490000);
		LatLng dis=new LatLng(dislat,dislong);
		//LatLng dis=new LatLng(39.7181400000,109.6166660000);
		double juli;
//		juli=DistanceUtil.getDistance(my,dis);
		juli=10;
		if(juli>100){
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
			Date curDate=new Date(System.currentTimeMillis());//获取当前时间
			String str=formatter.format(curDate);
			reachtime=str.substring(0,16);

			int scht=Tools.getTime(sstarttime);//开始时间转换的int
			int reat=Tools.getTime(reachtime);//到达时间的int

			//if(reat-scht>=0){//开始前来到这里不会开始停车
				netdata=new String();
				new AsyncTask<String, Void, Void>() {
					@Override
					protected Void doInBackground(String... params) {
						try {
							HttpConnection httpConnection=new HttpConnection(params[0]);
							httpConnection.GetConnect();
							JSONObject jsonObject=new JSONObject();
							jsonObject.put("reachtime",params[3]);
							jsonObject.put("createdtime",params[2]);
							jsonObject.put("starttime",params[1]);
							httpConnection.LoadData(String.valueOf(jsonObject));
							if(httpConnection.getResponseCode()!=200){//失败

							}else{
								JSONObject object = new JSONObject(httpConnection.ReadData());
								message=object.getString("message");
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(Void aVoid) {//数据加载完成后
						if(message.equals("success")){
							NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
							NotificationCompat.Builder notification=new NotificationCompat.Builder(getBaseContext());
							notification.setSmallIcon(R.drawable.maker);
							//notification.setContentTitle("检测到当前位置和目标地点距离低于50米"+dislat+"  "+dislong);
							notification.setContentTitle("检测到已经到车位附近");
							notification.setContentText("已经自动开始停车");
							notification.setAutoCancel(true);
							notification.setDefaults(Notification.DEFAULT_ALL);
							Intent intent=new Intent(getBaseContext(), Myorderlist.class);

							PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
							notification.setContentIntent(contentIntent);
							nm.notify(0,notification.build());
							clearLocalinf();
						}
						else{
							closeLocationTask();
							Log.i(TAG,"股东大师"+"jkd");
							stopSelf();//关闭
						}
					}
				}.execute(url,sstarttime,createdtime,reachtime);
			//}
			return true;
		}
	else {
			return false;
		}
	};

	//定时器
	private Timer myLocationTimer = null;
	//定时线程
	private  TimerTask myLocationTimerTask = null;

	/***
	 * 初始化定时器
	 */
	private void initLocationTime()
	{
		if(myLocationTimer==null)
		{
			Log.i(TAG, "myLocationTimer 已经被清空了");
			myLocationTimer = new Timer();
		}else
		{
			Log.i(TAG, "myLocationTimer 已经存在");
		}
	}

	/***
	 * 初始化 定时器线程
	 */
	private void initLocationTimeTask()
	{
		myLocationTimerTask = new TimerTask() {
			/***
			 * 定时器线程方法
			 */
			@Override
			public void run() {
				handler.sendEmptyMessage(1); //发送消息
			}
		};
	}

	/***
	 * 初始化 time 对象 和 timetask 对象
	 */
	private void initLocationTimeAndTimeTask()
	{
		initLocationTime();
		initLocationTimeTask();
	}

	/***
	 * 销毁 time 对象 和 timetask 对象
	 */
	private void destroyLocationTimeAndTimeTask()
	{
		myLocationTimer = null;
		myLocationTimerTask = null;

	}

	@Override
	public void onDestroy() {
		closeLocationTask();
		Log.i(TAG, " 噢噢噢噢噢噢噢退出了 ");
		super.onDestroy();
	}

	/***
	 * 打开定位定时器线程
	 */
	public void openLocationTask()
	{

		try {


			if(!isOpenLocationTask)   ///如果不是打开状态，则打开线程
			{
				startLocation();//启动定位更新经纬度
				//开启定时器
				initLocationTimeAndTimeTask();  //初始化定时器和定时线程
				myLocationTimer.schedule(myLocationTimerTask, myTime, myTime);
				Log.i(TAG, " 打开了定位定时器线程 ");
				isOpenLocationTask = true;  //标记为打开了定时线程
			}else
			{
				Log.i(TAG, " 已经开启了定位定时器线程 ");
			}
		} catch (Exception e) {
			Log.i(TAG, "打开定位定时器线程 异常"+e.toString());
		}
	}

	/***
	 * 关闭定位定时器线程
	 */
	public void closeLocationTask()
	{
		try {
			if(isOpenLocationTask)  //如果是打开状态，则关闭
			{
				closeLocation();
				//关闭定时器
				myLocationTimer.cancel();
				destroyLocationTimeAndTimeTask();

				Log.i(TAG, " 关闭了定位定时器线程 ");
				isOpenLocationTask = false;  //标记为关闭了定时线程
			}else
			{
				Log.i(TAG, " 已经关闭了定位定时器线程 ");
			}

		} catch (Exception e) {
			Log.i(TAG, "关闭定位定时器线程异常: "+e.toString());
		}
	}

	/***
	 * 将经纬度保存在本地
	 * @return
	 */

	private int setLocalJingweidu()
	{
		int r = 0;
		try {
			//获取活动的 preferences 对象
			SharedPreferences usersetting = getApplicationContext().getSharedPreferences("test",Activity.MODE_PRIVATE);
			//获取编辑对象
			Editor userinfoeditor = usersetting.edit();
			userinfoeditor.putString("Location_longitude", jingweidu[0]+"");//经度
			userinfoeditor.putString("Location_latitude", jingweidu[1]+""); //纬度
			userinfoeditor.commit();
			return 1;

		} catch (Exception e) {
			Log.i(TAG,"  保存经纬度，到本地失败"+e.toString());
			r = 0;
		}
		return r;
	}

	/***
	 * 获取本地的经纬度
	 * @return
	 */
	private double[] getLocalJingweidu()
	{
		double[] jinweidu = new double[]{0,0};
		try {
			//获取活动的 preferences 对象
			SharedPreferences usersetting =  getApplicationContext().getSharedPreferences("test",Activity.MODE_PRIVATE);
			double longitude = Double.parseDouble(usersetting.getString("Location_longitude", "0")); //经度
			double latitude = Double.parseDouble(usersetting.getString("Location_latitude", "0"));  //纬度
			jinweidu[0] = longitude;
			jinweidu[1] = latitude;
		} catch (NumberFormatException e)
		{
			jinweidu = new double[]{0,0};
			Log.i(TAG, "空值异常"+e.toString());
		} catch(Exception e)
		{
			jinweidu = new double[]{0,0};
			Log.i(TAG, "获取异常"+e.toString());
		}
		return jinweidu;
	}
}
