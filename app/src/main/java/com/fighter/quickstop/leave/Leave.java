package com.fighter.quickstop.leave;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.fighter.quickstop.R;
import com.fighter.quickstop.findcarpos.ChangeTimeframe;
import com.fighter.quickstop.myorderlist.Myorderlist;
import com.fighter.quickstop.utils.HttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuch on 2016/2/10.
 */
public class Leave extends Activity {
    String url="leavepay";
   SharedPreferences sp;
    Intent lastintent;
    String username;
    String createdtime;
    Double price;//
    String leavetime;//离开
    double money;//总价格
    int totalmin;//总分钟数
    String starttime;//开始时间
    String endtime;//结束时间
    String reachtime;//到达时间
    TextView starttimetext;//开始时间
    TextView leavetimetext;//结束时间

    TextView money_text;//价钱
    Button leave_btn;//离开的按钮
    TextView priceperhour;//每小时价格
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave);
        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        lastintent=getIntent();//上一个传来的
        username=lastintent.getStringExtra("username");//
        createdtime=lastintent.getStringExtra("createdtime");//
        price=Double.valueOf(lastintent.getStringExtra("price"));//
        starttime=lastintent.getStringExtra("starttime");//
        reachtime=lastintent.getStringExtra("reachtime");
        reachtime=reachtime.substring(11,16);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate=new Date(System.currentTimeMillis());//获取当前时间
        String str=formatter.format(curDate);
        endtime=str.substring(11,16);

        initWedgets();//读取各种控件的标签赋值

        getDatas();//获取数据
        setListen();
    }
    void initWedgets(){
        starttimetext=(TextView)findViewById(R.id.starttimetext);//
        starttimetext.setText(reachtime);
        leave_btn=(Button)findViewById(R.id.leave_btn);//按钮
        leavetimetext=(TextView)findViewById(R.id.leavetimetext);//
        leavetimetext.setText(endtime);
        priceperhour=(TextView)findViewById(R.id.priceperhour);
        priceperhour.setText("每小时"+price+" 元。");
        totalmin=(ChangeTimeframe.getHour(endtime)-ChangeTimeframe.getHour(reachtime))*60+ChangeTimeframe.getMinute(endtime)-ChangeTimeframe.getMinute(reachtime);
        money=totalmin*1.0/60*price;
        DecimalFormat df=new DecimalFormat("#0.00");
        money=Double.valueOf(df.format(money));
        money_text=(TextView)findViewById(R.id.money_text);//
        money_text.setText(money+"");

    }


    void getDatas(){

    }

    void setListData(int finished){

    }
    void setListen(){
        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date curDate=new Date(System.currentTimeMillis());//获取当前时间
                String str=formatter.format(curDate);
                leavetime=str.substring(0,16);
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            HttpConnection httpConnection=new HttpConnection(params[0]);
                            httpConnection.GetConnect();
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("leavetime",params[4]);
                            jsonObject.put("money",params[3]);
                            jsonObject.put("starttime",params[2]);
                            jsonObject.put("createdtime",params[1]);
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
                        final AlertDialog.Builder builder = new AlertDialog.Builder(Leave.this);
                        builder.setMessage("已经成功付款")
                                .setTitle("提示")
                                .setCancelable(true)// 不可取消（即返回键不能取消此对话框）
                                .setNegativeButton("返回我的订单", new DialogInterface.OnClickListener() {// 设置第二个按钮的标签及其事件监听器
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent();
                                intent.setClass(Leave.this,Myorderlist.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog alert = builder.create();// 用对话框构造器创建对话框
                        alert.show();// 显示对话框
                    }
                }.execute(url,createdtime,starttime,""+money,leavetime);
            }
        });
    }

}
