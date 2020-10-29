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
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.interfaces.AlertActionListener;
import com.irozon.alertview.objects.AlertAction;

import org.beyonity.mrpubgcash.Activity.LoginActivity;
import org.beyonity.mrpubgcash.BuildConfig;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;


public class ApiCalls {


    private static final org.beyonity.mrpubgcash.Utils.ApiCalls apiCalls = new org.beyonity.mrpubgcash.Utils.ApiCalls();
    private static final String TAG = "ApiCalls";

    public interface OnResult {
        void onSuccess(JSONObject response);

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

    public static org.beyonity.mrpubgcash.Utils.ApiCalls getInstance() {
        return apiCalls;
    }

    public ApiCalls() {
    }

    public void init() {

    }

    public void login(HashMap<String, String> body, final Activity activity, final OnResult onResult) {
        final Dialog dialog = org.beyonity.mrpubgcash.Utils.Helper.showProgress(activity);
        AndroidNetworking.post(org.beyonity.mrpubgcash.Utils.Helper.LOGIN)
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
            AndroidNetworking.post(org.beyonity.mrpubgcash.Utils.Helper.LOGOUT)
                    .addBodyParameter("token", org.beyonity.mrpubgcash.Utils.AESEncyption.decrypt(org.beyonity.mrpubgcash.Utils.Helper.sp.gettoken2()))
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
        final Dialog dialog = org.beyonity.mrpubgcash.Utils.Helper.showProgress(activity);
        try {
            AndroidNetworking.get(link)
                    .addHeaders("x-access-token", org.beyonity.mrpubgcash.Utils.AESEncyption.decrypt(org.beyonity.mrpubgcash.Utils.Helper.sp.gettoken1()))
                    .addQueryParameter(query)
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
                                            org.beyonity.mrpubgcash.Utils.RefreshToken refreshToken = new org.beyonity.mrpubgcash.Utils.RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new org.beyonity.mrpubgcash.Utils.RefreshToken.OnTokenCreated() {
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
                                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    org.beyonity.mrpubgcash.Utils.Helper.sp.clear();
                                                    Intent i = new Intent(activity, LoginActivity.class);
                                                    activity.startActivity(i);
                                                    activity.finish();
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
                    .addHeaders("x-access-token", org.beyonity.mrpubgcash.Utils.AESEncyption.decrypt(org.beyonity.mrpubgcash.Utils.Helper.sp.gettoken1()))
                    .addQueryParameter(query)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                           // Log.d(TAG, "onResponse: " + response);
                            onResult.responseReceived();

                            try {
                                if (!response.getBoolean("error")) {
                                    onResult.onSuccess(response);
                                } else {
                                    try {
                                        if (response.getString("message").equals("TokenExpiredError")) {
                                            org.beyonity.mrpubgcash.Utils.RefreshToken refreshToken = new org.beyonity.mrpubgcash.Utils.RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new org.beyonity.mrpubgcash.Utils.RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    onResult.onTryAgain();
                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    org.beyonity.mrpubgcash.Utils.Helper.sp.clear();
                                                    Intent i = new Intent(activity, LoginActivity.class);
                                                    activity.startActivity(i);
                                                    activity.finish();
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

    public void postMethod(String link, HashMap<String, String> body, final Activity activity, final boolean tryAgainhandler, final OnResult onResult) {
        final Dialog dialog = org.beyonity.mrpubgcash.Utils.Helper.showProgress(activity);
        Log.d(TAG, "postMethod: " + body);
        try {
            AndroidNetworking.post(link)
                    .addHeaders("x-access-token", org.beyonity.mrpubgcash.Utils.AESEncyption.decrypt(org.beyonity.mrpubgcash.Utils.Helper.sp.gettoken1()))
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
                                            org.beyonity.mrpubgcash.Utils.RefreshToken refreshToken = new org.beyonity.mrpubgcash.Utils.RefreshToken();
                                            refreshToken.refreshToken();
                                            refreshToken.setOnTokenCreated(new org.beyonity.mrpubgcash.Utils.RefreshToken.OnTokenCreated() {
                                                @Override
                                                public void onTokenCreatedSuccessfully() {
                                                    onResult.onTryAgain();
                                                }

                                                @Override
                                                public void onTokenCreationFailed() {
                                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Token Expired and creation of new token failed please login again", activity.getApplicationContext());
                                                    org.beyonity.mrpubgcash.Utils.Helper.sp.clear();
                                                    Intent i = new Intent(activity, LoginActivity.class);
                                                    activity.startActivity(i);
                                                    activity.finish();
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

    public void checkAppUpdate(Activity activity){

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
        AndroidNetworking.get(org.beyonity.mrpubgcash.Utils.Helper.CHECK_FOR_UPDATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response);
                        try {
                            boolean error = response.getBoolean("error");
                            if(!error){
                                JSONObject object = response.getJSONObject("values");
                                String versionname = object.getString("version_name");
                                String versioncode = (object.getString("version_code"));
                                PackageInfo pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                Log.d(TAG, "onResponse: "+ PackageInfoCompat.getLongVersionCode(pinfo));
                                if(PackageInfoCompat.getLongVersionCode(pinfo)<Long.parseLong(versioncode)){
                                    /*final MainActivity.DownloadTask downloadTask = new MainActivity.DownloadTask(MainActivity.this,versionname,pinfo.versionName);
                                    downloadTask.execute("https://mr.com/download/app/release/pubggod_v_"+versionname+".apk");*/
                                    File file = new File(activity.getFilesDir()+"/mrpubgcash_v"+pinfo.versionName+".apk");
                                    if(file.exists()){
                                        file.delete();
                                    }
                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Update Available Downloading now...",activity.getApplicationContext());
                                    dialog.show();
                                    String link = org.beyonity.mrpubgcash.Utils.Helper.downloadlink+versionname+".apk";
                                    String filename = "mrpubgcash_v"+versionname+".apk";
                                    AndroidNetworking.download(link,activity.getFilesDir().getAbsolutePath(),filename)
                                            .build()
                                            .setDownloadProgressListener(new DownloadProgressListener() {
                                                @Override
                                                public void onProgress(long bytesDownloaded, long totalBytes) {
                                                    long percentage = ((bytesDownloaded*100)/totalBytes);
                                                    progressBar.setProgress((int) percentage);

                                                    Log.d(TAG, "onProgress: "+percentage);
                                                    if (Long.compare(bytesDownloaded, totalBytes) == 0) {
                                                        dialog.dismiss();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        progressBar2.setVisibility(View.VISIBLE);
                                                        //Helper.sendToast( "Processing... please wait, May take a while", activity.getApplicationContext());
                                                    }
                                                }
                                            })
                                            .startDownload(new DownloadListener() {
                                                @Override
                                                public void onDownloadComplete() {
                                                    File file = new File(activity.getFilesDir(), filename);
                                                    Uri fileUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);

                                                    Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                                                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                                                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
                                                    activity.startActivity(intent);
                                                }

                                                @Override
                                                public void onError(ANError anError) {
                                                    dialog.dismiss();
                                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("App update failed...",activity.getApplicationContext());
                                                    Log.d(TAG, "onError: Failed to update the app");
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
                                                }
                                            });
                                }

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {

                            e.printStackTrace();
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d(TAG, "onError: "+anError.getErrorDetail());
                    }
                });
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
