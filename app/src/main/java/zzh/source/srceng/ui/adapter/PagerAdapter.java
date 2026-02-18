package zzh.source.srceng.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.util.*;
import zzh.source.srceng.ui.activity.MainActivity; import zzh.source.srceng.ui.activity.HomeActivity; import zzh.source.srceng.ui.activity.WebActivity; import zzh.source.srceng.ui.activity.OptionsActivity;
import androidx.viewpager2.widget.ViewPager2;

public class PagerAdapter extends FragmentStateAdapter {
    private ViewPager2 viewPager;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, ViewPager2 viewPager) {
        super(fragmentActivity);
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WebActivity(); // 调换下位置 ↓
            case 1:
                return new HomeActivity();
            case 2:
                return new OptionsActivity();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

/*
ViewPager 适配器 (MyPagerAdapter.kt)：

kotlin
复制代码
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        // 返回相应的 Fragment
        return when (position) {
            0 -> Tab1Fragment()
            1 -> Tab2Fragment()
            2 -> Tab3Fragment()
            else -> Tab1Fragment()
        }
    }

    override fun getItemCount(): Int {
        // 返回选项卡的数量
        return 3
    }
}
*/
