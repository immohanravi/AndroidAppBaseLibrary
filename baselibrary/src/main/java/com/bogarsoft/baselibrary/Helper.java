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

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Helper {


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    static HashMap<String,String> query = new HashMap<>();
    static HashMap<String,String> body = new HashMap<>();
    private static final String TAG = "Helper";
    //private static final String paytmhost = "https://securegw-stage.paytm.in/";


    private static HashMap<String,String> getrequest = new HashMap<>();
    private static HashMap<String,String> postrequest = new HashMap<>();
    private static HashMap<String,String> links = new HashMap<>();
    private static StorageUtility sp;
    private static String link;
    public static String LOGIN = link+"/login";
    public static String LOGOUT = link+"/logout";
    public static String TOKEN = link+"/token";

    private static final String emailregex = "^(.+)@(.+)$";
    private static Pattern pattern = Pattern.compile(emailregex);
/*

*/

    //public static SQLiteSignInHandler sqLiteSignInHandler;
    public static void init(Context context, String domainLink){

        sp = new StorageUtility(context);
        link = domainLink;
        //sqLiteSignInHandler = new SQLiteSignInHandler(context);
        //setUser(context);
    }



    public static void sendToast(String message, Context context){
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
        View view =toast.getView();
        view.setBackgroundResource(R.drawable.toast_round);
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
        return link+getrequest.get(key);
    }

    public static String postRequestLink(String key){
        return link+postrequest.get(key);
    }

    public static void setPostrequest(String key,String link){
        postrequest.put(key,link);
    }
    public static void setGetrequest(String key,String link){
        getrequest.put(key,link);
    }
    public static void setLinks(String key,String link){
        links.put(key,link);
    }

    public static String getLink(String key) {
        return link+links.get(key);
    }

    public static String getDomainLink(){
        return link;
    }

    public static StorageUtility getStorgeUtil(){
        return sp;
    }


    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return "receipt_"+sb.toString();
    }

    public static boolean isEmailValid(TextInputEditText email){
        return pattern.matcher(getString(email)).matches();
    }

    public static String getText(View inputEditText){
        if(inputEditText instanceof TextInputEditText){
            return ((TextInputEditText)inputEditText).getText().toString().trim();
        }else {
            return ((MaterialAutoCompleteTextView)inputEditText).getText().toString().trim();
        }

    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static long getTodaysDateInMilli(){
        String inputRaw = Helper.getDate(System.currentTimeMillis(),"yyyy/MM/dd") +" 00:00:00";
        Log.d(TAG, "setHolidaySwitch: "+inputRaw);
        String input = inputRaw.replace( "/", "-" ).replace( " ", "T" );
        DateTimeZone zone = DateTimeZone.getDefault();
        DateTime dateTime = new DateTime( input, zone );
        long millisecondsSinceUnixEpoch = dateTime.getMillis();
        return millisecondsSinceUnixEpoch;

    }

    public static long getDateInmilli(long date){
        String inputRaw = Helper.getDate(date,"yyyy/MM/dd") +" 00:00:00";
        String input = inputRaw.replace( "/", "-" ).replace( " ", "T" );
        DateTimeZone zone = DateTimeZone.getDefault();
        DateTime dateTime = new DateTime( input, zone );
        long millisecondsSinceUnixEpoch = dateTime.getMillis();
        return millisecondsSinceUnixEpoch;
    }


    public static long getDateInmilliUsingString(int year,int month,int dateofmonth){
        String inputRaw = year+"/"+(month)+"/"+dateofmonth+" 00:00:00";
        String input = inputRaw.replace( "/", "-" ).replace( " ", "T" );
        DateTimeZone zone = DateTimeZone.getDefault();
        DateTime dateTime = new DateTime( input, zone );
        long millisecondsSinceUnixEpoch = dateTime.getMillis();
        return millisecondsSinceUnixEpoch;
    }



    public static String withCaps(String source) {
        source = source.toLowerCase();
        StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        Log.e("string array", source);

        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();

            if (stringArray.length > 0) {
                if((int)stringArray[0] != 32) {
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    str = new String(stringArray);
                }
            }



            res.append(str).append(" ");
        }

        return res.toString().trim();
    }

}
