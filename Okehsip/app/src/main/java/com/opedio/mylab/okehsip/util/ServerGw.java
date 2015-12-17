package com.opedio.mylab.okehsip.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.opedio.mylab.okehsip.OkehsipApplication;
import com.opedio.mylab.okehsip.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by 247 on 12/10/2015.
 */
public class ServerGw {
    private static String TAG = "ServerGw";
    Context context;
    private ProgressDialog pDialog;
    private MyListener myListener;
    static OkehsipApplication apps;
    public static final String TOPDOMAIN = apps.isDev ? "http://10.0.2.2:8000":"http://okehsip-rtpoai.rhcloud.com";
    //URL of the PHP API
    private static String registerbyemail = TOPDOMAIN+"/gcmserver/registerbyemail/";
    private static String androidSubmitDataProfileURL = TOPDOMAIN+"/androidSubmitDataProfileURL/";
    //user login untuk android
    private static String androidUserLoginURL = TOPDOMAIN+"/androidUserLoginURL/";
    //user signup untuk android
    private static String androidUserSignupURL = TOPDOMAIN+"/androidUserSignupURL/";
    //user forgot password untuk android
    private static String androidForgotPasswordURL = TOPDOMAIN+"/androidForgotPasswordURL/";
    //user create new password
    private static String androidCreateNewPasswordURL = TOPDOMAIN+"/androidCreateNewPasswordURL/";
    //untuk verifikasi account yg di signup
    private static String androidAccountVerifyURL = TOPDOMAIN+"/androidAccountVerifyURL/";

    JSONParser jsonParser;
    public static ServerGw newInstance(Context ctx){
        ServerGw me1 = new ServerGw(ctx);
        return me1;
    }
    public ServerGw(Context ctx) {
        this.context = ctx;
        try {
            myListener = (MyListener) ctx;
        }catch (Exception e){
            myListener = null;
        }
        jsonParser = new JSONParser();
    }
    public void SubmitData(){
        new AsyncTask<String,String,Intent>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(context);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Network Checking ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected Intent doInBackground(String... params) {
                Log.i(TAG, "NetCheck doInBackground");
                Bundle data = new Bundle();
                final Intent res = new Intent();
                if(apps.isDev) return res;
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    try {
                        Log.i(TAG,"check network dengan melakukan hit ke http://www.google.com");
                        URL url = new URL("http://www.google.com");
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setConnectTimeout(6000);
                        Log.i(TAG,"HIT ke http://www.google.com");
                        urlc.connect();
                        Log.i(TAG,"response dari http://www.google.com");
                        if (urlc.getResponseCode() == 200) {
                            res.putExtras(data);
                            return res;
                        }
                        else{
                            //coba hit ke server aja
                            URL url2 = new URL(TOPDOMAIN);
                            HttpURLConnection urlc2 = (HttpURLConnection) url2.openConnection();
                            urlc2.setConnectTimeout(6000);
                            urlc2.connect();
                            if (urlc2.getResponseCode() == 200) {
                                res.putExtras(data);
                                return res;
                            }
                        }
                    } catch (MalformedURLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                data.putString(apps.KEY_ERROR_MESSAGE, context.getResources().getString(R.string.SYSTEM_ERROR_NETWORK_MSG));
                res.putExtras(data);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                boolean th = true;
                if (intent.hasExtra(apps.KEY_ERROR_MESSAGE)) {
                    th = false;
                }
                Log.i(TAG,"onPostExecute checking network "+th);
                if(th == true){
                    pDialog.dismiss();
//                if (!TextUtils.isEmpty(userEmail)){
                    myListener.SubmitData(intent);
//                }else{
//                	Toast.makeText(getApplicationContext(), "User is empty!", Toast.LENGTH_LONG).show();
//                    txtViewErrorMsg.setText("User is empty!");
//                }
                }
                else{
                    pDialog.dismiss();
                    myListener.SetAlertMessage(intent.getStringExtra(apps.KEY_ERROR_MESSAGE));
//                    txtViewErrorMsg.setTextColor(getResources().getColor(R.color.red01));
//                    txtViewErrorMsg.setText(intent.getStringExtra(KEY_ERROR_MESSAGE));
                }

            }

        }.execute();
    }
    public JSONObject userSignIn(String user, String pass, String authType, String client){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("email", ""+user);
        params.put("pass", ""+pass);
        params.put("client", ""+client);
        JSONObject json = jsonParser.getJSONFromUrlV2(androidUserLoginURL, params);
        return json;
    }
    public JSONObject userSignUp(String name, String email, String pass, String vercode){
//        name=opet&email=opet%40gmail.com&pass=asdfadsf&client=android
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("name", ""+name);
        params.put("email", ""+email);
        params.put("pass", ""+pass);
        params.put("client", "ANDROID");
        params.put("vercode", vercode);

        JSONObject json = jsonParser.getJSONFromUrlV2(androidUserSignupURL,params);
        return json;
    }
    public JSONObject forgotPasword(String email, String vercode){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("email", ""+email);
        params.put("vercode", ""+vercode);
        JSONObject json = jsonParser.getJSONFromUrlV2(androidForgotPasswordURL,params);
        return json;
    }
    public JSONObject createNewPasword(String email,String VerificationCode, String Password, String ConfPassword,String client){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("email", ""+email);
        params.put("vercode", ""+VerificationCode);
        params.put("pass", ""+Password);
        params.put("confPass", ""+ConfPassword);
        params.put("client", ""+client);

        JSONObject json = jsonParser.getJSONFromUrlV2(androidCreateNewPasswordURL,params);
        return json;
    }
    public JSONObject accountVerifing(String email, String verifingCode){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("email", ""+email);
        params.put("vercode", ""+verifingCode);
        JSONObject json = jsonParser.getJSONFromUrlV2(androidAccountVerifyURL,params);
        return json;
    }

    public interface MyListener {
        public void SubmitData(Intent intent);
        public void SetAlertMessage(String msg);
//        public void SubmitData(Intent intent);
    }
    public static JSONParser getJSONParser(){
        ServerGw me = ServerGw.newInstance(null);
        return me.jsonParser;
    }
    public class JSONParser {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // constructor
        public JSONParser() {
            is = null;
            jObj = null;
            json = "";
        }
        public JSONObject getJSONFromUrlV2(String url, HashMap<String, String> params) {
            disableConnectionReuseIfNecessary();
//        HashMap<String, String> parameter = new HashMap<String,String>();
//        parameter.put("", "");
            String json = performPostCall(url,params);
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            // return JSON String
            return jObj;

        }
        private void disableConnectionReuseIfNecessary() {
            // HTTP connection reuse which was buggy pre-froyo
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }
        }
        public String  performPostCall(String requestURL,
                                       HashMap<String, String> postDataParams) {

            URL url;
            String response = "";
            HttpURLConnection conn = null;
            try {
                url = new URL(requestURL);

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

//                throw new HttpException(responseCode+"");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    conn = null;
                }catch (Exception e){

                }
            }

            return response;
        }
        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

}
