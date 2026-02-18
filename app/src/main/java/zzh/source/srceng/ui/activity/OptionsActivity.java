package zzh.source.srceng.ui.activity;

import zzh.source.srceng.databinding.OptionsActivityBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import zzh.source.srceng.ui.activity.MainActivity;
import zzh.source.srceng.ui.widget.blur.BlurAlgorithm;
import zzh.source.srceng.ui.widget.blur.BlurView;
import zzh.source.srceng.ui.widget.blur.RenderEffectBlur;
import zzh.source.srceng.ui.widget.blur.RenderScriptBlur;
import android.view.animation.ScaleAnimation;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.*;
import android.graphics.drawable.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.File;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import zzh.source.srceng.ui.component.FileManagerActivity;
import zzh.source.srceng.R;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.graphics.Color;
// 编译器上色
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
// ----------------------------------------------------------
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
// 关键词上色
import android.widget.*;
import java.io.BufferedReader;
import android.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.*;
import java.util.Map;
import java.util.HashMap;
import android.view.animation.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;
import zzh.source.srceng.util.command.*;
import zzh.source.srceng.util.log.LogManager;
import com.google.android.material.checkbox.MaterialCheckBox;

public class OptionsActivity extends Fragment {
  private EditText GamePath, EnvEdit, UserName, fpsConfig, UserNameViewIS;
  private AutoCompleteTextView cmdArgs;
  private SharedPreferences mPref;
  public static String srcengDir;
  private TextView LogoutView;
  private MaterialCheckBox useVolumeButtons,
      checkBoxImmersiveMode,
      checkBoxPlayVideo,
      checkBoxUseVolume;
  public static String gameconfigdir = null;
  private Button LogoutViewbutton;
  private SeekBar sb_normal;
  private ScrollView surfaces_logsview;
  public String progressa = "";
  private LogManager logManager;
  private OptionsActivityBinding binding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    mPref = getActivity().getSharedPreferences("mod", 0);
  }

  private Map<String, Integer> HIGHLIGHT_KEYWORDS;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = OptionsActivityBinding.inflate(inflater, container, false);
    View view = binding.getRoot(); // 获取根视图
    initializeUI(view);
    bindseedkbViews();

    LogoutViewbutton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            initializeLogManager();
          }
        });
   new Handler()
        .postDelayed(
            () -> {
              initializeLogManager();
              loadLogs();
            },
            400);

    initializeUI(view);
    

    return view;
  }

  private void initializeUI(View view) {
    cmdArgs = binding.editCmdline;
    sb_normal = binding.setLogviewTextsize;
    EnvEdit = binding.editEnv;
    GamePath = binding.editGamepath;
    LogoutView = binding.logViewControl;
    LogoutViewbutton = binding.logMenuTore;
    surfaces_logsview = binding.surfacesLogsview;
    checkBoxImmersiveMode = binding.checkboxImmersiveMode;
    checkBoxPlayVideo = binding.checkboxPlayVideo;
    checkBoxUseVolume = binding.checkboxPlayPadui;
    surfaces_logsview.setOnScrollChangeListener(
        new View.OnScrollChangeListener() {
          @Override
          public void onScrollChange(
              View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (!logManager.isLoading() && logManager.shouldLoadMoreLogs(surfaces_logsview)) {
              logManager.loadMoreLogs();
            }
          }
        });
    String cmdArgsText = mPref.getString("argv", "-console");
    String envEditText = mPref.getString("env", "LIBGL_USEVBO=0");
    cmdArgs.setText(cmdArgsText);
    GamePath.setText(mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng"));
    EnvEdit.setText(envEditText);
    AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.edit_cmdline);
    List<String> suggestions =
        Arrays.asList(
            "-console",
            "-nobackgroundlevel",
            "-high",
            "-nolog",
            "-game episodic",
            "-fps_max 30",
            "-int");
    SuggestionAdapter adapter = new SuggestionAdapter(getActivity(), suggestions);
    autoCompleteTextView.setAdapter(adapter);
    autoCompleteTextView.setThreshold(1); //
    view.findViewById(R.id.set_srcengDir)
        .setOnClickListener(
            v -> {
              Intent intent = new Intent(getActivity(), FileManagerActivity.class);
              startActivity(intent);
              if (getActivity() != null) {
                getActivity()
                    .overridePendingTransition(
                        R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
              }
            });
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    executor.execute(
        () -> {
          boolean useVolume = loadCheckBoxState("checkbox_use_volume");
          boolean immersiveMode = loadCheckBoxState("checkbox_immersive_mode");
          boolean playVideo = loadCheckBoxState("checkbox_play_video");
          handler.post(
              () -> {
                checkBoxUseVolume.setChecked(useVolume);
                checkBoxImmersiveMode.setChecked(immersiveMode);
                checkBoxPlayVideo.setChecked(playVideo);
              });
        });
    checkBoxUseVolume.setOnCheckedChangeListener(
        (buttonView, isChecked) -> saveCheckBoxState("checkbox_use_volume", isChecked));
    checkBoxImmersiveMode.setOnCheckedChangeListener(
        (buttonView, isChecked) -> saveCheckBoxState("checkbox_immersive_mode", isChecked));
    checkBoxPlayVideo.setOnCheckedChangeListener(
        (buttonView, isChecked) -> saveCheckBoxState("checkbox_play_video", isChecked));
  }

  private void cacheTextContent() {
    ExecutorService executorlog = Executors.newFixedThreadPool(1);
    executorlog.submit(
        () -> {
          Bitmap bitmap =
              Bitmap.createBitmap(
                  LogoutView.getWidth(), LogoutView.getHeight(), Bitmap.Config.ARGB_8888);
          Canvas canvas = new Canvas(bitmap);
          LogoutView.draw(canvas);
        });
  }

  private void bindseedkbViews() {
    sb_normal.setMax(30);
    sb_normal.setMin(10);
    sb_normal.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogoutView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 使用软件层
            cacheTextContent(); //
            Rect rect = new Rect();
            surfaces_logsview.getHitRect(rect);
            int lineCount = LogoutView.getLineCount();
            for (int i = 0; i < lineCount; i++) {
              int baseline = LogoutView.getLineBounds(i, rect);
              if (baseline >= surfaces_logsview.getScrollY()
                  && baseline <= (surfaces_logsview.getScrollY() + surfaces_logsview.getHeight())) {
                LogoutView.setTextSize(progress);
              }
            }
            LogoutView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // 恢复硬件加速
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });
  }

  @Override
  public void onPause() {
    super.onPause();
    saveSettings(mPref.edit());
  }

  public void saveSettings(SharedPreferences.Editor editor) {
    String argv = cmdArgs.getText().toString();
    String gamepath = GamePath.getText().toString();
    String env = EnvEdit.getText().toString();
    editor.putString("argv", argv);
    editor.putString("gamepath", gamepath);
    editor.putString("env", env);
    editor.commit();
  }

  private void saveCheckBoxState(String key, boolean value) {
    SharedPreferences sharedPreferences =
        getContext().getSharedPreferences("CheckboxPreferences", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(key, value);
    editor.apply();
  }

  private boolean loadCheckBoxState(String key) {
    SharedPreferences sharedPreferences =
        getContext().getSharedPreferences("CheckboxPreferences", Context.MODE_PRIVATE);
    return sharedPreferences.getBoolean(key, false); // Default value is false
  }

  @Override
  public void onResume() {
    super.onResume();
    mPref = getActivity().getSharedPreferences("mod", 0);
    GamePath.setText(mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng"));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    saveSettings(mPref.edit());
  }

  private void initializeLogManager() {
    Map<String, Integer> highlightKeywords = new HashMap<>();
    highlightKeywords.put("Can't use cheat cvar", Color.GRAY);
    highlightKeywords.put("Error: Material", Color.RED);
    highlightKeywords.put("Particles: Missing", Color.MAGENTA);
    highlightKeywords.put(
        " Java_com_valvesoftware_ValveActivity2_setenv ",
        getResources().getColor(R.color.github_io_java));
    highlightKeywords.put("Can't find module", Color.YELLOW);
    highlightKeywords.put(
        "Compiler version: Clang 11.1.0", getResources().getColor(R.color.github_io_c));
    highlightKeywords.put("Compiler LDFLAGS:", getResources().getColor(R.color.github_io_c));
    highlightKeywords.put("Compiler CFLAGS:", getResources().getColor(R.color.github_io_c));
    highlightKeywords.put("HUAWEI", getResources().getColor(R.color.huawei_color));
    highlightKeywords.put("Xiaomi", getResources().getColor(R.color.xiaomi_color));
    highlightKeywords.put("Android", getResources().getColor(R.color.android_studio));
    highlightKeywords.put("/android-ndk-r10e/", getResources().getColor(R.color.github_io_shell));
    String logFilePath =
        mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng") + "/engine.log";
    logManager = new LogManager(LogoutView, logFilePath, highlightKeywords);
  }

  private void loadLogs() {
    logManager.loadMoreLogs();
  }
}
