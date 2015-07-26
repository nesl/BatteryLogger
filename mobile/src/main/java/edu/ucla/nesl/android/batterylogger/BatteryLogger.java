package edu.ucla.nesl.android.batterylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by cgshen on 7/22/15.
 */
public class BatteryLogger extends BroadcastReceiver{
    private static final String TAG = "Mobile/BatteryLogger";
    public static final String START_MEASUREMENT = "START";
    public static final String STOP_MEASUREMENT = "STOP";
    public static final String NAME = "NAME";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received: " + intent.getAction());

        if (intent.getAction().contains(START_MEASUREMENT)) {
            Intent startServiceIntent = new Intent(context, BatteryLoggerIntentService.class);
            startServiceIntent.setAction(START_MEASUREMENT);
            startServiceIntent.putExtra(NAME, intent.getStringExtra(NAME));
            context.startService(startServiceIntent);
        }
        else if (intent.getAction().contains(STOP_MEASUREMENT)) {
            Intent startServiceIntent = new Intent(context, BatteryLoggerIntentService.class);
            startServiceIntent.setAction(STOP_MEASUREMENT);
            context.startService(startServiceIntent);
        }
        else {
            Log.i(TAG, "Unknown action");
        }

    }
}
