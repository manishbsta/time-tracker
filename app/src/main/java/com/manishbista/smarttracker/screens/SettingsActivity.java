package com.manishbista.smarttracker.screens;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.manishbista.smarttracker.R;
import com.manishbista.smarttracker.Utils.AppHelper;
import com.manishbista.smarttracker.Utils.AppInfo;
import com.manishbista.smarttracker.adapters.AppListAdapter;
import com.manishbista.smarttracker.preferences.TimeTrackerPrefHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements
        AppListAdapter.OnSettingsChangedListener {

    private FirebaseAuth mAuth;

    private List<AppInfo> appList = new ArrayList<AppInfo>();
    private AppListAdapter appListAdapter;
    private RecyclerView appRecyclerList;
    private PackageManager packageManager = null;
    private List<String> prefList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appRecyclerList = (RecyclerView) findViewById(R.id.app_list);
        setAppList();
        packageManager = getPackageManager();
        new LoadApplications().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.mItemLogOut) {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    private void setAppList() {
        String serialized = TimeTrackerPrefHandler.INSTANCE.getPkgList(getApplicationContext());
        if (serialized != null) {
            prefList = new LinkedList<String>(Arrays.asList
                    (TextUtils.split(serialized, ",")));
        }

        appListAdapter = new AppListAdapter(this, appList);
        if (appListAdapter != null) {
            appRecyclerList.setAdapter(appListAdapter);
            appRecyclerList.setLayoutManager(new LinearLayoutManager(this));
            appListAdapter.setOnSettingsChangedListener(this);
        }
    }

    private void checkForLaunchIntent(List<ApplicationInfo> list) {
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = calendar.getTimeInMillis();
        Map<String, UsageStats> lUsageStatsMap = AppHelper.getUsageStatsManager().
                queryAndAggregateUsageStats(
                        millis,
                        System.currentTimeMillis());
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    boolean isChecked = false;
                    if (prefList.contains(info.packageName)) {
                        isChecked = true;
                    }
                    appList.add(new AppInfo((String) info.loadLabel(packageManager),
                            info.packageName,
                            info.loadIcon(packageManager),
                            isChecked));
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListChanged(String pkgName, boolean isChecked) {
        if (isChecked) {
            if (!prefList.contains(pkgName)) {
                prefList.add(pkgName);
            }
        }
        else {
            if (prefList.contains(pkgName)) {
                prefList.remove(pkgName);
            }
        }
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            checkForLaunchIntent(
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Collections.sort(appList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo lhs, AppInfo rhs) {
                    return (lhs.getAppName().compareTo(rhs.getAppName()));
                }
            });
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            appListAdapter.notifyDataSetChanged();
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(SettingsActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeTrackerPrefHandler.INSTANCE.savePkgList
                (TextUtils.join(",", prefList), this);
    }
}
