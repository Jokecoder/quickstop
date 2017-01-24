package com.fighter.quickstop.myorderlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fighter.quickstop.GifView;
import com.fighter.quickstop.R;
import com.fighter.quickstop.findcarpos.FindAdapt;
import com.fighter.quickstop.findcarpos.Findcarpos;
import com.fighter.quickstop.guideapi.GuidetoDistnation;
import com.fighter.quickstop.leave.Leave;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.CircleImageView;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.PictureTools;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuch on 2016/2/10.
 */
public class Myorderlist extends AppCompatActivity {
    ListView listshow=null;
    TextView finished=null;
    RelativeLayout showrela;
    SharedPreferences sp;
    CircleImageView headshow;
    TextView ufinished=null;
    List<Map<String, Object>> showlist=new ArrayList<Map<String,Object>>();//显示的list数据
    Double latitude;//经度
    Double longitude;//维度
    private final String del="deleteorder";
    TextView orderusername;
    ArrayList<OrderListObject> orderListObjects;
    private final String url="myorderlist";
    OrderListObject choosedInformation=new OrderListObject();
    public static String MYORDERLIST="com.example.zhuch.quickstop.myorderlist";
    TextView linear_not_orderlist;
    GifView load;
    String netdata;
    boolean tagfinished=false;//初始标签为未完成
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorderlist);
        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        //标题栏相关设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_MyOrderList);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                startActivity(new Intent(Myorderlist.this,Findcarpos.class));
            }
        });

        initWedgets();//读取各种控件的标签赋值

        getDatas();//获取数据
    }
    void initWedgets(){
        load=(GifView)findViewById(R.id.orderlist_loading);//加载中那个动图
        load.setMovieResource(R.drawable.loading);//开始loading
        linear_not_orderlist=(TextView)findViewById(R.id.linear_not_orderlist);
        listshow=(ListView)findViewById(R.id.listshow);//list显示部分
        finished=(TextView)findViewById(R.id.finishedlisttext);//完成标签
        ufinished=(TextView)findViewById(R.id.ufinishedlisttext);//未完成标签
//        headshow=(CircleImageView) findViewById(R.id.headshow);//图片
//        headshow.setImageBitmap(PictureTools.ToBitmap(PictureTools.ReadPicture(sp)));
//
//        orderusername=(TextView)findViewById(R.id.orderusername);//名字
//        orderusername.setText(Tools.getData(sp,"nickname"));
    }


    void getDatas(){
        orderListObjects=new ArrayList<OrderListObject>();
        netdata=new String();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    HttpConnection httpConnection=new HttpConnection(params[0]);
                    CheckCookie.setShared(sp);
                    httpConnection.GetConnect(CheckCookie.getCookie());
                    netdata=httpConnection.ReadData();
                    JSONArray jsonArray=new JSONArray(netdata);
                    JSONObject object=new JSONObject();
                    for (int i=0;i<=jsonArray.length()-1;i++)
                    {
                        object=jsonArray.getJSONObject(i);
                        OrderListObject orderlistobject=new OrderListObject();
                        orderlistobject.setIsfinished(Integer.parseInt(object.getString("isfinished")));
                        orderlistobject.setCreatedtime(object.getString("createdtime"));
                        orderlistobject.setUsername(object.getString("username"));
                        orderlistobject.setEndTime(object.getString("endtime"));
                        orderlistobject.setStarttime(object.getString("starttime"));
                        orderlistobject.setCost(Float.parseFloat(object.getString("cost")));
                        orderlistobject.setReachtime(object.getString("reachtime"));
                        orderlistobject.setLeavetime(object.getString("leavetime"));
                        orderlistobject.setDescription(object.getString("description"));
                        orderlistobject.setLocation(object.getString("location"));
                        orderlistobject.setLatitude(Double.parseDouble(object.getString("latitude")));
                        orderlistobject.setLongitude(Double.parseDouble(object.getString("longitude")));
                        orderlistobject.setNickname(object.getString("nickname"));
                        orderListObjects.add(orderlistobject);
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
                if(orderListObjects.size()==0){
                    load.setVisibility(View.GONE);//加载动画不显示
                    listshow.setVisibility(View.GONE);//列表显示
                    linear_not_orderlist.setVisibility(View.VISIBLE);
                }else{
                    setListData(0);//0表示未完成的,初始化列表数据
                }
                setListen();//设置控件监听
            }
        }.execute(url);
    }

    void setListData(int finished){
        showlist.clear();
        for (int i = 0;i<orderListObjects.size(); i++) {
            OrderListObject orderlistobject=orderListObjects.get(i);
            if(orderlistobject.getIsfinished()==finished){//如果是finished一样的就添加到列表中
                Map<String, Object> map=new HashMap<String, Object>();
                map.put("editlistlinear_pic", R.drawable.publish_preset2);
                map.put("editlistlinear_location", orderlistobject.getLocation());
                map.put("editlistlinear_des", orderlistobject.getDescription());
                map.put("username", orderlistobject.getUsername());
                map.put("createdtime", orderlistobject.getCreatedtime());
                showlist.add(map);
            }
        }
        listshow.setDividerHeight(10);//每行高度间隔
        FindAdapt adapt=new FindAdapt(this,showlist);
        adapt.notifyDataSetChanged();//内容改变后改变listview
        listshow.setAdapter(adapt);
        load.setVisibility(View.GONE);//加载动画不显示
        listshow.setVisibility(View.VISIBLE);//列表显示
    }
    void setListen(){
        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tagfinished) {
                    tagfinished=true;
                    finished.setBackgroundColor(Color.parseColor("#e9dada"));
                    ufinished.setBackgroundColor(Color.WHITE);
                    setListData(1);
                }
            }
        });
        ufinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagfinished) {
                    tagfinished=false;
                    setListData(0);//重置数据
                    ufinished.setBackgroundColor(Color.parseColor("#e9dada"));
                    finished.setBackgroundColor(Color.WHITE);
                }
            }
        });
        listshow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                getPOS(position);
                if(tagfinished){//已完成显示的
                        final AlertDialog.Builder builder = new AlertDialog.Builder(Myorderlist.this);
                        builder.setMessage("你于"+choosedInformation.getReachtime()+"到达并于\n"+choosedInformation.getLeavetime()+"离开这里\n共计消费"+choosedInformation.getCost()+"元")
                                .setTitle("订单详情")
                                .setCancelable(true)// 不可取消（即返回键不能取消此对话框）
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {// 设置第一个按钮的标签及其事件监听器
                                    public void onClick(DialogInterface dialog, int id) {
                                        onDestroy();
                                    }
                                });
                        AlertDialog alert = builder.create();// 用对话框构造器创建对话框
                        alert.show();// 显示对话框
                    }
                else {//未完成显示的
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Myorderlist.this);
                    builder.setMessage("请选择您接下来的操作")
                            .setTitle("提示")
                            .setCancelable(true);// 不可取消（即返回键不能取消此对话框）
                    if(choosedInformation.getReachtime().equals("0")){
                        builder.setNegativeButton("导航前去", new DialogInterface.OnClickListener() {// 设置第二个按钮的标签及其事件监听器
                            public void onClick(DialogInterface dialog, int id) {
                                Intent broadcast=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putInt("option",2);
                                broadcast.putExtras(bundle);
                                broadcast.setAction("android.intent.action.START");

                                Intent intent = new Intent();
                                intent.putExtra("latitude", latitude);//穿经度
                                intent.putExtra("longitude", longitude);//传维度
                                intent.setClass(Myorderlist.this, GuidetoDistnation.class);
                                startActivity(intent);
                            };
                        });
                    }else{
                            builder .setNegativeButton("我要离开", new DialogInterface.OnClickListener() {// 设置第一个按钮的标签及其事件监听器
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent=new Intent();
                                    intent.putExtra("username",choosedInformation.getUsername());//我这个用户
                                    intent.putExtra("createdtime",choosedInformation.getCreatedtime());//
                                    intent.putExtra("price",choosedInformation.getCost()+"");
                                    intent.putExtra("starttime",choosedInformation.getStarttime());//开始时间
                                    intent.putExtra("reachtime",choosedInformation.getReachtime());//到达时间
                                    intent.setClass(Myorderlist.this,Leave.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                    AlertDialog alert = builder.create();// 用对话框构造器创建对话框
                    alert.show();// 显示对话框
                }
            }
        });
        listshow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getPOS(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(Myorderlist.this);
                builder.setMessage("确定要删除这一条信息吗！！！")
                        .setTitle("提示")
                        .setCancelable(true)// 不可取消（即返回键不能取消此对话框）
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {// 设置第一个按钮的标签及其事件监听器
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncTask<String, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(String... params) {
                                        try {
                                            HttpConnection httpConnection=new HttpConnection(params[0]);
                                            httpConnection.GetConnect();
                                            JSONObject jsonObject=new JSONObject();
                                            jsonObject.put("starttime",params[3]);
                                            jsonObject.put("createdtime",params[2]);
                                            jsonObject.put("username",params[1]);
                                            httpConnection.LoadData(String.valueOf(jsonObject));

                                            if(httpConnection.getResponseCode()!=200){//失败

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
                                        Toast.makeText(Myorderlist.this,"删除成功", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Myorderlist.this,Myorderlist.class));
                                        finish();
                                    }
                                }.execute(del,choosedInformation.getUsername(),choosedInformation.getCreatedtime(),choosedInformation.getStarttime());
                            }
                        }).setNegativeButton("我后悔了", new DialogInterface.OnClickListener() {// 设置第二个按钮的标签及其事件监听器
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();// 用对话框构造器创建对话框
                alert.show();// 显示对话框
                return false;
            }
        });
    }
    void getPOS(int position){
        String username=showlist.get(position).get("username").toString();
        String createdtime=showlist.get(position).get("createdtime").toString();
        for(OrderListObject order:orderListObjects){
            if(order.getUsername().contentEquals(username)&&order.getCreatedtime().contentEquals(createdtime)){//找到了那个东东
                latitude=order.getLatitude();
                longitude=order.getLongitude();
                choosedInformation.setLeavetime(order.getLeavetime());
                choosedInformation.setReachtime(order.getReachtime());
                choosedInformation.setCost(order.getCost());
                choosedInformation.setUsername(order.getUsername());
                choosedInformation.setCreatedtime(order.getCreatedtime());
                choosedInformation.setStarttime(order.getStarttime());
                break;
            }
        }
    }

}
