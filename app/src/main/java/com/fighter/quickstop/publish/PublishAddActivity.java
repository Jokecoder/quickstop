package com.fighter.quickstop.publish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fighter.quickstop.R;
import com.fighter.quickstop.distance.DistanceService;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.CustomDialog;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.PictureTools;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.TimeZone;

/*
* 添加车位信息的界面
* */
public class PublishAddActivity extends AppCompatActivity implements View.OnClickListener {

    private Button add_preserve;            //保存按钮
    private ImageView imageView;            //图片的全局变量
    private LinearLayout layout_addimage1;
    private LinearLayout layout_addimage2;
    private LinearLayout layout_addimage3;
    private ImageView add_addimage1;        //添加图片1
    private ImageView add_addimage2;        //添加图片2
    private ImageView add_addimage3;        //添加图片3
    private TimePicker add_begintime;       //开始时间
    private TimePicker add_endtime;         //结束时间
    private LinearLayout linear_repeat;     //选择重复信息的选项布局
    private TextView add_repeat;            //重复信息的显示
    private LinearLayout linear_location;  //选择位置的选项布局
    private TextView add_location;         //位置信息的显示
    private EditText add_price;      //价格
    private EditText add_discribe;  //车位的描述信息


    private CheckBox choice_monday;  //选择周一的选择框
    private CheckBox choice_tuesday;  //选择周二的选择框
    private CheckBox choice_wednesday;  //选择周三的选择框
    private CheckBox choice_thursday;  //选择周四的选择框
    private CheckBox choice_friday;  //选择周五的选择框
    private CheckBox choice_saturday;  //选择周六的选择框
    private CheckBox choice_sunday;  //选择周天的选择框

    private static String mYear;  //年份
    private static String mMonth;//月份
    private static String mDay;   //日期
    private static String mWay;   //星期
    private static String mHour;   //时
    private static String mMinute;   //分

    private static String repeat="";  //重复的信息

    private String createdTime;  //创建时间
    private String image1="image1";   //第一张图片的链接
    private String image2="image2";    //第二张图片的链接
    private String image3="image3";   //第三张图片的链接
    private String beginTime; //开始的时间
    private String endTime;  //结束的时间
    private String isOpen="1";     //是否开启，0代表关闭，1或任意非0数字代表开启
    private static String monday="0";     //周一是否开启
    private static String tuesday="0";    //周二是否开启
    private static String wednesday="0";  //周三是否开启
    private static String thursday="0";   //周四是否开启
    private static String friday="0";     //周五是否开启
    private static String saturday="0";   //周六是否开启
    private static String sunday="0";     //周日是否开启
    private String addlocation=""; //位置
    private String longitude="0.0";  //经度
    private String latitude="0.0";  //纬度
    private String price;    //每小时价格
    private String description;   //车位的描述
    private String usedtime;      //被使用的次数

    private double lon;   //经纬度信息
    private double lat;
    private String addloc;  //位置信息
    int m=0;
    private String httpurl="setinfo";
    private String message="";   //服务器返回信息
    private int responseCode;  //返回码

    private SharedPreferences sp;    //读取手机数据


    //拍照相关
    private String[] items = new String[] { "选择本地图片", "拍照" };
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "/faceImage.jpg";

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int RESULT_GET_LOCATION = 5;

    private Bitmap bitmap1,bitmap2,bitmap3;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_add);

        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        sp=this.getSharedPreferences("Cookies",Context.MODE_PRIVATE);
        //标题栏相关设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.publish_add_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add_preserve = (Button) findViewById(R.id.add_preserve);
        layout_addimage1= (LinearLayout) findViewById(R.id.layout_addimage1);
        layout_addimage2= (LinearLayout) findViewById(R.id.layout_addimage2);
        layout_addimage3= (LinearLayout) findViewById(R.id.layout_addimage3);
        add_addimage1 = (ImageView) findViewById(R.id.add_addimage1);
        add_addimage2 = (ImageView) findViewById(R.id.add_addimage2);
        add_addimage3 = (ImageView) findViewById(R.id.add_addimage3);
        add_begintime = (TimePicker) findViewById(R.id.add_begintime);
        add_endtime = (TimePicker) findViewById(R.id.add_endtime);
        linear_repeat = (LinearLayout) findViewById(R.id.linear_repeat);
        add_repeat = (TextView) findViewById(R.id.add_repeat);
        linear_location = (LinearLayout) findViewById(R.id.linear_location);
        add_location = (TextView) findViewById(R.id.add_location);
        add_price = (EditText) findViewById(R.id.add_price);
        add_discribe = (EditText) findViewById(R.id.add_discribe);

        add_begintime.setIs24HourView(true);  //将开始的时间定义为24小时制
        add_endtime.setIs24HourView(true);  //将开始的时间定义为24小时制

        //设置初始时
        add_begintime.setCurrentHour(8);
        add_begintime.setCurrentMinute(30);
        add_endtime.setCurrentHour(18);
        add_endtime.setCurrentMinute(30);

        linear_repeat.setOnClickListener(this);    //为重复的layout设置点击事件
        linear_location.setOnClickListener(this);  //为位置的layout设置点击事件

        add_preserve.setOnClickListener(this); //为保存按钮设置点击事件

        add_addimage1.setDrawingCacheEnabled(true);
        add_addimage2.setDrawingCacheEnabled(true);
        add_addimage3.setDrawingCacheEnabled(true);
        add_addimage1.setOnClickListener(this);
        add_addimage2.setOnClickListener(this);
        add_addimage3.setOnClickListener(this);

        getData();   //调用获取日期信息的函数
        setDefaultWay();   //在重复信息的一栏默认显示当天

        if(m==0) {
            getRecentLocation();  //获取当前位置信息
        }
    }


    //获取当前的时间信息
    public static void getData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = changetime(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = changetime(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  //获取当天为星期几
        mHour=changetime(c.get(Calendar.HOUR_OF_DAY));   //获取实时的小时信息
        mMinute = changetime(c.get(Calendar.MINUTE));   //获取分钟信息
    }

    //设置默认的显示的时间
    void setDefaultWay(){
        if ("1".equals(mWay)) {
            mWay = "星期天";
            monday="0";
            tuesday="0";
            wednesday="0";
            thursday="0";
            friday="0";
            saturday="0";
            sunday="1";
        } else if ("2".equals(mWay)) {
            mWay = "星期一";
            monday="1";
            tuesday="0";
            wednesday="0";
            thursday="0";
            friday="0";
            saturday="0";
            sunday="0";
        } else if ("3".equals(mWay)) {
            mWay = "星期二";
            monday="0";
            tuesday="1";
            wednesday="0";
            thursday="0";
            friday="0";
            saturday="0";
            sunday="0";
        } else if ("4".equals(mWay)) {
            mWay = "星期三";
            monday="0";
            tuesday="0";
            wednesday="1";
            thursday="0";
            friday="0";
            saturday="0";
            sunday="0";
        } else if ("5".equals(mWay)) {
            mWay = "星期四";
            monday="0";
            tuesday="0";
            wednesday="0";
            thursday="1";
            friday="0";
            saturday="0";
            sunday="0";
        } else if ("6".equals(mWay)) {
            mWay = "星期五";
            monday="0";
            tuesday="0";
            wednesday="0";
            thursday="0";
            friday="1";
            saturday="0";
            sunday="0";
        } else if ("7".equals(mWay)) {
            mWay = "星期六";
            monday="0";
            tuesday="0";
            wednesday="0";
            thursday="0";
            friday="0";
            saturday="1";
            sunday="0";
        }
        add_repeat.setText(mWay);    //重复的信息默认显示为当天
    }

    //获取要上传的信息
    public void getPublishInformation(){

        beginTime=String.valueOf(changetime(add_begintime.getCurrentHour())+":"+String.valueOf(changetime(add_begintime.getCurrentMinute())));
        endTime=String.valueOf(changetime(add_endtime.getCurrentHour())+":"+String.valueOf(changetime(add_endtime.getCurrentMinute())));
        createdTime=mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute;
        price=add_price.getText().toString();
        description=add_discribe.getText().toString();
        usedtime="0";

        longitude=String.valueOf(lon);
        latitude=String.valueOf(lat);
        addlocation=addloc;


        if(bitmap1!=null){
            image1=PictureTools.ToString(bitmap1);
        }
        else {
            image1="";
        }

        if(bitmap2!=null){
            image2=PictureTools.ToString(bitmap2);
        }
        else {
            image2="";
        }
        if(bitmap3!=null){
            image3=PictureTools.ToString(bitmap3);
        }
        else {
            image3="";
        }
    }

    //对弹出对话框选择重复的范围时做出初始化的操作
    public void setDefaultDay(){
        if(monday.equals("1")){
            choice_monday.setChecked(true);
        }
        if(tuesday.equals("1")){
            choice_tuesday.setChecked(true);
        }
        if(wednesday.equals("1")){
            choice_wednesday.setChecked(true);
        }
        if(thursday.equals("1")){
            choice_thursday.setChecked(true);
        }
        if(friday.equals("1")){
            choice_friday.setChecked(true);
        }
        if(saturday.equals("1")){
            choice_saturday.setChecked(true);
        }
        if(sunday.equals("1")){
            choice_sunday.setChecked(true);
        }
    }

    //点击弹出选择重复信息对话框的操作
    public void setClickDialog(){
        int t=0;
        if(choice_monday.isChecked()){
            monday="1";
            repeat=repeat+" 星期一";
            t++;
        }else {
            monday="0";
        }
        if(choice_tuesday.isChecked()){
            tuesday="1";
            repeat=repeat+" 星期二";
            t++;
        }else {
            tuesday="0";
        }
        if(choice_wednesday.isChecked()){
            wednesday="1";
            repeat=repeat+" 星期三";
            t++;
        }else {
            wednesday="0";
        }
        if(choice_thursday.isChecked()){
            thursday="1";
            repeat=repeat+" 星期四";
            t++;
        }else {
            thursday="0";
        }
        if(choice_friday.isChecked()){
            friday="1";
            repeat=repeat+" 星期五";
            t++;
        }else {
            friday="0";
        }
        if(choice_saturday.isChecked()){
            saturday="1";
            repeat=repeat+" 星期六";
            t++;
        }else {
            saturday="0";
        }
        if(choice_sunday.isChecked()){
            sunday="1";
            repeat=repeat+" 星期日";
            t++;
        }else {
            sunday="0";
        }
        if(t==7){
            repeat=" 每天 " ;
        }
    }

    //点击事件的响应
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_repeat:         //点击重复信息那一行的操作
                View contents = View.inflate(this, R.layout.pop_repeat_choice, null);
                //对话框点击的绑定
                choice_monday = (CheckBox) contents.findViewById(R.id.choice_monday);
                choice_tuesday = (CheckBox) contents.findViewById(R.id.choice_tuesday);
                choice_wednesday = (CheckBox) contents.findViewById(R.id.choice_wednesday);
                choice_thursday = (CheckBox) contents.findViewById(R.id.choice_thursday);
                choice_friday = (CheckBox) contents.findViewById(R.id.choice_friday);
                choice_saturday = (CheckBox) contents.findViewById(R.id.choice_saturday);
                choice_sunday = (CheckBox) contents.findViewById(R.id.choice_sunday);
                setDefaultDay();
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setTitle("请选择时间")
                        .setContentView(contents)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                repeat="";
                                setClickDialog();
                                add_repeat.setText(repeat);
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                break;

            case R.id.linear_location:     //点击位置信息那一行的操作
                Intent intent = new Intent(PublishAddActivity.this, PublishAddLocationActivity.class);
                startActivityForResult(intent,RESULT_GET_LOCATION);     //使用这个函数是为了得到下一个界面返回的值
                break;
            case R.id.add_preserve:
                getData();
                getPublishInformation();
                CustomDialog.Builder builder1 = new CustomDialog.Builder(this);
                builder1.setTitle("提示")
                        .setMessage("您确定填写的信息完全正确，不需要修改吗？")
                        .setPositiveButton("不需要修改", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();   //对话框消失
                                getPublishInformation();    //获取将要上传的信息
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
//                                            if(!CheckCookie.hasCookie()){     //无cookie状态链接 获取链接
//                                                return null;
//                                            }
                                            httpConnection.GetConnect(CheckCookie.getCookie());     //有cookie状态链接 获取链接
//                                            httpConnection.GetConnect();
                                            JSONObject jsonObject=new JSONObject();   //新建Json对象
                                            jsonObject.put("createdtime", params[20]);   //初始化JSON
                                            if(!params[1].equals(""))
                                                 jsonObject.put("image1", params[1]);
                                            if(!params[2].equals(""))
                                                jsonObject.put("image2", params[2]);
                                            if(!params[3].equals(""))
                                                jsonObject.put("image3", params[3]);
                                            jsonObject.put("starttime", params[4]);
                                            jsonObject.put("endtime", params[5]);
                                            jsonObject.put("isopen", params[6]);
                                            jsonObject.put("MON", params[7]);
                                            jsonObject.put("TUE", params[8]);
                                            jsonObject.put("WED", params[9]);
                                            jsonObject.put("THU", params[10]);
                                            jsonObject.put("FRI", params[11]);
                                            jsonObject.put("SAT", params[12]);
                                            jsonObject.put("SUN", params[13]);
                                            jsonObject.put("location", params[14]);
                                            jsonObject.put("longitude", params[15]);
                                            jsonObject.put("latitude", params[16]);
                                            jsonObject.put("price", params[17]);
                                            jsonObject.put("description", params[18]);
                                            jsonObject.put("usedtimes", params[19]);

                                            httpConnection.LoadData(String.valueOf(jsonObject));  //上传数据

                                            if(httpConnection.getResponseCode()==200){//返回码
                                                   //获取服务器返回数据
                                                responseCode=200;
                                                JSONObject object = new JSONObject(httpConnection.ReadData());
                                                message=object.getString("message");
                                            }
                                            else {
                                                responseCode=0;
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
                                        if(responseCode!=200){
                                            Toast.makeText(getApplication(), "没有成功上传，请检查网络", Toast.LENGTH_LONG).show();
                                        }
                                        else if(message.equals("success")){
                                            Intent intent=new Intent();
                                            intent.setClass(PublishAddActivity.this, PublishListActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);   //界面切换动画
                                        }
                                        else {
                                            Toast.makeText(getApplication(),"发生未知错误，请重新上传",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }.execute(httpurl,image1,image2,image3,beginTime,endTime,isOpen, monday,tuesday,
                                        wednesday,thursday,friday,saturday,sunday,addlocation,longitude,latitude,price,description,usedtime,createdTime);
                                //设置你的操作事项
                            }
                        })
                        .setNegativeButton("我需要修改", new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //直接将对话框消失来进行修改的操作
                                dialog.dismiss();
                            }
                        });
                builder1.create().show();
                break;
            case R.id.add_addimage1:
                imageView=add_addimage1;
                showDialog();
                break;
            case R.id.add_addimage2:
                imageView=add_addimage2;
                showDialog();
                break;
            case R.id.add_addimage3:
                imageView=add_addimage3;
                showDialog();
                break;

            default:
                break;
        }
    }


    //获取当前位置信息
    public void getRecentLocation(){
        LocationClient locationClient = new LocationClient(this);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setProdName("LocationDemo"); // 设置产品线名称
        option.setScanSpan(1000); // 设置定时定位的时间间隔。单位毫秒
        option.setAddrType("all"); //返回的定位结果
        locationClient.setLocOption(option);
        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
               // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
                if (location.getDistrict() != null&&m==0) {

                    m=1;
                    String address=location.getAddrStr();
                    add_location.setText(address);

                    latitude=String.valueOf(location.getLatitude());    //为纬度赋值
                    longitude=String.valueOf(location.getLongitude());  //为经度赋值
                    lon=location.getLongitude();
                    lat=location.getLatitude();
                    addloc=address;   //为位置信息赋值
                }
            }
        });
        locationClient.start();
    }

    public static String changetime(int value)
    {
        return value>=10? ""+value:"0"+value;
    }




    //添加照片
    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("添加照片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (Tools.hasSdcard()) {
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                }
                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //回调函数，得到从下一个activity传回来的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Tools.hasSdcard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(PublishAddActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        System.out.println(data);
                        getImageToView(data);
                        if(imageView==add_addimage1)
                            layout_addimage2.setVisibility(View.VISIBLE);
                        if(imageView==add_addimage2)
                            layout_addimage3.setVisibility(View.VISIBLE);
                    }
                    break;
                case RESULT_GET_LOCATION:
                    Bundle bundle=data.getExtras();
                    lon=bundle.getDouble("longitude");
                    lat=bundle.getDouble("latitude");
                    addloc=bundle.getString("location");
                    add_location.setText( addloc);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //裁剪照片
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,2);
    }
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            imageView.setImageDrawable(drawable);
            switch (imageView.getId()){
                case R.id.add_addimage1:
                    bitmap1=Bitmap.createBitmap(add_addimage1.getDrawingCache());
                    break;
                case R.id.add_addimage2:
                    bitmap2=Bitmap.createBitmap(add_addimage2.getDrawingCache());
                    break;
                case R.id.add_addimage3:
                    bitmap3=Bitmap.createBitmap(add_addimage3.getDrawingCache());
                    break;
            }

        }
    }
}
