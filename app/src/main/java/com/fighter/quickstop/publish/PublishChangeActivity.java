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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.fighter.quickstop.R;
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

public class PublishChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button change_makesure;
    private ImageView imageView;            //图片的全局变量
    private LinearLayout layout_changeimage1;
    private LinearLayout layout_changeimage2;
    private LinearLayout layout_changeimage3;
    private ImageView change_image1;  //修改图片1
    private ImageView change_image2;  //修改图片2
    private ImageView change_image3;  //修改图片3
    private TimePicker change_begintime;       //开始时间
    private TimePicker change_endtime;         //结束时间
    private LinearLayout layout_change_repeat;     //选择重复信息的选项布局
    private TextView change_repeat;            //重复信息的显示
    private LinearLayout layout_change_location;  //选择位置的选项布局
    private TextView change_location;         //位置信息的显示
    private EditText change_price;      //价格
    private EditText change_discribe;  //车位的描述信息


    private CheckBox choice_monday;  //选择周一的选择框
    private CheckBox choice_tuesday;  //选择周二的选择框
    private CheckBox choice_wednesday;  //选择周三的选择框
    private CheckBox choice_thursday;  //选择周四的选择框
    private CheckBox choice_friday;  //选择周五的选择框
    private CheckBox choice_saturday;  //选择周六的选择框
    private CheckBox choice_sunday;  //选择周天的选择框

    private static String repeat="";  //重复的信息

    private String createdTime;  //创建时间
    private String image1="";   //第一张图片的链接
    private String image2="";    //第二张图片的链接
    private String image3="";   //第三张图片的链接
    private String beginTime; //开始的时间
    private String endTime;  //结束的时间
    private static String monday="0";     //周一是否开启
    private static String tuesday="0";    //周二是否开启
    private static String wednesday="0";  //周三是否开启
    private static String thursday="0";   //周四是否开启
    private static String friday="0";     //周五是否开启
    private static String saturday="0";   //周六是否开启
    private static String sunday="0";     //周日是否开启
    private String changelocation; //位置
    private String price;    //每小时价格
    private String description;   //车位的描述
    private String httpurl="changeinfo";
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

    private Bitmap bitmap1,bitmap2,bitmap3;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_change);

        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        sp=this.getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        //标题栏相关设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.publish_change_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        change_makesure = (Button) findViewById(R.id.change_makesure);
        layout_changeimage1= (LinearLayout) findViewById(R.id.layout_changeimage1);
        layout_changeimage2= (LinearLayout) findViewById(R.id.layout_changeimage2);
        layout_changeimage3= (LinearLayout) findViewById(R.id.layout_changeimage3);
        change_image1 = (ImageView) findViewById(R.id.change_image1);
        change_image2 = (ImageView) findViewById(R.id.change_image2);
        change_image3 = (ImageView) findViewById(R.id.change_image3);
        change_begintime = (TimePicker) findViewById(R.id.change_begintime);
        change_endtime = (TimePicker) findViewById(R.id.change_endtime);
        layout_change_repeat = (LinearLayout) findViewById(R.id.layout_change_repeat);
        change_repeat = (TextView) findViewById(R.id.change_repeat);
        layout_change_location = (LinearLayout) findViewById(R.id.layout_change_location);
        change_location = (TextView) findViewById(R.id.change_location);
        change_price = (EditText) findViewById(R.id.change_price);
        change_discribe = (EditText) findViewById(R.id.change_discribe);

        change_begintime.setIs24HourView(true);  //将开始的时间定义为24小时制
        change_endtime.setIs24HourView(true);  //将开始的时间定义为24小时制



        layout_change_repeat.setOnClickListener(this);    //为重复的layout设置点击事件
        layout_change_location.setOnClickListener(this);  //为位置的layout设置点击事件

        change_makesure.setOnClickListener(this); //为保存按钮设置点击事件

        change_image1.setDrawingCacheEnabled(true);
        change_image2.setDrawingCacheEnabled(true);
        change_image3.setDrawingCacheEnabled(true);
        change_image1.setOnClickListener(this);
        change_image2.setOnClickListener(this);
        change_image3.setOnClickListener(this);

        getInformationFromLastLayout();
    }

    //获取从上一个界面传过来的所有值并显示到界面上
    public void getInformationFromLastLayout(){
        createdTime=getIntent().getStringExtra("createdtime");
        changelocation=getIntent().getStringExtra("location");
        image1=getIntent().getStringExtra("image1");
        image2=getIntent().getStringExtra("image2");
        image3=getIntent().getStringExtra("image3");
        beginTime=getIntent().getStringExtra("begintime");
        endTime=getIntent().getStringExtra("endtime");
        price=getIntent().getStringExtra("price");
        description=getIntent().getStringExtra("description");
        monday=getIntent().getStringExtra("monday" );
        tuesday=getIntent().getStringExtra("tuesday");
        wednesday =getIntent().getStringExtra("wednesday" );
        thursday=getIntent().getStringExtra("thursday");
        friday=getIntent().getStringExtra("friday");
        saturday=getIntent().getStringExtra("saturday");
        sunday=getIntent().getStringExtra("sunday");

        int t=0;
        repeat="";
        if(monday.equals("1")){
            t++;
            repeat=repeat+" 周一";
        }
        if(tuesday.equals("1")){
            t++;
            repeat=repeat+" 周二";
        }
        if(wednesday.equals("1")){
            t++;
            repeat=repeat+" 周三";
        }
        if(thursday.equals("1")){
            t++;
            repeat=repeat+" 周四";
        }
        if(friday.equals("1")){
            t++;
            repeat=repeat+" 周五";
        }
        if(saturday.equals("1")){
            t++;
            repeat=repeat+" 周六";
        }
        if(sunday.equals("1")){
            t++;
            repeat=repeat+" 周日";
        }
        if(t==7)
            repeat="每天";

        change_repeat.setText(repeat);

        if(image1.equals("")||image1.equals(null)){

        }else {
            change_image1.setImageBitmap(PictureTools.ToBitmap(image1));
            layout_changeimage2.setVisibility(View.VISIBLE);
        }
        if(image2.equals("")||image2.equals(null)){

        }else {
            change_image2.setImageBitmap(PictureTools.ToBitmap(image2));
            layout_changeimage3.setVisibility(View.VISIBLE);
        }
        if(image3.equals("")||image3.equals(null)){

        }else {
            change_image3.setImageBitmap(PictureTools.ToBitmap(image3));
        }
        //设置初始时
        change_begintime.setCurrentHour(Integer.parseInt(beginTime.substring(0,beginTime.indexOf(":"))));
        change_begintime.setCurrentMinute(Integer.parseInt(beginTime.substring(beginTime.indexOf(":") + 1, beginTime.length())));
        change_endtime.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
        change_endtime.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":")+1,endTime.length())));
        change_location.setText(changelocation);
        change_price.setText(price);
        change_discribe.setText(description);
    }


    //获取要修改的信息
    public void getChangeInformation(){
        beginTime=String.valueOf(changetime(change_begintime.getCurrentHour()) + ":" + String.valueOf(changetime(change_begintime.getCurrentMinute())));
        endTime=String.valueOf(changetime(change_endtime.getCurrentHour())+ ":" + String.valueOf(changetime(change_endtime.getCurrentMinute())));
        price=change_price.getText().toString();
        description=change_discribe.getText().toString();
        if(bitmap1!=null){
            image1= PictureTools.ToString(bitmap1);
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
            repeat=repeat+" 周一";
            t++;
        }else {
            monday="0";
        }
        if(choice_tuesday.isChecked()){
            tuesday="1";
            repeat=repeat+" 周二";
            t++;
        }else {
            tuesday="0";
        }
        if(choice_wednesday.isChecked()){
            wednesday="1";
            repeat=repeat+" 周三";
            t++;
        }else {
            wednesday="0";
        }
        if(choice_thursday.isChecked()){
            thursday="1";
            repeat=repeat+" 周四";
            t++;
        }else {
            thursday="0";
        }
        if(choice_friday.isChecked()){
            friday="1";
            repeat=repeat+" 周五";
            t++;
        }else {
            friday="0";
        }
        if(choice_saturday.isChecked()){
            saturday="1";
            repeat=repeat+" 周六";
            t++;
        }else {
            saturday="0";
        }
        if(choice_sunday.isChecked()){
            sunday="1";
            repeat=repeat+" 周日";
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
            case R.id.layout_change_repeat:         //点击重复信息那一行的操作
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
                                change_repeat.setText(repeat);
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;

            case R.id.layout_change_location:     //点击位置信息那一行的操作
                Toast.makeText(PublishChangeActivity.this,"位置信息无法修改",Toast.LENGTH_LONG).show();
                break;
            case R.id.change_makesure:
                CustomDialog.Builder builder1 = new CustomDialog.Builder(this);
                builder1.setTitle("提示")
                        .setMessage("您确定填写的信息完全正确，不需要修改吗？")
                        .setPositiveButton("不需要修改", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();   //对话框消失
                                getChangeInformation(); //获取将要修改的信息
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
                                            jsonObject.put("createdtime", params[15]);   //初始化JSON
                                            if(!params[1].equals(""))
                                                jsonObject.put("image1", params[1]);
                                            if(!params[2].equals(""))
                                                jsonObject.put("image2", params[2]);
                                            if(!params[3].equals(""))
                                                jsonObject.put("image3", params[3]);
                                            jsonObject.put("starttime", params[4]);
                                            jsonObject.put("endtime", params[5]);
                                            jsonObject.put("MON", params[6]);
                                            jsonObject.put("TUE", params[7]);
                                            jsonObject.put("WED", params[8]);
                                            jsonObject.put("THU", params[9]);
                                            jsonObject.put("FRI", params[10]);
                                            jsonObject.put("SAT", params[11]);
                                            jsonObject.put("SUN", params[12]);
                                            jsonObject.put("price", params[13]);
                                            jsonObject.put("description", params[14]);

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
                                            intent.setClass(PublishChangeActivity.this, PublishListActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);   //界面切换动画
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(getApplication(),"发生未知错误，请重新上传",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }.execute(httpurl,image1,image2,image3,beginTime,endTime, monday,tuesday,
                                        wednesday,thursday,friday,saturday,sunday,price,description,createdTime);
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
            case R.id.change_image1:
                imageView=change_image1;
                showDialog();
                break;
            case R.id.change_image2:
                imageView=change_image2;
                showDialog();
                break;
            case R.id.change_image3:
                imageView=change_image3;
                showDialog();
                break;

            default:
                break;
        }
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
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(PublishChangeActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        System.out.println(data);
                        getImageToView(data);
                        if(imageView==change_image1)
                            layout_changeimage2.setVisibility(View.VISIBLE);
                        if(imageView==change_image2)
                            layout_changeimage3.setVisibility(View.VISIBLE);
                    }
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
                case R.id.change_image1:
                    bitmap1=Bitmap.createBitmap(change_image1.getDrawingCache());
                    break;
                case R.id.change_image2:
                    bitmap2=Bitmap.createBitmap(change_image2.getDrawingCache());
                    break;
                case R.id.change_image3:
                    bitmap3=Bitmap.createBitmap(change_image3.getDrawingCache());
                    break;
            }

        }
    }
}
