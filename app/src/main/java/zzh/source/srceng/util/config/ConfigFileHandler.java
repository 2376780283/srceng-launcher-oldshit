package zzh.source.srceng.util.config;

import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;



public class ConfigFileHandler {
    public static String filePath;
    private final String lineToFind = "exec shader";
    private final Pattern pattern = Pattern.compile("^exec shader\\b.*", Pattern.MULTILINE);
    public ConfigFileHandler(String gamePath) {
    this.filePath = gamePath; //  不需要多余的参数 但是要注意 game.cfg 或者 skill.cfg 能保证进游戏时加载 cfg
  }
    
    // 读取文件内容
    private List<String> readFile() throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    // 保存文件内容
    private void writeFile(List<String> lines) throws IOException {
        FileWriter writer = new FileWriter(new File(filePath));
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
        writer.close();
    }

    // 检查是否存在 exec shader 这一行
    public boolean isLinePresent() throws IOException {
        List<String> lines = readFile();
        for (String line : lines) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    // 添加 exec shader 这一行
    public void addLine() throws IOException {
        List<String> lines = readFile();
        if (!isLinePresent()) {
            lines.add(lineToFind); // 添加到文件末尾
            writeFile(lines);
        }
    }

    // 删除 exec shader 这一行
    public void removeLine() throws IOException {
        List<String> lines = readFile();
        lines.removeIf(line -> pattern.matcher(line).find()); // 删除匹配的行
        writeFile(lines);
    }

    // 开关控制函数
    public void toggleLine(boolean isChecked) throws IOException {
        if (isChecked) {
            addLine();
        } else {
            removeLine();
        }
    }
}
