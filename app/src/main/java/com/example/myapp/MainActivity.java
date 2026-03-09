package com.example.myapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.ActivityManager;
import android.content.Context;
import android.os.StatFs;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.TrafficStats;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare all our views
    ProgressBar batteryProgress, ramProgress, storageProgress;
    TextView batteryText, ramText, storageText, networkText;
    long lastRxBytes = 0;
    long lastTxBytes = 0;


    // Handler for real-time updates every second
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Java variables to XML views using their IDs
        batteryProgress = findViewById(R.id.batteryProgress);
        ramProgress     = findViewById(R.id.ramProgress);
        storageProgress = findViewById(R.id.storageProgress);
        batteryText     = findViewById(R.id.batteryText);
        ramText         = findViewById(R.id.ramText);
        storageText     = findViewById(R.id.storageText);
        networkText     = findViewById(R.id.networkText);


        // Start updating every second
        startUpdating();
        // Button to go to Apps screen
        Button appsButton = findViewById(R.id.appsButton);
        appsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppsActivity.class);
            startActivity(intent);
        });

        Button deviceButton = findViewById(R.id.deviceButton);
        deviceButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
            startActivity(intent);
        });

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_apps) {
                startActivity(new Intent(MainActivity.this, AppsActivity.class));
                return true;
            } else if (id == R.id.nav_device) {
                startActivity(new Intent(MainActivity.this, DeviceActivity.class));
                return true;
            }
            return true;
        });
    }

    private void startUpdating() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBattery();
                updateRAM();
                updateStorage();
                updateNetwork();

                // Repeat every 1 second
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) ((level / (float) scale) * 100);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        batteryProgress.setProgress(batteryPct);
        batteryText.setText(batteryPct + "% — " + (isCharging ? "Charging ⚡" : "Not Charging"));
    }

    private void updateRAM() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);

        long totalRam  = memInfo.totalMem  / (1024 * 1024); // Convert to MB
        long availRam  = memInfo.availMem  / (1024 * 1024);
        long usedRam   = totalRam - availRam;
        int  ramPercent = (int) ((usedRam / (float) totalRam) * 100);

        ramProgress.setProgress(ramPercent);
        ramText.setText(usedRam + " MB used of " + totalRam + " MB  (" + ramPercent + "%)");
    }

    private void updateStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        long totalBytes = stat.getTotalBytes();
        long freeBytes  = stat.getFreeBytes();
        long usedBytes  = totalBytes - freeBytes;

        long totalGB = totalBytes / (1024 * 1024 * 1024);
        long usedGB  = usedBytes  / (1024 * 1024 * 1024);
        int  storagePct = (int) ((usedBytes / (float) totalBytes) * 100);

        storageProgress.setProgress(storagePct);
        storageText.setText(usedGB + " GB used of " + totalGB + " GB  (" + storagePct + "%)");
    }

    private void updateNetwork() {
        long currentRxBytes = TrafficStats.getTotalRxBytes();
        long currentTxBytes = TrafficStats.getTotalTxBytes();

        if (lastRxBytes != 0) {
            // Calculate speed per second
            long downloadSpeed = currentRxBytes - lastRxBytes;
            long uploadSpeed   = currentTxBytes - lastTxBytes;

            // Convert bytes to KB or MB for display
            String downText, upText;

            if (downloadSpeed > 1024 * 1024) {
                downText = String.format("%.1f MB/s", downloadSpeed / (1024.0 * 1024.0));
            } else {
                downText = String.format("%.1f KB/s", downloadSpeed / 1024.0);
            }

            if (uploadSpeed > 1024 * 1024) {
                upText = String.format("%.1f MB/s", uploadSpeed / (1024.0 * 1024.0));
            } else {
                upText = String.format("%.1f KB/s", uploadSpeed / 1024.0);
            }

            networkText.setText("↓ Download: " + downText + "\n↑ Upload: " + upText);
        }

        // Save current values for next second comparison
        lastRxBytes = currentRxBytes;
        lastTxBytes = currentTxBytes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop updates when app is closed
        handler.removeCallbacksAndMessages(null);
    }
}