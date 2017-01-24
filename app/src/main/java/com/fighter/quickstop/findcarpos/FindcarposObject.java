package com.fighter.quickstop.findcarpos;

import java.io.Serializable;

/**
 * Created by zhuch on 2016/3/15.
 */
public class FindcarposObject implements Serializable {
    private static final long serialVersionUID = 1250374334815016472L;
    private String username;  //用户名
    private String starttime;//
    private String endtime;//
    private String price;//

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getUserpicture() {
        return userpicture;
    }

    public void setUserpicture(String userpicture) {
        this.userpicture = userpicture;
    }

    private String userpicture;//头像
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String createdTime;  //创建时间
    private String location; //位置
    private double longitude;  //经度
    private double latitude;  //纬度
    private String description;   //车位的描述
}
