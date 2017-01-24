package com.fighter.quickstop.findcarpos;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.baidu.mapapi.SDKInitializer;
import com.fighter.quickstop.GifView;
import com.fighter.quickstop.R;
import com.fighter.quickstop.lib.MenuFragment;
import com.fighter.quickstop.lib.Sliding;
import com.fighter.quickstop.lib.SlidingFragmentActivity;
import com.fighter.quickstop.myorderlist.Myorderlist;
import com.fighter.quickstop.publish.PublishListActivity;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuch on 2016/2/9.
 */

public class Findcarpos extends SlidingFragmentActivity {
    String url1="getdistination";
    String sstarttime;//开始时间
    String createdtime;//创建时间
    SharedPreferences locinfosp;//本地信息
    String netdata1;
    double dislat;
    double dislong;
    SharedPreferences sp;
    TextView textView_input;
    TextView textView_map;
    FrameLayout frame_findpos;
    LinearLayout fcpchoosebg;
    int isMap=1;
    ArrayList<FindcarposObject>  findcarposObjects =new ArrayList<FindcarposObject>();
    String netdata;
    private final String url="findcarpos";//获取所有发布的信息
    public static String FCP_MAIN="com.example.zhuch.quickstop.findcarpos.findcarpos";//主界面
    private Fragment mContent;

    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.findcarpos_);

        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);

        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        locinfosp=this.getSharedPreferences("Locinfo", Context.MODE_PRIVATE);
        get();
        initwidget();//初始各种控件
        getNetDatas();//获取网络数据

        // check if the content frame contains the menu frame
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu()
                    .setTouchModeAbove(Sliding.TOUCHMODE_MARGIN);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(Sliding.TOUCHMODE_NONE);
        }

        // set the Above View Fragment
        if (savedInstanceState != null) {
            mContent = getFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }


        // set the Behind View Fragment
        MenuFragment mu=new MenuFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, mu).commit();

        // customize the SlidingMenu
        Sliding sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeEnabled(false);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);

        sm.setBackgroundImage(R.drawable.img_frame_background);
        sm.setBehindCanvasTransformer(new Sliding.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, -canvas.getWidth() / 2,
                        canvas.getHeight() / 2);
            }
        });

        sm.setAboveCanvasTransformer(new Sliding.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });

    }
    void getNetDatas(){
        netdata=new String();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }
            @Override
            protected Void doInBackground(String... params) {
                try {
                    HttpConnection httpConnection=new HttpConnection(params[0]);
                    httpConnection.GetConnect();
                    if(httpConnection.getResponseCode()!=200){

                    }
                    netdata=httpConnection.ReadData();
                    JSONArray jsonArray=new JSONArray(netdata);
                    JSONObject object=new JSONObject();
                    int t=jsonArray.length();
                    int b=0;
                    for (int i=0;i<=jsonArray.length()-1;i++)
                    {
                        object=jsonArray.getJSONObject(i);
                        FindcarposObject fcppi=new FindcarposObject();
                        fcppi.setCreatedTime(object.getString("createdtime"));
                        fcppi.setUsername(object.getString("username"));
                        fcppi.setLatitude(Double.parseDouble(object.getString("latitude")));
                        fcppi.setLongitude(Double.parseDouble(object.getString("longitude")));
                        fcppi.setDescription(object.getString("description"));
                        fcppi.setLocation(object.getString("location"));
                        fcppi.setStarttime(object.getString("starttime"));
                        fcppi.setEndtime(object.getString("endtime"));
                        fcppi.setPrice(object.getString("price"));
                        findcarposObjects.add(fcppi);//添加到list中
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

            @Override
            protected void onPostExecute(Void aVoid) {//数据加载完成后
                if(loadingDialog.isShowing())
                    loadingDialog.cancel();
                frame_findpos.setVisibility(View.VISIBLE);//地图界面显示
                FindcarposMap map=new FindcarposMap();//替换下面的Fragment
                Bundle bundle=new Bundle();
                bundle.putSerializable(FCP_MAIN, findcarposObjects);
                map.setArguments(bundle);//设置传递的参数
                getFragmentManager().beginTransaction().replace(R.id.frame_findpos, map).commit();
                setListener();
            }
        }.execute(url);
    }
    void initDatas(){

    }
    void initwidget(){//初始化控件
        frame_findpos=(FrameLayout)findViewById(R.id.frame_findpos);//下方的Fragment
        textView_map=(TextView)findViewById(R.id.textView_map);//地图文字按钮
        textView_input=(TextView)findViewById(R.id.textView_input);//输入文字按钮
        fcpchoosebg=(LinearLayout)findViewById(R.id.fcpchoosebg);//地图输入状态切换背景图片
        fcpchoosebg.setBackground(getResources().getDrawable(R.drawable.selected));//设置初始背景图片
    }
    void setListener(){//事件触发设置
        textView_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//输入触摸事件
                if(isMap==1){
                    isMap=0;//设置当前为输入状态
                    fcpchoosebg.setBackground(getResources().getDrawable(R.drawable.unselected));//设置背景图片
                    textView_map.setTextColor(0xff3fa0e4);//改变字体颜色
                    textView_input.setTextColor(Color.parseColor("#ffffff"));//改变字体颜色
                    FindcarposInput input=new FindcarposInput();//替换下面的Fragment
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(FCP_MAIN, findcarposObjects);
                    input.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.frame_findpos,input).commit();
                }else{}
            }
        });
        textView_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//地图按钮触摸事件
                if(isMap==0){
                    isMap=1;//设置当前为地图界面
                    fcpchoosebg.setBackground(getResources().getDrawable(R.drawable.selected));//替换背景为地图选中状态
                    textView_input.setTextColor(Color.parseColor("#3fa0e4"));//替换输入文字颜色
                    textView_map.setTextColor(Color.parseColor("#ffffff"));//替换地图文字颜色
                    FindcarposMap map=new FindcarposMap();//替换下面的Frgment

                    Bundle bundle=new Bundle();
                    bundle.putSerializable(FCP_MAIN, findcarposObjects);
                    map.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.frame_findpos, map).commit();
                }
                else {;//如果不是地图点击了没有任何响应
                }
            }
        });
    }
    void get(){
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    HttpConnection httpConnection=new HttpConnection(params[0]);
                    CheckCookie.setShared(sp);
                    httpConnection.GetConnect(CheckCookie.getCookie());
                    netdata1=httpConnection.ReadData();
                    JSONObject object=new JSONObject(netdata1);
                    sstarttime=object.getString("starttime");
                    createdtime=object.getString("createdtime");
                    dislat=Double.valueOf(object.getString("latitude"));
                    dislong=Double.valueOf(object.getString("longitude"));
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
                Intent broadcast=new Intent();
                Bundle bundle=new Bundle();
                bundle.putInt("option",1);
                broadcast.putExtras(bundle);
                broadcast.setAction("android.intent.action.START");
                sendOrderedBroadcast(broadcast, null);
                ArrayList<String> data=new ArrayList<String>();

                data.add(dislat+"");
                data.add(dislong+"");
                data.add(sstarttime);
                data.add(createdtime);

                Tools.saveArray(locinfosp,"Locinfo",data);
            }
        }.execute(url1);
    }
}
