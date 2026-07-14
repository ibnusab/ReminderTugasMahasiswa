package com.example.remindertugasmahasiswa.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.room.Room;

import com.example.remindertugasmahasiswa.database.AppDatabase;
import com.example.remindertugasmahasiswa.helper.AlarmHelper;
import com.example.remindertugasmahasiswa.model.Task;

import java.util.List;

public class BootReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (intent.getAction() != null
                &&
                intent.getAction().equals(
                        Intent.ACTION_BOOT_COMPLETED
                )) {

            AppDatabase db =
                    Room.databaseBuilder(
                                    context,
                                    AppDatabase.class,
                                    "task_db"
                            )

                            .allowMainThreadQueries()

                            .fallbackToDestructiveMigration()

                            .build();

            List<Task> taskList =
                    db.taskDao().getAllTasks();

            // =========================
            // JADWALKAN ULANG SEMUA ALARM
            // =========================

            for (Task task : taskList) {

                if (!task.completed) {

                    AlarmHelper.setAlarm(
                            context,
                            task.date,
                            task.time,
                            task.title,
                            task.repeatType
                    );
                }
            }
        }
    }
}