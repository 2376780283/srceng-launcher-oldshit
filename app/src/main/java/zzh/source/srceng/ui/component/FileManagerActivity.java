package zzh.source.srceng.ui.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import android.content.pm.PackageManager;
import zzh.source.srceng.R;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import java.security.MessageDigest;
import android.util.Base64;
import android.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zzh.source.srceng.ui.activity.MainActivity;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import java.util.Arrays;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import zzh.source.srceng.ui.activity.OptionsActivity;

import zzh.source.srceng.ui.widget.blur.BlurAlgorithm;
import zzh.source.srceng.ui.widget.blur.BlurView;
import zzh.source.srceng.ui.widget.blur.RenderEffectBlur;
import zzh.source.srceng.ui.widget.blur.RenderScriptBlur;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import java.io.*;
import java.util.*;
import android.view.WindowManager;
import android.animation.*;
import com.google.android.material.*;
import androidx.cardview.*;
import android.view.animation.OvershootInterpolator;
import androidx.cardview.widget.CardView;

public class FileManagerActivity extends Activity implements OnTouchListener {
    public static final int sdk = Integer.valueOf(Build.VERSION.SDK).intValue();
    public static String cur_dir;
    static LinearLayout body;
    public SharedPreferences mPref;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            TextView btn = (TextView) v.findViewById(R.id.dirname);
            if (cur_dir == null) ListDirectory("" + btn.getText());
            else ListDirectory(cur_dir + "/" + btn.getText());
        }
        return false;
    }

    public void ListDirectory(String path) {
        TextView header = (TextView) findViewById(R.id.header_txt);
        File myDirectory = new File(path);

        File[] directories =
                myDirectory.listFiles(
                        new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.isDirectory();
                            }
                        });

        if (directories != null && directories.length > 1) {
            Arrays.sort(
                    directories,
                    new Comparator<File>() {
                        @Override
                        public int compare(File object1, File object2) {
                            return object1.getName()
                                    .toUpperCase()
                                    .compareTo(object2.getName().toUpperCase());
                        }
                    });
        }

        LayoutInflater ltInflater = getLayoutInflater();
        if (directories == null) return;

        try {
            cur_dir = myDirectory.getCanonicalPath();
            header.setText(cur_dir);
        } catch (IOException e) {
        }
        body.removeAllViews();
        View view = ltInflater.inflate(R.layout.directory, body, false);
        TextView txt = (TextView) view.findViewById(R.id.dirname);
        txt.setText("..");
        body.addView(view);
        view.setOnTouchListener(this);
        Map<String, Integer> iconMap = new HashMap<>();
        iconMap.put("bin", R.drawable.ic_game_bin);
        iconMap.put("platform", R.drawable.ic_game_platfrom);
        iconMap.put("csso", R.drawable.ic_game_csso);
        iconMap.put("hl2", R.drawable.ic_game_main);
        iconMap.put("cstrike", R.drawable.ic_game_cstrike);

        for (File dir : directories) {
            view = ltInflater.inflate(R.layout.directory, body, false);
            txt = (TextView) view.findViewById(R.id.dirname);
            txt.setText(dir.getName());
            ImageView icon = (ImageView) view.findViewById(R.id.folder_icon);
            Integer iconRes = iconMap.get(dir.getName());
            if (iconRes != null) {
                icon.setImageResource(iconRes);
            } else {
                icon.setImageResource(R.drawable.ic_folder_grid); // 默认图标
            }
            body.addView(view);
            view.setOnTouchListener(this);
        }
    }

    public List<String> getExtStoragePaths() {
        List<String> list = new ArrayList<String>();
        File fileList[] = new File("/storage/").listFiles();
        if (fileList == null) return list;

        for (File file : fileList) {
            if (!file.getAbsolutePath()
                            .equalsIgnoreCase(
                                    Environment.getExternalStorageDirectory().getAbsolutePath())
                    && file.isDirectory()
                    && file.canRead()) list.add(file.getAbsolutePath());
        }
        return list;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences("mod", 0);

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getWindow().setDecorFitsSystemWindows(false);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_directory_choice);
        CardView cardView = findViewById(R.id.anim_c);
        cur_dir = null;
        body = (LinearLayout) findViewById(R.id.bodych);
        TextView header = (TextView) findViewById(R.id.header_txt);
        header.setText("");

        BlurView blurView = findViewById(R.id.blurView);
        float radius = 10f; // 模糊半径
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground) // 可选
                .setBlurRadius(radius);
        bounceAnimation(cardView);
        Button button = (Button) findViewById(R.id.button_choice);
        button.setOnClickListener(v -> saveAndExit());
        Button button_back = (Button) findViewById(R.id.button_back_launcher);
        button_back.setOnClickListener(v -> finishWithAnimation());
        List<String> l = getExtStoragePaths();
        if (l == null || l.isEmpty()) {
            ListDirectory(MainActivity.getDefaultDir());
            return;
        }

        LayoutInflater ltInflater = getLayoutInflater();
        View view = ltInflater.inflate(R.layout.directory, body, false);
        TextView txt = (TextView) view.findViewById(R.id.dirname);
        txt.setText(MainActivity.getDefaultDir());
        body.addView(view);
        view.setOnTouchListener(this);

        for (String dir : l) {
            view = ltInflater.inflate(R.layout.directory, body, false);
            txt = (TextView) view.findViewById(R.id.dirname);
            txt.setText(dir);
            body.addView(view);
            view.setOnTouchListener(this);
        }
    }

    // 动画
    private void bounceAnimation(View view) {
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.2f, 1.0f); // 0.8 , 1.2 , 1.0
        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.2f, 1.0f); // with上okay?
        scaleX.setDuration(700);
        scaleY.setDuration(700);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    private void saveAndExit() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("gamepath", cur_dir + "/");
        editor.apply();
        finishWithAnimation();
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.activity_fade_in_scer, R.anim.activity_fade_out_scer);
    }
}
