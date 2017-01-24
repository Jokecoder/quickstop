package com.fighter.quickstop.findcarpos;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fighter.quickstop.R;
import com.fighter.quickstop.utils.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuch on 2016/2/13.
*/
 public class FindcarposInput extends Fragment {
    ListView listView=null;
    List<Map<String, Object>> alldatalist;
    List<Map<String, Object>> datalist;
    ClearEditText fcpinputedit;
    TextView findcarpos_searchtext;
    Handler myhandler=new Handler();
    ArrayList<FindcarposObject> allpi;//所有发布了的信息
    FindAdapt adapt;
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.findcarpos_input, null);
        initwidget();

        initData();

        setListener();

         return view;
    }
    void initwidget(){
        listView=(ListView)view.findViewById(R.id.findcarpos_searchlist);//下方的list
        fcpinputedit=(ClearEditText)view.findViewById(R.id.fcpinputedit);//可以清除的输入框
        findcarpos_searchtext=(TextView)view.findViewById(R.id.findcarpos_searchtext);//搜索按钮
    }
    void initData(){
        allpi=(ArrayList<FindcarposObject>)getArguments().getSerializable(Findcarpos.FCP_MAIN);//上个界面传递过来的所有的值
        alldatalist=new ArrayList<Map<String,Object>>();
        datalist=new ArrayList<Map<String,Object>>();
        for (int i = 0;i<allpi.size(); i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("editlistlinear_pic", R.drawable.publish_preset2);
            map.put("editlistlinear_location", allpi.get(i).getLocation());
            map.put("editlistlinear_des", allpi.get(i).getDescription());
            map.put("username", allpi.get(i).getUsername());
            map.put("createdtime", allpi.get(i).getCreatedTime());
            datalist.add(map);
            alldatalist.add(map);
        }
        adapt=new FindAdapt(view.getContext(), datalist);
        listView.setAdapter(adapt);
    }
    void setListener(){
        findcarpos_searchtext.setOnClickListener(new View.OnClickListener() {//搜索按钮触摸
            @Override
            public void onClick(View v) {//调用查找所有结果活动
               Intent intent = new Intent();
                Bundle mbundle=new Bundle();
                mbundle.putString("inputkey",fcpinputedit.getText().toString().trim());
                mbundle.putSerializable(Findcarpos.FCP_MAIN,allpi);//传递值
                intent.putExtras(mbundle);
                intent.setClass(getActivity(), SearchResult.class);
                startActivity(intent);
//                startActivity(new Intent(getActivity(),SearchResult.class));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username=datalist.get(position).get("username").toString();
                String createdtime=datalist.get(position).get("createdtime").toString();
                Intent intent = new Intent();
                intent.putExtra("username",username);
                intent.putExtra("createdtime",createdtime);
                intent.setClass(getActivity(), FindcarOrder.class);
                startActivity(intent);
            }
        });

        fcpinputedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                myhandler.post(eChanged);
            }
        });

    }
    void getContentData(List<Map<String, Object>> t,String a){
        int x=0;
        String c="1";
        for(x=0;x<allpi.size();x++){
            Map map=alldatalist.get(x);

                if(map.get("editlistlinear_location").toString().contains(a)||map.get("editlistlinear_des").toString().contains(a)){
                    Map<String, Object> newmap=new HashMap<String, Object>();
                    newmap.put("editlistlinear_pic", map.get("editlistlinear_pic"));
                    newmap.put("editlistlinear_location",map.get("editlistlinear_location"));
                    newmap.put("editlistlinear_des",map.get("editlistlinear_des"));
                    newmap.put("username", map.get("username"));
                    newmap.put("createdtime",map.get("createdtime"));
                    datalist.add(map);
                }
        }
    }
    Runnable eChanged = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            datalist.clear();//先要清空，不然会叠加
            getContentData(alldatalist,fcpinputedit.getText().toString().trim());//获取更新数据
            adapt.notifyDataSetChanged();//更新
        }
    };
 }

