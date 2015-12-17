package com.opedio.mylab.okehsip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;

import com.opedio.mylab.okehsip.models.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 247 on 12/7/2015.
 */
public class OkehsipApplication extends Application implements
        SharedPreferences.OnSharedPreferenceChangeListener  {

    private static SharedPreferences prefs;
    private static final String TAG = OkehsipApplication.class.getName();

    public static final String ACTION_REGISTER = "com.opedio.mylab.okehsip.REGISTER";
    public static final String ACTION_PUSHNEWMESSAGEDISKUSI = "com.opedio.mylab.okehsip.PUSHNEWMESSAGEDISKUSI";
    public static final String EXTRA_STATUS = "status";
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 0;

    public static final String KEY_VERCODE = "vercode";
    public static final String KEY_ERROR_MESSAGE = "err_msg";
    public static final String KEY_ERROR_CODE = "err_code";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String KEY_SERVER_NOTIF = "notif";
    public final static String KEY_MY_EMAIL = "KEY_MY_EMAIL";
    public final static String KEY_AUTH_TOKEN = "authtoken";
    public final static String KEY_TOKEN = "token";
    public final static String KEY_VERIFICATION_CODE = "code";

    public static final String REG_PINCODE = "regPincode";

    public  static String SYSTEM_ERROR_MSG = "Maaf, system sedang sibuk. Cobalah kembali. Terima kasih";
    public  static String SYSTEM_ERROR_NETWORK_MSG = "Error in Network Connection";

    public static String CONST_CLIENT = "ANDROID";

    //data GCM
    public static String SENDER_ID = "909608338008"; //https://console.developers.google.com/project/temporal-storm-487
    public static String SERVER_URL = "http://homepage-opetstudio.rhcloud.com/gcmserver";



    public static Account[] account_arr;
    public static String[] email_arr;
    public static String[] email_app_arr;

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase db;



    private static String msisdnku = "";
    private static String vercode = "";
    public static boolean isDev = false; //ganti-ganti... kalo mau run di server... ganti false
//    private AccountManager mAccountManager;
//    private static Account availableAccounts[];

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefs.registerOnSharedPreferenceChangeListener(this);

        //ambil msg dari strings.xml
        SYSTEM_ERROR_MSG = getResources().getString(R.string.SYSTEM_ERROR_MSG);
        SYSTEM_ERROR_NETWORK_MSG = getResources().getString(R.string.SYSTEM_ERROR_NETWORK_MSG);

//        mAccountManager = AccountManager.get(this);
//        Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        List<String> emailList = getEmailList();
        email_arr = emailList.toArray(new String[emailList.size()]);

        //buat koneksi database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    public static String getPreferredPincode() {
        String pincode = prefs.getString("pref_pincode", "123456");
        return pincode;
    }
    public static String getPreferredPincodeRegAccount() {
        String pincode = prefs.getString(REG_PINCODE, "xxxxxx");
        return pincode;
    }
    public static String getSenderId() {
        return prefs.getString("sender_id_pref",SENDER_ID);
    }

    public static String getPreferredMsisdn() {
        String msisdn = prefs.getString("msisdnku","");
        Log.d(TAG, "getPreferredMsisdn: " + msisdn);
        return msisdn;
    }

    public static String getPreferredEmail() {
        String email = prefs.getString("chat_email_id", email_arr.length==0 ? "" : email_arr[0]);
        Log.d(TAG, "getPreferredEmail: " + email);
        return email;
    }
    private List<String> getEmailList() {
        List<String> lst = new ArrayList<String>();
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                lst.add(account.name);
            }
        }
        return lst;
    }
    public static String getServerUrl() {
        return prefs.getString("server_url_pref", SERVER_URL);
    }
    public static String getMsisdnku() {

        String _p_msisdn = msisdnku;
        if(_p_msisdn != null){
            if (_p_msisdn.startsWith("62")) {
                _p_msisdn = _p_msisdn.substring(2);
            } else if (_p_msisdn.startsWith("08")) {
                _p_msisdn = _p_msisdn.substring(1);
            } else if (_p_msisdn.startsWith("+62")) {
                _p_msisdn = _p_msisdn.substring(3);
            }
//				_p_msisdn = "62"+_p_msisdn;

        }
        return _p_msisdn;
    }
    public static String genRandomString(){
        String pincode = "";
        String alphabet = "0123456789ABCDE";
        int N = alphabet.length();
        Random r = new Random();
        for(int i = 0; i<4 ;i++){
            pincode = pincode+alphabet.charAt(r.nextInt(N));
        }
        return pincode;
    }
    public static void setVercode(String vVercode){
       vercode = vVercode;
    }
    public static String getVercode(){
        return vercode;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG,"onSharedPreferenceChanged");

    }
}
