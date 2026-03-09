package com.example.myapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView appCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        // Show back arrow at top
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Installed Apps");
        }

        recyclerView  = findViewById(R.id.appsRecyclerView);
        appCountText  = findViewById(R.id.appCountText);

        // Load apps list
        loadApps();
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();

        // Get all installed apps
        List<ApplicationInfo> allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // Filter only user installed apps (not system apps)
        List<ApplicationInfo> userApps = new ArrayList<>();
        for (ApplicationInfo app : allApps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                userApps.add(app);
            }
        }

        // Sort alphabetically
        Collections.sort(userApps, (a, b) ->
                pm.getApplicationLabel(a).toString()
                        .compareToIgnoreCase(pm.getApplicationLabel(b).toString())
        );

        // Show count
        appCountText.setText(userApps.size() + " apps");

        // Setup RecyclerView
        AppAdapter adapter = new AppAdapter(userApps, pm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to previous screen
        return true;
    }
}
