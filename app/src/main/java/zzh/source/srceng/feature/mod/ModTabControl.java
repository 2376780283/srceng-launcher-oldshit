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


public class ModTabControl extends FragmentStateAdapter {

    public ModTabControl(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new ModlistExa();
            case 1:
                return new AboutExa();
            default:
                return new ModlistExa();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}