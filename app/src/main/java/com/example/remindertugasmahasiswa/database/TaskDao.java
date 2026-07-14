package com.example.remindertugasmahasiswa.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remindertugasmahasiswa.model.Task;

import java.util.List;

@Dao

public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY deadlineMillis ASC")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id=:id")
    Task getTaskById(int id);

    @Query("SELECT COUNT(*) FROM tasks")
    int getTotalTasks();

    @Query("SELECT COUNT(*) FROM tasks WHERE completed=1")
    int getCompletedTasks();



    // =========================
    // FILTER BERDASARKAN TANGGAL
    // =========================

    @Query("SELECT * FROM tasks WHERE date=:date ORDER BY deadlineMillis ASC")
    List<Task> getTaskByDate(String date);

    @Query("SELECT SUM(productivityPoint) FROM tasks")
    Integer getTotalXP();

}