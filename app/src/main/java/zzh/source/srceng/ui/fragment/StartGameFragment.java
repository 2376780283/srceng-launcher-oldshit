package zzh.source.srceng.ui.fragment;

import zzh.source.srceng.databinding.HomePageuiABinding;
import zzh.source.srceng.R;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import zzh.source.srceng.ui.widget.blur.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.List;
// 报错后 补包
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import zzh.source.srceng.ui.adapter.VerticalPageTransformer;
import android.graphics.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import zzh.source.srceng.feature.mod.*;
import zzh.source.srceng.ui.activity.MainActivity;
import com.valvesoftware.ValveActivity2;
import org.libsdl.app.SDLActivity;
import android.content.res.ColorStateList;
import zzh.source.srceng.feature.tile.TileAdapterTab;
import zzh.source.srceng.feature.tile.Tile;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import android.graphics.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import android.os.Handler;

public class StartGameFragment extends Fragment {
  private RecyclerView modslist;
  private TabLayout zzhexa;
  private ViewPager2 zzhexap;
  //
  private RecyclerView recyclerView;
  // private RecyclerView.LayoutManager layoutManager;
  private TileAdapterTab adapter;
  private List<Tile> tileList; // 确保在类中声明
  //
  private Runnable scrollRunnable;
  private Handler handler;
  private int currentPosition = 0;
  private boolean isAutoScrolling = false;
  private HomePageuiABinding binding;
  private LinearLayoutManager layoutManager;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // 使用 ViewBinding 初始化
    binding = HomePageuiABinding.inflate(inflater, container, false);
    View view = binding.getRoot(); // 获取根视图
    // zzhexa = binding.zzhexa;
    zzhexap = binding.viewPagerExa;
    recyclerView = binding.recyclerView;
    // setupnvgs();
    setupRecyclerView(view);
    return view;
  }
  /*
  private void setupnvgs() {
    ColorStateList colorStateList =
        new ColorStateList(
            new int[][] {
              new int[] {android.R.attr.state_selected}, // 选中状态
              new int[] {-android.R.attr.state_selected} // 未选中状态
            },
            new int[] {
              getResources().getColor(R.color.bilibili_pink), // 选中状态颜色
              getResources().getColor(R.color.apple_switch_track_color) // 未选中状态颜色
            });
    zzhexa.setTabIconTint(colorStateList);
    new TabLayoutMediator(
            zzhexa,
            zzhexap,
            (tab, position) -> {
              int[] tabIcons = {
                R.string.launcher_mod_ep1, R.string.launcher_mod_ep2, R.string.srceng_launcher_about
              };
              tab.setText(tabIcons[position]);
            })
        .attach();
  }*/

  private void setupRecyclerView(View view) {
    zzhexap.setAdapter(new ModTabControl(getActivity()));
    zzhexap.setOffscreenPageLimit(1);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    ExecutorService executor = Executors.newFixedThreadPool(1);
    List<Tile> tileList = initializeTileList();
    executor.submit(
        () -> {
          adapter = new TileAdapterTab(tileList);
          recyclerView.post(() -> recyclerView.setAdapter(adapter)); // UI thread update 是的
        });

    new LinearSnapHelper().attachToRecyclerView(recyclerView);
    zzhexap.setUserInputEnabled(false);
    zzhexap.setPageTransformer(new VerticalPageTransformer());
    recyclerView.addOnScrollListener(
        new RecyclerView.OnScrollListener() {
          @Override
          public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int position = layoutManager.findFirstVisibleItemPosition();
            View firstVisibleView = layoutManager.findViewByPosition(position);
            if (firstVisibleView != null) {
              int itemHeight = firstVisibleView.getHeight();
              float offset = (float) -firstVisibleView.getTop() / itemHeight;
              // 更新 ViewPager2 的位置并启用平滑动画
              zzhexap.setCurrentItem(position + Math.round(offset), true);
            }
          }
        });
  }

  private List<Tile> initializeTileList() {
    List<Tile> tileList = new ArrayList<>();
    tileList.add(new Tile(R.drawable.background, "CSSO(custom)", "Addons管理"));
    tileList.add(new Tile(R.drawable.background, "环境变量设置", "启动参数管理"));
    tileList.add(new Tile(R.drawable.zzhlife, "ZZHlife", "现代风格 启动器(*≧ω≦)ﾉ"));
    return tileList;
  }
}
