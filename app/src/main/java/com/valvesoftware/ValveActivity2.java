package com.valvesoftware;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.*;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import zzh.source.csso.MainActivity;
import zzh.source.csso.R;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import java.util.Locale;
import java.io.File;
import zzh.ui.OptionsActivity;
import zzh.util.includeAssets.ExtractAssets;
import java.util.Locale;

public class ValveActivity2 {
  public static native void setArgs(String args);

  public static native int setenv(String name, String value, int overwrite);

  public static String steamDeck = null;
  public static String savedCmdArgs = null;
  private static Context context; // 保留 context 为静态变量

  public static void initNatives(Context context, Intent intent) {
    ValveActivity2.context = context;
    SharedPreferences mPref = context.getSharedPreferences("mod", Context.MODE_PRIVATE);
    String savedCmdArgs = mPref.getString("argv", "-console");
    String savedGamePath = mPref.getString("gamepath", MainActivity.getDefaultDir() + "/srceng");

    ExtractAssets.extractAssets(context);
    String vpkPath = context.getFilesDir().getPath() + "/" + ExtractAssets.VPK_NAME;
    setenv("APP_MOD_LIB", savedGamePath, 1);
    setenv("EXTRAS_VPK_PATH", vpkPath, 1);
    setenv("LANG", Locale.getDefault().toString(), 1);
    setenv("APP_DATA_PATH", context.getApplicationInfo().dataDir, 1);
    setenv("APP_LIB_PATH", context.getApplicationInfo().nativeLibraryDir, 1);
    setenv("VALVE_GAME_PATH", savedGamePath, 1);
    
    SharedPreferences checkBoxUseVolume =
        context.getSharedPreferences("CheckboxPreferences", Context.MODE_PRIVATE);
    boolean isChecked_on = checkBoxUseVolume.getBoolean("checkbox_use_volume", false);
    if (isChecked_on) {
      steamDeck = "-gamepadui ";
      savedCmdArgs = "";
    } else {
      steamDeck = "";
    }

    String userNamed = " +name " + mPref.getString("user_name", "Unknown");
    String Fps_Count = " +fps_max " + mPref.getString("env_fps", "30");
    String gameMod = " -game " +  "csso"; // 如果没有给与值的话
    // String gameMod = " -game " + "csso";
    setArgs(gameMod + " "+ steamDeck + savedCmdArgs + Fps_Count + userNamed);
  }
}
