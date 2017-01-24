package com.fighter.quickstop.entrance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import com.fighter.quickstop.MainActivity;
import com.fighter.quickstop.R;
import com.fighter.quickstop.findcarpos.Findcarpos;
import com.fighter.quickstop.publish.PublishListActivity;
import com.fighter.quickstop.utils.CheckCookie;
import com.fighter.quickstop.utils.CircleImageView;
import com.fighter.quickstop.utils.ClearEditText;
import com.fighter.quickstop.utils.HttpConnection;
import com.fighter.quickstop.utils.LoadingDialog;
import com.fighter.quickstop.utils.PictureTools;
import com.fighter.quickstop.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private ClearEditText username;
    private ClearEditText password;
    private CircleImageView userpicture;
    private Bitmap user_picture;
    private String temp;
    private Button login;
    private TextView newuser;
    private SharedPreferences sp;
    String message="请检查网络连接";
    private final String urlPath="login";
    private final String picPath="getpicture";
    URL url;
    private String pic;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingDialog=new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);

        sp=this.getSharedPreferences("Cookies",Context.MODE_PRIVATE);
        username= (ClearEditText) findViewById(R.id.username);
        password= (ClearEditText) findViewById(R.id.password);
        login= (Button) findViewById(R.id.login);
        newuser= (TextView) findViewById(R.id.newuser);
        userpicture= (CircleImageView) findViewById(R.id.userpicture);
        userpicture.setImageDrawable(getResources().getDrawable(R.drawable.defaultpic));
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(username.getText().toString().trim().length()<6) {
                    userpicture.setImageDrawable(getResources().getDrawable(R.drawable.defaultpic));
                    return;
                }
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            HttpConnection getpicture=new HttpConnection(params[0]);
                            getpicture.GetConnect();
                            JSONObject pic_name=new JSONObject();
                            pic_name.put("username",params[1]);
                            getpicture.LoadData(String.valueOf(pic_name));
                            if(getpicture.getResponseCode()==200)
                            {
                                temp=getpicture.ReadData();
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
                        try {
                            if(temp==null||temp.length()==0) {
                                userpicture.setImageDrawable(getResources().getDrawable(R.drawable.defaultpic));
                                return;
                            }
                            JSONObject tempjson=new JSONObject(temp);
                            pic=tempjson.getString("pic");
                            user_picture= PictureTools.ToBitmap(pic);
                            userpicture.setImageBitmap(user_picture);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(picPath, username.getText().toString().trim());
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        JSONObject user = new JSONObject();
                        try {
                            user.put("username", params[0]);
                            user.put("password", params[1]);
                            HttpConnection connection = new HttpConnection(params[2]);
                            connection.GetConnect();
                            connection.LoadData(String.valueOf(user));
                            int code = connection.getResponseCode();
                            if (code != 200) {
                                message = "请检查网络连接";
                            } else {
                                JSONObject jsonObject = new JSONObject(connection.ReadData());
                                message = jsonObject.getString("message");
                                String nickname = jsonObject.getString("nickname");
                                Tools.saveData(sp, "nickname", nickname);
                                pic=jsonObject.getString("pic");
                                PictureTools.SavePicture(sp, pic);
                                connection.GetCookie(sp);
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
                        if (message.equals("登陆成功")) {
                            startActivity(new Intent(LoginActivity.this, Findcarpos.class));
                            finish();
                        }
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }.execute(username.getText().toString(), password.getText().toString(), urlPath);
            }
        });
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CodeGet.class));
            }
        });

    }
}
