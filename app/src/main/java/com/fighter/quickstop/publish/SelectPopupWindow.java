package com.fighter.quickstop.publish;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.fighter.quickstop.R;


/**
 * Created by xey on 2016/2/23.
 */
public class SelectPopupWindow extends PopupWindow{
    private View mMenuView;
    private  LinearLayout linear_publish_add;

    public SelectPopupWindow(Activity context,View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_repeat_choice, null);

        linear_publish_add=(LinearLayout)context.findViewById(R.id.linear_publish_add);
//        linear_publish_add.getBackground().setAlpha(210);
        this.setContentView(mMenuView);            //设置SelectPicPopupWindow的View
        this.setWidth(LayoutParams.MATCH_PARENT);  //设置SelectPicPopupWindow弹出窗体的宽
        this.setHeight(LayoutParams.WRAP_CONTENT); //设置SelectPicPopupWindow弹出窗体的高
        this.setFocusable(true);                    //设置SelectPicPopupWindow弹出窗体可点击
        this.setAnimationStyle(R.style.AnimBottom);  //设置SelectPicPopupWindow弹出窗体动画效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);//实例化一个ColorDrawable颜色为半透明
        this.setBackgroundDrawable(dw);               //设置SelectPicPopupWindow弹出窗体的背景
        mMenuView.setOnTouchListener(new OnTouchListener() {   //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_repeat_choice).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
//                        linear_publish_add.getBackground().setAlpha(0);
                    }
                }
                return true;
            }
        });

    }
}
