package com.fighter.quickstop.findcarpos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fighter.quickstop.GifView;
import com.fighter.quickstop.R;
import com.fighter.quickstop.utils.CircleImageView;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.PictureTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class FindcarOrder extends AppCompatActivity {
    String name;

    ArrayList<Integer> timelist;//时间序列
    EditText start;
    EditText end;
    CircleImageView userimg_order;//用户头像
    EditText username_order;//用户名
    TextView usedpeople_order;//最近使用的人数
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;

    LinearLayout order_ok;//正常的linear界面，在数据加载了后显示
    GifView loadinggif;
    String username;//上个界面传过来的车位的用户
    String createdtime;//上个界面传过来的车位的创建时间
    FrameLayout orderFrame;
    Button orderButton;//预定按钮
    FindcarposObject findcarposObject;//得到的全部详细数据
    String url="findcarorder";//url
    String netdata;//网络数据

    public static String FCP_ORDER="com.example.zhuch.quickstop.findcarpos.findcarOrder";//主界面
    OrderObject orderobject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findcar_order);
        Toolbar toolbar= (Toolbar) findViewById(R.id.order_toolsbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initWidget();
        initDatas();

    }
    void initWidget(){
        loadinggif=(GifView)findViewById(R.id.fcpo_loading);//加载中图片
        loadinggif.setMovieResource(R.drawable.loading);
        loadinggif.setVisibility(View.VISIBLE);
        orderFrame=(FrameLayout)findViewById(R.id.orderFrame);
        orderButton=(Button)findViewById(R.id.orderButton);
       order_ok=(LinearLayout)findViewById(R.id.order_ok);
        userimg_order=(CircleImageView)findViewById(R.id.userimg_order);//用户头像
        username_order=(EditText)findViewById(R.id.username_order);//用户名
        usedpeople_order=(TextView)findViewById(R.id.usedpeople_order);//用过的人数
        imageView1=(ImageView)findViewById(R.id.image1_order);
        imageView2=(ImageView) findViewById(R.id.image2_order);
        imageView3=(ImageView)findViewById(R.id.image3_order);//三个图片
    }
void initDatas(){
    Intent intent=getIntent();//得到上一个界面的信息
    orderobject=new OrderObject();
    username=intent.getStringExtra("username");//用户名
    createdtime=intent.getStringExtra("createdtime");//创建时间
    //传用户名和创建时间
    //得到该车位的具体信息
    //以及订单表的信息
    //以及用户的头像
    netdata=new String();
    new AsyncTask<String, Void, Void>() {
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpConnection httpConnection=new HttpConnection(params[0]);
                httpConnection.GetConnect();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("username",params[1]);
                jsonObject.put("createdtime",params[2]);
                httpConnection.LoadData(String.valueOf(jsonObject));

                if(httpConnection.getResponseCode()!=200){//失败

                }
                netdata=httpConnection.ReadData();
                JSONObject object=new JSONObject(netdata);
                orderobject.setNickname(object.getString("nickname"));
                orderobject.setUserimage(object.getString("userimage"));
                orderobject.setUsername(object.getString("username"));
                orderobject.setCreatedTime(object.getString("createdtime"));
                orderobject.setImage1(object.getString("image1"));
                orderobject.setImage2(object.getString("image2"));
                orderobject.setImage3(object.getString("image3"));
                orderobject.setUsedcount(Integer.parseInt(object.getString("usedcount")));
                orderobject.setLocation(object.getString("location"));
                orderobject.setLatitude(Double.parseDouble(object.getString("latitude")));
                orderobject.setLongitude(Double.parseDouble(object.getString("longitude")));
                orderobject.setPrice(Integer.parseInt(object.getString("price")));
                orderobject.setDescription(object.getString("description"));
                orderobject.setStarttime(object.getString("starttime"));
                orderobject.setEndtime(object.getString("endtime"));

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

            FindcarFragment order=new FindcarFragment();//替换下面的Fragment
            Bundle bundle=new Bundle();
            bundle.putSerializable(FCP_ORDER,orderobject);
            order.setArguments(bundle);//设置传递的参数
            getFragmentManager().beginTransaction().add(R.id.orderFrame, order).commit();//加载界面
            setData();
            addListener();//添加各种listener
        }
    }.execute(url,username,createdtime);
}
    void addListener(){

    }

    void setData(){
        username_order.setText(orderobject.getNickname());//设置用户名
        usedpeople_order.setText("最近"+orderobject.getUsedcount()+"个人使用过他的车位");
        //三个图片
        userimg_order.setImageBitmap(PictureTools.ToBitmap(orderobject.getUserimage()));

        if(!orderobject.getImage1().equals("")) {
            imageView1.setImageBitmap(PictureTools.ToBitmap(orderobject.getImage1()));
            imageView1.setVisibility(View.VISIBLE);
        }else{
            imageView1.setVisibility(View.GONE);
        }
        if(!orderobject.getImage2().equals("")) {
            imageView2.setImageBitmap(PictureTools.ToBitmap(orderobject.getImage2()));
            imageView2.setVisibility(View.VISIBLE);
        }else{
            imageView2.setVisibility(View.GONE);
        }
        if(!orderobject.getImage3().equals("")) {
            imageView3.setImageBitmap(PictureTools.ToBitmap(orderobject.getImage3()));
            imageView3.setVisibility(View.VISIBLE);
        }else{
            imageView3.setVisibility(View.GONE);
        }
        if(orderobject.getImage1().equals("")&&orderobject.getImage2().equals("")&&orderobject.getImage3().equals("")){
            imageView1.setVisibility(View.VISIBLE);
        }
        loadinggif.setVisibility(View.GONE);
        order_ok.setVisibility(View.VISIBLE);//下面framgment可见
    }
}
