package com.fighter.quickstop.utils;

import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import android.content.SharedPreferences.Editor;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Since on 2016/2/25.
 */
public class HttpConnection {
//    private static String stapath = "http://quicktest.nat123.net/";
    private static String stapath = "http://www.liangjiancloud.com/";
    private static String cookiename = "Cookie";
    private URL url;
    HttpURLConnection conn;

    public HttpConnection(String path) throws MalformedURLException {
        url = new URL(stapath + path+"/");
    }

    public void GetConnect() throws IOException {
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.connect();
    }
    public void GetConnect(String usercookie) throws IOException {
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        System.out.println(usercookie);
        conn.setRequestProperty("Cookie", usercookie);
        conn.connect();
    }

    public void GetCookie(SharedPreferences shared) {
        String cookie = conn.getHeaderField("Set-Cookie");
        Editor editor = shared.edit();
        editor.putString(cookiename, cookie);
        editor.commit();
    }

    public void LoadData(String data) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(data);
        bw.flush();
        bw.close();
        osw.close();
    }

    public int getResponseCode() throws IOException {
        return conn.getResponseCode();
    }

    public String ReadData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
