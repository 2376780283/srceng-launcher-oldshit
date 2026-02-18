package zzh.source.srceng.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
// import androidx.viewpager.widget.PagerAdapter;

import android.view.*;
import android.view.WindowManager;
import android.content.Intent;
import android.provider.Settings;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.pm.PackageManager;
import android.content.Context;

import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.os.Build;
import android.util.*;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import zzh.source.srceng.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zzh.source.srceng.util.crash.CrashReportActivity;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import zzh.source.srceng.ui.activity.MainActivity;
import android.util.Log;
import zzh.source.srceng.feature.tile.*;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

public class SplashActivity extends AppCompatActivity {
    // private final SplashViewModel splashViewModel = new SplashViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> handleUncaughtException(throwable));
        setContentView(R.layout.activity_spash);

        setFullScreen();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        // OpeningStartAnimation openingStartAnimation2 =
        //     new OpeningStartAnimation.Builder(this).setDrawStategy(new
        // LineDrawStrategy()).create();
        // openingStartAnimation2.show(this);

        new Handler()
                .postDelayed(
                        () -> {
                            startActivity(intent);
                            overridePendingTransition(
                                    R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
                            finish();
                        },
                        1400);
        //   finish();

        /*
        // 开始后台初始化任务
        splashViewModel.initializeApp(this);

        splashViewModel
            .getInitializationState()
            .observe(
                this,
                isInitialized -> {
                  if (isInitialized) {
                    // 初始化完成后跳转到 MainActivity
                    new Handler(Looper.getMainLooper())
                        .postDelayed(
                            () -> {
                              Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                              startActivity(intent);
                              finish();
                              // 过渡动画
                              overridePendingTransition(
                                  R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
                            },
                            1400); // 1.4秒
                  }
                });*/
    }

    private void setFullScreen() {
        View decorView = getWindow().getDecorView();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getWindow().setDecorFitsSystemWindows(false);
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void handleUncaughtException(Throwable throwable) {
        saveCrashLog(throwable); // 保存崩溃日志到本地
        Intent intent = new Intent(SplashActivity.this, CrashReportActivity.class);
        intent.putExtra("error", getStackTrace(throwable)); // 传递崩溃信息
        startActivity(intent);
        finish(); // 结束当前活动
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private void saveCrashLog(Throwable throwable) {
        try {
            File file = new File(getFilesDir(), "crash_log.txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getStackTrace(throwable).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCrashLog() {
        File file = new File(getFilesDir(), "crash_log.txt");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                fis.close();
                // 发送到崩溃报告页面
                Intent intent = new Intent(SplashActivity.this, CrashReportActivity.class);
                intent.putExtra("error", stringBuilder.toString());
                startActivity(intent);
                // 删除崩溃日志文件
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
/*
class SplashViewModel extends ViewModel {
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final MutableLiveData<Boolean> initializationState = new MutableLiveData<>();

  LiveData<Boolean> getInitializationState() {
    return initializationState;
  }

  void initializeApp(Context context) {
    executorService.execute(
        () -> {
          // 这里可以进行初始化操作，例如加载数据或预加载资源
          // 在主线程更新初始化状态
          new Handler(Looper.getMainLooper()).post(() -> initializationState.setValue(true));
        });
  }
}*/
