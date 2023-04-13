package com.example.Geburtstagsplaner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.Geburtstagsplaner.fragement.Calendar;
import com.example.Geburtstagsplaner.util.NotificationsWorker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    /*
    App for saving and displaying Birthdays, includes Notifications,Import and Export of Database
    created by: Jan Kramer
    Version: 1.1
     */

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.cancelAllWork();
        workManager.pruneWork();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder
                (NotificationsWorker.class, 6, TimeUnit.SECONDS).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(this);
        }
        workManager.enqueueUniquePeriodicWork("NotificationPeriodicWork",
                ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

        setContentView(R.layout.activity_main);
        
        toolbar = findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Calendar calendar = new Calendar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_fragments, calendar).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static void checkPermission(Activity activity) {
        if (!(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}