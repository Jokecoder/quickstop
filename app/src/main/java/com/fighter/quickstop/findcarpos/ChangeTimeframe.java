package com.fighter.quickstop.findcarpos;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.fighter.quickstop.MylookView;
import com.fighter.quickstop.R;
import com.fighter.quickstop.myorderlist.Myorderlist;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.CustomDialog;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.PickerView;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuch on 2016/3/8.
 */
public class ChangeTimeframe extends Fragment {
    SharedPreferences sp;
    View view;
    ArrayList<Boolean> timelist=new ArrayList<Boolean>();//时间序列
    ArrayList<Integer> time;
    List<String> hour = new ArrayList<String>();
    List<String> minute = new ArrayList<String>();
    MylookView timevisible;//颜色条
    Button orderButton_Time;//预定按钮
    OrderObject orderObject;//上面传来的orderobject
    String zero="0";//默认为0的值
    String message;//服务器返回消息
    SharedPreferences locinfosp;//本地信息
    int starthour;
    int endhour;
    int startminute;
    int endminute;

    String url="order";
    String starttime;//开始预定时间
    String endtime;//结束预定时间
    PickerView s_hour;//开始小时
    PickerView e_hour;//结束小时
    PickerView s_minute;//开始分钟
    PickerView e_minute;//结束分钟
    String sh;//开始小时
    String sm;//开始分钟
    String eh;//结束小时
    String em;//结束分钟
    TextView changestart;//开始
    TextView changeend;//结束

    LoadingDialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.changetimefra,null);
        loadingDialog=new LoadingDialog(getActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        sp=getActivity().getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        locinfosp=getActivity().getSharedPreferences("Locinfo", Context.MODE_PRIVATE);
        initWidgets();//初始化控件
        initData();//初始化数据
        setListener();//设置监听

        return view;
    }
    void initWidgets(){
        orderButton_Time=(Button)view.findViewById(R.id.orderButton_Time);
        timevisible=(MylookView)view.findViewById(R.id.timevisible);
        s_hour=(PickerView)view.findViewById(R.id.s_hour);
        s_minute=(PickerView)view.findViewById(R.id.s_minute);
        e_hour=(PickerView)view.findViewById(R.id.e_hour);
        e_minute=(PickerView)view.findViewById(R.id.e_minute);
        changeend=(TextView)view.findViewById(R.id.changeend);
        changestart=(TextView)view.findViewById(R.id.changestart);
    }
    void initData(){
        orderObject=(OrderObject)getArguments().getSerializable(FindcarOrder.FCP_ORDER);//上个界面传递过来的所有的值
        time=new ArrayList<Integer>();
        starthour=getHour(orderObject.getStarttime());
        endhour=getHour(orderObject.getEndtime());
        startminute=getMinute(orderObject.getStarttime());
        endminute=getMinute(orderObject.getEndtime());
        //int start=orderObject.getStarttime()
//        starthour=11;
//        startminute=40;
//        endhour=16;
//        endminute=30;
        int i=0;
        int start=starthour*60+startminute;
        int end=endhour*60+endminute;
        for(i=0;i<1440;i++){//默认0为不可用，即显示红色
            if(i<start||i>end){
                timelist.add(false);
            }
            else{
                timelist.add(true);//
            }
        }
        for (i = 0; i <24; i++)
        {
            hour.add(i < 10 ? "0" + i : "" + i);//初始化分钟
        }
        for (i = 0; i < 60; i++)
        {
            minute.add(i < 10 ? "0" + i : "" + i);//初始化分钟
        }
        s_hour.setData(hour);
        e_hour.setData(hour);
        s_minute.setData(minute);
        e_minute.setData(minute);//为几个控件设置默认值
        s_hour.setSelected(starthour);
        s_minute.setSelected(startminute);
        e_hour.setSelected(endhour);
        e_minute.setSelected(endminute);
        //timevisible.setStart(starthour*60+startminute);//设置选中的开始时间
        //timevisible.setEnd(endhour*60+endminute);//设置结束的选中时间
        sh=starthour+"";
        sm=startminute+"";
        eh=endhour+"";
        em=endminute+"";
        timevisible.setTime(timelist);
        changestart.setText("预定开始\n不早于"+orderObject.getStarttime());
        changeend.setText("预定结束\n不迟于"+orderObject.getEndtime());
    }

    public static int getHour(String s){//获取一个字符串的例如：00:00
        String m=s.substring(0,2);
        return Integer.valueOf(m);
    }
    public static int getMinute(String s){//获取分钟
        String m=s.substring(3,5);
        return Integer.valueOf(m);
    }
    void setListener(){
        /*
        * 为四个控件设置触摸事件，读取数据
        * */
        s_hour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                sh=text;
                if(Double.valueOf(sh)*60+Double.valueOf(sm)<starthour*60+startminute||Double.valueOf(sh)*60+Double.valueOf(sm)>=Double.valueOf(eh)*60+Double.valueOf(em)||Double.valueOf(eh)*60+Double.valueOf(em)>endhour*60+endminute){
                    orderButton_Time.setClickable(false);
                    orderButton_Time.setBackgroundResource(R.drawable.unorderbutton);
                }
                else{
                    orderButton_Time.setClickable(true);
                    orderButton_Time.setBackgroundResource(R.drawable.orderbutton);
                }
            }
        });
        e_hour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                eh=text;
                if(Double.valueOf(sh)*60+Double.valueOf(sm)<starthour*60+startminute||Double.valueOf(sh)*60+Double.valueOf(sm)>=Double.valueOf(eh)*60+Double.valueOf(em)||Double.valueOf(eh)*60+Double.valueOf(em)>endhour*60+endminute){
                    orderButton_Time.setClickable(false);
                    orderButton_Time.setBackgroundResource(R.drawable.unorderbutton);

                }else{
                    orderButton_Time.setClickable(true);
                    orderButton_Time.setBackgroundResource(R.drawable.orderbutton);
                }
            }
        });
        s_minute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                sm=text;
                if(Double.valueOf(sh)*60+Double.valueOf(sm)<starthour*60+startminute||Double.valueOf(sh)*60+Double.valueOf(sm)>=Double.valueOf(eh)*60+Double.valueOf(em)||Double.valueOf(eh)*60+Double.valueOf(em)>endhour*60+endminute){
                    orderButton_Time.setClickable(false);
                    orderButton_Time.setBackgroundResource(R.drawable.unorderbutton);
                }else{
                    orderButton_Time.setClickable(true);
                    orderButton_Time.setBackgroundResource(R.drawable.orderbutton);
                }
            }
        });
        e_minute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                em=text;
                if(Double.valueOf(sh)*60+Double.valueOf(sm)<starthour*60+startminute||Double.valueOf(sh)*60+Double.valueOf(sm)>=Double.valueOf(eh)*60+Double.valueOf(em)||Double.valueOf(eh)*60+Double.valueOf(em)>endhour*60+endminute){
                    orderButton_Time.setClickable(false);
                    orderButton_Time.setBackgroundResource(R.drawable.unorderbutton);

                }else{
                    orderButton_Time.setClickable(true);
                    orderButton_Time.setBackgroundResource(R.drawable.orderbutton);
                }
            }
        });

        orderButton_Time.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date curDate=new Date(System.currentTimeMillis());//获取当前时间
                String str=formatter.format(curDate);
                String nowtime=str.substring(0,11);
//                starttime=nowtime+startTimePicker.getHour()+":"+startTimePicker.getMinute();//设置开始时间以及结束时间
//                endtime=nowtime+endTimePicker.getHour()+":"+endTimePicker.getMinute();//设置开始时间以及结束时间
                if(sh.length()==1){
                    sh="0"+sh;
                }
                if(eh.length()==1){
                    eh="0"+eh;
                }
                starttime=nowtime+sh+":"+sm;
               // starttime=str.substring(0,16);
                //endtime="14:20";
                endtime=nowtime+eh+":"+em;
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        loadingDialog.show();
                    }
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            HttpConnection httpConnection=new HttpConnection(params[0]);   //新建链接
                            CheckCookie.setShared(sp);
                            httpConnection.GetConnect(CheckCookie.getCookie());
                            JSONObject jsonObject=new JSONObject();   //新建Json对象
                            //初始化JSON
                            jsonObject.put("pusername",params[1]);//pusername
                            jsonObject.put("pcreatedtime", params[2]);
                            jsonObject.put("sstarttime", params[3]);
                            jsonObject.put("sendtime", params[4]);
                            jsonObject.put("isfinished", params[5]);
                            jsonObject.put("ucost", params[6]);
                            jsonObject.put("ureachtime", params[7]);
                            jsonObject.put("uleavetime", params[8]);
                            httpConnection.LoadData(String.valueOf(jsonObject));  //上传数据

                            if(httpConnection.getResponseCode()==200){//返回码
                                //获取服务器返回数据
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
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(loadingDialog.isShowing())
                            loadingDialog.cancel();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        orderButton_Time.setClickable(true);
                        //保存数据到本地信息
                        ArrayList<String> data=new ArrayList<String>();
                        data.add(orderObject.getLatitude()+"");
                        data.add(orderObject.getLongitude()+"");
                        data.add(starttime);
                        data.add(orderObject.getCreatedTime());
                        Tools.saveArray(locinfosp,"Locinfo",data);

                        Intent broadcast=new Intent();
                        broadcast.setAction("android.intent.action.START");
                        getActivity().sendOrderedBroadcast(broadcast, null);
                        //开始位置监测线程

                        builder.setMessage("已经成功预定该车位\n请选择您接下来的操作")
                                .setTitle("提示")
                                .setPositiveButton("查看我的订单", new DialogInterface.OnClickListener() {// 设置第一个按钮的标签及其事件监听器
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 跳转到车位信息界面
                                        /*getFragmentManager().beginTransaction().replace(R.id.fram, new HelloFra()).commit();*/
                                        startActivity(new Intent(getActivity(),Myorderlist.class));
                                        getActivity().finish();
                                    }
                                }).setNegativeButton("返回主界面", new DialogInterface.OnClickListener() {// 设置第二个按钮的标签及其事件监听器
                            public void onClick(DialogInterface dialog, int id) {
                                //跳转到发布信息主页面
                                startActivity(new Intent(getActivity(),Findcarpos.class));
                            }
                        });
                        builder.create().show();
                        Toast.makeText(getActivity(), "预定成功", Toast.LENGTH_SHORT).show();
                    }
                }.execute(url,orderObject.getUsername(),orderObject.getCreatedTime(),starttime,endtime,zero,orderObject.getPrice()+"",zero,zero);

            }
        });
    }
}
