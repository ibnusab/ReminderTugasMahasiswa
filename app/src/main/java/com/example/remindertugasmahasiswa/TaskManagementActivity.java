package com.example.remindertugasmahasiswa;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.remindertugasmahasiswa.adapter.TaskAdapter;
import com.example.remindertugasmahasiswa.database.AppDatabase;
import com.example.remindertugasmahasiswa.databinding.ActivityTaskManagementBinding;
import com.example.remindertugasmahasiswa.model.Task;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import android.graphics.Color;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity {

    private ActivityTaskManagementBinding binding;
    private AppDatabase db;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityTaskManagementBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );

        db = androidx.room.Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "task_db"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        loadData();

        binding.btnAdd.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            TaskManagementActivity.this,
                            AddEditTaskActivity.class
                    )
            );

        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        loadData();

    }

    private void loadData() {

        taskList =
                db.taskDao().getAllTasks();

        adapter = new TaskAdapter(
                this,
                db,
                taskList
        );

        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.recyclerView.setAdapter(
                adapter
        );

        updateSummary();

        setupChart();

        updateAchievement();

    }

    private void updateSummary() {

        int total = taskList.size();

        int xp = 0;

        for (Task task : taskList) {

            if (task.completed) {

                xp += task.productivityPoint;
            }
        }

        binding.txtTotalTask2.setText(
                String.valueOf(total)
        );

        binding.txtXP2.setText(
                String.valueOf(xp)
        );
    }

    private void updateAchievement() {

        int completed = 0;

        for (Task task : taskList) {

            if (task.completed) {

                completed++;

            }
        }

        StringBuilder achievements =
                new StringBuilder();

        if (completed >= 1) {

            achievements.append(
                    "🥉 First Task\n"
            );
        }

        if (completed >= 10) {

            achievements.append(
                    "🥈 Produktif\n"
            );
        }

        if (completed >= 50) {

            achievements.append(
                    "🥇 Rajin\n"
            );
        }

        if (completed >= 100) {

            achievements.append(
                    "👑 Legend\n"
            );
        }

        if (achievements.length() == 0) {

            achievements.append(
                    "Belum ada achievement"
            );
        }

        binding.txtAchievement.setText(
                achievements.toString()
        );
    }

    private void setupChart() {

        int totalTask =
                taskList.size();

        int completedTask = 0;

        for (Task task : taskList) {

            if (task.completed) {

                completedTask++;
            }
        }

        int activeTask =
                totalTask - completedTask;

        ArrayList<BarEntry> entries =
                new ArrayList<>();

        entries.add(
                new BarEntry(0, totalTask)
        );

        entries.add(
                new BarEntry(1, activeTask)
        );

        entries.add(
                new BarEntry(2, completedTask)
        );

        BarDataSet dataSet =
                new BarDataSet(
                        entries,
                        "Statistik"
                );

        dataSet.setColors(
                Color.parseColor("#2563EB"),
                Color.parseColor("#F59E0B"),
                Color.parseColor("#16A34A")
        );

        dataSet.setValueTextSize(14f);

        BarData data =
                new BarData(dataSet);

        binding.barChart.setData(data);

        String[] labels = {
                "Total",
                "Aktif",
                "Selesai"
        };

        XAxis xAxis =
                binding.barChart.getXAxis();

        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(labels)
        );

        xAxis.setGranularity(1f);

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        Description description =
                new Description();

        description.setText("");

        binding.barChart.setDescription(
                description
        );

        binding.barChart.animateY(1000);

        binding.barChart.invalidate();
    }

    // =========================
// REFRESH REALTIME
// =========================

    public void refreshRealtime() {

        taskList = db.taskDao().getAllTasks();

        updateSummary();

        setupChart();

        updateAchievement();
    }
}




