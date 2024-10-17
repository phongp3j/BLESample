package com.example.mypets.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.mypets.Model.Pet;
import com.example.mypets.Model.Reminder;
import com.example.mypets.R;
import com.example.mypets.ReminderActivity;
import com.example.mypets.SQLite.PetDao;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String KEY_REMINDER = "KEY_REMINDER";

    private Reminder reminder;
    private Pet pet;
    private PetDao petDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Khi báo thức được kích hoạt, hiển thị thông báo
        petDao = new PetDao(context);
        reminder = (Reminder) intent.getSerializableExtra(KEY_REMINDER);

        showNotification(context);
        petDao.close();
    }

    private void showNotification(Context context) {
        // Tạo Intent để mở ReminderActivity khi người dùng chạm vào thông báo
        Intent openActivityIntent = new Intent(context, ReminderActivity.class);
        pet = petDao.getById(reminder.getPetId());
        openActivityIntent.putExtra(ReminderActivity.KEY_PET, pet);
        openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo thông báo kèm âm thanh
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo thông báo với PendingIntent
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.dog)
                .setContentTitle("Reminder for " + pet.getName() + ": " + reminder.getType())
                .setContentText("Don't forget! It's time for your scheduled task.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)                                                        // Thông báo tự động xoá sau khi người dùng chạm vào
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Thiết lập âm thanh
                .setContentIntent(pendingIntent);                                          // Gắn PendingIntent để xử lý sự kiện khi người dùng chạm vào

        // Hiển thị thông báo
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}