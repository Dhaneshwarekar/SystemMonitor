package com.example.myapp;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceActivity extends AppCompatActivity {

    TextView deviceName, deviceBrand, deviceModel;
    TextView androidVersion, apiLevel, processor;
    TextView resolution, screenSize, totalRam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Show back arrow at top
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Device Information");
        }

        // Connect views
        deviceName     = findViewById(R.id.deviceName);
        deviceBrand    = findViewById(R.id.deviceBrand);
        deviceModel    = findViewById(R.id.deviceModel);
        androidVersion = findViewById(R.id.androidVersion);
        apiLevel       = findViewById(R.id.apiLevel);
        processor      = findViewById(R.id.processor);
        resolution     = findViewById(R.id.resolution);
        screenSize     = findViewById(R.id.screenSize);
        totalRam       = findViewById(R.id.totalRam);

        // Load all device info
        loadDeviceInfo();
    }

    private void loadDeviceInfo() {

        // Device name, brand, model — from Build class
        deviceName.setText(Build.DEVICE);
        deviceBrand.setText(Build.MANUFACTURER);
        deviceModel.setText(Build.MODEL);

        // Android version and API level
        androidVersion.setText(Build.VERSION.RELEASE);
        apiLevel.setText(String.valueOf(Build.VERSION.SDK_INT));

        // Processor architecture
        processor.setText(Build.SUPPORTED_ABIS[0]);

        // Screen resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width  = metrics.widthPixels;
        int height = metrics.heightPixels;
        resolution.setText(width + " x " + height);

        // Screen size in inches
        double x = Math.pow(width  / metrics.xdpi, 2);
        double y = Math.pow(height / metrics.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        screenSize.setText(String.format("%.1f inches", screenInches));

        // Total RAM
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalRamMB = memInfo.totalMem / (1024 * 1024);
        totalRam.setText(totalRamMB + " MB");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to previous screen
        return true;
    }
}