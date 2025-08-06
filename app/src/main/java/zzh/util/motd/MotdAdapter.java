package zzh.util.motd;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import zzh.util.HomeUIcontrol.ViewPagerAdapterhome;
import zzh.util.HomeUIcontrol.*;
import android.view.MenuItem;
import zzh.util.VerticalPageTransformer;
import android.animation.*;
import android.view.animation.OvershootInterpolator;
import com.zzh.widget.blurview.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import java.io.*;
import java.util.*;
import zzh.util.motd.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import zzh.source.csso.R;

public class MotdAdapter extends RecyclerView.Adapter<MotdAdapter.TileViewHolder> {
  private List<motdofitem> tileList;
  private View.OnClickListener onClickListener;

  public MotdAdapter(List<motdofitem> tileList) {
    this.tileList = tileList;
  }

  public void setOnClickListener(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  public motdofitem getTileAt(int position) {
    return tileList.get(position);
  }

  public List<motdofitem> getTileList() {
    return tileList;
  }

  @NonNull
  @Override
  public TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_motd_of, parent, false);
    return new TileViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TileViewHolder holder, int position) {
    motdofitem tile = tileList.get(position);
    holder.bind(tile);
    holder.itemView.setOnClickListener(
        v -> {
          if (onClickListener != null) {
            onClickListener.onClick(v);
          }
          // 添加弹跳效果
           bounceAnimation(holder.itemView);
        });
  }

  @Override
  public int getItemCount() {
    return tileList.size();
  }

  public static class TileViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView primaryTextView;
    private TextView secondaryTextView;

    public TileViewHolder(@NonNull View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.tile_image);
      primaryTextView = itemView.findViewById(R.id.tile_text);
      secondaryTextView = itemView.findViewById(R.id.tile_text_s);
    }

    public void bind(motdofitem tile) {
      imageView.setImageResource(tile.getImageResId());
      primaryTextView.setText(tile.getPrimaryText());
      secondaryTextView.setText(tile.getSecondaryText());
    }
  }

  private void bounceAnimation(View view) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.2f, 1.0f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.2f, 1.0f);
    scaleX.setDuration(700);
    scaleY.setDuration(700);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    animatorSet.setInterpolator(new OvershootInterpolator());
    animatorSet.start();
  }
}
