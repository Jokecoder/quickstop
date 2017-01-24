package com.fighter.quickstop.utils;

import android.content.SharedPreferences;



/**
 * Created by Since on 2016/2/26.
 */
public class CheckCookie {
    private static SharedPreferences shared;
    private static String cookiename = "Cookie";

    public static void setShared(SharedPreferences shared){
        CheckCookie.shared=shared;
    }
    public static boolean hasCookie() {
        String _session_id = shared.getString(cookiename, null);
        if (_session_id == null || _session_id.equals("")) {
            return false;
        }
        return true;
    }
    public static String getCookie() {
        String _session_id = shared.getString(cookiename, null);
        return _session_id;
    }

}
