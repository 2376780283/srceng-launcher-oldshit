package zzh.util.update;

import android.content.*;
import java.io.*;
import java.net.*;
import zzh.source.csso.R;
import android.os.AsyncTask;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import android.util.Log;
import zzh.util.update.UpdateService;

// new
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class UpdateSystem {

  // private static final String git_url =
  // "https://raw.githubusercontent.com/2376780283/update_source-engine/main/";
  // private static final String git_url =
  //     "https://raw.fastgit.org/2376780283/update_source-engine/main/";
  // 或者使用 GitHub Proxy
  private static final String git_url =
      "https://ghproxy.com/https://raw.githubusercontent.com/2376780283/update_source-engine/main/";
  // 这种方式可以通过访问镜像站点来绕过网络限制。
  private static final String app = "Episodic.apk";
  private String deploy_branch, last_commit;
  private Context mContext;
  private ExecutorService executorService = Executors.newSingleThreadExecutor();
  private static final String TAG = "UpdateSystem";

  public UpdateSystem(Context context) {
    mContext = context;
    deploy_branch = context.getResources().getString(R.string.srceng_app_name);
    last_commit = context.getResources().getString(R.string.srceng_update);
    Log.d(
        TAG, "UpdateSystem initialized with deploy_branch: " + " and last_commit: " + last_commit);
  }

  public void checkForUpdate() {
    executorService.execute(
        () -> {
          try {
            Log.d(TAG, "Checking for updates");
            URL url = new URL(git_url + "/version");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String result = toString(connection.getInputStream());
            Log.d(TAG, "Update check result: " + result);
            if (result != null && !result.isEmpty() && !last_commit.equals(result)) {
              Log.d(TAG, "Update available, starting service");
              Intent notif = new Intent(mContext, UpdateService.class);
              notif.putExtra("update_url", git_url + app);
              mContext.startService(notif);
            } else {
              Log.d(TAG, "No update required or result is empty");
            }
          } catch (IOException e) {
            Log.e(TAG, "IOException in checkForUpdate", e);
          }
        });
  }

  private String toString(InputStream inputStream) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      return sb.toString();
    } catch (IOException e) {
      Log.e(TAG, "IOException in toString", e);
      return "";
    }
  }
}
