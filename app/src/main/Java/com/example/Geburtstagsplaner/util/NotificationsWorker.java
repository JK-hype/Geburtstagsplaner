package com.example.Geburtstagsplaner.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.Geburtstagsplaner.R;
import com.example.Geburtstagsplaner.WhatsAppOpener;
import com.example.Geburtstagsplaner.pojo.Birthday;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationsWorker extends Worker {

    final String CHANNEL_ID = "Notification_Channel_Birthday";

    public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Databasehelper databasehelper = new Databasehelper(getApplicationContext());
        ArrayList<Birthday> listOfBirthdays = databasehelper.getAllBirthdays();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> age = new ArrayList<>();

        for (Birthday birthday : listOfBirthdays) {
            if (birthday.getMonth() == month && birthday.getDay() == day) {
                names.add(birthday.getName());
                age.add(year - birthday.getYear());
            }
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Notification_Birthday_App", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(getApplicationContext().getString(R.string.channel_description));
        NotificationManager notificationManager = getApplicationContext().
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID).setSmallIcon(R.drawable.ic_birthday_cake).setPriority
                (NotificationCompat.PRIORITY_MAX).setAutoCancel(true);

        //random number
        int i = 888;
        for (String name : names) {
            Intent intent = new Intent(getApplicationContext(), WhatsAppOpener.class);
            intent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentText(name + " hat heute " + age.get(names.indexOf(name))
                    + ". Geburtstag. Gratuliere ihm/ihr!").
                    setContentTitle("Geburtstag von " + name).setContentIntent(pendingIntent);
            Notification notification = builder.build();
            notificationManager.notify(i, notification);
            i++;
        }

        return Result.success();
    }


}
