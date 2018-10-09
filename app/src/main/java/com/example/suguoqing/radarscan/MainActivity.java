package com.example.suguoqing.radarscan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.suguoqing.radarscan.been.Gender;
import com.example.suguoqing.radarscan.been.Info;
import com.example.suguoqing.radarscan.custom.CircleView;
import com.example.suguoqing.radarscan.custom.RadarView;
import com.example.suguoqing.radarscan.custom.RadarViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int[] mImgs = {R.drawable.len, R.drawable.leo, R.drawable.lep,
            R.drawable.leq, R.drawable.ler, R.drawable.les, R.drawable.mln, R.drawable.mmz, R.drawable.mna,
            R.drawable.mnj, R.drawable.leo, R.drawable.leq, R.drawable.les, R.drawable.lep};
    private String[] mNames = {"ImmortalZ", "唐马儒", "王尼玛", "张全蛋", "蛋花", "王大锤", "叫兽", "哆啦A梦"};
    private SparseArray<Info> infoList;
    private RadarViewGroup radarViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();




        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void initView() {
        radarViewGroup = findViewById(R.id.radarViewGroup);
    }

    private void initData() {
        infoList = new SparseArray<>();
        for (int i = 0; i < mImgs.length; i++) {
            Info info = new Info();
            info.setId(mImgs[i]);
            info.setAge(((int) Math.random() * 25 + 16) + "岁");
            info.setName(mNames[(int) (Math.random() * mNames.length)]);
            info.setGender(i % 3 == 0 ? Gender.男 : Gender.女);
            info.setDistance(Math.round((Math.random() * 10) * 100) / 100);
            Log.d(TAG, "initData: "+info);
            infoList.put(i, info);

        }

        radarViewGroup.setInfoList(infoList);
    }

}
