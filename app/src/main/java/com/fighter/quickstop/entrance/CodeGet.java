package com.fighter.quickstop.entrance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fighter.quickstop.R;



public class CodeGet extends AppCompatActivity {

    private TextView service_declear;
    private CheckBox checkBox;
    private EditText phonenumber;
    private Button next_button;
    private String acode="+86";
    private Spinner area_codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_get);
        Toolbar toolbar= (Toolbar) findViewById(R.id.code_toolbar);
        toolbar.setTitle("");
//        SMSSDK.initSDK(this, "9e2b178bb86c", "e536c119e5abe1699cb705bde727f5db");
//        SMSSDK.getVerificationCode("86", "18615971036");
        service_declear= (TextView) findViewById(R.id.service_declear);
        service_declear.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkBox= (CheckBox) findViewById(R.id.checkBox);
        next_button= (Button) findViewById(R.id.get_next);
        area_codes= (Spinner) findViewById(R.id.area_code);
        phonenumber= (EditText) findViewById(R.id.phonenumber);
//        String[] acodes=getResources().getStringArray(R.array.codes);
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.codes_item,acodes);
//        area_codes.setAdapter(adapter);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    next_button.setEnabled(true);
                else
                    next_button.setEnabled(false);
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CodeGet.this, CodeSubmit.class);
                intent.putExtra("username",phonenumber.getText().toString().trim());
                startActivity(intent);
            }
        });
        area_codes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] acodes=getResources().getStringArray(R.array.codes);
                acode=acodes[position];
                Toast.makeText(CodeGet.this,acode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
