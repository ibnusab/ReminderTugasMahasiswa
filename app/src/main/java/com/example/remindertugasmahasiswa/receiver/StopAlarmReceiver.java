package com.example.remindertugasmahasiswa.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindertugasmahasiswa.service.AlarmService;

public class StopAlarmReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        // =========================
        // STOP SUARA ALARM
        // =========================

        AlarmService.stopAlarm();

        // =========================
        // STOP SERVICE
        // =========================

        context.stopService(
                new Intent(
                        context,
                        AlarmService.class
                )
        );
    }
}