package zzh.util.ConfigManagers;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ColorCorrection {

    private final String filePath;

    public ColorCorrection(String filePath) {
        this.filePath = filePath;
    }

    // 读取配置文件内容
    public String readConfigFile() {
        File file = new File(filePath);
        try {
            return file.exists() ? new String(Files.readAllBytes(file.toPath())) : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 检查是否启用 mat_colorcorrection
    public boolean isMatColorCorrectionEnabled() {
        String configContent = readConfigFile();
        String targetLine = "\"mat_colorcorrection\"";
        if (configContent.isEmpty()) return false;

        for (String line : configContent.split("\n")) {
            if (line.contains(targetLine) && line.contains("\"1\"")) {
                return true;
            }
        }
        return false;
    }

  // 更新配置文件中的 "mat_colorcorrection" 设置
  public String updateMatColorCorrection(String configContent, boolean isEnabled) {
    String targetLine = "\"mat_colorcorrection\"";
    String newValue = isEnabled ? "1" : "0";

    // 使用正则表达式匹配目标行，并替换值
    return configContent.replaceAll(
        "(?m)^\\s*\"mat_colorcorrection\"\\s+\"\\d+\"", targetLine + "\t\t\"" + newValue + "\"");
  }

    // 写入新的配置文件内容
    public void writeConfigFile(String newContent) {
        File file = new File(filePath);
        try {
            Files.write(file.toPath(), newContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
    private ConfigManager configManager;
    private final String CONFIG_PATH = "/config.txt";  // 根据实际路径调整

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ConfigManager
        configManager = new ConfigManager(getFilesDir().getPath() + CONFIG_PATH);

        // 设置 Switch 初始状态
        boolean isColorCorrectionEnabled = configManager.isMatColorCorrectionEnabled();
        switchColorCorrection.setChecked(isColorCorrectionEnabled);

        // 设置 Switch 监听器
        setupSwitchListener();
    }

    private void setupSwitchListener() {
        switchColorCorrection.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                String configContent = configManager.readConfigFile();
                if (!configContent.isEmpty()) {
                    String updatedContent = configManager.updateMatColorCorrection(configContent, isChecked);
                    configManager.writeConfigFile(updatedContent);
                }
            }
        );
    }
}
*/