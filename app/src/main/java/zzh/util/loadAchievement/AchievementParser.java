package zzh.util.loadAchievement;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import android.os.Handler;
import android.os.Looper;

// 返回数量
public class AchievementParser {
  private File file;
  private ExecutorService executorService;
  private Handler mainHandler;

  public AchievementParser(File file) {
    this.file = file;
    // 初始化线程池，单线程即可满足需求
    this.executorService = Executors.newSingleThreadExecutor();
    // 获取主线程的Handler
    this.mainHandler = new Handler(Looper.getMainLooper());
  }

  // 同步解析成就文件并返回解锁的成就数量
  public int parseUnlockedAchievementsSync() {
    int unlockedAchievements = 0;
    Pattern pattern = Pattern.compile("\"(\\d+)\"\\s*\\{");
    StringBuilder fileContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        fileContent.append(line).append("\n");
      }

      Matcher matcher = pattern.matcher(fileContent.toString());
      while (matcher.find()) {
        unlockedAchievements++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Error reading file: " + e.getMessage());
    }

    return unlockedAchievements;
  }

  // 异步解析成就文件，结果通过回调返回
  public void parseUnlockedAchievementsAsync(AchievementCallback callback) {
    // 提交异步任务到线程池
    executorService.submit(
        () -> {
          int result = parseUnlockedAchievementsSync(); // 调用同步方法执行实际的解析
          // 将结果通过Handler传回主线程
          mainHandler.post(() -> callback.onResult(result));
        });
  }

  // 释放线程池
  public void shutdown() {
    executorService.shutdown();
  }

  // 回调接口，用于返回结果
  public interface AchievementCallback {
    void onResult(int unlockedAchievements);
  }
}
