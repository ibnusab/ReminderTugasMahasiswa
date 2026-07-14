package com.example.remindertugasmahasiswa.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.remindertugasmahasiswa.model.Task;

@Database(
        entities = {Task.class},
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "task_database"
                    )
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}