package zzh.source.srceng.ui.activity;

import androidx.viewpager.widget.ViewPager;
import zzh.source.srceng.databinding.ActivityMainBinding;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import androidx.core.app.ActivityCompat;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.EditText;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Handler;
import android.graphics.*;
import android.os.SystemClock;
import android.os.Looper;
import android.content.res.ColorStateList;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zzh.source.srceng.ui.adapter.VerticalPageTransformer;
import org.libsdl.app.SDLActivity;
import zzh.source.srceng.ui.widget.tablayout.VerticalTabLayout;
import zzh.source.srceng.ui.widget.tablayout.VerticalTabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;
import com.valvesoftware.ValveActivity2;
import zzh.source.srceng.ui.adapter.PagerAdapter;
import zzh.source.srceng.util.crash.CrashReportActivity;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import zzh.source.srceng.util.size.DirectorySizeCalculator;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import com.google.android.material.button.MaterialButton;
import zzh.source.srceng.R;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final int AUDIO_PERMISSION_CODE = 2;
    private static final int REQUEST_PERMISSIONS = 42;
    private SharedPreferences mPref;
    private TextView outtime;
    private TextView textViewSize;
    public static String lastLaunchTime;
    public static String currentTime;
    public static String PKG_NAME;
    private ExecutorService executorService;

    private static final String KEY_PLAY_TIME = "PlayTime";
    private DirectorySizeCalculator sizeCalculator;
    private ViewPager2 viewPager;
    private PagerAdapter pagerAdapter;
    private static final String VALID_SIGNATURE = "18F805905AC4A5327A8D1CA046D5E93D6A3F1160122BD79B8A8C1D3E1666F964";

    public static String getDefaultDir() {
        File dir = Environment.getExternalStorageDirectory();
        return dir != null && dir.exists() ? dir.getPath() : "/sdcard/";
    }

    public static String getAndroidDataDir() {
        String path = getDefaultDir() + "/Android/data/" + PKG_NAME + "/files";
        File directory = new File(path);
        if (!directory.exists()) directory.mkdirs();
        return path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> handleUncaughtException(throwable));
        loadCrashLog();
        setFullScreen();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPref = getSharedPreferences("mod", 0);
        // loadLastLaunchTime();
        requestNecessaryPermissions();
        viewPager = binding.viewPager;
        VerticalTabLayout tabLayoutV = binding.verticalTabLayout;
        textViewSize = binding.gameSize;
        pagerAdapter = new PagerAdapter(this, viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setUserInputEnabled(false);
        viewPager.setPageTransformer(new VerticalPageTransformer());
        setupTabLayout();
        sizeCalculator = new DirectorySizeCalculator(getDefaultDir() + "/srceng");
        String gamePath = mPref.getString("gamepath", null);
        sizeCalculator.calculateAndDisplaySize(
                gamePath, () -> runOnUiThread(this::updateDirectorySizeUI));
        /*if (!isSignatureValid()) {
            showSignatureErrorDialog();
        }*/
    }

    private void updateDirectorySizeUI() {
        long directorySize = sizeCalculator.calculateDirectorySize(
                new File(mPref.getString("gamepath", getDefaultDir() + "/srceng")));
        String formattedSize = DirectorySizeCalculator.formatSize(directorySize);
        if (textViewSize != null) {
            textViewSize.setText(" " + formattedSize);
        }
    }

    private void setFullScreen() {
        View decorView = getWindow().getDecorView();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getWindow().setDecorFitsSystemWindows(false);
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
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
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void requestNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                    },
                    REQUEST_PERMISSIONS);
        }
    }

    /*
        private void loadLastLaunchTime() {
            SharedPreferences sharedPreferences =
                    getSharedPreferences("last_launch_time", Context.MODE_PRIVATE);
            currentTime = getCurrentTime();
            lastLaunchTime = sharedPreferences.getString("last_launch_time", currentTime);
            outtime.setText(" " + lastLaunchTime);
        }
    */
    public static String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = getSharedPreferences("last_launch_time", Context.MODE_PRIVATE).edit();
        editor.putString("last_launch_time", currentTime);
        editor.apply();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    // 处理未捕获异常
    private void handleUncaughtException(Throwable throwable) {
        String stackTrace = getStackTrace(throwable);
        saveCrashLog(stackTrace);
        startCrashReportActivity(stackTrace);
        finish();
    }

    // 启动崩溃报告页面
    private void startCrashReportActivity(String error) {
        Intent intent = new Intent(MainActivity.this, CrashReportActivity.class);
        intent.putExtra("error", error);
        startActivity(intent);
    }

    // 获取异常堆栈信息
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    // 保存崩溃日志
    private void saveCrashLog(String log) {
        try (FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "crash_log.txt"))) {
            fos.write(log.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 加载并清理崩溃日志
    private void loadCrashLog() {
        File file = new File(getFilesDir(), "crash_log.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            StringBuilder errorLog = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorLog.append(line).append("\n");
            }
            startCrashReportActivity(errorLog.toString());
            file.delete(); // 删除崩溃日志文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTabLayout() {
        VerticalTabLayout tabLayoutV = binding.verticalTabLayout;
        int colorUnselected = ContextCompat.getColor(this, R.color.apple_switch_track_color);
        int colorSelected = ContextCompat.getColor(this, R.color.colorPrimary);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected}, // 选中状态
                        new int[]{-android.R.attr.state_selected} // 未选中状态
                },
                new int[]{
                        Color.WHITE, // 选中状态颜色
                        Color.GRAY // 未选中状态颜色
                });
        tabLayoutV.setTabIconTint(colorStateList);
        tabLayoutV.setSelectedTabIndicatorColor(getResources().getColor(R.color.bilibili_pink));
        tabLayoutV.setTabTextColors(colorUnselected, colorSelected);
        tabLayoutV.setBackground(null);
        tabLayoutV.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        new VerticalTabLayoutMediator(
                tabLayoutV,
                viewPager,
                (tab, position) -> {
                    String[] tabTitles = {
                            getString(R.string.tab_main_activity),
                            getString(R.string.tab_other_activity),
                            getString(R.string.tab_options_activity),
                            getString(R.string.daily)
                    };
                    int[] tabIcons = {
                            R.drawable.ic_tab_hone,
                            R.drawable.ic_tab_web,
                            R.drawable.ic_tab_opt,
                            R.drawable.baseline_edit_24
                    };
                    tab.setText(tabTitles[position]);
                    tab.setIcon(tabIcons[position]);
                })
                .attach();
    }

    private boolean isSignatureValid() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? PackageManager.GET_SIGNING_CERTIFICATES : PackageManager.GET_SIGNATURES);

            Signature[] signatures = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? packageInfo.signingInfo.getApkContentsSigners() : packageInfo.signatures;

            for (Signature signature : signatures) {
                if (VALID_SIGNATURE.equals(computeSHA256(signature.toByteArray()))) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            showSignatureErrorDialog();
        }
        return false;
    }

    // 计算 SHA-256 签名的工具方法
    private String computeSHA256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return bytesToHex(md.digest()).toUpperCase().trim();
    }

    // 字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // 显示签名错误对话框
    private void showSignatureErrorDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_signature_error, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();

        dialogView.findViewById(R.id.dialog_button_exit).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
            System.exit(0); // 确保应用程序完全退出
        });

        dialog.show();
    }
}
