package zzh.ui;

import zzh.source.csso.databinding.WebActivityBinding;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import zzh.source.csso.MainActivity;
import zzh.source.csso.R;
import android.util.Log;
import android.Manifest.*;
import android.view.animation.OvershootInterpolator;
import androidx.cardview.widget.CardView;
import android.animation.*;
import com.google.android.material.*;
import zzh.util.modcontrol.ModAdapter;
import zzh.util.motd.motdofitem;
import com.zzh.widget.blurview.BlurAlgorithm;
import com.zzh.widget.blurview.BlurView;
import com.zzh.widget.blurview.RenderEffectBlur;
import com.zzh.widget.blurview.RenderScriptBlur;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import android.content.pm.PackageManager;

import zzh.util.motd.*;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import java.security.MessageDigest;
import android.util.Base64;
import android.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import java.util.Arrays;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.util.*;
import android.os.*;
import android.content.res.ColorStateList;
import java.util.concurrent.ExecutorService;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executors;
import com.valvesoftware.ValveActivity2;
import org.libsdl.app.SDLActivity;
import com.google.android.material.button.MaterialButton;
import android.animation.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import zzh.util.modcontrol.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import zzh.util.modcontrol.ModTabControl;
import androidx.viewpager2.widget.ViewPager2;
import zzh.util.tile.*;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import com.zzh.widget.progressbar.ProgressBarRoundNormal;
import com.zzh.widget.progressbar.ProgressBarRoundNormalofEP;
import zzh.util.loadAchievement.AchievementParser;
import android.animation.ValueAnimator;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import zzh.ui.dirchUI.DirchActivity;

public class WebActivity extends Fragment {
    private WebActivityBinding binding;
    private BlurView blurView;
    private float initialRadius = 0.1f; // 初始模糊半径
    private float finalRadius = 12f; // 最终模糊半径
    private long duration = 3000; // 动画时长
    private Handler handler = new Handler();
    private SharedPreferences mPref;
    private RecyclerView recyclerView;
    private MotdAdapter adapter;
    private ProgressBarRoundNormal progress;
    private TextView Achievement_Environment;
    public static String lastLaunchTime;
    public static String currentTime;
    private TextView UserNameViewIS;
    private TextView outtime,playtimer;
    private ImageButton episodic, eptwo;
    public static String gamed = ""; // 这样更安全一点 至少是个空的字符而不是 null
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_TOTAL_TIME = "TotalTime";
    private long startTime;
    private long accumulatedTime;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // 使用 ViewBinding 初始化
        binding = WebActivityBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); // 获取根视图
        mPref = getActivity().getSharedPreferences("mod", 0);

        // 使用 DataBinding 获取控件
        blurView = binding.blurView;
        Achievement_Environment = binding.AchievementEnvironment; // 通过 DataBinding 获取
        progress = binding.achievementView; // 通过 DataBinding 获取
        MaterialButton button = binding.ButtonLaunch; // 通过 DataBinding 获取按钮
        UserNameViewIS = binding.UserIDView; // 通过 DataBinding 获取用户名视图
        outtime = binding.outtime;
        playtimer = binding.playtimer;
        // 设置按钮点击监听器
        button.setOnClickListener(v -> rungame());
        FrameLayout bottomSheet = binding.bottomSheet;
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        // 设置初始状态
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.postDelayed(this::setupBlurView, 200); // 延迟  毫秒
        handler.postDelayed(this::checkGameState, 300);
        handler.postDelayed(this::updateUserName, 250);
        handler.postDelayed(this::updateTimerText, 230);
        // checkGameState();
    }

    private void checkGameState() {
        String gamePath =
                mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng")
                        + "/csso/gamestate.txt";
        File gamestateFile = new File(gamePath);
        if (gamestateFile.exists()) {
            setupAllAchievements();
        }
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences("last_launch_time", Context.MODE_PRIVATE);
        currentTime = MainActivity.getCurrentTime();
        lastLaunchTime = sharedPreferences.getString("last_launch_time", currentTime);
        outtime.setText(" " + lastLaunchTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserName();
        checkGameState();
    }

    private void updateUserName() {
        String userName = mPref.getString("user_name", "Unknown");
        if (userName != null) {
            UserNameViewIS.setText(userName);
        }
    }

    private void setupBlurView() {
        ViewGroup rootView =
                (ViewGroup)
                        getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Drawable windowBackground = getActivity().getWindow().getDecorView().getBackground();
        blurView.setupWith(rootView, new RenderScriptBlur(getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(initialRadius);

        ValueAnimator animator = ValueAnimator.ofFloat(initialRadius, finalRadius);
        animator.setDuration(duration);
        animator.addUpdateListener(
                animation -> {
                    float animatedValue = (float) animation.getAnimatedValue();
                    float validBlurRadius = Math.max(1f, Math.min(animatedValue, 8f));
                    blurView.setBlurRadius(validBlurRadius);
                });
        animator.start();
    }

    public void rungame() {
        String path =
                mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng") + "/hl2/";
        File file = new File(path);
        if (file.isDirectory()) {
            binding.ButtonLaunch.setIconResource(R.drawable.stop_launch);
            binding.ButtonLaunch.setText(R.string.srceng_launcher_cancel);
            Intent intent = new Intent(getActivity(), SDLActivity.class);
            startActivity(intent);
            getActivity()
                    .overridePendingTransition(
                            R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
        } else {
            showFileISnone();
        }
    }

    private void showFileISnone() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_gamefile_none, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        MaterialButton btnExit = dialogView.findViewById(R.id.dialog_button_exit);
        MaterialButton btnsetting = dialogView.findViewById(R.id.dialog_setting_gamepath);
        btnExit.setOnClickListener(v -> dialog.dismiss());
        btnsetting.setOnClickListener(
                v -> {
                    settingthegamepath();
                    dialog.dismiss();
                });
        dialog.show();
    }

    private void settingthegamepath() {
        Intent intent = new Intent(getActivity(), DirchActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity()
                    .overridePendingTransition(
                            R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
        }
    }

    @Override
    public void onDestroyView() {
        blurView.setBlurEnabled(false); // 释放模糊效果资源
        super.onDestroyView();
    }

    private void setupAchievement(
            String filePath,
            TextView achievementTextView,
            ProgressBarRoundNormal progressBar,
            int maxAchievements) {
        File gamestateFile = new File(filePath);
        if (!gamestateFile.exists() || !gamestateFile.canRead()) {
            return;
        }
        AchievementParser parser = new AchievementParser(gamestateFile);
        parser.parseUnlockedAchievementsAsync(
                unlockedAchievements ->
                        delayUpdateUI(
                                unlockedAchievements,
                                400,
                                achievementTextView,
                                progressBar,
                                maxAchievements));
    }

    // 统一的延时更新方法
    private void delayUpdateUI(
            int unlockedAchievements,
            long delayMillis,
            TextView achievementTextView,
            ProgressBarRoundNormal progressBar,
            int maxAchievements) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(
                () ->
                        updateUI(
                                unlockedAchievements,
                                achievementTextView,
                                progressBar,
                                maxAchievements),
                delayMillis);
    }

    // 统一的 UI 更新方法
    private void updateUI(
            int unlockedAchievements,
            TextView achievementTextView,
            ProgressBarRoundNormal progressBar,
            int maxAchievements) {
        if (unlockedAchievements == maxAchievements) {
            achievementTextView.setText(R.string.srceng_launcher_Achievement_override);
            achievementTextView.setTextColor(getActivity().getColor(R.color.bilibili_pink));
        } else {
            achievementTextView.setText(R.string.srceng_launcher_Achievement_on);
        }
        progressBar.setProgress(unlockedAchievements);
    }

    // 在适当的地方调用方法，分别处理两个成就文件
    private void setupAllAchievements() {
        String basePath = mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng");
        // 处理 episodic 文件夹的成就
        String episodicPath = basePath + "/csso/gamestate.txt";
        setupAchievement(episodicPath, Achievement_Environment, progress, 13);
    }
    private void updateTimerText() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        accumulatedTime = prefs.getLong(KEY_TOTAL_TIME, 0);
      // 将毫秒转换为分钟，保留一位小数
double minutes = accumulatedTime / (10000000.0 * 60); // 使用浮点运算
String formattedMinutes = String.format("%.1f", minutes); // 保留1位小数

// 显示在 TextView
playtimer.setText("累计时间: " + formattedMinutes + " 分钟");
    }
}
