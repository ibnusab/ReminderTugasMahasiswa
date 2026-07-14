package com.example.remindertugasmahasiswa.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.remindertugasmahasiswa.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmHelper {

    public static void setAlarm(
            Context context,
            String date,
            String time,
            String title,
            String repeatType
    ) {

        try {

            // =========================
            // FORMAT TANGGAL
            // =========================

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    );

            Calendar calendar =
                    Calendar.getInstance();

            calendar.setTime(
                    sdf.parse(date + " " + time)
            );

            // =========================
            // ALARM MANAGER
            // =========================

            AlarmManager alarmManager =
                    (AlarmManager)
                            context.getSystemService(
                                    Context.ALARM_SERVICE
                            );

            if (alarmManager == null) {

                return;
            }

            // =========================
            // WAKTU SEKARANG
            // =========================

            long currentTime =
                    System.currentTimeMillis();

            long taskTime =
                    calendar.getTimeInMillis();

            // =========================
            // JIKA DEADLINE SUDAH LEWAT
            // =========================

            if (taskTime < currentTime) {

                return;
            }

            // =====================================================
            // ALARM 24 JAM SEBELUM DEADLINE
            // =====================================================

            long twentyFourHourBefore =
                    taskTime
                            - (24 * 60 * 60 * 1000);

            // =========================
            // JIKA BELUM LEWAT
            // =========================

            if (twentyFourHourBefore > currentTime) {

                Intent before24Intent =
                        new Intent(
                                context,
                                AlarmReceiver.class
                        );

                before24Intent.putExtra(
                        "title",
                        "24 jam lagi deadline: " + title
                );

                before24Intent.putExtra(
                        "repeatType",
                        repeatType
                );

                int before24Code =
                        (title + date + time + "24hour")
                                .hashCode();

                PendingIntent before24PendingIntent =
                        PendingIntent.getBroadcast(
                                context,
                                before24Code,
                                before24Intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                                        | PendingIntent.FLAG_IMMUTABLE
                        );

                setExactAlarm(
                        alarmManager,
                        twentyFourHourBefore,
                        before24PendingIntent
                );
            }

            // =====================================================
            // ALARM 1 JAM SEBELUM DEADLINE
            // =====================================================

            long oneHourBefore =
                    taskTime
                            - (60 * 60 * 1000);

            // =========================
            // HANYA JIKA BELUM LEWAT
            // =========================

            if (oneHourBefore > currentTime) {

                Intent reminderIntent =
                        new Intent(
                                context,
                                AlarmReceiver.class
                        );

                reminderIntent.putExtra(
                        "title",
                        "1 jam lagi deadline: " + title
                );

                reminderIntent.putExtra(
                        "repeatType",
                        repeatType
                );

                int reminderCode =
                        (title + date + time + "1hour")
                                .hashCode();

                PendingIntent reminderPendingIntent =
                        PendingIntent.getBroadcast(
                                context,
                                reminderCode,
                                reminderIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                                        | PendingIntent.FLAG_IMMUTABLE
                        );

                setExactAlarm(
                        alarmManager,
                        oneHourBefore,
                        reminderPendingIntent
                );
            }

            // =====================================================
            // ALARM TEPAT SAAT DEADLINE
            // =====================================================

            Intent deadlineIntent =
                    new Intent(
                            context,
                            AlarmReceiver.class
                    );

            deadlineIntent.putExtra(
                    "title",
                    "Deadline tugas: " + title
            );

            deadlineIntent.putExtra(
                    "repeatType",
                    repeatType
            );

            int deadlineCode =
                    (title + date + time + "deadline")
                            .hashCode();

            PendingIntent deadlinePendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            deadlineCode,
                            deadlineIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE
                    );

            // =====================================================
            // REPEAT HARIAN
            // =====================================================

            if (repeatType.equals("Harian")) {

                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        taskTime,
                        AlarmManager.INTERVAL_DAY,
                        deadlinePendingIntent
                );
            }

            // =====================================================
            // REPEAT MINGGUAN
            // =====================================================

            else if (
                    repeatType.equals("Mingguan")
            ) {

                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        taskTime,
                        AlarmManager.INTERVAL_DAY * 7,
                        deadlinePendingIntent
                );
            }

            // =====================================================
            // ALARM SEKALI
            // =====================================================

            else {

                setExactAlarm(
                        alarmManager,
                        taskTime,
                        deadlinePendingIntent
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // EXACT ALARM METHOD
    // =========================

    private static void setExactAlarm(
            AlarmManager alarmManager,
            long triggerAtMillis,
            PendingIntent pendingIntent
    ) {

        try {

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {

                    alarmManager
                            .setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    triggerAtMillis,
                                    pendingIntent
                            );
                }

            } else {

                alarmManager
                        .setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerAtMillis,
                                pendingIntent
                        );
            }

        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }
}