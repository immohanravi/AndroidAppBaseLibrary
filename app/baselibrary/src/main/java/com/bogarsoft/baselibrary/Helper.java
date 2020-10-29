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

import com.google.android.material.textfield.TextInputEditText;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.beyonity.mrpubgcash.Activity.MainActivity;
import org.beyonity.mrpubgcash.Activity.Welcome_login;
import org.beyonity.mrpubgcash.Models.user;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Helper {

    static HashMap<String,String> query = new HashMap<>();
    static HashMap<String,String> body = new HashMap<>();
    private static final String TAG = "Helper";
    //private static final String paytmhost = "https://securegw-stage.paytm.in/";


    public static StorageUtility sp;
    public static interface PaymentDone{
        void done();
    }


    private static String link;
    private static String domainname = "https://mrpubgcash.com";

/*

*/

    //public static SQLiteSignInHandler sqLiteSignInHandler;
    public static void init(Context context,String weblink){

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


    public static void makePaymentUPI(final String orderId, final int amount, String upi, final Activity activity, final PaymentDone paymentDone){
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(activity)
                .setPayeeVpa(upi)
                .setPayeeName("capital money")
                .setTransactionId(orderId)
                .setTransactionRefId(orderId)
                .setDescription("Wallet Amount")
                .setAmount(String.valueOf(amount)+".00")
                .build();
        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
            @Override
            public void onTransactionCompleted(TransactionDetails transactionDetails) {
                Log.d(TAG, "onTransactionCompleted: "+transactionDetails);
                if(transactionDetails.getStatus().equals("SUCCESS")){
                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Transaction success",activity.getApplicationContext());
                    body.clear();
                    body.put("TID",transactionDetails.getTransactionId());
                    body.put("TIDREF",transactionDetails.getTransactionRefId());
                    body.put("amount", String.valueOf(amount));
                    ApiCalls.getInstance().postMethod(
                            org.beyonity.mrpubgcash.Utils.Helper.SET_PAYMENT_DETAILS,
                            body,
                            activity,
                            false,
                            new ApiCalls.OnResult() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Successfully updated wallet",activity.getApplicationContext());
                                    if(paymentDone!=null){
                                        paymentDone.done();
                                    }
                                    org.beyonity.mrpubgcash.Utils.Helper.setUser(activity);

                                    Log.d(TAG, "onSuccess: "+response);
                                }

                                @Override
                                public void onFailed(JSONObject response) {
                                    Log.d(TAG, "onFailed: "+response);
                                    //Helper.sendToast("failed to update wallet please contact support",getContext());
                                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("TID", orderId);
                                    clipboard.setPrimaryClip(clip);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Failed to Update Wallet");
                                    builder.setMessage("If Amount debited from your bank account and Wallet is not updated please contact support with the transaction id "+orderId+" also copied to your clipboard");
                                    AlertDialog dialog = builder.show();
                                    org.beyonity.mrpubgcash.Utils.Helper.sendToast("Transaction id copied to clipboard",activity);

                                }

                                @Override
                                public void responseReceived() {

                                }

                                @Override
                                public void onTryAgain() {

                                }
                            }
                    );
                }
            }

            @Override
            public void onTransactionSuccess() {
                org.beyonity.mrpubgcash.Utils.Helper.sendToast("Transaction Successfull",activity);
            }
            @Override
            public void onTransactionSubmitted() {
                Log.d(TAG, "onTransactionSubmitted: might be pending");
                org.beyonity.mrpubgcash.Utils.Helper.sendToast("transaction is pending",activity);
            }

            @Override
            public void onTransactionFailed() {
                Log.d(TAG, "onTransactionFailed: failed");
                org.beyonity.mrpubgcash.Utils.Helper.sendToast("transaction is failted",activity);
            }

            @Override
            public void onTransactionCancelled() {
                Log.d(TAG, "onTransactionCancelled: cancelled");
                org.beyonity.mrpubgcash.Utils.Helper.sendToast("transaction is cancelled",activity);
            }

            @Override
            public void onAppNotFound() {
                Log.d(TAG, "onAppNotFound: app not found");
                org.beyonity.mrpubgcash.Utils.Helper.sendToast("No app found for the transaction",activity);
            }
        });

        easyUpiPayment.startPayment();
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
}
