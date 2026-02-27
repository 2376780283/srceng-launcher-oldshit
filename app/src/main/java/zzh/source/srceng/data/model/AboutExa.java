package zzh.source.srceng.data.model;
import zzh.source.srceng.databinding.AboutExaBinding;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.service.autofill.Dataset;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import zzh.source.srceng.data.model.BuilderInfoAdapter;
import zzh.source.srceng.data.model.ListItem;
import zzh.source.srceng.data.model.LauncherUsageItem;
import zzh.source.srceng.data.model.DeveloperItem;
import zzh.source.srceng.util.json.JsonUtils;

public class AboutExa extends Fragment {
  private RecyclerView recyclerView;
  private SharedPreferences mPref;
  private BuilderInfoAdapter adapter;
  private List<ListItem> itemList = new ArrayList<>();
  public ExecutorService executorService;
  private AboutExaBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // 使用 ViewBinding 初始化
    binding = AboutExaBinding.inflate(inflater, container, false);
    View view = binding.getRoot(); // 获取根视图
    recyclerView = binding.builderView;
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    executorService = Executors.newFixedThreadPool(2);
    executorService.execute(
        () -> {
          adapter = new BuilderInfoAdapter(itemList, this::onDeveloperClick, this::onLauncherClick);
          recyclerView.setAdapter(adapter); // 设置空的适配器
          mPref = getActivity().getSharedPreferences("mod", 0);
        });
    loadAddonInfoAsync(); // 异步加载数据
    return view;
  }

  private void loadAddonInfoAsync() {
    executorService.execute(
        () -> {
          String addonInfoContent =
              JsonUtils.loadJSONFromAsset(getActivity(), "custom/mod/addoninfo.json");
          List<ListItem> items = loadItems(addonInfoContent);

          // 检查 Fragment 是否仍然附着在 Activity 上，以避免空指针异常
          if (getActivity() != null && isAdded()) {
            getActivity()
                .runOnUiThread(
                    () -> {
                      itemList.clear();
                      itemList.addAll(items); // 更新数据
                      adapter.notifyDataSetChanged(); // 通知适配器数据已更改
                    });
          }
        });
  }

    private List<ListItem> loadItems(String addonInfoContent) {
        List<ListItem> items = new ArrayList<>();
        // 添加开发者信息项
        items.add(new DeveloperItem("ZZH LIFE", "启动器开发者", R.drawable.zzhlife));
        items.add(new DeveloperItem("Carl-Y HH45137", "为启动器作出贡献", R.drawable.builder_one));
        items.add(new DeveloperItem("Nillerusr", "提供启动器代码", R.drawable.builder_two));
        items.add(new DeveloperItem("stephen-cusi", "为启动器作出贡献", R.drawable.builder_three));
        items.add(
                new LauncherUsageItem(
                        "关于illerusr",
                        "\n特別感謝：\nillerusr 製作的 port 程式\nvalve 製作的起源引擎\n\n不得用於商業用途！\n\n給作者打賞點小錢：https://new.donatepay.ru/@nillerusr\nBTC(Bitcoin): bc1qnjq92jj9uqjtafcx2zvnwd48q89hgtd6w8a6na\nXMR(Monero): 48iXvX61MU24m5VGc77rXQYKmoww3dZh6hn7mEwDaLVTfGhyBKq2teoPpeBq6xvqj4itsGh6EzNTzBty6ZDDevApCFNpsJg",
                        R.drawable.card_view_tabbg));

        items.add(new LauncherUsageItem("关于启动器", "由ZZH 制作 并修改 没什么好说的 \n lib版本：1.17.26", R.drawable.zzhlife));

        return items;
    }
  private void onDeveloperClick(View v) {
    // 处理开发者 item 点击事件的逻辑
  }

  // 点击启动器使用方法项时的处理逻辑
  private void onLauncherClick(View v) {

  }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
