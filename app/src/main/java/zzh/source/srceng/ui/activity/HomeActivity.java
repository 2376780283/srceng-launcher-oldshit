package zzh.source.srceng.ui.activity;

import zzh.source.srceng.databinding.HomeActivityBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import zzh.source.srceng.R;
import android.util.*;
import com.google.android.material.navigation.NavigationView;
import zzh.source.srceng.ui.fragment.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import zzh.source.srceng.ui.adapter.*;
import android.view.MenuItem;
import zzh.source.srceng.ui.adapter.VerticalPageTransformer;
import com.google.android.material.*;
import zzh.source.srceng.ui.widget.blur.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import zzh.source.srceng.ui.widget.tablayout.VerticalTabLayout;
import zzh.source.srceng.ui.widget.tablayout.VerticalTabLayout;
import zzh.source.srceng.ui.widget.tablayout.VerticalTabLayoutMediator;
import androidx.core.content.ContextCompat;
import java.util.Map;
import java.util.HashMap;
import android.content.res.ColorStateList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;

public class HomeActivity extends Fragment {

    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private VerticalTabLayout tabsa;
    private HomeActivityBinding binding;

    // Color cache 
    private int colorUnselected;
    private int colorSelected;
    private ColorStateList colorStateList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = HomeActivityBinding.inflate(inflater, container, false);
        initViews(); // Initialize view components
        return binding.getRoot();
    }

    private void initViews() {
        viewPager = binding.viewPager;
        tabsa = binding.tabsa;
        initColors();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager();
        setupNavigationView();
    }

    private void initColors() {
        
        colorUnselected = ContextCompat.getColor(requireContext(), R.color.apple_switch_track_color);
        colorSelected = ContextCompat.getColor(requireContext(), R.color.colorPrimary);
        colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{-android.R.attr.state_selected}
                },
                new int[]{
                        Color.WHITE,
                        Color.GRAY
                });
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setUserInputEnabled(false);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer(new VerticalPageTransformer());
    }

    private void setupNavigationView() {
        // Setup colors and icons for tab layout
        tabsa.setTabTextColors(colorUnselected, colorSelected);
        tabsa.setTabIconTint(colorStateList);

        new VerticalTabLayoutMediator(
                tabsa,
                viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setIcon(R.drawable.ic_home_vegb);
                            tab.setText(R.string.tab_source_home);
                            break;
                        case 1:
                            tab.setIcon(R.drawable.ic_home_vegc);
                            tab.setText(R.string.tab_source_video);
                            break;
                    }
                }
        ).attach();
    }
}
