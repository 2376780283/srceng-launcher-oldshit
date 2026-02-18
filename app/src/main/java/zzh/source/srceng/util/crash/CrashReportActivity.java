package zzh.source.srceng.util.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import zzh.source.srceng.R;
import zzh.source.srceng.ui.activity.MainActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import zzh.source.srceng.feature.tile.*;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.os.Handler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;

public class CrashReportActivity extends AppCompatActivity {
  private RecyclerView recyclerView;
  private RecyclerView.LayoutManager layoutManager;
  private TileAdapter adapter;
  private List<Tile> tileList; // 确保在类中声明
  private Runnable scrollRunnable;
  private Handler handler;
  private int currentPosition = 0;
  private boolean isAutoScrolling = false;
  private TextView errorDetailsTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crash_report);

    errorDetailsTextView = findViewById(R.id.errorDetailsTextView);
    Button restartButton = findViewById(R.id.restartButton);
    Button sendFeedbackButton = findViewById(R.id.sendFeedbackButton);
    // 获取传递过来的崩溃信息
    String errorDetails = getIntent().getStringExtra("error");

    if (errorDetails != null) {
      errorDetailsTextView.setText(errorDetails);
    }
    errorDetailsTextView.setTextIsSelectable(true);
    errorDetailsTextView.setLinksClickable(true);
    // 重新启动应用
    restartButton.setOnClickListener(v -> restartApp());
    // 发送反馈
    sendFeedbackButton.setOnClickListener(v -> sendCrashLog(errorDetails));
    recyclerView = findViewById(R.id.recycler_view);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    ExecutorService executor = Executors.newFixedThreadPool(1);
    List<Tile> tileList = initializeTileList();
    executor.submit(
        () -> {
          adapter = new TileAdapter(tileList);
          recyclerView.post(() -> recyclerView.setAdapter(adapter)); // 确保在主线程上更新 UI
        });
    new LinearSnapHelper().attachToRecyclerView(recyclerView);
    handler = new Handler();
    scrollRunnable =
        new Runnable() {
          @Override
          public void run() {
            if (adapter != null && isAutoScrolling) {
              int itemCount = adapter.getItemCount();
              recyclerView.smoothScrollToPosition(currentPosition);
              currentPosition = (currentPosition + 1) % itemCount; // 循环滚动
              handler.postDelayed(this, 3000); // 每 2 秒滚动一次
            }
          }
        };
  }

  // 重新启动应用
  private void restartApp() {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
    System.exit(2); // 退出应用
  }

  // 发送崩溃日志邮件
  private void sendCrashLog(String crashLog) {
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.setType("message/rfc822");
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"2376780283@qq.com"});
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Crash Report");
    emailIntent.putExtra(Intent.EXTRA_TEXT, crashLog);

    try {
      startActivity(Intent.createChooser(emailIntent, "Send email..."));
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(CrashReportActivity.this, "没有可发送邮件的app.", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    startAutoScroll(); // 在 onResume 中启动自动滚动
  }

  @Override
  public void onPause() {
    super.onPause();
    stopAutoScroll(); // 在 onPause 中停止自动滚动
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    stopAutoScroll(); // 清理资源，防止内存泄漏
    handler.removeCallbacksAndMessages(null); // 确保移除所有回调
  }

  private void startAutoScroll() {
    if (!isAutoScrolling) {
      isAutoScrolling = true;
      handler.postDelayed(scrollRunnable, 2000); // 启动自动滚动
    }
  }

  private void stopAutoScroll() {
    if (isAutoScrolling) {
      isAutoScrolling = false;
      handler.removeCallbacks(scrollRunnable); // 停止自动滚动
    }
  }

  private List<Tile> initializeTileList() {
    List<Tile> tileList = new ArrayList<>();

    tileList.add(new Tile(R.drawable.bg_cs_abydos, "崩溃原因•一", "设备配置不符合要求 或运行内存不足"));
    tileList.add(new Tile(R.drawable.ic_enm_xiaochun, "崩溃原因•二", "启动器在开发初期很多bug 闪退比较正常"));
    tileList.add(new Tile(R.drawable.zzhlife, "崩溃原因•三", "你的设备不支持 高斯模糊渲染"));
    return tileList;
  }
}
