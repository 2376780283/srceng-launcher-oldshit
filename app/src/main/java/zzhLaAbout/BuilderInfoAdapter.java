package zzhLaAbout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import zzh.source.csso.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import zzhLaAbout.countApt.*;
import zzhLaAbout.countApt.ListItem;
import java.util.List;
import android.view.MotionEvent;
public class BuilderInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<ListItem> data;
  private View.OnClickListener developerClickListener;
  private View.OnClickListener launcherClickListener;
  public BuilderInfoAdapter(
      List<ListItem> data,
      View.OnClickListener developerClickListener,
      View.OnClickListener launcherClickListener) {
    this.data = data;
    this.developerClickListener = developerClickListener;
    this.launcherClickListener = launcherClickListener;
  }
  @Override
  public int getItemViewType(int position) {
    return data.get(position).getType(); // 获取当前项的类型
  }
  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == ListItem.TYPE_DEVELOPER) {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_view, parent, false);
      return new DeveloperViewHolder(view);
    } else if (viewType == ListItem.TYPE_LAUNCHER) {
      View view =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_launcher_info_view, parent, false);
      return new LauncherViewHolder(view);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    int viewType = getItemViewType(position);

    if (viewType == ListItem.TYPE_DEVELOPER) {
      DeveloperItem developerItem = (DeveloperItem) data.get(position);
      DeveloperViewHolder developerHolder = (DeveloperViewHolder) holder;
      developerHolder.softwareInfo.setText(developerItem.getSoftwareInfo());
      developerHolder.developerInfo.setText(developerItem.getDeveloperInfo());
      developerHolder.imageView.setImageResource(developerItem.getImageResource());
    } else if (viewType == ListItem.TYPE_LAUNCHER) {
      LauncherUsageItem launcherItem = (LauncherUsageItem) data.get(position);
      LauncherViewHolder launcherHolder = (LauncherViewHolder) holder;
      launcherHolder.useStepTitle.setText(launcherItem.getUseStepTitle());
      launcherHolder.useLauncherStep.setText(launcherItem.getUseLauncherStep());
      launcherHolder.appIcon.setImageResource(launcherItem.getAppIcon());
            launcherHolder.useLauncherStep.setLinksClickable(true);
		launcherHolder.useLauncherStep.setTextIsSelectable(true);
    }
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  // DeveloperViewHolder for DeveloperItem
  public static class DeveloperViewHolder extends RecyclerView.ViewHolder {
    public TextView softwareInfo;
    public TextView developerInfo;
    public ImageView imageView;

    public DeveloperViewHolder(View view) {
      super(view);
      softwareInfo = view.findViewById(R.id.software_info);
      developerInfo = view.findViewById(R.id.developer_info);
      imageView = view.findViewById(R.id.imageView);
      // 设置触摸监听器或点击监听器
      view.setOnTouchListener(
          new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 触摸事件处理
                // 可以在这里做一些特定的触摸处理逻辑
                return true; // 表示触摸事件已处理
              }
              return false;
            }
          });
      view.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // 开发者 item 点击处理逻辑
            }
          });
    }
  }
  public static class LauncherViewHolder extends RecyclerView.ViewHolder {
    public TextView useStepTitle;
    public TextView useLauncherStep;
    public ImageView appIcon;

    public LauncherViewHolder(View view) {
      super(view);
      useStepTitle = view.findViewById(R.id.use_step_t);
      useLauncherStep = view.findViewById(R.id.use_launcehr_step);
      appIcon = view.findViewById(R.id.appicon);
      // 设置触摸监听器或点击监听器
      view.setOnTouchListener(
          new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 启动器使用方法 item 触摸处理逻辑
                return true;
              }
              return false;
            }
          });
      // 设置点击监听器
      view.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // 启动器 item 点击处理逻辑 
            }
          });
    }
  }
}
