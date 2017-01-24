package com.fighter.quickstop.utils;

import android.content.SharedPreferences;
import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by Since on 2016/2/17.
 */
public class Tools {
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    public static String getData(SharedPreferences shared,String key){
        String data = shared.getString(key, null);
        return data;
    }
    public static void saveData(SharedPreferences shared,String key,String data){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, data);
        editor.commit();
    }
    public static void clearData(SharedPreferences shared)
    {
        shared.edit().clear().commit();
    }

    public static boolean saveArray(SharedPreferences shared,String key,ArrayList<String> data) {
        SharedPreferences.Editor mEdit1= shared.edit();
        mEdit1.remove(key);
        mEdit1.putInt(key,data.size()); /*data is an array*/
        for(int i=0;i<data.size();i++) {
            mEdit1.remove(key + i);
            mEdit1.putString(key + i,data.get(i));
        }
        return mEdit1.commit();
    }
    public static int getArray(SharedPreferences shared,String key,ArrayList<String> data) {
        int r = 0;
        try {
            int size=shared.getInt(key,0);
            for(int i=0;i<size;i++) {
                data.add(shared.getString(key+ i, null));
            }
            return 1;
        } catch (Exception e) {
            r = 0;
        }
        return r;
    }
    public static int getTime(String time){
        String c="yyyy年MM月dd日HH:mm:ss";
        String year,month,day,hour,minute;
        month=time.substring(5,7);
        day=time.substring(8,10);
        hour=time.substring(11,13);
        minute=time.substring(14,16);
        int a= Integer.parseInt(month+day+hour+minute);
        return a;
    }
}
