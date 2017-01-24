package com.fighter.quickstop.publish;

/**
 * Created by xey on 2016/3/10.
 */
public class PublishInformation {
    private String username;  //用户名
    private String createdTime;  //创建时间
    private String image1;   //第一张图片的链接
    private String image2;    //第二张图片的链接
    private String image3;   //第三张图片的链接
    private String beginTime; //开始的时间
    private String endTime;  //结束的时间
    private int isOpen;     //是否开启，0代表关闭，1或任意非0数字代表开启
    private int monday;     //周一是否开启
    private int tuesday;    //周二是否开启
    private int wednesday;  //周三是否开启
    private int thursday;   //周四是否开启
    private int friday;     //周五是否开启
    private int saturday;   //周六是否开启
    private int sunday;     //周日是否开启
    private String location; //位置
    private double longitude;  //经度
    private double latitude;  //纬度
    private int price;    //每小时价格
    private String description;   //车位的描述
    private int usedtime;      //被使用的次数

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUsedtime() {
        return usedtime;
    }

    public void setUsedtime(int usedtime) {
        this.usedtime = usedtime;
    }
}
