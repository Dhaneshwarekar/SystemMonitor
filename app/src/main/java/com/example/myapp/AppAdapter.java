package com.example.myapp;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    List<ApplicationInfo> appList;
    PackageManager pm;

    // Constructor — receives the data
    public AppAdapter(List<ApplicationInfo> appList, PackageManager pm) {
        this.appList = appList;
        this.pm      = pm;
    }

    // Creates the visual container for each item
    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    // Fills each item with real data
    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        ApplicationInfo app = appList.get(position);
        holder.appName.setText(pm.getApplicationLabel(app).toString());
        holder.appIcon.setImageDrawable(pm.getApplicationIcon(app));
    }

    // Total number of items in list
    @Override
    public int getItemCount() {
        return appList.size();
    }

    // ViewHolder — holds references to each item's views
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView  appName;
        ImageView appIcon;

        public AppViewHolder(View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
        }
    }
}
