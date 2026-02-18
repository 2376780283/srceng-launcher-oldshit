package zzh.source.srceng.feature.mod;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.util.*;
import zzh.source.srceng.ui.activity.MainActivity; import zzh.source.srceng.ui.activity.HomeActivity; import zzh.source.srceng.ui.activity.WebActivity; import zzh.source.srceng.ui.activity.OptionsActivity;
import zzh.source.srceng.data.model.AboutExa;
import zzh.source.srceng.data.model.ModlistExa;
import zzh.source.srceng.ui.fragment.OptionsPage.PageEPOT;

public class ModTabControl extends FragmentStateAdapter {

    public ModTabControl(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new ModlistExa(); // 调换下位置 ↓
            case 1:
                return new PageEPOT();
            case 2:
                return new AboutExa();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        // 返回选项卡的数量
        return 3;
    }
}

/*
ViewPager 适配器 (MyPagerAdapter.kt)：

kotlin

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
