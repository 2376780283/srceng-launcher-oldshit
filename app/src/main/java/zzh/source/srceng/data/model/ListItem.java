package zzh.source.srceng.data.model;

import androidx.annotation.NonNull;

public interface ListItem {
  int TYPE_DEVELOPER = 0;
  int TYPE_LAUNCHER = 1;

  int getType();
}
