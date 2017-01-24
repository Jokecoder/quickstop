package com.fighter.quickstop.entrance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.fighter.quickstop.R;
import com.fighter.quickstop.findcarpos.Findcarpos;
import com.fighter.quickstop.utils.CheckCookie;


public class Welcome extends AppCompatActivity {
    private SharedPreferences sp;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//初始化百度地图api

        setContentView(R.layout.activity_welcome);

        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        CheckCookie.setShared(sp);
        if(!CheckCookie.hasCookie()){
            intent = new Intent(Welcome.this,LoginActivity.class);
        }
        else {
            intent = new Intent(Welcome.this, Findcarpos.class);
        }
        Runnable runnable=new Runnable(){//新建一个线程
            @Override
            public void run() {
                startActivity(intent);//开始跳转
                finish();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 1500);//等待1.5秒之后执行跳转，这个数值可以自己修改。
    }

    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
