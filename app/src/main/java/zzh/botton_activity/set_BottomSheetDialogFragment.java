package zzh.botton_activity;

import zzh.source.csso.databinding.FragmentBottomSheetBinding;
import android.view.*;
import android.util.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.*;
import com.google.android.material.button.*;
import com.zzh.glbviewer.CustomViewer;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import zzh.source.csso.MainActivity;
import zzh.source.csso.R;
import android.widget.*;
import com.zzh.widget.SwitchFlyView.SwitchView;
import com.zzh.widget.Mouse.ImprovemView;
import android.animation.ObjectAnimator;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.*;
import android.graphics.drawable.*;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.File;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class set_BottomSheetDialogFragment extends BottomSheetDialogFragment {
  private static final String PREFS_NAME = "SwitchViewPrefs";
  private static final String KEY_SWITCH1_STATE = "switch1_state";
  private static final String KEY_SWITCH2_STATE = "switch2_state";
  private View improvemView;
  private SurfaceView surfaceView;
  private CustomViewer customViewer;
  private Button exitb;
  private Button backb;
  private TextView outtimeTextView; // 计算玩家时间的空间
  SharedPreferences sharedPreferences; // 不知道会不会造成很
  private ImageView imageView;
  private boolean isExpanded = false;
  private BottomSheetBehavior<View> bottomSheetBehavior;
  private FragmentBottomSheetBinding binding;

  public set_BottomSheetDialogFragment(View improvemView) {
    this.improvemView = improvemView;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences sharedPreferences =
        getActivity().getSharedPreferences("last_launch_time", Context.MODE_PRIVATE);
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentBottomSheetBinding.inflate(inflater, container, false);
    View view = binding.getRoot(); // 获取根视图
    TextView outtimeTextView = view.findViewById(R.id.outtime);
    SwitchView switchView1 = binding.hideOss;
    SwitchView switchView2 = binding.debugModeMenu;
    imageView = binding.imageRoundLevel; // 修改为实际的ImageView ID
    surfaceView = binding.modelview;
    exitb = binding.exitSdl;
    backb = binding.exitBack;
    exitb.setOnClickListener(
        v -> {
          requireActivity().finishAffinity();
        });
    backb.setOnClickListener(
        v -> {
          requireActivity().finish();
        });
    customViewer = new CustomViewer();
    ExecutorService executor = Executors.newFixedThreadPool(1);
    executor.submit(
        () -> {
          setupFirstSwitchView(switchView1);
          setupSecondSwitchView(switchView2);
          outtimeTextView.setText(" " + MainActivity.lastLaunchTime);
        });
    view.post(
        () -> {
          View bottomSheet = (View) view.getParent();
          if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.addBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {
                  @Override
                  public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    // 状态变化的回调处理
                    if (newState == BottomSheetBehavior.STATE_EXPANDED && !isExpanded) {
                      rotateImage(180f);
                      isExpanded = true;
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED && isExpanded) {
                      rotateImage(0f);
                      isExpanded = false;
                    }
                  }

                  @Override
                  public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // 滑动时的UI变化处理
                  }
                });
          }
        });
    if (surfaceView != null) {
      initializeCustomViewer();
    }
    return view;
  }

  private void setupFirstSwitchView(SwitchView switchView) {
    SharedPreferences preferences =
        getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    boolean isSwitchOn = preferences.getBoolean(KEY_SWITCH1_STATE, false);
    switchView.toggleSwitch(isSwitchOn);
    improvemView.setVisibility(isSwitchOn ? View.VISIBLE : View.GONE);
    switchView.setOnStateChangedListener(
        new SwitchView.OnStateChangedListener() {
          @Override
          public void toggleToOn(SwitchView view) {
            improvemView.setVisibility(View.VISIBLE);
            view.toggleSwitch(true);
            saveSwitchState(KEY_SWITCH1_STATE, true); // 保存状态为开启
          }

          @Override
          public void toggleToOff(SwitchView view) {
            improvemView.setVisibility(View.GONE);
            view.toggleSwitch(false);
            saveSwitchState(KEY_SWITCH1_STATE, false); // 保存状态为关闭
          }
        });
  }

  private void setupSecondSwitchView(SwitchView switchView) {
    SharedPreferences preferences =
        getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    boolean isSwitchOn = preferences.getBoolean(KEY_SWITCH2_STATE, false);
    switchView.toggleSwitch(isSwitchOn);
    switchView.setOnStateChangedListener(
        new SwitchView.OnStateChangedListener() {
          @Override
          public void toggleToOn(SwitchView view) {
            view.toggleSwitch(true);
            saveSwitchState(KEY_SWITCH2_STATE, true); // 保存状态为开启
          }

          @Override
          public void toggleToOff(SwitchView view) {
            view.toggleSwitch(false);
            saveSwitchState(KEY_SWITCH2_STATE, false); // 保存状态为关闭
          }
        });
  }

  private void saveSwitchState(String key, boolean isOn) {
    SharedPreferences preferences =
        getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(key, isOn);
    editor.apply();
  }

  private void initializeCustomViewer() {
    try {
      customViewer.loadEntity();
      customViewer.setSurfaceView(surfaceView);
      customViewer.loadGlb(requireContext(), "grogu", "medal__cs2_counter-terrorist_coin");
      // customViewer.loadGltf(requireContext(), "miyu", "scene");
      customViewer.loadIndirectLight(requireContext(), "default_env");
      // customViewer.loadEnviroment(requireContext(), "default_env");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (customViewer != null) {
      customViewer.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (customViewer != null) {
      customViewer.onPause();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (customViewer != null) {
      customViewer.onDestroy();
    }
  }

  private void rotateImage(float degree) {
    ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", degree);
    animator.setDuration(300); // 设置动画时间为300毫秒
    animator.start();
  }
}
