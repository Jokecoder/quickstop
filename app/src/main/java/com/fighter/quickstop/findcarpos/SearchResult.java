package com.fighter.quickstop.findcarpos;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.fighter.quickstop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhuch on 2016/2/9.
 */

public class SearchResult extends AppCompatActivity {

    Toolbar toolbar;
    ListView list_result;
    TextView no_result;
    String inputkey;
    FindAdapt adapt;
    List<Map<String, Object>> alldatalist;
    List<Map<String, Object>> datalist;
    ArrayList<FindcarposObject> allpi;//所有发布了的信息
    private final String url="findcarpos";//获取所有发布的信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.searchresult);

        initwidget();//初始各种控件

        initData();//初始数据
        getData();

        setListener();
    }
    void initwidget(){

        toolbar= (Toolbar) findViewById(R.id.regist_toolbar);//找到标题栏
        no_result=(TextView)findViewById(R.id.no_result);//未找到结果
        list_result=(ListView)findViewById(R.id.list_result); //结果listview

    }
    void initData(){
        inputkey=(String)getIntent().getStringExtra("inputkey");
        allpi=(ArrayList<FindcarposObject>)getIntent().getSerializableExtra(Findcarpos.FCP_MAIN);//上个界面传递过来的所有的值
        toolbar.setTitle("");//设置标题栏文字
        setSupportActionBar(toolbar);//设置为标题栏
        no_result.setVisibility(View.VISIBLE);//初始可见
        list_result.setVisibility(View.GONE);//初始不可见

        datalist=getData();
        list_result.setAdapter(new FindAdapt(this, datalist));
    }
    void setListener(){//事件触发
        list_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String username=datalist.get(position).get("username").toString();
                        String createdtime=datalist.get(position).get("createdtime").toString();
                        Intent intent = new Intent();
                        intent.putExtra("username",username);
                        intent.putExtra("createdtime",createdtime);
                        intent.setClass(SearchResult.this, FindcarOrder.class);
                        startActivity(intent);
                        SearchResult.this.finish();

            }
        });
    }
    public List<Map<String, Object>> getData(){//获取数据
        alldatalist=new ArrayList<Map<String,Object>>();
        datalist=new ArrayList<Map<String,Object>>();
        for (int i = 0;i<allpi.size(); i++) {
            FindcarposObject fc=allpi.get(i);
            if(fc.getDescription().contains(inputkey)||fc.getLocation().contains(inputkey)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("editlistlinear_pic", R.drawable.publish_preset2);
                map.put("editlistlinear_location", allpi.get(i).getLocation());
                map.put("editlistlinear_des", allpi.get(i).getDescription());
                map.put("username", fc.getUsername());
                map.put("createdtime", fc.getCreatedTime());
                datalist.add(map);
                alldatalist.add(map);
            }
        }
        adapt=new FindAdapt(getBaseContext(), datalist);
        list_result.setAdapter(adapt);

        no_result.setVisibility(View.GONE);//初始可见
        list_result.setVisibility(View.VISIBLE);//初始不可见
        return datalist;
    }
}
