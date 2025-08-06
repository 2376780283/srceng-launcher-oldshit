package zzhLaAbout.countApt;

import androidx.annotation.NonNull;

// LauncherUsageItem 实现
public class LauncherUsageItem implements ListItem {

  private String useStepTitle;
  private String useLauncherStep;
  private int appIcon;

  public LauncherUsageItem(String useStepTitle, String useLauncherStep, int appIcon) {
    this.useStepTitle = useStepTitle;
    this.useLauncherStep = useLauncherStep;
    this.appIcon = appIcon;
  }

  public String getUseStepTitle() {
    return useStepTitle;
  }

  public String getUseLauncherStep() {
    return useLauncherStep;
  }

  public int getAppIcon() {
    return appIcon;
  }

  @Override
  public int getType() {
    return TYPE_LAUNCHER;
  }
}
