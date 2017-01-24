package com.fighter.quickstop.findcarpos;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.fighter.quickstop.R;

import java.util.ArrayList;

/**
 * Created by zhuch on 2016/3/8.
 */
public class FindcarFragment extends Fragment {
    View view;
    Button orderbut;//预定按钮
    TextView location;//位置
    TextView detail;//详细
    TextView price;//价格
    OrderObject orderObject;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.findcarframe,null);

        initWidget();
        initDatas();
        setListen();
        return view;
    }
    void initWidget(){
        orderbut=(Button)view.findViewById(R.id.orderButton);
        location=(TextView)view.findViewById(R.id.location_order);//车位位置
        detail=(TextView)view.findViewById(R.id.detail_order);//详细信息
        price=(TextView)view.findViewById(R.id.price_order);//价格
    }
    void initDatas(){
        orderObject=(OrderObject)getArguments().getSerializable(FindcarOrder.FCP_ORDER);//上个界面传递过来的所有的值
        location.setText(orderObject.getLocation());//设置位置
        detail.setText(orderObject.getDescription());//设置描述
        price.setText(orderObject.getPrice()+"");//设置价格
    }
    void setListen(){
        orderbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTimeframe change=new ChangeTimeframe();//替换下面的Fragment
                Bundle bundle=new Bundle();
                bundle.putSerializable(FindcarOrder.FCP_ORDER,orderObject);
                change.setArguments(bundle);//设置传递的参数
                getFragmentManager().beginTransaction().replace(R.id.orderFrame,change,null).commit();//加载界面
            }
        });
    }
}
