package com.bogarsoft.androidprojectbaselib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bogarsoft.baselibrary.ApiCalls;
import com.bogarsoft.baselibrary.Helper;
import com.bogarsoft.baselibrary.Query;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: "+ Helper.getStorgeUtil().isLoggedIn());
        Helper.sendToast("test toast",getApplicationContext());

        ApiCalls.getInstance().getMethodWithoutAuthWithArray(
                "https://jsonplaceholder.typicode.com/todos",
                new Query(),
                this,
                false,
                new ApiCalls.OnResultArray() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailed(JSONObject response) {
                        Log.d(TAG, "onFailed: "+response);
                    }

                    @Override
                    public void responseReceived() {

                    }

                    @Override
                    public void onTryAgain() {

                    }
                }

        ,true);

    }
}