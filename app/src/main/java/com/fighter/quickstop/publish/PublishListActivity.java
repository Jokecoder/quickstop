package com.fighter.quickstop.publish;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.fighter.quickstop.R;
import com.fighter.quickstop.findcarpos.Findcarpos;
import com.fighter.quickstop.lib.MenuFragment;
import com.fighter.quickstop.lib.Sliding;
import com.fighter.quickstop.lib.SlidingFragmentActivity;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.CustomDialog;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.PictureTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishListActivity extends AppCompatActivity {
    private ListView pl_list;   //列表
    private  Button toolbar_publish_list_add;
    private LinearLayout linear_not_publish;


    final ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private List<String> images=new ArrayList<String>();     //图片
    private List<String> locations=new ArrayList<String>();     //车位位置
    private List<String> repeats=new ArrayList<String>();       //重复的信息
    private List<String> createdtimes=new ArrayList<String>();   //创建时间
    private List<String> isOpens=new ArrayList<String>();     //是否开启

    private List<Boolean> opens=new ArrayList<Boolean>();     //是否开启

    private List<String> MONs=new ArrayList<String>();     //周一
    private List<String> TUEs=new ArrayList<String>();       //周二
    private List<String> WEDs=new ArrayList<String>();   //周三
    private List<String> THUs=new ArrayList<String>();     //周四
    private List<String> FRIs=new ArrayList<String>();     //周五
    private List<String> SATs=new ArrayList<String>();       //周六
    private List<String> SUNs=new ArrayList<String>();   //周日

    int t=0;    //计数
    String string = "getinfo";
    String repeat;

    private String httpurl="deleinfo";
    private String message="";   //服务器返回信息
    private int responseCode;  //返回码

    private SharedPreferences sharedPreferences;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());      //添加地图相关
        setContentView(R.layout.activity_publish_list);

        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        sharedPreferences=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        Toast.makeText(this,"长按可删除车位信息",Toast.LENGTH_LONG).show();

        //标题栏相关设置
        Toolbar toolbar= (Toolbar) findViewById(R.id.publish_add_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                startActivity(new Intent(PublishListActivity.this,Findcarpos.class));
            }
        });


        toolbar_publish_list_add= (Button) findViewById(R.id.toolbar_publish_list_add);
        toolbar_publish_list_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishListActivity.this, PublishAddActivity.class);
                startActivity(intent);
            }
        });
        pl_list= (ListView) findViewById(R.id.pl_list);
        linear_not_publish= (LinearLayout) findViewById(R.id.linear_not_publish);
        getPublishList();
        pl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final boolean open;
                final ToggleButton toggleButton= (ToggleButton) view.findViewById(R.id.pi_switch);
                final TextView textView= (TextView) view.findViewById(R.id.pi_repeat);
                String str=textView.getText().toString();
                open=toggleButton.isChecked();
                toggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(open==true){
                            toggleButton.setChecked(false);
                            textView.setText("未开启");
                            Toast.makeText(PublishListActivity.this,"关闭",Toast.LENGTH_LONG).show();
                        }
                        else
                            toggleButton.setChecked(false);
                        Toast.makeText(PublishListActivity.this,"开启",Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent();
                intent.setClass(PublishListActivity.this, PublishDetailActivity.class);
                intent.putExtra("createdtime", createdtimes.get(position));
                intent.putExtra("repeat", repeats.get(position));
                startActivity(intent);
            }
        });

        //长按选项的操作
        pl_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CustomDialog.Builder builder=new CustomDialog.Builder(PublishListActivity.this);
                builder.setMessage("您要删除这条车位信息吗？");
                builder.setTitle("警告");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePublishItem(createdtimes.get(position));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

    }

    //获取数据库中本人的所有车位信息
    void getPublishList(){
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpConnection httpConnection=new HttpConnection(params[0]);
                    CheckCookie.setShared(sharedPreferences);
                    httpConnection.GetConnect(CheckCookie.getCookie());   //加cookie
                    JSONArray list=new JSONArray(httpConnection.ReadData());
                    JSONObject object=new JSONObject();
                    t=list.length();
                    for (int i=0;i<list.length();i++)
                    {
                        Map<String, Object> item = new HashMap<String, Object>();
                        object=list.getJSONObject(i);
                        images.add(object.getString("image1"));
                        locations.add(object.getString("location"));
                        isOpens.add(object.getString("isopen"));
                        MONs.add(object.getString("MON"));
                        TUEs.add(object.getString("TUE"));
                        WEDs.add(object.getString("WED"));
                        THUs.add(object.getString("THU"));
                        FRIs.add(object.getString("FRI"));
                        SATs.add(object.getString("SAT"));
                        SUNs.add(object.getString("SUN"));
                        createdtimes.add(object.getString("createdtime"));

                        if(!images.get(i).equals("1"))
                            item.put("image", PictureTools.ToBitmap(images.get(i)));
                        else
                           item.put("image",R.drawable.publish_preset2);
                        item.put("name", "我的车位" + String.valueOf(i + 1));
                        item.put("location",locations.get(i));
                        int k=0;
                        repeat="";
                        if(MONs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周一";
                        }
                        if(TUEs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周二";
                        }
                        if(WEDs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周三";
                        }
                        if(THUs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周四";
                        }
                        if(FRIs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周五";
                        }
                        if(SATs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周六";
                        }
                        if(SUNs.get(i).equals("1")){
                            k++;
                            if(k!=1)
                                repeat=repeat+" ";
                            repeat=repeat+"周日";
                        }
                        if(k==0) repeat="没有设置开启时间";
                        item.put("repeat", repeat);
                        repeats.add(repeat);
                        if(isOpens.get(i).equals("0")){
                            repeat="未开启";
                            opens.add(i,false);
                        }
                        else{
                            opens.add(i, true);
                        }
                        mData.add(item);
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
            protected void onPostExecute(String s) {
                if(loadingDialog.isShowing())
                    loadingDialog.cancel();
                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(),mData, R.layout.publish_item,
                        new String[]{"image","name","location","repeat"},new int[]{R.id.pi_icon,R.id.pi_name,R.id.pi_location,R.id.pi_repeat});


                simpleAdapter.setViewBinder(new ListViewBinder());

                pl_list.setAdapter(simpleAdapter);

                //设置显示的视图，如果没有数据，显示无数据的视图，有数据，显示列表
                if(t!=0) {
                    linear_not_publish.setVisibility(View.GONE);
                    pl_list.setVisibility(View.VISIBLE);
                }
                else {
                    linear_not_publish.setVisibility(View.VISIBLE);
                    pl_list.setVisibility(View.GONE);
                }
            }
        }.execute(string);
    }


    void deletePublishItem(String creattime){
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("createdtime", params[0]);
                    HttpConnection connection = new HttpConnection(params[1]);
                    CheckCookie.setShared(sharedPreferences);
                    connection.GetConnect(CheckCookie.getCookie());   //加cookie
                    connection.LoadData(String.valueOf(jsonObject));
                    int code = connection.getResponseCode();
                    if (code != 200) {
                        message = "请检查网络连接";
                    } else {
                        JSONObject information = new JSONObject(connection.ReadData());
                        message = information.getString("message");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                if (message.equals("success")) {
                    Toast.makeText(PublishListActivity.this,"已成功删除车位信息，正在刷新",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(PublishListActivity.this, PublishListActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);   //界面切换动画
                }else {
                    Toast.makeText(PublishListActivity.this,"查询信息出错",Toast.LENGTH_LONG).show();
                }
            }
        }.execute(creattime, httpurl);
    }

}
