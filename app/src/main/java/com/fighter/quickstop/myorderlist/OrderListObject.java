package com.fighter.quickstop.myorderlist;

import java.io.Serializable;

/**
 * Created by zhuch on 2016/3/21.
 */
public class OrderListObject implements Serializable {

        String username;//车位的用户名
        String createdtime;//车位创建日期
        String nickname;//车位的所有者的昵称

        String location;//车位位置
        String description;//描述
        String starttime;//预定开始时间
        String endTime;//预定结束时间

        int isfinished;//这次是不是完成
        float cost;//这次消耗总共金额
        String reachtime;//user到达时间
        String leavetime;//user离开时间

        double latitude;//经纬度
        double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIsfinished() {
        return isfinished;
    }

    public void setIsfinished(int isfinished) {
        this.isfinished = isfinished;
    }

    public String getLeavetime() {
        return leavetime;
    }

    public void setLeavetime(String leavetime) {
        this.leavetime = leavetime;
    }

    public String getLocation() {
        return location;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReachtime() {
        return reachtime;
    }

    public void setReachtime(String reachtime) {
        this.reachtime = reachtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
