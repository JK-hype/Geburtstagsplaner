package com.example.Geburtstagsplaner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

public class StartOpenContactFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getIntent().getExtras() != null;
        String name = getIntent().getExtras().getString("name");
        checkPermission(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wähle aus, worüber du kontaktieren willst.")
                .setItems(new String[]{"WhatsApp", "Email", "Telefon"}, (dialog, which) -> {
                    Intent intent = switch (which) {
                        case 2 -> new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" +
                                getPhoneNumber(name)));
                        case 1 -> getEmailIntent(getEmail(name));
                        default -> getWhatsAppIntent(getPhoneNumber(name));
                    };
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });
        builder.show();
    }

    private Intent getEmailIntent(String email) {
        Intent intent = null;
        if (email != null) {
            intent = new Intent();
            intent.setType("vnd.android.cursor.dir/email");
            String[] to = {email};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Alles Gute");
        }
        return intent;
    }

    private Intent getWhatsAppIntent(String number) {
        Intent whatsAppIntent = null;
        if (isApInstalled("com.whatsapp")) {
            whatsAppIntent = new Intent(Intent.ACTION_VIEW);
            whatsAppIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number));
        } else {
            ShortToast.makeToast(this,
                    "WhatsApp is not installed on this device");
        }
        return whatsAppIntent;
    }

    private Intent getSignalIntent(String number) {
        Intent whatsAppIntent = null;
        if (isApInstalled("com.whatsapp")) {
            whatsAppIntent = new Intent(Intent.ACTION_VIEW);
            whatsAppIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number));
        } else {
            ShortToast.makeToast(this,
                    "WhatsApp is not installed on this device");
        }
        return whatsAppIntent;
    }

    private Intent getTelegramIntent(String number) {
        Intent whatsAppIntent = null;
        if (isApInstalled("org.telegram")) {
            whatsAppIntent = new Intent(Intent.ACTION_VIEW);
            whatsAppIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number));
        } else {
            ShortToast.makeToast(this,
                    "WhatsApp is not installed on this device");
        }
        return whatsAppIntent;
    }


    private String getPhoneNumber(String name) {
        String number = null;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String[] args = {name};
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?", args,
                null);
        if (cursor.moveToFirst()) {
            number = cursor.getString(0);
        } else {
            ShortToast.makeToast(this, "Could not find a number for " + name);
        }
        cursor.close();
        return number;
    }

    private String getEmail(String name) {
        String email = null;

        String[] projection = {ContactsContract.CommonDataKinds.Email.DATA};
        String[] args = {name};
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                args,
                null);
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        } else {
            ShortToast.makeToast(this, "Could not find an email address for " + name);
        }
        cursor.close();
        return email;
    }


    private boolean isApInstalled(String packageName) {
        PackageManager packageManager = this.getPackageManager();
        boolean appInstalled;
        try {
            // WhatsApp Url
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    private static void checkPermission(Activity activity) {
        if (!(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }
    }

