package com.example.mypets.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Model.Reminder;
import com.example.mypets.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    public static final int BTN_DONE = 0, BTN_SNOOZE = 1;

    private List<Reminder> reminders;
    private OnItemClickListener listener;

    public ReminderAdapter(OnItemClickListener listener) {
        this.reminders = new ArrayList<>();
        this.listener = listener;
    }

    public void setList(List<Reminder> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        boolean hasDone = reminder.isHasDone() == 1;

        holder.tvReminderTitle.setText(reminder.getType());
        holder.tvReminderTime.setText(reminder.getTime());
        holder.tvReminderDate.setText(reminder.getDate());

        if (hasDone) {
            holder.tvReminderDone.setVisibility(View.VISIBLE);
            holder.llActionBar.setVisibility(View.GONE);
        } else {
            holder.tvReminderDone.setVisibility(View.GONE);
            holder.llActionBar.setVisibility(View.VISIBLE);

            holder.btnReminderDone.setOnClickListener(v -> {
                listener.onItemClick(BTN_DONE, position);
            });
            holder.btnReminderSnooze.setOnClickListener(v -> {
                listener.onItemClick(BTN_SNOOZE, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int btnPosition, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReminderTitle, tvReminderTime, tvReminderDate, tvReminderDone;
        LinearLayout llActionBar;
        Button btnReminderDone, btnReminderSnooze;

        ViewHolder(View itemView) {
            super(itemView);

            tvReminderTitle = itemView.findViewById(R.id.tv_reminder_title);
            tvReminderDone = itemView.findViewById(R.id.tv_reminder_done);
            tvReminderTime = itemView.findViewById(R.id.tv_reminder_time);
            tvReminderDate = itemView.findViewById(R.id.tv_reminder_date);

            llActionBar = itemView.findViewById(R.id.ll_action_bar);
            btnReminderDone = itemView.findViewById(R.id.btn_reminder_done);
            btnReminderSnooze = itemView.findViewById(R.id.btn_reminder_snooze);
        }
    }
}
