package com.example.remindertugasmahasiswa.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.remindertugasmahasiswa.MainActivity;
import com.example.remindertugasmahasiswa.R;
import com.example.remindertugasmahasiswa.database.AppDatabase;

import androidx.room.Room;

public class TaskWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds
    ) {

        for (int appWidgetId : appWidgetIds) {

            updateWidget(
                    context,
                    appWidgetManager,
                    appWidgetId
            );
        }
    }

    public static void updateWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {

        AppDatabase db =
                Room.databaseBuilder(
                                context,
                                AppDatabase.class,
                                "task_db"
                        )

                        .allowMainThreadQueries()

                        .fallbackToDestructiveMigration()

                        .build();

        int total =
                db.taskDao().getTotalTasks();

        int selesai =
                db.taskDao().getCompletedTasks();

        RemoteViews views =
                new RemoteViews(
                        context.getPackageName(),
                        R.layout.widget_task
                );

        views.setTextViewText(
                R.id.txtWidgetTitle,
                "Reminder Tugas"
        );

        views.setTextViewText(
                R.id.txtWidgetContent,
                "Total: "
                        + total
                        + " | Selesai: "
                        + selesai
        );

        Intent intent =
                new Intent(
                        context,
                        MainActivity.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        views.setOnClickPendingIntent(
                R.id.widgetRoot,
                pendingIntent
        );

        appWidgetManager.updateAppWidget(
                appWidgetId,
                views
        );
    }

    public static void refreshWidget(
            Context context
    ) {

        Intent intent =
                new Intent(
                        context,
                        TaskWidget.class
                );

        intent.setAction(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE
        );

        int[] ids =
                AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(
                                new ComponentName(
                                        context,
                                        TaskWidget.class
                                )
                        );

        intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                ids
        );

        context.sendBroadcast(intent);
    }
}