package com.fighter.quickstop;

import java.io.Serializable;

/**
 * Created by zhuch on 2016/3/5.
 */
public class Schedule implements Serializable{
    String pusername;//车位的用户名
    String pcreatedtime;//车位创建日期

    String sstarttime;//预定开始时间
    String sendTime;//预定结束时间

    int isfinished;//这次是不是完成
    float ucost;//这次消耗总共金额
    String ureachtime;//user到达时间
    String uleavetime;//user离开时间

    public float getUcost() {
        return ucost;
    }

    public void setUcost(float ucost) {
        this.ucost = ucost;
    }

    public String getUreachtime() {
        return ureachtime;
    }

    public void setUreachtime(String ureachtime) {
        this.ureachtime = ureachtime;
    }

    public String getUleavetime() {

        return uleavetime;
    }

    public void setUleavetime(String uleavetime) {
        this.uleavetime = uleavetime;
    }

    public String getSstarttime() {

        return sstarttime;
    }

    public void setSstarttime(String sstarttime) {
        this.sstarttime = sstarttime;
    }

    public String getSendTime() {

        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getPusername() {

        return pusername;
    }

    public void setPusername(String pusername) {
        this.pusername = pusername;
    }

    public String getPcreatedtime() {

        return pcreatedtime;
    }

    public void setPcreatedtime(String pcreatedtime) {
        this.pcreatedtime = pcreatedtime;
    }

    public int getIsfinished() {

        return isfinished;
    }

    public void setIsfinished(int isfinished) {
        this.isfinished = isfinished;
    }


}
