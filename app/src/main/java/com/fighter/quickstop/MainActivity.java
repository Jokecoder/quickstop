package com.fighter.quickstop;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fighter.quickstop.entrance.LoginActivity;
import com.fighter.quickstop.entrance.RegistActivity;
import com.fighter.quickstop.entrance.Welcome;
import com.fighter.quickstop.lib.MenuFragment;
import com.fighter.quickstop.lib.Sliding;
import com.fighter.quickstop.lib.SlidingFragmentActivity;

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

public class MainActivity extends SlidingFragmentActivity {

    private Fragment mContent;
    private EditText editText;
    private Button button;
    String success;
    final String urlPath="http://quicktest.nat123.net/input/";
    final String sms="http://quicktest.nat123.net/smssdk/";
    URL url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hello Word");
        setSupportActionBar(toolbar);

//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, RegistActivity.class));
//                finish();
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });

        editText= (EditText) findViewById(R.id.editText);
//        button= (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AsyncTask<String, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(String... params) {
////                        Looper.prepare();
//                        JSONObject object=new JSONObject();
//                        try {
//                            url=new URL(params[0]);
//                            object.put("name",params[1]);
//                            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//                            conn.setDoOutput(true);
//                            conn.setRequestMethod("POST");
//                            OutputStreamWriter osw=new OutputStreamWriter(conn.getOutputStream(),"utf-8");
//                            BufferedWriter bw=new BufferedWriter(osw);
//                            bw.write(String.valueOf(object));
//                            bw.flush();
//                            bw.close();
//                            osw.close();
//                            int code=conn.getResponseCode();
//                            if(code==200)
//                            {
//                                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
//                                String retData=null;
//                                String responseData="";
//                                while ((retData=in.readLine())!=null)
//                                {
//                                    responseData+=retData;
//                                }
//                                JSONObject jsonObject=new JSONObject(responseData);
//                                JSONObject succobject=jsonObject.getJSONObject("regsucc");
//                                success=succobject.getString("id");
//                                in.close();
//                                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
//                                startActivity(intent);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                        Looper.loop();
//                        return  null;
//                    }
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        Toast.makeText(MainActivity.this,success,Toast.LENGTH_SHORT).show();
//                    }
//                }.execute(urlPath,editText.getText().toString().trim());
//            }
//        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Welcome.class));
                finish();
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(MainActivity.this,AreaCode.class));
            }
        });
//        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,Main2Activity.class));
//            }
//        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
