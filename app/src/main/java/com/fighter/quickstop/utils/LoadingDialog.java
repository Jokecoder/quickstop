package com.fighter.quickstop.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fighter.quickstop.GifView;
import com.fighter.quickstop.R;

/**
 * Created by Since on 2016/2/2.
 */
public class LoadingDialog extends Dialog{
    private TextView tv;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
//        tv = (TextView)this.findViewById(R.id.tv);
//        tv.setText("请稍候...");
        LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
        GifView gifView= (GifView) findViewById(R.id.loadingcar);
        gifView.setMovieResource(R.drawable.loading_car);
    }
}
