package zzh.source.srceng.data.model;

import androidx.annotation.NonNull;

// DeveloperItem 实现
public class DeveloperItem implements ListItem {

  private String softwareInfo;
  private String developerInfo;
  private int imageResource;

  public DeveloperItem(String softwareInfo, String developerInfo, int imageResource) {
    this.softwareInfo = softwareInfo;
    this.developerInfo = developerInfo;
    this.imageResource = imageResource;
  }

  public String getSoftwareInfo() {
    return softwareInfo;
  }

  public String getDeveloperInfo() {
    return developerInfo;
  }

  public int getImageResource() {
    return imageResource;
  }

  @Override
  public int getType() {
    return TYPE_DEVELOPER;
  }
}
