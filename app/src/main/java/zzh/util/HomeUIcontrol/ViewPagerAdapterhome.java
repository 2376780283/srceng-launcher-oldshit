package zzh.util.HomeUIcontrol;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import zzh.HomePageui.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.*;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import zzh.HomePageui.OptionsPage.PageEPOT;

public class ViewPagerAdapterhome extends FragmentStateAdapter {
    public ViewPagerAdapterhome(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StartGameFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
