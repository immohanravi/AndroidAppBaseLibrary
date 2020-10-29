package com.bogarsoft.androidprojectbaselib;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.bogarsoft.androidprojectbaselib.ui.login.LoginActivity;
import com.bogarsoft.baselibrary.AESEncyption;
import com.bogarsoft.baselibrary.ApiCalls;
import com.bogarsoft.baselibrary.Helper;

public class App extends Application {

    String link = "http://192.168.1.104:3000";
    @Override
    public void onCreate() {
        super.onCreate();
        Helper.init(getApplicationContext(),link);
        ApiCalls.getInstance().setOnLogin(new ApiCalls.OnLogin() {
            @Override
            public void Loging() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        AESEncyption.init("a45fdsa564fd456sa456s","456adsf456ds456f");
    }
}
