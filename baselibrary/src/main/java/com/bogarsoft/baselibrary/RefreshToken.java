package com.bogarsoft.baselibrary;

import android.util.Log;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RefreshToken {
    private static final String TAG = "RefreshToken";

    private OnTokenCreated onTokenCreated;
    public interface OnTokenCreated{
        void onTokenCreatedSuccessfully();
        void onTokenCreationFailed();
    }

    public void setOnTokenCreated(OnTokenCreated onTokenCreated) {
        this.onTokenCreated = onTokenCreated;
    }

    public RefreshToken(){

    }

    public void refreshToken(){
        try {
            Log.d(TAG, "refreshToken: "+Helper.TOKEN);
            AndroidNetworking.post(Helper.TOKEN)
                    .addBodyParameter("token", AESEncyption.decrypt(Helper.getStorgeUtil().gettoken2()))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: on refresh token"+response);
                            try {
                                if(!response.getBoolean("error")){
                                    String accessToken =  response.getString("accessToken");
                                    Helper.getStorgeUtil().settoken1(AESEncyption.encrypt(accessToken));
                                    if(onTokenCreated!=null){
                                        onTokenCreated.onTokenCreatedSuccessfully();
                                    }
                                }else {
                                        onTokenCreated.onTokenCreationFailed();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, "onError: "+anError.getErrorDetail());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
