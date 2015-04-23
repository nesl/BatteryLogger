package edu.ucla.nesl.android.batterylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cgshen on 4/21/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Wear/BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorCollector");
        wl.acquire();
        Log.i(TAG, "Alarm Received");

        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;

            BufferedWriter outputBattery = new BufferedWriter(new FileWriter("/storage/sdcard0/sensor_data/battery_normal_use.txt", true));

            if (batteryPct > 0.2) {
                outputBattery.append(String.valueOf(System.currentTimeMillis()) + "," + String.valueOf(batteryPct) + "\n");
                outputBattery.flush();

                Log.d(TAG, "Battery level = " + batteryPct);
            }
            else {
                outputBattery.flush();
                outputBattery.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

       wl.release();
    }
}
