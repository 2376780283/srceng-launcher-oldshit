package zzh.source.srceng.data.model;

import zzh.source.srceng.databinding.ModlistExaBinding;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import zzh.source.srceng.ui.activity.MainActivity;
import zzh.source.srceng.R;
import android.util.Log;
import android.Manifest.*;
import android.view.animation.OvershootInterpolator;
import androidx.cardview.widget.CardView;
import android.animation.*;
import com.google.android.material.*;
import zzh.source.srceng.feature.mod.ModAdapter;
import zzh.source.srceng.ui.widget.blur.BlurAlgorithm;
import zzh.source.srceng.ui.widget.blur.BlurView;
import zzh.source.srceng.ui.widget.blur.RenderEffectBlur;
import zzh.source.srceng.ui.widget.blur.RenderScriptBlur;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import android.content.pm.PackageManager;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import java.security.MessageDigest;
import android.util.Base64;
import android.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import java.util.Arrays;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.util.*;
import android.os.*;

import java.util.concurrent.ExecutorService;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executors;
import com.valvesoftware.ValveActivity2;
import org.libsdl.app.SDLActivity;
import com.google.android.material.button.MaterialButton;
import android.animation.*;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import zzh.source.srceng.feature.mod.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import zzh.source.srceng.feature.motd.MotdAdapter;
import zzh.source.srceng.feature.motd.motdofitem;

public class ModlistExa extends Fragment {
  private RecyclerView modslist;
  private SharedPreferences mPref;
  private static final int REQUEST_PERMISSION_CODE = 100;

  public static String getDefaultDir() {
    File dir = Environment.getExternalStorageDirectory();
    return (dir != null && dir.exists()) ? dir.getPath() : "/sdcard/";
  }

  private ExecutorService executorService = Executors.newSingleThreadExecutor();
  private MotdAdapter adapter;
  private ModlistExaBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // 使用 ViewBinding 初始化
    binding = ModlistExaBinding.inflate(inflater, container, false);
    View view = binding.getRoot(); // 获取根视图
    mPref = getActivity().getSharedPreferences("mod", 0);
    modslist = binding.modlistViewl;
    checkmotdandpis();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    checkmotdandpis();
  }

  private void checkmotdandpis() {
    if (ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(
          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    } else {
      initzelmodlistview();
    }
  }

  private void initzelmodlistview() {
    String modPath = mPref.getString("gamepath", getDefaultDir() + "/srceng") + "/csso/custom/";
    File file = new File(modPath);

    // 检查文件夹是否存在且有内容
    if (file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0) {
      List<Modinfo> modFolders = getModFolders(modPath);
      setupGridLayoutManager(2); // 设置每行项目数
      loadModFoldersAsync(modFolders);
    } else {
      setupMotd();
    }
  }

  // 使用 GridLayoutManager 布局
  private void setupGridLayoutManager(int numberOfColumns) {
    modslist.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
  }

  // 使用 LinearLayoutManager 布局
  private void setupLinearLayoutManager() {
    modslist.setLayoutManager(new LinearLayoutManager(getActivity()));
  }

  // 异步加载 Mod 文件夹列表
  private void loadModFoldersAsync(List<Modinfo> modFolders) {
    executorService.execute(
        () -> {
          ModAdapter adapter = new ModAdapter(requireContext(), modFolders);
          new Handler(Looper.getMainLooper()).post(() -> modslist.setAdapter(adapter)); // 切换回主线程
        });
  }

  // 设置默认消息布局
  public void setupMotd() {
    setupLinearLayoutManager();
    adapter = new MotdAdapter(initializeTileList());
    modslist.setAdapter(adapter);
  }

  // 初始化空的消息列表
  private List<motdofitem> initializeTileList() {
    List<motdofitem> tileList = new ArrayList<>();
    tileList.add(new motdofitem(R.drawable.ic_s_empty, "模组为空", "没有模组"));
    return tileList;
  }

  private List<Modinfo> getModFolders(String path) {
    List<Modinfo> modList = new ArrayList<>();
    File directory = new File(path);
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            String iconPath = new File(file, "addonicon.png").getAbsolutePath();
            File addonInfoFile = new File(file, "addoninfo.json");
            String addonTitle = "";
            String addonauthor = "";
            String addonversion = "";
            String addonDescription = "";

            // 检查是否存在 addoninfo.json 并解析 addontitle, addonauthor, addonversion
            if (addonInfoFile.exists()) {
              addonTitle = getAddonInfo(addonInfoFile, "addontitle");
              addonauthor = getAddonInfo(addonInfoFile, "addonauthor");
              addonversion = getAddonInfo(addonInfoFile, "addonversion");
              addonDescription = getAddonInfo(addonInfoFile, "addonDescription");
            }

            // 检查是否存在 hl_icon.png
            if (new File(iconPath).exists()) {
              modList.add(
                  new Modinfo(
                      file.getName(),
                      iconPath,
                      file.getAbsolutePath(),
                      addonTitle,
                      addonauthor,
                      addonversion,
                      addonDescription));
            } else {
              // 如果没有图标，设置默认图标路径或处理逻辑
              modList.add(
                  new Modinfo(
                      file.getName(),
                      null, // 不建议iconPath
                      file.getAbsolutePath(),
                      addonTitle,
                      addonauthor,
                      addonversion,
                      addonDescription));
            }
          }
        }
      }
    }
    return modList;
  }

  private String getAddonInfo(File addonInfoFile, String key) {
    String result = "";
    try (BufferedReader reader = new BufferedReader(new FileReader(addonInfoFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(key)) {
          // 假设格式是 key "..."，使用正则提取内容
          result = line.split("\"")[1];
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public String getAddonTitle(File addonInfoFile) {
    return getAddonInfo(addonInfoFile, "addontitle");
  }

  public String getAddonVersion(File addonInfoFile) {
    return getAddonInfo(addonInfoFile, "addonversion");
  }

  public String getAddonAuthor(File addonInfoFile) {
    return getAddonInfo(addonInfoFile, "addonauthor");
  }

  public String getaddonDescription(File addonInfoFile) {
    return getAddonInfo(addonInfoFile, "addonDescription");
  }
}
