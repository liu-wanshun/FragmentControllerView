package com.example.myapplication;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.lws.fragmentcontrollerview.FragmentControllerView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentControllerView fragmentControllerView = new FragmentControllerView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSLUCENT;
        // use FragmentControllerView or it's subClass as root
        getWindowManager().addView(fragmentControllerView, params);

        // use it or it's childView as fragmentContainer
        ViewGroup fragmentContainer = fragmentControllerView;
        fragmentContainer.setId(View.generateViewId());
        fragmentControllerView.getSupportFragmentManager().beginTransaction().add(fragmentContainer.getId(), new TestFragment1()).commitNowAllowingStateLoss();

        // control it's lifecycle, enjoy it !
        fragmentControllerView.onCreate(null);
        fragmentControllerView.onStart();
        fragmentControllerView.onResume();
        fragmentControllerView.onPostResume();

//        fragmentHostView.onPause();
//        fragmentHostView.onStop();
//        fragmentHostView.onDestroy();
    }
}