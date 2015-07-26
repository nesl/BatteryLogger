package edu.ucla.nesl.android.batterylogger;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by cgshen on 7/22/15.
 */
public class BatteryLoggerIntentService extends IntentService {
    private static final String TAG = "Mobile/BatteryLoggerIntentService";
    private static final String CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    private static final String VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";
    private String LOG_NAME = "/sdcard/powerlog/powerlog_" + System.currentTimeMillis() + ".csv";
    private static ScheduledExecutorService mScheduleTaskExecutor;
    private static ScheduledFuture scheduledFuture;

    @Override
    protected void onHandleIntent(Intent workIntent) {
        if (workIntent.getAction().contains(BatteryLogger.START_MEASUREMENT)) {
            LOG_NAME = "/sdcard/powerlog/powerlog_" + workIntent.getStringExtra(BatteryLogger.NAME) + "_" + System.currentTimeMillis() + ".csv";
            Log.i(TAG, "Start battery logging...");
            // Log power evert second
            scheduledFuture = mScheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    logPower();
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
        else if (workIntent.getAction().contains(BatteryLogger.STOP_MEASUREMENT)) {
            Log.i(TAG, "Stop battery logging...");
            // Cancel power logging
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            else {
                Log.i(TAG, "scheduled future null!");
            }

            mScheduleTaskExecutor.shutdown();
        }
    }

    /**
     * Read current/voltage from system file (nexus 5)
     */
    private void logPower() {
        float currentNow = readValue(CURRENT_NOW);
        float voltageNow = readValue(VOLTAGE_NOW);
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(LOG_NAME, true));
            buf.write(System.currentTimeMillis() + "," + voltageNow + "," + currentNow + "\n");
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to read value from a file
     * @param fileName
     * @return
     */
    private float readValue(String fileName) {
        float result = 0.0f;

        try {
            BufferedReader buf = new BufferedReader(new FileReader(fileName));
            result = Float.parseFloat(buf.readLine().trim());
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public BatteryLoggerIntentService() {
        super("BatteryLoggerIntentService");
        mScheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    }
}
