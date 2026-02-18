package zzh.source.srceng.util.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import zzh.source.srceng.R;

public class UpdateService extends Service {
  private static final String CHANNEL_ID = "update_channel";
  private static final int NOTIFICATION_ID = 1;
  private boolean serviceWork = false;
  private static final String TAG = "UpdateService";

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "Service Created");
    createNotificationChannel();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "Service Started");
    if (!serviceWork) {
      serviceWork = true;
      try {
        if (intent != null && intent.getExtras() != null) {
          String updateUrl = intent.getExtras().getString("update_url");
          if (updateUrl != null) {
            Log.d(TAG, "Update URL: " + updateUrl);
            sendNotif(updateUrl);
          } else {
            Log.d(TAG, "No update URL found in intent");
          }
        } else {
          Log.d(TAG, "Intent or extras are null");
        }
      } catch (Exception e) {
        Log.e(TAG, "Exception in onStartCommand", e);
      }
    } else {
      Log.d(TAG, "Service is already working");
    }
    return START_NOT_STICKY;
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "Update Channel";
      String description = "Channel for update notifications";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
      Log.d(TAG, "Notification channel created");
    }
  }

  private void sendNotif(String updateUrl) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
    PendingIntent pIntent =
        PendingIntent.getActivity(this, 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification =
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Update Available")
            .setContentText("Click to update the app")
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build();

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(NOTIFICATION_ID, notification);
    Log.d(TAG, "Notification sent");
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
/*
主要改进点
使用 NotificationCompat.Builder:
NotificationCompat.Builder 更加现代化，提供了对不同 Android 版本的更好支持，并简化了通知创建过程。
通知频道 (NotificationChannel):
Android 8.0（Oreo）及以上版本需要通知频道，使用 NotificationChannel 以确保通知能够显示。
安全检查:
对 intent 和 extras 进行非空检查，以防止潜在的 NullPointerException。
线程安全:
确保 serviceWork 变量的正确使用，防止服务被多次启动。
通知常量:
使用常量定义通知 ID 和频道 ID，避免硬编码。
总结
UpdateService 处理应用更新通知，用户通过点击通知来访问更新。优化后的代码使用了更现代的 API，增加了对不同 Android 版本的支持，提高了代码的安全性和可读性。
*/
