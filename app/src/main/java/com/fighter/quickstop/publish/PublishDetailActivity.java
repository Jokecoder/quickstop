package com.fighter.quickstop.publish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fighter.quickstop.MainActivity;
import com.fighter.quickstop.R;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.PictureTools;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLOutput;

public class PublishDetailActivity extends AppCompatActivity {

    private LinearLayout publish_detail_wait;   //等待的视图
    private LinearLayout publish_detail_exist;   //有消息的视图


    private Button detail_change;    //确定修改的按钮
    private ImageView detail_ImageView1; //第一张照片
    private ImageView detail_ImageView2; //第二张照片
    private ImageView detail_ImageView3; //第三张照片
    private TextView detail_isopen;  //位置
    private TextView detail_location;  //位置
    private TextView detail_createdtime; //创建时间
    private TextView detail_time;      //空闲时间
    private TextView detail_repeat;   //重复的信息
    private TextView detail_price;   //收费
    private TextView detail_description; //描述
    private TextView detail_usedtime;    //被使用的次数

    private String detailImageView1; //第一张照片
    private String detailImageView2; //第二张照片
    private String detailImageView3; //第三张照片
    private String detailIsopen;    //开启状态
    private String detailLocation;  //位置
    private String detailBegintime;      //空闲时间
    private String detailEndtime;      //空闲时间
    private String detailTime;      //空闲时间
    private String detailCreatedtime; //创建时间
    private String monday;  //星期一
    private String tuesday;  //星期二
    private String wednesday;  //星期三
    private String thursday;  //星期四
    private String friday;  //星期五
    private String saturday;  //星期六
    private String sunday;  //星期日
    private String detailRepeat;   //重复的信息
    private String detailPrice;   //价格
    private String detailDescription; //描述
    private String detailUsedtime;    //被使用的次数


    private String httpurl="getinfobycreatedtime";
    private String message="";   //服务器返回信息
    private int responseCode;  //返回码

    private SharedPreferences sp;    //读取手机数据
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_detail);

        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);

        //标题栏相关设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.publish_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        toolbar.setMenu();
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        detail_change= (Button) findViewById(R.id.detail_change);
        detail_ImageView1= (ImageView) findViewById(R.id.detail_image1);
        detail_ImageView2= (ImageView) findViewById(R.id.detail_image2);
        detail_ImageView3= (ImageView) findViewById(R.id.detail_image3);
        publish_detail_wait= (LinearLayout) findViewById(R.id.publish_detail_wait);
        publish_detail_exist= (LinearLayout) findViewById(R.id.publish_detail_exist);
        detail_isopen= (TextView) findViewById(R.id.detail_isopen);
        detail_location= (TextView) findViewById(R.id.detail_location);
        detail_createdtime= (TextView) findViewById(R.id.detail_createdtime);
        detail_time= (TextView) findViewById(R.id.detail_time);
        detail_repeat= (TextView) findViewById(R.id.detail_repeat);
        detail_price= (TextView) findViewById(R.id.detail_price);
        detail_description= (TextView) findViewById(R.id.detail_description);
        detail_usedtime= (TextView) findViewById(R.id.detail_usedtime);

        detailCreatedtime=getIntent().getStringExtra("createdtime");
        detail_createdtime.setText(detailCreatedtime);

        getDetailInformation();

        detail_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(PublishDetailActivity.this,PublishChangeActivity.class);
                intent.putExtra("createdtime",detailCreatedtime);
                intent.putExtra("image1", detailImageView1);
                intent.putExtra("image2", detailImageView2);
                intent.putExtra("image3",detailImageView3);
                intent.putExtra("isopen",detailIsopen);
                intent.putExtra("location",detailLocation);
                intent.putExtra("begintime",detailBegintime);
                intent.putExtra("endtime",detailEndtime);
                intent.putExtra("price", detailPrice);
                intent.putExtra("description",detailDescription);
                intent.putExtra("monday", monday);
                intent.putExtra("tuesday",tuesday);
                intent.putExtra("wednesday",wednesday);
                intent.putExtra("thursday",thursday);
                intent.putExtra("friday",friday);
                intent.putExtra("saturday",saturday);
                intent.putExtra("sunday",sunday);
                startActivity(intent);
                finish();
            }
        });
    }

    //将获取到的信息设置到界面上
    void setDetailInformation(){
        if(detailImageView1.equals("")||detailImageView1.equals(null)){

        }else {
            detail_ImageView1.setImageBitmap(PictureTools.ToBitmap(detailImageView1));
        }
        if(detailImageView2.equals("")||detailImageView2.equals(null)){

        }else {
            detail_ImageView2.setVisibility(View.VISIBLE);
            detail_ImageView2.setImageBitmap(PictureTools.ToBitmap(detailImageView2));
        }
        if(detailImageView3.equals("")||detailImageView3.equals(null)){

        }else {
            detail_ImageView3.setVisibility(View.VISIBLE);
            detail_ImageView3.setImageBitmap(PictureTools.ToBitmap(detailImageView3));
        }
        int t=0;
        detailRepeat="";
        if(monday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周一";
        }
        if(tuesday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周二";
        }
        if(wednesday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周三";
        }
        if(thursday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周四";
        }
        if(friday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周五";
        }
        if(saturday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周六";
        }
        if(sunday.equals("1")){
            t++;
            if(t!=1)
                detailRepeat=detailRepeat+" ";
            detailRepeat=detailRepeat+"周日";
        }
        if(t==0){
            detailRepeat="没有设置开启时间";
        }
        if(detailIsopen.equals("0"))
            detail_isopen.setText("未开启");
        else
            detail_isopen.setText("开启中");
        detail_repeat.setText(detailRepeat);
        detail_location.setText(detailLocation);
        detail_time.setText(detailTime);
        detail_price.setText(detailPrice + " 元/小时");
        detail_description.setText(detailDescription);
        detail_usedtime.setText(detailUsedtime);
    }

    //从服务器获取到该条车位信息并显示到界面上
    void getDetailInformation(){
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
                    CheckCookie.setShared(sp);
                    connection.GetConnect(CheckCookie.getCookie());   //加cookie
                    connection.LoadData(String.valueOf(jsonObject));
                    int code = connection.getResponseCode();
                    if (code != 200) {
                        message = "请检查网络连接";
                    } else {
                        JSONObject information = new JSONObject(connection.ReadData());
                        System.out.println(information);
                        detailImageView1=information.getString("image1");
                        detailImageView2=information.getString("image2");
                        detailImageView3=information.getString("image3");
                        message = information.getString("message");
                        detailIsopen=information.getString("isopen");
                        detailLocation = information.getString("location");
                        detailBegintime=information.getString("starttime");
                        detailEndtime=information.getString("endtime");
                        detailTime=detailBegintime+"-"+detailEndtime;
                        detailDescription=information.getString("description");
                        detailPrice=information.getString("price");
                        detailUsedtime=information.getString("usedtimes");

                        monday=information.getString("MON");
                        tuesday=information.getString("TUE");
                        wednesday=information.getString("WED");
                        thursday=information.getString("THU");
                        friday=information.getString("FRI");
                        saturday=information.getString("SAT");
                        sunday=information.getString("SUN");

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
                if(loadingDialog.isShowing())
                    loadingDialog.cancel();
                if (message.equals("success")) {
//                    publish_detail_wait.setVisibility(View.GONE);
//                    publish_detail_exist.setVisibility(View.VISIBLE);
                    setDetailInformation();
                }else {
                    Toast.makeText(PublishDetailActivity.this,"查询信息出错",Toast.LENGTH_LONG).show();
                }
            }
        }.execute(detailCreatedtime, httpurl);

    }
}
