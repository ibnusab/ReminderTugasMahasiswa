package com.example.remindertugasmahasiswa.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.remindertugasmahasiswa.MainActivity;
import com.example.remindertugasmahasiswa.R;
import com.example.remindertugasmahasiswa.service.AlarmService;

public class AlarmReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        // =========================
        // AMBIL TITLE
        // =========================

        String title =
                intent.getStringExtra(
                        "title"
                );

        if (title == null) {

            title = "Reminder tugas";
        }

        // =========================
        // START ALARM SERVICE
        // =========================

        Intent serviceIntent =
                new Intent(
                        context,
                        AlarmService.class
                );

        try {

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.O) {

                context.startForegroundService(
                        serviceIntent
                );

            } else {

                context.startService(
                        serviceIntent
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        // =========================
        // OPEN APP INTENT
        // =========================

        Intent openIntent =
                new Intent(
                        context,
                        MainActivity.class
                );

        openIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        PendingIntent openPendingIntent =
                PendingIntent.getActivity(
                        context,
                        1000,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        // =========================
        // STOP ALARM INTENT
        // =========================

        Intent stopIntent =
                new Intent(
                        context,
                        StopAlarmReceiver.class
                );

        PendingIntent stopPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        2000,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        // =========================
        // CHANNEL ID
        // =========================

        String channelId =
                "TASK_REMINDER_CHANNEL";

        // =========================
        // NOTIFICATION MANAGER
        // =========================

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager == null) {

            return;
        }

        // =========================
        // CREATE CHANNEL
        // =========================

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "Reminder Tugas",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Alarm reminder tugas mahasiswa"
            );

            channel.enableVibration(true);

            channel.setLockscreenVisibility(
                    android.app.Notification.VISIBILITY_PUBLIC
            );

            manager.createNotificationChannel(
                    channel
            );
        }

        // =========================
        // BUILD NOTIFICATION
        // =========================

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        channelId
                )

                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )

                        .setContentTitle(
                                "Reminder Deadline"
                        )

                        .setContentText(
                                title
                        )

                        .setPriority(
                                NotificationCompat.PRIORITY_MAX
                        )

                        .setCategory(
                                NotificationCompat.CATEGORY_ALARM
                        )

                        .setVisibility(
                                NotificationCompat.VISIBILITY_PUBLIC
                        )

                        .setAutoCancel(
                                false
                        )

                        .setOngoing(
                                true
                        )

                        .setOnlyAlertOnce(
                                false
                        )

                        .setContentIntent(
                                openPendingIntent
                        )

                        // =========================
                        // STOP BUTTON
                        // =========================

                        .addAction(
                                0,
                                "STOP ALARM",
                                stopPendingIntent
                        );

        // =========================
        // SHOW NOTIFICATION
        // =========================

        manager.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
    }
}