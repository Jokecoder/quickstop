package com.fighter.quickstop.findcarpos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuch on 2016/3/17.
 */
public class OrderObject implements Serializable{
    private String userimage;   //用户图片
    private String username;  //用户名

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;//用户昵称
    private String createdTime;  //创建时间
    private String image1;   //第一张图片的链接
    private String image2;    //第二张图片的链接
    private String image3;   //第三张图片的链接
    private int usedcount;//用户统计
    private String location; //位置
    private double longitude;  //经度
    private double latitude;  //纬度
    private int price;    //每小时价格
    private String description;   //车位的描述
    private String starttime;//开始时间
    private String endtime;//结束时间

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
    //    public ArrayList<Integer> getTimelist() {
//        return timelist;
//    }
//
//    public void setTimelist(ArrayList<Integer> timelist) {
//        this.timelist = timelist;
//    }
//
//    private ArrayList<Integer> timelist;//时间序列

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUsedcount() {
        return usedcount;
    }

    public void setUsedcount(int usedcount) {
        this.usedcount = usedcount;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
