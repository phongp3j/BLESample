package com.example.mypets;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.Pet;
import com.example.mypets.Model.Reminder;
import com.example.mypets.SQLite.ReminderDao;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    public static final String TAG = "AddReminderActivity";
    public static final String KEY_PET_ADD_REMINDER = "KEY_PET_ADD_REMINDER";

    private Pet pet;
    private Reminder reminder;
    private ReminderDao reminderDao;

    private String selectedDate, selectedTime;

    private EditText edtPetId, edtYourPet, edtTypeReminder;
    private Button btnSelectDateTime, btnCancel, btnSave;

    private Calendar calendar;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        initView();

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET_ADD_REMINDER);

        if (pet != null) {
            edtPetId.setText(String.valueOf(pet.getId()));
            edtYourPet.setText(pet.getName());
            edtYourPet.setEnabled(false);
        }

        calendar = Calendar.getInstance();
        btnSelectDateTime.setOnClickListener(view -> {
            // Hiển thị DatePickerDialog trước
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (dateView, year, monthOfYear, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                        Log.d(TAG, "onCreate: date: " + selectedDate);

                        // Hiển thị TimePickerDialog sau khi chọn ngày
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    // Lưu thời gian được chọn
                                    selectedTime = selectedHour + ":" + String.format("%02d", selectedMinute);

                                    Log.d(TAG, "onCreate: date: " + selectedTime);

                                    String selectedDateTime = selectedDate + " " + selectedTime;
                                    btnSelectDateTime.setText(selectedDateTime);

                                }, hour, minute, true);

                        timePickerDialog.show();
                    }, year, month, day);

            datePickerDialog.show();
        });

        reminderDao = new ReminderDao(this);
        btnSave.setOnClickListener(view -> {
            if (checkRequireData()) {
                reminder = new Reminder();
                reminder.setPetId(Integer.parseInt(edtPetId.getText().toString()));
                reminder.setType(edtTypeReminder.getText().toString());
                reminder.setDate(selectedDate);
                reminder.setTime(selectedTime);

                Log.d(TAG, "onCreate: " + reminder.toString());

                if (reminderDao.add(reminder) != -1) {
                    Toast.makeText(this, "Save reminder successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: save reminder successfully");

                    onBackPressed();
                } else
                    Log.w(TAG, "onCreate: save reminder failed");
            }
        });

        btnCancel.setOnClickListener(view->{
            onBackPressed();
        });

    }

    private boolean checkRequireData() {
        if (TextUtils.isEmpty(edtPetId.getText().toString().trim()))
            return false;
        if (TextUtils.isEmpty(edtYourPet.getText().toString().trim()))
            return false;
        if (TextUtils.isEmpty(edtTypeReminder.getText().toString().trim()))
            return false;
        if (TextUtils.isEmpty(btnSelectDateTime.getText().toString().trim()))
            return false;

        return true;
    }

    private void initView() {
        edtPetId = findViewById(R.id.edt_pet_id);
        edtYourPet = findViewById(R.id.edt_your_pet);
        edtTypeReminder = findViewById(R.id.edt_type_reminder);

        btnSelectDateTime = findViewById(R.id.btn_select_date_time);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
    }
}