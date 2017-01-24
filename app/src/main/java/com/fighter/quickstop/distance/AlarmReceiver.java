package com.fighter.quickstop.distance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by zhuch on 2016/3/9.
 */
public class AlarmReceiver  extends BroadcastReceiver{

    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context,DistanceService.class);
        context.startService(i);
    }


}
