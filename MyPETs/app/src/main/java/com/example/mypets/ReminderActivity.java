package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Adapters.ReminderAdapter;
import com.example.mypets.Model.Pet;
import com.example.mypets.Model.Reminder;
import com.example.mypets.SQLite.ReminderDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderActivity extends AppCompatActivity implements ReminderAdapter.OnItemClickListener {

    public static final String KEY_PET = "key_pet";
    private static final String TAG = "ReminderActivity";
    private Pet pet;
    private ReminderAdapter completedReminderAdapter, pendingReminderAdapter;
    private List<Reminder> completedReminderByPetId, pendingReminderByPetId;
    private ReminderDao reminderDao;

    private ImageView avtPet;
    private TextView tvPetName, tvPetInfo, tvEmptyCompletedReminder, tvEmptyPendingReminder;
    private Button btnAddReminder;
    private RecyclerView rvCompletedReminders, rvPendingReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        initView();

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET);

        reminderDao = new ReminderDao(this);

        completedReminderAdapter = new ReminderAdapter(this::onItemClick);
        pendingReminderAdapter = new ReminderAdapter(this::onItemClick);

        LinearLayoutManager pendingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager completedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvPendingReminders.setLayoutManager(pendingLayoutManager);
        rvCompletedReminders.setLayoutManager(completedLayoutManager);

        rvPendingReminders.setAdapter(pendingReminderAdapter);
        rvCompletedReminders.setAdapter(completedReminderAdapter);

        if (pet != null) {
//            avtPet.setImageResource("");
            tvPetName.setText(pet.getName());
            tvPetInfo.setText(pet.getBreed() + ", " + pet.getAge() + "years");

            Log.d(TAG, "onCreate: __" + pet.getName());

            btnAddReminder.setOnClickListener(view -> {
                Intent toAddReminder = new Intent(this, AddReminderActivity.class);
                toAddReminder.putExtra(AddReminderActivity.KEY_PET_ADD_REMINDER, pet);
                startActivity(toAddReminder);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCompletedReminders();
        updatePendingReminders();
    }

    public void updateCompletedReminders() {
        completedReminderByPetId = reminderDao.getAll(pet.getId(), true);
        if (completedReminderByPetId.isEmpty()) {
            // hiển thị rỗng
            rvCompletedReminders.setVisibility(View.GONE);
            tvEmptyCompletedReminder.setVisibility(View.VISIBLE);
        } else {
            rvCompletedReminders.setVisibility(View.VISIBLE);
            tvEmptyCompletedReminder.setVisibility(View.GONE);

            completedReminderAdapter.setList(completedReminderByPetId);
        }
    }

    public void updatePendingReminders() {
        pendingReminderByPetId = reminderDao.getAll(pet.getId(), false);
        if (pendingReminderByPetId.isEmpty()) {
            // hiển thị rỗng
            rvPendingReminders.setVisibility(View.GONE);
            tvEmptyPendingReminder.setVisibility(View.VISIBLE);
        } else {
            rvPendingReminders.setVisibility(View.VISIBLE);
            tvEmptyPendingReminder.setVisibility(View.GONE);

            pendingReminderAdapter.setList(pendingReminderByPetId);
        }
    }

    private void initView() {
        avtPet = findViewById(R.id.avt_pet);
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetInfo = findViewById(R.id.tv_pet_info);
        btnAddReminder = findViewById(R.id.btn_add_reminder);

        rvCompletedReminders = findViewById(R.id.rv_completed_reminders);
        rvPendingReminders = findViewById(R.id.rv_pending_reminders);

        tvEmptyCompletedReminder = findViewById(R.id.tv_empty_completed_reminders);
        tvEmptyPendingReminder = findViewById(R.id.tv_empty_pending_reminders);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reminderDao.close();
    }

    @Override
    public void onItemClick(int btnPosition, int position) {
        Reminder updateReminder = pendingReminderByPetId.get(position);

        switch (btnPosition) {
            case ReminderAdapter.BTN_DONE:
                handleDoneAction(updateReminder);
                break;
            case ReminderAdapter.BTN_SNOOZE:
                handleSnoozeAction(updateReminder);
                break;
        }
    }

    private void handleSnoozeAction(Reminder updateReminder) {
        int snoozeMinutes = 30;
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date dateTime = dateTimeFormat.parse(updateReminder.getDate() + " " + updateReminder.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            calendar.add(Calendar.MINUTE, snoozeMinutes);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String newDate, newTime;
            newDate = dateFormat.format(calendar.getTime());
            newTime = timeFormat.format(calendar.getTime());

            updateReminder.setDate(newDate);
            updateReminder.setTime(newTime);

            if (reminderDao.update(updateReminder) > 0) {
                Toast.makeText(this, "Reminder snoozed for 30 minutes!", Toast.LENGTH_SHORT).show();
                updatePendingReminders();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed Parse date time", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDoneAction(Reminder updateReminder) {
        // Xử lý khi người dùng chọn nút Done
        updateReminder.setHasDone(1);
        if (reminderDao.update(updateReminder) > 0) {
            Toast.makeText(this, "Reminder marked as Done", Toast.LENGTH_SHORT).show();
            updatePendingReminders();
            updateCompletedReminders();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}