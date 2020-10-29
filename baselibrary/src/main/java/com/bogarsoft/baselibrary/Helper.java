package com.bogarsoft.baselibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Helper {

    static HashMap<String,String> query = new HashMap<>();
    static HashMap<String,String> body = new HashMap<>();
    private static final String TAG = "Helper";
    //private static final String paytmhost = "https://securegw-stage.paytm.in/";


    private static HashMap<String,String> getrequest = new HashMap<>();
    private static HashMap<String,String> postrequest = new HashMap<>();

    private static StorageUtility sp;
    private static String link;
    public static String LOGIN = link+"/login";
    public static String LOGOUT = link+"/logout";
    public static String TOKEN = link+"/token";




/*

*/

    //public static SQLiteSignInHandler sqLiteSignInHandler;
    public static void init(Context context, String weblink){

        sp = new StorageUtility(context);
        link = weblink;
        //sqLiteSignInHandler = new SQLiteSignInHandler(context);
        //setUser(context);
    }


    public static void sendToast(String message, Context context){
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
        View view =toast.getView();
        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(Color.WHITE);
        toast.show();
    }

    public static Dialog showProgress(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        dialog.setContentView(R.layout.progress_dialog);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
         dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }



    public static String remainging(long data){
        long seconds = data / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        //String time = days + "day " + hours % 24 + " hours" + minutes % 60 + " minutes" + seconds % 60;
        String time = "";
        if(days == 0){
            time = hours%24 +" Hrs " + minutes % 60+" Mins to go";
        }else if(days == 1){
            time = days +" day "+hours%24 +" Hrs " + minutes % 60+" Mins to go";
        }else {
            time = days +" days "+hours%24 +" Hrs " + minutes % 60+" Mins to go";
        }

        return time;
   }

    public static boolean checkifEmpty(TextInputEditText... views){
        boolean ans = false;
        for(TextInputEditText inputEditText : views){
            if(TextUtils.isEmpty(inputEditText.getText())){
                inputEditText.setError(inputEditText.getHint());
                ans = true;
            }else {
                inputEditText.setError(null);
            }
        }
        return ans;
    }

    public static String getString(TextInputEditText input){
        return input.getText().toString().trim();
    }

    public static String getRequestLink(String key){
        return getrequest.get(key);
    }

    public static String postRequestLink(String key){
        return postrequest.get(key);
    }

    public static void setPostrequest(String key,String link){
        postrequest.put(key,link);
    }
    public static void setGetrequest(String key,String link){
        getrequest.put(key,link);
    }
    public static StorageUtility getStorgeUtil(){
        return sp;
    }


}
