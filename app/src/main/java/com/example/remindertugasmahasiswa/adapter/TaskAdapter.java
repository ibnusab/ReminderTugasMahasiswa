package com.example.remindertugasmahasiswa.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.remindertugasmahasiswa.AddEditTaskActivity;
import com.example.remindertugasmahasiswa.MainActivity;
import com.example.remindertugasmahasiswa.TaskManagementActivity;
import com.example.remindertugasmahasiswa.database.AppDatabase;
import com.example.remindertugasmahasiswa.databinding.ItemTaskBinding;
import com.example.remindertugasmahasiswa.model.Task;
import com.example.remindertugasmahasiswa.widget.TaskWidget;

import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    Context context;

    AppDatabase db;

    List<Task> list;

    public TaskAdapter(
            Context context,
            AppDatabase db,
            List<Task> list
    ) {

        this.context = context;

        this.db = db;

        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType
    ) {

        ItemTaskBinding binding =
                ItemTaskBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            ViewHolder holder,
            int position
    ) {

        Task task = list.get(position);

        // =========================
        // SET DATA
        // =========================

        holder.binding.txtTitle.setText(
                task.title
        );

        holder.binding.txtCourse.setText(
                task.course
        );

        holder.binding.txtDate.setText(
                task.date + " • " + task.time
        );

        // =========================
        // COUNTDOWN DEADLINE
        // =========================

        long current =
                System.currentTimeMillis();

        long diff =
                task.deadlineMillis - current;

        long days =
                diff / (1000 * 60 * 60 * 24);

        long hours =
                (diff / (1000 * 60 * 60)) % 24;

        // =========================
        // STATUS TASK
        // =========================

        if (task.completed) {

            // =========================
            // SUDAH SELESAI
            // =========================

            holder.binding.txtCountdown.setText(
                    "Selesai"
            );

            holder.binding.txtCountdown.setTextColor(
                    Color.parseColor("#16A34A")
            );
        }

        // =========================
        // TERLAMBAT
        // =========================

        else if (diff < 0) {

            holder.binding.txtCountdown.setText(
                    "Terlambat"
            );

            holder.binding.txtCountdown.setTextColor(
                    Color.parseColor("#DC2626")
            );
        }

        // =========================
        // HARI INI
        // =========================

        else if (days == 0) {

            holder.binding.txtCountdown.setText(
                    hours + " jam tersisa"
            );

            holder.binding.txtCountdown.setTextColor(
                    Color.parseColor("#F59E0B")
            );
        }

        // =========================
        // MASIH AMAN
        // =========================

        else {

            holder.binding.txtCountdown.setText(
                    days + " hari tersisa"
            );

            holder.binding.txtCountdown.setTextColor(
                    Color.parseColor("#2563EB")
            );
        }

        // =========================
        // RESET LISTENER
        // =========================

        holder.binding.checkDone
                .setOnCheckedChangeListener(null);

        // =========================
        // SET CHECKBOX
        // =========================

        holder.binding.checkDone.setChecked(
                task.completed
        );

// jika sudah selesai disable checkbox
        if (task.completed) {

            holder.binding.checkDone.setEnabled(false);

        } else {

            holder.binding.checkDone.setEnabled(true);
        }

        // =========================
        // WARNA CHECKBOX
        // =========================

        holder.binding.checkDone.setButtonTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#2563EB")
                )
        );

        // =========================
        // WARNA STATUS TASK
        // =========================

        updateTaskColor(
                holder,
                task.completed
        );

        // =========================
// CHECKLIST TASK
// =========================

        holder.binding.checkDone
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            if (!isChecked) {

                                holder.binding.checkDone.setChecked(true);

                                Toast.makeText(
                                        context,
                                        "Tugas yang sudah selesai tidak bisa dibatalkan",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            long currentTime =
                                    System.currentTimeMillis();

                            if (task.deadlineMillis < currentTime) {

                                holder.binding.checkDone.setChecked(false);

                                Toast.makeText(
                                        context,
                                        "Deadline sudah lewat",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            task.completed = true;

                            int earnedXP;

                            if ("Tinggi".equals(task.priority)) {

                                earnedXP = 100;

                            } else if ("Normal".equals(task.priority)) {

                                earnedXP = 70;

                            } else {

                                earnedXP = 50;
                            }

                            task.productivityPoint = earnedXP;

                            db.taskDao().update(task);

                            Toast.makeText(
                                    context,
                                    "+" + earnedXP + " XP 🎉",
                                    Toast.LENGTH_SHORT
                            ).show();

                            holder.binding.txtCountdown.setText(
                                    "Selesai"
                            );

                            holder.binding.txtCountdown.setTextColor(
                                    Color.parseColor("#16A34A")
                            );

                            updateTaskColor(
                                    holder,
                                    true
                            );

                            if (context instanceof MainActivity) {

                                ((MainActivity) context)
                                        .refreshRealtime();
                            }

                            if (context instanceof TaskManagementActivity) {

                                ((TaskManagementActivity) context)
                                        .refreshRealtime();
                            }

                            TaskWidget.refreshWidget(
                                    context
                            );

                            notifyItemChanged(
                                    holder.getAdapterPosition()
                            );

                        });
        // =========================
        // HAPUS TASK
        // =========================

        holder.binding.btnDelete
                .setOnClickListener(v -> {

                    new AlertDialog.Builder(context)

                            .setTitle("Hapus Tugas")

                            .setMessage(
                                    "Yakin ingin menghapus tugas ini?"
                            )

                            .setPositiveButton(
                                    "Ya",
                                    (dialog, which) -> {

                                        int adapterPosition =
                                                holder.getAdapterPosition();

                                        // =========================
                                        // VALIDASI POSITION
                                        // =========================

                                        if (adapterPosition
                                                == RecyclerView.NO_POSITION) {

                                            return;
                                        }

                                        // =========================
                                        // HAPUS DATABASE
                                        // =========================

                                        db.taskDao()
                                                .delete(task);

                                        // =========================
                                        // HAPUS LIST
                                        // =========================

                                        list.remove(
                                                adapterPosition
                                        );

                                        // =========================
                                        // REFRESH ADAPTER
                                        // =========================

                                        notifyItemRemoved(
                                                adapterPosition
                                        );

                                        notifyItemRangeChanged(
                                                adapterPosition,
                                                list.size()
                                        );

                                        // =========================
                                        // REFRESH REALTIME
                                        // =========================

                                        if (context instanceof TaskManagementActivity) {

                                            ((TaskManagementActivity) context)
                                                    .refreshRealtime();
                                        }

                                        // =========================
                                        // REFRESH WIDGET
                                        // =========================

                                        TaskWidget.refreshWidget(
                                                context
                                        );
                                    })

                            .setNegativeButton(
                                    "Batal",
                                    null
                            )

                            .show();
                });

        // =========================
        // EDIT TASK
        // =========================

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            AddEditTaskActivity.class
                    );

            intent.putExtra(
                    "taskId",
                    task.id
            );

            context.startActivity(intent);
        });
    }

    // =========================
    // UPDATE WARNA TASK
    // =========================

    private void updateTaskColor(
            ViewHolder holder,
            boolean completed
    ) {

        if (completed) {

            // =========================
            // BIRU = SELESAI
            // =========================

            holder.binding.viewPriority
                    .setBackgroundColor(
                            Color.parseColor("#2563EB")
                    );

        } else {

            // =========================
            // MERAH = BELUM SELESAI
            // =========================

            holder.binding.viewPriority
                    .setBackgroundColor(
                            Color.parseColor("#EF4444")
                    );
        }
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ItemTaskBinding binding;

        public ViewHolder(
                ItemTaskBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}