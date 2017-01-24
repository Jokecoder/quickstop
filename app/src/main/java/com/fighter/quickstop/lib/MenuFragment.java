package com.fighter.quickstop.lib;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fighter.quickstop.R;
import com.fighter.quickstop.entrance.LoginActivity;
import com.fighter.quickstop.findcarpos.Findcarpos;
import com.fighter.quickstop.myorderlist.Myorderlist;
import com.fighter.quickstop.publish.PublishAddActivity;
import com.fighter.quickstop.publish.PublishListActivity;
import com.fighter.quickstop.utils.CircleImageView;
import com.fighter.quickstop.utils.CustomDialog;
import com.fighter.quickstop.utils.PictureTools;
import com.fighter.quickstop.utils.Tools;

/**
 * Created by l1034 on 2015/9/17.
 */
public class MenuFragment extends Fragment {
    private CircleImageView sl_picture;
    private SharedPreferences sp;
    private TextView sl_nickname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_menu, null);
        sp=getActivity().getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        sl_nickname= (TextView) view.findViewById(R.id.sl_nickname);
        sl_picture= (CircleImageView) view.findViewById(R.id.sl_picture);
        sl_picture.setImageBitmap(PictureTools.ToBitmap(PictureTools.ReadPicture(sp)));
        sl_nickname.setText(Tools.getData(sp,"nickname"));
        view.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage("您确认退出吗？\n（注:这将清空您所有的缓存信息）");
                builder.setTitle("警告");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Tools.clearData(sp);
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage("您确认切换吗？\n（注:这将清空您所有的缓存信息）");
                builder.setTitle("警告");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Tools.clearData(sp);
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        view.findViewById(R.id.myloca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PublishListActivity.class));
            }
        });
        view.findViewById(R.id.mypay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Myorderlist.class));
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
