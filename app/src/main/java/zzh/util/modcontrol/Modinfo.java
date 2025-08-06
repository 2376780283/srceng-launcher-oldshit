package zzh.util.modcontrol;

public class Modinfo {
  private String folderName;
  private String iconPath;
  private String folderPath;
  private String addonTitle;
  private String addonauthor;
  private String addonversion;
  private String addonDescription;

  public Modinfo(
      String folderName,
      String iconPath,
      String folderPath,
      String addonTitle,
      String addonAuthor,
      String addonVersion,
      String addonDescription) {
    this.folderName = folderName;
    this.iconPath = iconPath;
    this.folderPath = folderPath;
    this.addonTitle = addonTitle;
    this.addonauthor = addonAuthor; // 可有点特殊
    this.addonversion = addonVersion; // 有点特殊
    this.addonDescription = addonDescription;
  }

  public String getAddonTitle() {
    return addonTitle;
  }

  public String getaddonauthor() {
    return addonauthor;
  }

  public String getaddonversion() {
    return addonversion;
  }

  public String getaddonDescription() {
    return addonDescription;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public String getIconPath() {
    return iconPath;
  }

  public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
  }

  public String getFolderPath() {
    return folderPath;
  }

  public void setFolderPath(String folderPath) {
    this.folderPath = folderPath;
  }

  // 判断文件夹是否已隐藏
  public boolean isHidden() {
    return folderName.startsWith(".");
  }
}
