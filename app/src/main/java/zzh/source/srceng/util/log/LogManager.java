package zzh.source.srceng.util.log;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class LogManager {
  private final TextView logView;
  private final Map<String, Integer> highlightKeywords;
  private final String logFilePath;
  private int currentLogOffset = 0; // 当前已经加载的日志文件偏移量
  private static final int CHUNK_SIZE = 8 * 512; // 每次加载的日志块大小（10KB）
  private boolean isLoading = false; // 是否正在加载更多日志

  public LogManager(TextView logView, String logFilePath, Map<String, Integer> highlightKeywords) {
    this.logView = logView;
    this.logFilePath = logFilePath;
    this.highlightKeywords = highlightKeywords;
  }

  public void loadMoreLogs() {
    if (isLoading) return;
    isLoading = true;
    new ReadLogTask().execute(logFilePath);
  }

  public boolean shouldLoadMoreLogs(ScrollView scrollView) {
    int diff = logView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
    return diff <= 100; // 当距离底部小于 100 时加载更多
  }

  private class ReadLogTask extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
      String filePath = params[0];
      StringBuilder content = new StringBuilder();
      try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
        file.seek(currentLogOffset); // 从上次读取的位置开始
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead = file.read(buffer);
        if (bytesRead > 0) {
          content.append(new String(buffer, 0, bytesRead));
          currentLogOffset += bytesRead; // 更新偏移量
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return content.toString();
    }

    @Override
    protected void onPostExecute(String result) {
      appendColoredText(result);
      isLoading = false; // 加载完成后设置为 false
    }
  }

  private void appendColoredText(String text) {
    SpannableString spannable = new SpannableString(text);
    for (Map.Entry<String, Integer> entry : highlightKeywords.entrySet()) {
      String keyword = entry.getKey();
      int color = entry.getValue();
      int start = text.indexOf(keyword);
      while (start >= 0) {
        int end = start + keyword.length();
        spannable.setSpan(
            new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = text.indexOf(keyword, end);
      }
    }
    logView.append(spannable);
  }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

}
