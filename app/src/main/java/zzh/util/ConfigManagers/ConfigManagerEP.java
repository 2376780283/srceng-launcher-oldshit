package zzh.util.ConfigManagers;

import java.io.*;
import java.util.*;
import android.widget.*;

/*

// 在主类中，比如在某个 Activity 或 Fragment 中

// 初始化 ConfigManager
ConfigManager configManager = new ConfigManager(MainActivity.getDefaultDir());
Map<String, EditText> paramMap = new HashMap<>();
paramMap.put("r_bloomtintb", r_bloomtintb);
paramMap.put("r_bloomtintg", r_bloomtintg);
paramMap.put("r_bloomtintr", r_bloomtintr);
paramMap.put("r_bloomtintexponent", r_bloomtintexponent);
paramMap.put("mat_motion_blur_strength", mat_motion_blur_strength);

// 初始化参数和控件的映射
configManager.initializeParameterMap(paramMap);

// 加载配置文件内容
configManager.loadConfig();

// 用户修改后保存配置文件
configManager.saveConfig();

*/
// 返回字符串
public class ConfigManagerEP {

  public String configFilePath;
  private Map<String, EditText> parameterMap = new HashMap<>();

  // 构造方法，初始化配置文件路径
  public ConfigManagerEP(String gamePath) {
    this.configFilePath = gamePath; // 不需要多余的参数 注意是 shader.cfg
  }

  // 初始化参数和控件的映射
  public void initializeParameterMap(Map<String, EditText> paramMap) {
    this.parameterMap = paramMap;
  }

  // 加载配置文件
  public void loadConfig() {
    File bloomFile = new File(configFilePath);

    if (!bloomFile.exists()) {
      logError("Config file not found: " + configFilePath);
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(bloomFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // 循环处理每个参数
        for (Map.Entry<String, EditText> entry : parameterMap.entrySet()) {
          String param = entry.getKey();
          EditText editText = entry.getValue();
          if (line.startsWith(param)) {
            if (editText != null) {
              editText.setText(getValue(line));
            }
            break; // 匹配到就跳过后续检查
          }
        }
      }
    } catch (IOException e) {
      logError("Error reading config file: " + e.getMessage());
    }
  }

  // 保存配置文件
  public void saveConfig() {
    File bloomFile = new File(configFilePath);

    if (!bloomFile.exists()) {
      logError("Config file not found: " + configFilePath);
      return;
    }

    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(bloomFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        boolean modified = false;
        for (Map.Entry<String, EditText> entry : parameterMap.entrySet()) {
          String param = entry.getKey();
          EditText editText = entry.getValue();
          if (line.startsWith(param)) {
            line = param + " \"" + editText.getText().toString() + "\"";
            modified = true;
            break;
          }
        }
        lines.add(line);
      }
    } catch (IOException e) {
      logError("Error reading config file: " + e.getMessage());
      return;
    }

    // 将修改后的内容写回文件
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(bloomFile))) {
      for (String outputLine : lines) {
        writer.write(outputLine);
        writer.newLine(); // 保持换行
      }
    } catch (IOException e) {
      logError("Error writing config file: " + e.getMessage());
    }
  }

  // 提取数值的方法
  private String getValue(String line) {
    String[] parts = line.split("\"");
    if (parts.length > 1) {
      return parts[1].trim();
    } else {
      return "";
    }
  }

  // 错误日志打印，方便排查问题
  private void logError(String errorMessage) {
    System.err.println(errorMessage);
  }
}
