package zzh.HomePageui.OptionsPage;

import zzh.source.csso.databinding.HomePageuiOtherBinding;
import zzh.source.csso.MainActivity;
import zzh.source.csso.R;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// z这是 设置活动 b
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
// 读取文件的包
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import android.widget.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import android.os.*;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import zzh.ui.OptionsActivity;
import com.zzh.widget.progressbar.ProgressBarRound;
import zzh.util.OptionsActivitytools.ScreenConfigManager;
import java.util.Map;
import java.util.HashMap;
import zzh.util.ConfigManagers.ConfigManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zzh.util.tile.*;
import zzh.util.ConfigManagers.ConfigFileHandler;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import com.zzh.widget.SwitchFlyView.SwitchView;
import zzh.util.ConfigManagers.ConfigFileHandler;
import zzh.util.OptionsActivitytools.ScreenConfigManager;
import zzh.util.ConfigManagers.ColorCorrection;

public class PageEPOT extends Fragment {

    private EditText userNameEditText, fpsCountEditText;
    private SharedPreferences mPref;
    private ConfigManager configManager;
    private String filePath, configViewPath, configBloomPath;
    private ScreenConfigManager screenConfigManager;
    private ConfigFileHandler configFileHandler;
    private SwitchView switchView, switchColorCorrection;
    private EditText r_bloomtintb,
            r_bloomtintg,
            r_bloomtintr,
            mat_motion_blur_strength,
            mat_bloom_scalefactor_scalar,
            mat_bloomscale,
            r_bloomtintexponent;
    private ColorCorrection colorcorrect;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final String CONFIG_PATH = "/csso/videoconfig_android.cfg";
    private static final String CONFIG_BLOOM_PATH = "/csso/cfg/skill.cfg";
    private EditText videoWidthEditText, videoHeightEditText;
    private HomePageuiOtherBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = HomePageuiOtherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        mPref = getActivity().getSharedPreferences("mod", 0);
        // 重要设置
        filePath = mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng");
        configViewPath = filePath + CONFIG_PATH;
        configBloomPath = filePath + CONFIG_BLOOM_PATH;
        screenConfigManager = new ScreenConfigManager(configViewPath);
        configFileHandler = new ConfigFileHandler(configBloomPath);
        colorcorrect = new ColorCorrection(filePath + CONFIG_PATH);
        configManager = new ConfigManager(configBloomPath);
        setupUIElements(view);
    }

    private void setupUIElements(View view) {

        userNameEditText = binding.videoName;
        fpsCountEditText = binding.videoFps;
        fpsCountEditText.setText(mPref.getString("env_fps", "30"));
        userNameEditText.setText(mPref.getString("user_name", "Unknown"));

        videoWidthEditText = binding.videoW;
        videoHeightEditText = binding.videoH;
        switchView = binding.Controlwithbloom;
        switchColorCorrection = view.findViewById(R.id.Controlwithficolor);
        setupSwitchView();
        setupSwitchListener();
        initializeFileConfigs();
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("env_fps", fpsCountEditText.getText().toString());
        editor.putString("user_name", userNameEditText.getText().toString());
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveConfiguration();
        saveSettings();
    }

    @Override
    public void onResume() {
        mPref = getActivity().getSharedPreferences("mod", 0);
        // 重要设置
        filePath = mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng");
        configViewPath = filePath + CONFIG_PATH;
        configBloomPath = filePath + CONFIG_BLOOM_PATH;
        super.onResume();
        initializeFileConfigs();
        setupSwitchView();

        boolean isColorCorrectionEnabled = colorcorrect.isMatColorCorrectionEnabled();
        switchColorCorrection.toggleSwitch(isColorCorrectionEnabled);
    }

    // 判断是否开启颜色修正

    private void setupSwitchView() {
        try {
            boolean isLinePresent = configFileHandler.isLinePresent();
            switchView.toggleSwitch(isLinePresent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (switchView != null) {

            switchView.setOnStateChangedListener(
                    new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            toggleSwitchState(true);
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            toggleSwitchState(false);
                        }
                    });
        }
    }

    private void toggleSwitchState(boolean isOn) {
        switchView.toggleSwitch(isOn);
        try {
            if (isOn) {
                configFileHandler.addLine();
            } else {
                configFileHandler.removeLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSwitchListener() {
        if (switchColorCorrection != null) {
            switchColorCorrection.setOnStateChangedListener(
                    new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            // 读取最新配置内容
                            String configContent = colorcorrect.readConfigFile();
                            if (!configContent.isEmpty()) {
                                // 将 true 传递给 updateMatColorCorrection 表示开启
                                String updatedContent =
                                        colorcorrect.updateMatColorCorrection(configContent, true);
                                colorcorrect.writeConfigFile(updatedContent);
                            }
                            view.toggleSwitch(true); // 切换到 ON 状态
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            // 读取最新配置内容
                            String configContent = colorcorrect.readConfigFile();
                            if (!configContent.isEmpty()) {
                                // 将 false 传递给 updateMatColorCorrection 表示关闭
                                String updatedContent =
                                        colorcorrect.updateMatColorCorrection(configContent, false);
                                colorcorrect.writeConfigFile(updatedContent);
                            }
                            view.toggleSwitch(false); // 切换到 OFF 状态
                        }
                    });
        }
    }

    // 分辨率配置
    private void initializeFileConfigs() {
        File file = new File(filePath);
        if (file.exists()) {
            loadConfigurationFromFile();
        }
    }

    private void loadConfigurationFromFile() {
        if (videoWidthEditText != null) {
            videoWidthEditText.setText("");
        }
        if (videoHeightEditText != null) {
            videoHeightEditText.setText("");
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    String screenWidth = screenConfigManager.readScreenWidth();
                    String screenHeight = screenConfigManager.readScreenHeight();
                    new Handler(Looper.getMainLooper())
                            .post(
                                    () -> {
                                        videoWidthEditText.setText(screenWidth);
                                        videoHeightEditText.setText(screenHeight);
                                    });
                });
    }

    private void saveConfiguration() {
        new Thread(
                        () -> {
                            File file = new File(filePath);
                            if (file.exists()) {
                                String screenWidth = videoWidthEditText.getText().toString();
                                String screenHeight = videoHeightEditText.getText().toString();
                                screenConfigManager.updateScreenConfig(screenWidth, screenHeight);
                                initializeFileConfigs();
                            }
                        })
                .start();
    }

}
