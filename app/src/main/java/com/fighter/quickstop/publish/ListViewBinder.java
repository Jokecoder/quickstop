package com.fighter.quickstop.publish;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

/**
 * Created by xey on 2016/4/5.
 */
public class ListViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        // TODO Auto-generated method stub
        if ((view instanceof ImageView) && (data instanceof Bitmap)) {
            ImageView imageView = (ImageView) view;
            Bitmap bmp = (Bitmap) data;
            imageView.setImageBitmap(bmp);
            return true;
        }
        return false;
    }
}