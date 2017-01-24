package com.fighter.quickstop.findcarpos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.fighter.quickstop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuch on 2016/2/9.
 */
public class FindAdapt extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    final ArrayList<String> adaptlist=new ArrayList<String>();
    public  ArrayList<String> getAdaptlist(){
        return adaptlist;
    }
    public FindAdapt(Context context, List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class Findh{//寻找的类一行的组件类
        public ImageView editlistlinear_pic;
        public TextView editlistlinear_location;
        public TextView editlistlinear_des;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Findh findh;//申明组件
        if(convertView==null){
            findh=new Findh();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.editlist_linear, null);
            findh.editlistlinear_pic=(ImageView)convertView.findViewById(R.id.editlinear_pic);
            findh.editlistlinear_des=(TextView)convertView.findViewById(R.id.editlistlinear_des);
            findh.editlistlinear_location=(TextView)convertView.findViewById(R.id.editlistlinear_location);
            convertView.setTag(findh);
        }else{
            findh=(Findh)convertView.getTag();
        }
        //绑定数据
        findh.editlistlinear_pic.setBackgroundResource((Integer)data.get(position).get("editlistlinear_pic"));
        findh.editlistlinear_des.setText(data.get(position).get("editlistlinear_des").toString());
        findh.editlistlinear_location.setText(data.get(position).get("editlistlinear_location").toString());
        return convertView;
    }
}
