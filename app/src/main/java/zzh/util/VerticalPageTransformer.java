package zzh.util;

import android.view.View;
import androidx.viewpager2.widget.ViewPager2;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import zzh.HomePageui.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VerticalPageTransformer implements ViewPager2.PageTransformer {

  @Override
  public void transformPage(@NonNull View page, float position) {
    page.setTranslationX(-page.getWidth() * position);
    page.setTranslationY(page.getHeight() * position);
      
  
  /*
  @Override
  public void transformPage(View page, float position) {
    // 透明度：中心页面时 position = 0，透明度为1；离开中心，透明度减小
    page.setAlpha(1 - Math.abs(position));

    // 可选：缩放效果
    float scaleFactor = 0.85f + (1 - Math.abs(position)) * 0.15f;
    page.setScaleX(scaleFactor);
    page.setScaleY(scaleFactor);
        page.setTranslationX(0);
        page.setTranslationY(0);
  }*/
}}
