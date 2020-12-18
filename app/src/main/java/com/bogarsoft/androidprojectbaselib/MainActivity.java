package com.bogarsoft.androidprojectbaselib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bogarsoft.baselibrary.ApiCalls;
import com.bogarsoft.baselibrary.Helper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: "+ Helper.getStorgeUtil().isLoggedIn());
        Helper.sendToast("test toast",getApplicationContext());


    }
}