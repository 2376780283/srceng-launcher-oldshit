package zzh.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectorySizeCalculator {

  private ExecutorService executorService;
  private final String defaultPath;

  public DirectorySizeCalculator(String defaultPath) {
    this.defaultPath = defaultPath;
    executorService = Executors.newSingleThreadExecutor();
  }

  public void calculateAndDisplaySize(String gamePath, Runnable uiUpdateTask) {
    executorService.execute(
        () -> {
          String srcengDir = gamePath != null ? gamePath : defaultPath;
          long directorySize = calculateDirectorySize(new File(srcengDir));
          uiUpdateTask.run();
        });
  }

  // 使用 File 类来计算目录大小
  public long calculateDirectorySize(File directory) {
    if (directory == null || !directory.exists()) {
      return 0;
    }
    if (directory.isFile()) {
      return directory.length();
    }

    File[] files = directory.listFiles();
    if (files == null) {
      return 0;
    }

    long size = 0;
    for (File file : files) {
      if (file.isDirectory()) {
        size += calculateDirectorySize(file); // 递归计算子目录i大小
      } else {
        size += file.length(); // 计算文件大小
      }
    }
    return size;
  }

  public static String formatSize(long size) {
    DecimalFormat df = new DecimalFormat("#.##");
    double bytes = size;
    double kilobytes = bytes / 1024;
    double megabytes = kilobytes / 1024;
    double gigabytes = megabytes / 1024;
    if (gigabytes >= 1) {
      return df.format(gigabytes) + " GB";
    } else if (megabytes >= 1) {
      return df.format(megabytes) + " MB";
    } else if (kilobytes >= 1) {
      return df.format(kilobytes) + " KB";
    } else {
      return df.format(bytes) + " B";
    }
  }
}
