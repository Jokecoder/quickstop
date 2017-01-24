package com.fighter.quickstop.entrance;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fighter.quickstop.R;

public class CodeSubmit extends AppCompatActivity {

    private Button submit_next;
    private Button retry;
    private EditText codeinput;
    private TimeCount time;
    private String phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_submit);
        Toolbar toolbar= (Toolbar) findViewById(R.id.submit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ret_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        phonenumber=getIntent().getExtras().getString("username");
        time=new TimeCount(60000,1000);
        time.start();
        codeinput= (EditText) findViewById(R.id.codeinput);
        retry= (Button) findViewById(R.id.retry);
        submit_next= (Button) findViewById(R.id.submit_next);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.start();
            }
        });
        submit_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CodeSubmit.this,RegistActivity.class);
                intent.putExtra("username",phonenumber);
                startActivity(intent);
                finish();
            }
        });
    }
    class TimeCount extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            retry.setEnabled(false);
            retry.setText("重新发送("+millisUntilFinished/1000+")");
        }

        @Override
        public void onFinish() {
            retry.setEnabled(true);
            retry.setText("重新发送");
        }
    }
}
