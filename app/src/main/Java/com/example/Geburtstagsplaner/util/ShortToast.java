package com.example.Geburtstagsplaner.util;

import android.content.Context;
import android.widget.Toast;

public class ShortToast {

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
