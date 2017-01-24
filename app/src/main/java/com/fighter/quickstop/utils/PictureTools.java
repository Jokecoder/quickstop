package com.fighter.quickstop.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Since on 2016/3/24.
 */
public class PictureTools {
    public static String ToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public static Bitmap ToBitmap(String pic){
        Bitmap bitmap;
        byte[] bitmapArray;
        System.out.println(pic+"333333");
        bitmapArray = Base64.decode(pic, Base64.DEFAULT);
        bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        return bitmap;
    }
    public static void SavePicture(SharedPreferences shared,String pic){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("userpicture", pic);
        editor.commit();
    }
    public static String ReadPicture(SharedPreferences shared){
        String userpicture = shared.getString("userpicture", null);
        return userpicture;
    }

    public static boolean HasPicture(SharedPreferences shared){
        String userpicture = shared.getString("userpicture", null);
        if (userpicture == null || userpicture.equals("")) {
            return false;
        }
        return true;
    }
}
