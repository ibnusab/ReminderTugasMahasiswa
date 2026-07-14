package com.example.remindertugasmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.graphics.Color;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.applandeo.materialcalendarview.EventDay;
import com.example.remindertugasmahasiswa.adapter.TaskAdapter;
import com.example.remindertugasmahasiswa.database.AppDatabase;
import com.example.remindertugasmahasiswa.databinding.ActivityMainBinding;
import com.example.remindertugasmahasiswa.model.Task;
import com.example.remindertugasmahasiswa.widget.TaskWidget;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.XAxis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity
        extends AppCompatActivity {

    ActivityMainBinding binding;

    AppDatabase db;

    TaskAdapter adapter;

    List<Task> currentTaskList =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityMainBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        binding.cardManageTask.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            TaskManagementActivity.class
                    );

            startActivity(intent);

        });

        // =========================
        // ANIMASI HALAMAN
        // =========================

        binding.getRoot().startAnimation(
                AnimationUtils.loadAnimation(
                        this,
                        android.R.anim.fade_in
                )
        );

        // =========================
        // DATABASE
        // =========================

        db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "task_db"
                )

                .allowMainThreadQueries()

                .fallbackToDestructiveMigration()

                .build();

        // =========================
        // RECYCLER VIEW
        // =========================

        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.recyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(
                        this,
                        R.anim.layout_animation_fall_down
                )
        );

        // =========================
        // LOAD DATA
        // =========================

        loadData();

        loadCalendarEvents();


        // =========================
        // KLIK TANGGAL KALENDER
        // =========================

        binding.calendarView.setOnDayClickListener(
                eventDay -> {

                    Calendar calendar =
                            eventDay.getCalendar();

                    int day =
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            );

                    int month =
                            calendar.get(
                                    Calendar.MONTH
                            ) + 1;

                    int year =
                            calendar.get(
                                    Calendar.YEAR
                            );

                    String selectedDate =
                            String.format(
                                    "%02d/%02d/%d",
                                    day,
                                    month,
                                    year
                            );

                    loadTaskByDate(
                            selectedDate
                    );

                });

        // =========================
        // TOTAL TASK
        // =========================

        binding.txtTotal.setOnClickListener(v -> {

            loadData();

            loadCalendarEvents();

            Toast.makeText(
                    this,
                    "Semua tugas ditampilkan",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================
        // TASK SELESAI
        // =========================

        binding.txtDone.setOnClickListener(v -> {

            loadCompletedTasks();

        });

        // =========================
        // TASK AKTIF
        // =========================

        binding.txtActive.setOnClickListener(v -> {

            loadActiveTasks();

        });
    }

    // =========================
    // LOAD SEMUA TASK
    // =========================

    private void loadData() {

        currentTaskList =
                db.taskDao().getAllTasks();


        updateStatistics();


        updateNearestDeadline();
    }

    // =========================
    // SETUP RECYCLER
    // =========================

    private void setupRecycler(
            List<Task> taskList
    ) {

        adapter =
                new TaskAdapter(
                        this,
                        db,
                        taskList
                );

        binding.recyclerView.setAdapter(
                adapter
        );

        binding.recyclerView.scheduleLayoutAnimation();
    }

    // =========================
    // UPDATE STATISTIK
    // =========================

    private void updateStatistics() {

        int totalTask =
                db.taskDao().getTotalTasks();

        int completedTask =
                db.taskDao().getCompletedTasks();

        int activeTask =
                totalTask - completedTask;

        Integer xp =
                db.taskDao().getTotalXP();

        if (xp == null) {
            xp = 0;
        }

        // =========================
        // SISTEM LEVEL BARU (80 + 30)
        // =========================

        int level = 1;

        int requiredXP = 80; // level 1 requirement
        int currentXP = xp;

        while (currentXP >= requiredXP) {

            currentXP -= requiredXP;

            level++;

            requiredXP += 30; // naik +30 tiap level
        }

        // =========================
        // PROGRESS LEVEL (BAR XP)
        // =========================

        int progressPercent = 0;

        if (requiredXP > 0) {

            progressPercent =
                    (int) (((float) currentXP / requiredXP) * 100);
        }

        // =========================
        // GELAR MAHASISWA
        // =========================

        String title;

        if (xp >= 1000) {

            title = "Mahasiswa Teladan";

        } else if (xp >= 500) {

            title = "Mahasiswa Produktif";

        } else if (xp >= 200) {

            title = "Mahasiswa Rajin";

        } else {

            title = "Mahasiswa Pemula";
        }

        // =========================
        // BADGE BERDASARKAN LEVEL
        // =========================

        String badge;

        if (level >= 21) {

            badge = "👑 Legendary";

        } else if (level >= 16) {

            badge = "💎 Titanium";

        } else if (level >= 11) {

            badge = "🥇 Gold";

        } else if (level >= 6) {

            badge = "🥈 Silver";

        } else {

            badge = "🥉 Bronze";
        }

        // =========================
        // PRODUKTIVITAS
        // =========================

        int productivity = 0;

        if (totalTask > 0) {

            productivity =
                    (completedTask * 100)
                            / totalTask;
        }

        // =========================
        // XP KE LEVEL BERIKUTNYA
        // =========================

        int xpToNextLevel =
                requiredXP - currentXP;

        // =========================
        // UPDATE UI
        // =========================

        binding.txtTotal.setText(
                String.valueOf(totalTask)
        );

        binding.txtDone.setText(
                String.valueOf(completedTask)
        );

        binding.txtActive.setText(
                String.valueOf(activeTask)
        );

        binding.txtLevelNumber.setText(
                String.valueOf(level)
        );

        binding.txtLevel.setText(
                title
        );

        binding.txtBadge.setText(
                badge
        );

        binding.txtXP.setText(
                "XP : " + xp
        );

        binding.txtProductivity.setText(
                "Produktivitas : " + productivity + "%"
        );

        binding.txtXPProgress.setText(
                currentXP + " / " + requiredXP + " XP"
        );

        binding.txtNextLevel.setText(
                xpToNextLevel
                        + " XP lagi menuju Level "
                        + (level + 1)
        );

        binding.progressXP.setProgress(
                progressPercent
        );


    }

    private void updateNearestDeadline() {

        List<Task> tasks =
                db.taskDao().getAllTasks();

        Task nearestTask = null;

        long nearestTime =
                Long.MAX_VALUE;

        long now =
                System.currentTimeMillis();

        for (Task task : tasks) {

            if (!task.completed
                    && task.deadlineMillis > now
                    && task.deadlineMillis < nearestTime) {

                nearestTime =
                        task.deadlineMillis;

                nearestTask =
                        task;
            }
        }

        if (nearestTask != null) {

            long diff =
                    nearestTask.deadlineMillis - now;

            long days =
                    diff / (1000 * 60 * 60 * 24);

            binding.txtNearestTask.setText(
                    nearestTask.title
                            + "\n"
                            + days
                            + " hari lagi"
            );

        } else {

            binding.txtNearestTask.setText(
                    "Tidak ada deadline aktif"
            );
        }
    }




    // =========================
    // FILTER TASK BERDASARKAN
    // TANGGAL
    // =========================

    private void loadTaskByDate(
            String date
    ) {

        List<Task> taskList =
                db.taskDao()
                        .getTaskByDate(date);

        setupRecycler(taskList);

        if (taskList.isEmpty()) {

            Toast.makeText(
                    this,
                    "Tidak ada tugas pada "
                            + date,
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Menampilkan tugas "
                            + date,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================
    // TASK SELESAI
    // =========================

    private void loadCompletedTasks() {

        List<Task> completedTasks =
                new ArrayList<>();

        for (Task task : currentTaskList) {

            if (task.completed) {

                completedTasks.add(task);
            }
        }

        setupRecycler(completedTasks);

        Toast.makeText(
                this,
                "Tugas selesai ditampilkan",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================
    // TASK AKTIF
    // =========================

    private void loadActiveTasks() {

        List<Task> activeTasks =
                new ArrayList<>();

        for (Task task : currentTaskList) {

            if (!task.completed) {

                activeTasks.add(task);
            }
        }

        setupRecycler(activeTasks);

        Toast.makeText(
                this,
                "Tugas aktif ditampilkan",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================
    // EVENT KALENDER
    // =========================

    public void loadCalendarEvents() {

        try {

            List<Task> taskList =
                    db.taskDao().getAllTasks();

            // =========================
            // RESET TOTAL EVENT
            // =========================

            binding.calendarView.setEvents(
                    new ArrayList<>()
            );

            List<EventDay> events =
                    new ArrayList<>();

            // =========================
            // CEGAH DUPLIKAT TANGGAL
            // =========================

            Map<String, Boolean> eventMap =
                    new HashMap<>();

            for (Task task : taskList) {

                try {

                    if (task.date == null
                            || task.date.isEmpty()) {

                        continue;
                    }

                    String[] split =
                            task.date.split("/");

                    if (split.length != 3) {

                        continue;
                    }

                    int day =
                            Integer.parseInt(
                                    split[0]
                            );

                    int month =
                            Integer.parseInt(
                                    split[1]
                            ) - 1;

                    int year =
                            Integer.parseInt(
                                    split[2]
                            );

                    Calendar calendar =
                            Calendar.getInstance();

                    calendar.set(
                            Calendar.YEAR,
                            year
                    );

                    calendar.set(
                            Calendar.MONTH,
                            month
                    );

                    calendar.set(
                            Calendar.DAY_OF_MONTH,
                            day
                    );

                    calendar.set(
                            Calendar.HOUR_OF_DAY,
                            0
                    );

                    calendar.set(
                            Calendar.MINUTE,
                            0
                    );

                    calendar.set(
                            Calendar.SECOND,
                            0
                    );

                    calendar.set(
                            Calendar.MILLISECOND,
                            0
                    );

                    String key =
                            day + "-" + month + "-" + year;

                    // =========================
                    // PRIORITAS MERAH
                    // JIKA MASIH ADA TASK AKTIF
                    // =========================

                    if (!task.completed) {

                        eventMap.put(
                                key,
                                false
                        );

                    } else {

                        // =========================
                        // BIRU HANYA JIKA
                        // BELUM ADA MERAH
                        // =========================

                        if (!eventMap.containsKey(key)) {

                            eventMap.put(
                                    key,
                                    true
                            );
                        }
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // =========================
            // BUAT EVENT FINAL
            // =========================

            for (Map.Entry<String, Boolean> entry
                    : eventMap.entrySet()) {

                try {

                    String[] splitKey =
                            entry.getKey().split("-");

                    int day =
                            Integer.parseInt(
                                    splitKey[0]
                            );

                    int month =
                            Integer.parseInt(
                                    splitKey[1]
                            );

                    int year =
                            Integer.parseInt(
                                    splitKey[2]
                            );

                    Calendar calendar =
                            Calendar.getInstance();

                    calendar.set(
                            Calendar.YEAR,
                            year
                    );

                    calendar.set(
                            Calendar.MONTH,
                            month
                    );

                    calendar.set(
                            Calendar.DAY_OF_MONTH,
                            day
                    );

                    calendar.set(
                            Calendar.HOUR_OF_DAY,
                            0
                    );

                    calendar.set(
                            Calendar.MINUTE,
                            0
                    );

                    calendar.set(
                            Calendar.SECOND,
                            0
                    );

                    calendar.set(
                            Calendar.MILLISECOND,
                            0
                    );

                    boolean completed =
                            entry.getValue();

                    if (completed) {

                        events.add(
                                new EventDay(
                                        calendar,
                                        R.drawable.ic_blue_event
                                )
                        );

                    } else {

                        events.add(
                                new EventDay(
                                        calendar,
                                        R.drawable.ic_red_event
                                )
                        );
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // =========================
            // SET EVENT BARU
            // =========================

            binding.calendarView.setEvents(
                    events
            );

            // =========================
            // REFRESH KALENDER
            // =========================

            binding.calendarView.invalidate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // REFRESH REALTIME
    // =========================

    public void refreshRealtime() {

        currentTaskList =
                db.taskDao().getAllTasks();

        setupRecycler(currentTaskList);

        loadCalendarEvents();

        updateStatistics();

        updateNearestDeadline();

        TaskWidget.refreshWidget(this);
    }

    // =========================
    // REFRESH SAAT KEMBALI
    // =========================

    @Override
    protected void onResume() {

        super.onResume();

        loadData();

        loadCalendarEvents();

        TaskWidget.refreshWidget(this);
    }

    // =========================
    // ANIMASI KELUAR
    // =========================

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
    }
}