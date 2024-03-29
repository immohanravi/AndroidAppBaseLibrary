package com.bogarsoft.baselibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.PackageInfoCompat;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bogarsoft.baselibrary.Helper;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.interfaces.AlertActionListener;
import com.irozon.alertview.objects.AlertAction;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;


public class ApiCalls {


    private static final ApiCalls apiCalls = new ApiCalls();
    private static final String TAG = "ApiCalls";

    OnLogin onLogin;
    public interface OnLogin{
        void Loging();
    }

    public void setOnLogin(OnLogin onLogin) {
        this.onLogin = onLogin;
    }

    public interface OnResult {
        void onSuccess(JSONObject response);

        void onFailed(JSONObject response);

        void responseReceived();

        void onTryAgain();
    }

    public interface OnResultArray {
        void onSuccess(JSONArray response);

        void onFailed(JSONObject response);

        void responseReceived();

        void onTryAgain();
    }

    public interface OnProgress{
        void onProgress(int progress);
    }
    private interface OnAlertCallBack {
        void tryAgain();

        void cancel();
    }

    public static ApiCalls getInstance() {
        return apiCalls;
    }

    public ApiCalls() {
    }

    public void init() {

    }

    public void login(HashMap<String, String> body, final Activity activity, final OnResult onResult) {
        final Dialog dialog = Helper.showProgress(activity);
        AndroidNetworking.post(Helper.LOGIN)
                .addBodyParameter(body)
                .setTag("Login User")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            if (!response.getBoolean("error")) {
                                onResult.onSuccess(response);
                            } else {
                                onResult.onFailed(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onResult.onFailed(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            onResult.onFailed(response);
                        }
                        onResult.responseReceived();
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        if (anError.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                        } else {
                            Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                        }
                        onResult.responseReceived();
                        NetWorkAlertLogin(activity, new OnAlertCallBack() {
                            @Override
                            public void tryAgain() {
                                onResult.onTryAgain();
                            }

                            @Override
                            public void cancel() {
                            }
                        });

                    }
                });
    }

    public void logout(final OnResult onResult) {
        try {
            AndroidNetworking.post(Helper.LOGOUT)
                    .addBodyParameter("token", AESEncyption.decrypt(Helper.getStorgeUtil().gettoken2()))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    onResult.onFailed(response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onResult.onFailed(response);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)
                            } else {
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                            onResult.onFailed(new JSONObject());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMethod(final String link, final HashMap<String, String> query, final Activity activity, final boolean tryAgainhandler, final OnResult onResult) {
        final Dialog dialog = Helper.showProgress(activity);
        try {
            AndroidNetworking.get(link)
                    .addHeaders("authorization", "Basic " + AESEncyption.decrypt(Helper.getStorgeUtil().gettoken1()))
                    .addQueryParameter(query)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dialog.dismiss();
                            Log.d(TAG, "onResponse: " + response);
                            onResult.responseReceived();

                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    try {
                                        if (response.getString("message").equals("TokenExpiredError")) {
                                            RefreshToken refreshToken = new RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    //onResult.onTryAgain();
                                                    if(tryAgainhandler){
                                                        onResult.onTryAgain();
                                                    }else {
                                                        getMethod(link,query,activity,tryAgainhandler,onResult);
                                                    }

                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    Helper.getStorgeUtil().clear();
                                                    if(onLogin!=null){
                                                        onLogin.Loging();
                                                        activity.finish();
                                                    }
                                                }
                                            });
                                        } else {
                                            onResult.onFailed(response);
                                        }
                                    } catch (JSONException e) {
                                        onResult.onFailed(response);
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                onResult.onFailed(response);
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }

                            onResult.responseReceived();
                            if (tryAgainhandler) {
                                tryAgain(activity, new OnAlertCallBack() {
                                    @Override
                                    public void tryAgain() {
                                        onResult.onTryAgain();
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            }

                            dialog.dismiss();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethod(String link, HashMap<String, String> query, final Activity activity, final boolean tryAgainhandler, final OnResult onResult, boolean dialogshow) {
        try {
            AndroidNetworking.get(link)
                    .addHeaders("authorization", "Basic " + AESEncyption.decrypt(Helper.getStorgeUtil().gettoken1()))
                    .addQueryParameter(query)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                            onResult.responseReceived();

                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    try {
                                        if (response.getString("message").equals("TokenExpiredError")) {
                                            RefreshToken refreshToken = new RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    onResult.onTryAgain();
                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    Helper.getStorgeUtil().clear();
                                                    if(onLogin!=null){
                                                        onLogin.Loging();
                                                        activity.finish();
                                                    }
                                                }
                                            });
                                        } else {
                                            onResult.onFailed(response);
                                        }
                                    } catch (JSONException e) {
                                        onResult.onFailed(response);
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                onResult.onFailed(response);
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }

                            onResult.responseReceived();
                            if (tryAgainhandler) {
                                tryAgain(activity, new OnAlertCallBack() {
                                    @Override
                                    public void tryAgain() {
                                        onResult.onTryAgain();
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            }


                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethodWithoutAuth(String link, HashMap<String, String> query, final Activity activity, final boolean tryAgainhandler, final OnResult onResult, boolean dialogshow) {
        Dialog dialog = null;
        if (dialogshow){
            dialog = Helper.showProgress(activity);
        }
        try {
            Dialog finalDialog1 = dialog;
            AndroidNetworking.get(link)
                    .addQueryParameter(query)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);

                            onResult.responseReceived();
                            if (dialogshow){
                                finalDialog1.dismiss();
                            }

                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    try {
                                        if (response.getString("message").equals("TokenExpiredError")) {
                                            RefreshToken refreshToken = new RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    onResult.onTryAgain();
                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    Helper.getStorgeUtil().clear();
                                                    if(onLogin!=null){
                                                        onLogin.Loging();
                                                        activity.finish();
                                                    }
                                                }
                                            });
                                        } else {
                                            onResult.onFailed(response);
                                        }
                                    } catch (JSONException e) {
                                        onResult.onFailed(response);
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                onResult.onFailed(response);
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }

                            onResult.responseReceived();
                            if (tryAgainhandler) {
                                tryAgain(activity, new OnAlertCallBack() {
                                    @Override
                                    public void tryAgain() {
                                        onResult.onTryAgain();
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            }
                            if (dialogshow){
                                finalDialog1.dismiss();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethodWithoutAuthWithArray(String link, HashMap<String, String> query, final Activity activity, final boolean tryAgainhandler, final OnResultArray onResult, boolean dialogshow) {
        Dialog dialog = null;
        if (dialogshow){
            dialog = Helper.showProgress(activity);
        }
        try {
            Dialog finalDialog1 = dialog;
            AndroidNetworking.get(link)
                    .addQueryParameter(query)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            onResult.responseReceived();
                            if (dialogshow){
                                //finalDialog1.dismiss();
                            }
                            onResult.onSuccess(response);

                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }

                            onResult.responseReceived();
                            if (tryAgainhandler) {
                                tryAgain(activity, new OnAlertCallBack() {
                                    @Override
                                    public void tryAgain() {
                                        onResult.onTryAgain();
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            }
                            if (dialogshow){
                                //finalDialog1.dismiss();
                            }
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void postMethod(String link, HashMap<String, String> body, final Activity activity, final boolean tryAgainhandler, final OnResult onResult) {
        final Dialog dialog = Helper.showProgress(activity);
        Log.d(TAG, "postMethod: " + body);
        try {
            AndroidNetworking.post(link)
                    .addHeaders("authorization", "Basic " + AESEncyption.decrypt(Helper.getStorgeUtil().gettoken1()))
                    .addBodyParameter(body)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dialog.dismiss();
                            // Log.d(TAG, "onResponse: " + response);
                            onResult.responseReceived();
                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    try {
                                        if (response.getString("message").equals("TokenExpiredError")) {
                                            RefreshToken refreshToken = new RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    onResult.onTryAgain();
                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    Helper.getStorgeUtil().clear();
                                                    if(onLogin!=null){
                                                        onLogin.Loging();
                                                        activity.finish();
                                                    }
                                                }
                                            });
                                        } else {
                                            onResult.onFailed(response);
                                        }
                                    } catch (JSONException e) {
                                        onResult.onFailed(response);
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                onResult.onFailed(response);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (anError.getErrorCode() != 0) {
                                // received error from server
                                // error.getErrorCode() - the error code from server
                                // error.getErrorBody() - the error body from server
                                // error.getErrorDetail() - just an error detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                                // get parsed error object (If ApiError is your class)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }

                            onResult.responseReceived();
                            if (tryAgainhandler) {
                                tryAgain(activity, new OnAlertCallBack() {
                                    @Override
                                    public void tryAgain() {
                                        onResult.onTryAgain();
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            }
                            dialog.dismiss();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UploadAfileMethod(String link, File file, HashMap<String, String> query, Activity activity, boolean tryAgainhandler, OnResult onResult) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        dialog.setContentView(R.layout.progress_dialog_bar);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ProgressBar progressBar2 = dialog.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.red_active), PorterDuff.Mode.MULTIPLY);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        dialog.setCancelable(false);
        dialog.show();
        try {
            AndroidNetworking.upload(link)
                    .addHeaders("authorization", "Basic " + AESEncyption.decrypt(Helper.getStorgeUtil().gettoken1()))
                    .addMultipartFile("file", file)
                    .addQueryParameter(query)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            long percentage = ((bytesUploaded*100)/totalBytes);
                            progressBar.setProgress((int) percentage);

                            Log.d(TAG, "onProgress: "+percentage);
                            if (Long.compare(bytesUploaded, totalBytes) == 0) {
                                progressBar.setVisibility(View.INVISIBLE);
                                progressBar2.setVisibility(View.VISIBLE);
                            }
                        }
                    }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    onResult.responseReceived();
                    try {
                        if (!response.getBoolean("error")) {
                            onResult.onSuccess(response);
                        } else {
                            try {
                                if (response.getString("message").equals("TokenExpiredError")) {
                                    RefreshToken refreshToken = new RefreshToken();
                                    refreshToken.refreshToken();
                                    refreshToken.setOnTokenCreated(new RefreshToken.OnTokenCreated() {
                                        @Override
                                        public void onTokenCreatedSuccessfully() {
                                            onResult.onTryAgain();
                                        }

                                        @Override
                                        public void onTokenCreationFailed() {
                                            Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                            Helper.getStorgeUtil().clear();
                                            if(onLogin!=null){
                                                onLogin.Loging();
                                                activity.finish();
                                            }
                                        }
                                    });
                                } else {
                                    onResult.onFailed(response);
                                }
                            } catch (JSONException e) {
                                onResult.onFailed(response);
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        onResult.onFailed(response);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    if (anError.getErrorCode() != 0) {
                        // received error from server
                        // error.getErrorCode() - the error code from server
                        // error.getErrorBody() - the error body from server
                        // error.getErrorDetail() - just an error detail
                        Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                        Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                        Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                        // get parsed error object (If ApiError is your class)

                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                    }

                    onResult.responseReceived();
                    if (tryAgainhandler) {
                        tryAgain(activity, new OnAlertCallBack() {
                            @Override
                            public void tryAgain() {
                                onResult.onTryAgain();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void NetWorkAlertLogin(final Activity activity, final OnAlertCallBack onAlertCallBack) {

        AlertView alertView = new AlertView("Network Error", "", AlertStyle.BOTTOM_SHEET);
        alertView.addAction(new AlertAction("Try again", AlertActionStyle.POSITIVE, new AlertActionListener() {
            @Override
            public void onActionClick(@NotNull AlertAction alertAction) {
                onAlertCallBack.tryAgain();
            }
        }));

        alertView.addAction(new AlertAction("Close", AlertActionStyle.NEGATIVE, new AlertActionListener() {
            @Override
            public void onActionClick(@NotNull AlertAction alertAction) {
                onAlertCallBack.cancel();
                activity.finishAndRemoveTask();
            }
        }));

        alertView.show((AppCompatActivity) activity);
    }

    public void tryAgain(Activity activity, final OnAlertCallBack onAlertCallBack) {
        AlertView alertView = new AlertView("Network Error", "", AlertStyle.BOTTOM_SHEET);
        alertView.addAction(new AlertAction("Try again", AlertActionStyle.POSITIVE, new AlertActionListener() {
            @Override
            public void onActionClick(@NotNull AlertAction alertAction) {
                onAlertCallBack.tryAgain();
            }
        }));

        alertView.addAction(new AlertAction("not now", AlertActionStyle.NEGATIVE, new AlertActionListener() {
            @Override
            public void onActionClick(@NotNull AlertAction alertAction) {
                onAlertCallBack.cancel();
            }
        }));

        alertView.show((AppCompatActivity) activity);

    }


}
