package com.example.remindertugasmahasiswa.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")

public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public String course;

    public String date;

    public String time;

    public boolean completed;

    public String repeatType;

    public String priority;

    public long deadlineMillis;

    public int productivityPoint;


}