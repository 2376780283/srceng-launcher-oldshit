package zzh.util.modcontrol;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.filament.utils.Utils;
import zzh.source.csso.R;
import java.util.List;
import android.content.Context;
// 补
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.File;
import android.widget.CheckBox;
import android.widget.Button;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.webkit.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

public class ModAdapter extends RecyclerView.Adapter<ModAdapter.MyViewHolder> {
  private final List<Modinfo> modList;
  private Context context;
  public ExecutorService executor;

  public ModAdapter(Context context, List<Modinfo> modList) {
    this.modList = modList;
    this.context = context;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mods_view, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    Modinfo modInfo = modList.get(position);
    holder.textView.setText(modInfo.getFolderName());
    holder.addonTitleView.setText(modInfo.getAddonTitle());
    Glide.with(context)
        .load(modInfo.isHidden() ? R.drawable.ic_home_vega : modInfo.getIconPath())
        .error(R.drawable.ic_image_none)
        .into(holder.imageView);
    executor = Executors.newFixedThreadPool(1);

    holder.itemView.setOnClickListener(v -> showModInfoDialog(context, modInfo));
    holder.itemView.setOnLongClickListener(
        v -> {
          showHideDialog(v.getContext(), modInfo, position);
          return true;
        });
  }

  private void showModInfoDialog(Context context, Modinfo modInfo) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View dialogView = inflater.inflate(R.layout.dialog_modinfo, null);
    TextView titleView = dialogView.findViewById(R.id.dialog_modinfo_title);
    ImageView iconsaView = dialogView.findViewById(R.id.c_imageView);
    TextView descriptionView = dialogView.findViewById(R.id.dialog_modinfo_description);
    TextView descriptionView_dep = dialogView.findViewById(R.id.dialog_modinfo_description_dep);
    Button confirmButton = dialogView.findViewById(R.id.dialog_modinfo_confirm);
    TextView usrView = dialogView.findViewById(R.id.dialog_modinfo_description_uer);
    TextView verisonView = dialogView.findViewById(R.id.dialog_modinfo_description_ver);
    executor.submit(
        () -> {
          if (modInfo.getaddonDescription().isEmpty()) {
            descriptionView_dep.setText(
                context.getString(R.string.mod_info_info)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            descriptionView_dep.setText(
                context.getString(R.string.mod_info_info) + modInfo.getaddonDescription());
          }
          // 模组作者
          if (modInfo.getaddonauthor().isEmpty()) {
            usrView.setText(
                context.getString(R.string.mod_info_usr)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            usrView.setText(context.getString(R.string.mod_info_usr) + modInfo.getaddonauthor());
          }
          if (modInfo.getaddonversion().isEmpty()) {
            verisonView.setText(
                context.getString(R.string.mod_info_ver)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            verisonView.setText(
                context.getString(R.string.mod_info_ver) + modInfo.getaddonversion());
          }
          titleView.setText(modInfo.getFolderName()); // 可以设置为模组名称
          descriptionView.setText(modInfo.getAddonTitle()); // 显示从 addoninfo.json 中读取的模组介绍
          // 模组图标
          if (modInfo.isHidden()) {
            iconsaView.setImageResource(R.drawable.ic_home_vega); // 隐藏模组的图标
          } else {
            if (modInfo.getIconPath() != null) {
              Bitmap bitmap = BitmapFactory.decodeFile(modInfo.getIconPath());
              iconsaView.setImageBitmap(bitmap);
            } else {
              iconsaView.setImageResource(R.drawable.ic_image_none);
            }
          }
        });
    AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
    if (dialog.getWindow() != null) {
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    confirmButton.setOnClickListener(v -> dialog.dismiss());
    dialog.setCancelable(false); // 禁止用户取消对话框
    dialog.show();
  }

  private void showHideDialog(Context context, Modinfo modInfo, int position) {
    boolean isHidden = modInfo.getFolderName().startsWith(".");
    LayoutInflater inflater = LayoutInflater.from(context);
    View dialogView = inflater.inflate(R.layout.dialog_setmodshu_view, null);
    String nohide =
        context.getString(isHidden ? R.string.dialog_setnohide : R.string.dialog_setunhide);
    TextView messageView = dialogView.findViewById(R.id.dialog_message);
    Button buttonConfirm = dialogView.findViewById(R.id.button_confirm);
    Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
    TextView titleView = dialogView.findViewById(R.id.dialog_modinfo_title);
    ImageView iconsaView = dialogView.findViewById(R.id.c_imageView);
    TextView descriptionView = dialogView.findViewById(R.id.dialog_modinfo_description);
    TextView descriptionView_dep = dialogView.findViewById(R.id.dialog_modinfo_description_dep);
    TextView usrView = dialogView.findViewById(R.id.dialog_modinfo_description_uer);
    TextView verisonView = dialogView.findViewById(R.id.dialog_modinfo_description_ver);
    executor.submit(
        () -> {
          if (modInfo.getaddonDescription().isEmpty()) {
            descriptionView_dep.setText(
                context.getString(R.string.mod_info_info)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            descriptionView_dep.setText(
                context.getString(R.string.mod_info_info) + modInfo.getaddonDescription());
          }
          // 模组作者
          if (modInfo.getaddonauthor().isEmpty()) {
            usrView.setText(
                context.getString(R.string.mod_info_usr)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            usrView.setText(context.getString(R.string.mod_info_usr) + modInfo.getaddonauthor());
          }
          if (modInfo.getaddonversion().isEmpty()) {
            verisonView.setText(
                context.getString(R.string.mod_info_ver)
                    + (context.getString(R.string.mod_info_null)));
          } else {
            verisonView.setText(
                context.getString(R.string.mod_info_ver) + modInfo.getaddonversion());
          }
          titleView.setText(modInfo.getFolderName()); // 可以设置为模组名称
          descriptionView.setText(modInfo.getAddonTitle()); // 显示从 addoninfo.json 中读取的模组介绍
          // 模组图标
          if (modInfo.isHidden()) {
            iconsaView.setImageResource(R.drawable.ic_home_vega); // 隐藏模组的图标
          } else {
            if (modInfo.getIconPath() != null) {
              Bitmap bitmap = BitmapFactory.decodeFile(modInfo.getIconPath());
              iconsaView.setImageBitmap(bitmap);
            } else {
              iconsaView.setImageResource(R.drawable.ic_image_none);
            }
          }
        });
    messageView.setText(nohide);
    AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
    if (dialog.getWindow() != null) {
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    buttonConfirm.setOnClickListener(
        v -> {
          if (modInfo.isHidden()) {
            unhideModFolder(modInfo, position);
          } else {
            hideModFolder(modInfo, position);
          }
          dialog.dismiss();
        });
    buttonCancel.setOnClickListener(
        v -> {
          dialog.dismiss();
        });
    dialog.setCancelable(false);
    dialog.show();
  }

  private void hideModFolder(Modinfo modInfo, int position) {
    File folder = new File(modInfo.getFolderPath());
    if (folder.exists() && !folder.getName().startsWith(".")) {
      String parentDir = folder.getParent();
      File newFolder = new File(parentDir, "." + folder.getName());
      boolean success = folder.renameTo(newFolder);
      if (success) {
        modList.get(position).setFolderPath(newFolder.getAbsolutePath());
        modList.get(position).setFolderName(newFolder.getName());
        notifyItemChanged(position);
      } else {
      }
    }
  }

  private void unhideModFolder(Modinfo modInfo, int position) {
    File folder = new File(modInfo.getFolderPath());
    if (folder.exists() && folder.getName().startsWith(".")) {
      String parentDir = folder.getParent();
      File newFolder = new File(parentDir, folder.getName().substring(1)); // 去掉“.”
      boolean success = folder.renameTo(newFolder);
      if (success) {
        modInfo.setFolderPath(newFolder.getAbsolutePath());
        modInfo.setFolderName(newFolder.getName()); // 更新图标路径
        File iconFile = new File(newFolder, "addonicon.png");
        if (iconFile.exists()) {
          modInfo.setIconPath(iconFile.getAbsolutePath());
        } else {
          modInfo.setIconPath(null); // 没有图标时设为 null
        }
        notifyItemChanged(position);
      }
    }
  }

  @Override
  public int getItemCount() {
    return modList.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {
    public final TextView textView;
    public final TextView addonTitleView;
    public final ImageView imageView;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      textView = itemView.findViewById(R.id.textView);
      addonTitleView = itemView.findViewById(R.id.tiledview);
      imageView = itemView.findViewById(R.id.imageView);
    }
  }
}
