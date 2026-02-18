package zzh.source.srceng.feature.settings;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenConfigManager {

    private static final String TAG = "ScreenConfigManager";
    private static final String SCREEN_WIDTH_KEY = "\"ScreenWidth\"";
    private static final String SCREEN_HEIGHT_KEY = "\"ScreenHeight\"";

    private final String configFilePath;

    public ScreenConfigManager(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    // 从配置文件读取屏幕宽度
    public String readScreenWidth() {
        return readValueFromFile(SCREEN_WIDTH_KEY);
    }

    // 从配置文件读取屏幕高度
    public String readScreenHeight() {
        return readValueFromFile(SCREEN_HEIGHT_KEY);
    }

    // 从配置文件中根据 key 读取值
    private String readValueFromFile(String key) {
        String value = null;
        Pattern pattern = Pattern.compile(key + "\\s+\"(\\d+)\"");
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    value = matcher.group(1);
                    break;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
        }
        return value;
    }

    // 更新屏幕宽高到配置文件
    public void updateScreenConfig(String screenWidth, String screenHeight) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(SCREEN_WIDTH_KEY)) {
                    lines.add("\t" + SCREEN_WIDTH_KEY + "\t\t\"" + screenWidth + "\"");
                } else if (line.contains(SCREEN_HEIGHT_KEY)) {
                    lines.add("\t" + SCREEN_HEIGHT_KEY + "\t\t\"" + screenHeight + "\"");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading file for update", e);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file", e);
        }
    }
}
