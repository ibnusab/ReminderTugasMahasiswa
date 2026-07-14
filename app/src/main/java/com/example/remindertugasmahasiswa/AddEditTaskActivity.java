package com.example.remindertugasmahasiswa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.remindertugasmahasiswa.database.AppDatabase;
import com.example.remindertugasmahasiswa.databinding.ActivityAddEditTaskBinding;
import com.example.remindertugasmahasiswa.helper.AlarmHelper;
import com.example.remindertugasmahasiswa.model.Task;
import com.example.remindertugasmahasiswa.widget.TaskWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditTaskActivity
        extends AppCompatActivity {

    ActivityAddEditTaskBinding binding;

    AppDatabase db;

    String selectedDate = "";

    String selectedTime = "";

    String repeat = "Tidak";

    String priority = "Normal";

    int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityAddEditTaskBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        // =========================
        // ANIMASI MASUK HALUS
        // =========================

        View root = binding.getRoot();

        root.setAlpha(0f);

        root.setTranslationY(80f);

        root.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

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
        // AMBIL ID EDIT
        // =========================

        taskId =
                getIntent().getIntExtra(
                        "taskId",
                        -1
                );

        // =========================
        // SETUP DROPDOWN
        // =========================

        setupPriority();

        setupRepeat();

        // =========================
        // LOAD DATA EDIT
        // =========================

        setupEditData();

        // =========================
        // DATE PICKER
        // =========================

        binding.edtDate.setOnClickListener(
                v -> showDate()
        );

        // =========================
        // TIME PICKER
        // =========================

        binding.edtTime.setOnClickListener(
                v -> showTime()
        );

        // =========================
        // BUTTON SAVE
        // =========================

        binding.btnSave.setOnClickListener(
                v -> {

                    v.animate()
                            .scaleX(0.92f)
                            .scaleY(0.92f)
                            .setDuration(90)
                            .withEndAction(() -> {

                                v.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(90)
                                        .start();

                                saveTask();

                            })
                            .start();

                }
        );
    }

    // =========================
    // DROPDOWN PRIORITAS
    // =========================

    private void setupPriority() {

        String[] priorityItems = {
                "Tinggi",
                "Normal",
                "Rendah"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        priorityItems
                );

        binding.autoPriority.setAdapter(
                adapter
        );

        binding.autoPriority.setText(
                "Normal",
                false
        );

        binding.autoPriority.setOnItemClickListener(
                (parent, view, position, id) -> {

                    priority =
                            priorityItems[position];

                });
    }

    // =========================
    // DROPDOWN REPEAT
    // =========================

    private void setupRepeat() {

        String[] repeatItems = {
                "Tidak",
                "Harian",
                "Mingguan",
                "Bulanan"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        repeatItems
                );

        binding.autoRepeat.setAdapter(
                adapter
        );

        binding.autoRepeat.setText(
                "Tidak",
                false
        );

        binding.autoRepeat.setOnItemClickListener(
                (parent, view, position, id) -> {

                    repeat =
                            repeatItems[position];

                });
    }

    // =========================
    // LOAD DATA EDIT
    // =========================

    private void setupEditData() {

        if (taskId != -1) {

            Task task =
                    db.taskDao()
                            .getTaskById(taskId);

            if (task != null) {

                binding.edtTitle.setText(
                        task.title
                );

                binding.edtCourse.setText(
                        task.course
                );

                binding.edtDate.setText(
                        task.date
                );

                binding.edtTime.setText(
                        task.time
                );

                binding.autoPriority.setText(
                        task.priority,
                        false
                );

                binding.autoRepeat.setText(
                        task.repeatType,
                        false
                );

                selectedDate = task.date;

                selectedTime = task.time;

                priority = task.priority;

                repeat = task.repeatType;
            }
        }
    }

    // =========================
    // DATE PICKER
    // =========================

    private void showDate() {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            selectedDate =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%d",
                                            dayOfMonth,
                                            month + 1,
                                            year
                                    );

                            binding.edtDate.setText(
                                    selectedDate
                            );

                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        dialog.show();
    }

    // =========================
    // TIME PICKER
    // =========================

    private void showTime() {

        Calendar calendar =
                Calendar.getInstance();

        TimePickerDialog dialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            selectedTime =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            hourOfDay,
                                            minute
                                    );

                            binding.edtTime.setText(
                                    selectedTime
                            );

                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );

        dialog.show();
    }

    // =========================
    // VALIDASI INPUT
    // =========================

    private boolean isValidInput() {

        if (TextUtils.isEmpty(
                binding.edtTitle
                        .getText()
                        .toString()
                        .trim()
        )) {

            binding.edtTitle.setError(
                    "Judul tugas wajib diisi"
            );

            return false;
        }

        if (TextUtils.isEmpty(
                binding.edtCourse
                        .getText()
                        .toString()
                        .trim()
        )) {

            binding.edtCourse.setError(
                    "Mata kuliah wajib diisi"
            );

            return false;
        }

        if (selectedDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pilih tanggal terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (selectedTime.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pilih jam terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // =========================
    // SAVE TASK
    // =========================

    private void saveTask() {

        if (!isValidInput()) {
            return;
        }

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    );

            Date dateObj =
                    sdf.parse(
                            selectedDate
                                    + " "
                                    + selectedTime
                    );

            if (dateObj == null) {

                Toast.makeText(
                        this,
                        "Tanggal tidak valid",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Task task = new Task();

            task.title =
                    binding.edtTitle
                            .getText()
                            .toString()
                            .trim();

            task.course =
                    binding.edtCourse
                            .getText()
                            .toString()
                            .trim();

            task.date = selectedDate;

            task.time = selectedTime;

            task.priority = priority;

            task.repeatType = repeat;

            task.deadlineMillis =
                    dateObj.getTime();

            if (taskId != -1) {

                Task oldTask =
                        db.taskDao()
                                .getTaskById(taskId);

                if (oldTask != null) {

                    task.completed =
                            oldTask.completed;
                }

                task.id = taskId;

                db.taskDao().update(task);

            } else {

                task.completed = false;

                db.taskDao().insert(task);
            }

            AlarmHelper.setAlarm(
                    this,
                    selectedDate,
                    selectedTime,
                    task.title,
                    repeat
            );

            TaskWidget.refreshWidget(this);

            Toast.makeText(
                    this,
                    "Tugas berhasil disimpan",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Terjadi kesalahan",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================
    // TRANSISI KELUAR HALUS
    // =========================

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.slide_out_right
        );
    }
}