package com.example.Geburtstagsplaner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.Geburtstagsplaner.util.ShortToast;

public class WhatsAppOpener extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission(this);

        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String[] args = {name};
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?", args,
                null);
        if (cursor.moveToFirst()) {
            String number = cursor.getString(0);
            if (isWhatsAppInstalled()) {
                Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW);
                whatsAppIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number));
                startActivity(whatsAppIntent);
            } else {
                ShortToast.makeToast(this,
                        "WhatsApp is not installed on this device");
            }
        } else {
            ShortToast.makeToast(this, "Could not find a number for " + name);
        }
        cursor.close();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    private boolean isWhatsAppInstalled() {
        PackageManager packageManager = getPackageManager();
        boolean app_installed;
        try {
            // WhatsApp Url
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private static void checkPermission(Activity activity) {
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
