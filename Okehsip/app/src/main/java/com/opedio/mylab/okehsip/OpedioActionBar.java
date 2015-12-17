package com.opedio.mylab.okehsip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.opedio.mylab.okehsip.authentication.AccountGeneral;

/**
 * Created by 247 on 12/8/2015.
 */
public class OpedioActionBar extends AppCompatActivity {
    private boolean mInvalidate;
    private static final String TAG = "OpedioActionBar";
    private static final String STATE_DIALOG = "state_dialog";
    private static final String STATE_INVALIDATE = "state_invalidate";
    private AlertDialog mAlertDialog;
    private AccountManager mAccountManager;

    private Context context;
    private static OkehsipApplication apps;
//    private OkehsipApplication apps;

    public OpedioActionBar() {
        Log.d(TAG, "OpedioActionBar constructor");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        mAccountManager = AccountManager.get(this);
        showAccountPicker(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
    }

    /**
     * Show all the accounts registered on the account manager. Request an auth token upon user select.
     * @param authTokenType
     */
    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
        mInvalidate = invalidate;
        Account myPrefAcc = getPrefAccount();
       if(myPrefAcc != null){
//            String name[] = new String[availableAccounts.length];
//            for (int i = 0; i < availableAccounts.length; i++) {
//                name[i] = availableAccounts[i].name;
//            }

            if(invalidate)
                invalidateAuthToken(myPrefAcc, authTokenType);
            else
                getExistingAccountAuthToken(myPrefAcc, authTokenType);
        }
        else{
           finish();
       }
    }
    private Account getPrefAccount(){
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length == 0) {
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            return null;
        }
        else{
            return availableAccounts[0];
        }
    }
    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */

    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        Log.d(TAG, "getExistingAccountAuthToken");
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "getExistingAccountAuthToken future run");
                    Bundle bnd = future.getResult();
                    Log.d(TAG, "getExistingAccountAuthToken future getResult");
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    Log.d(TAG, "getExistingAccountAuthToken future getResult authtoken");
                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                    Log.d(TAG, "GetToken Bundle is " + bnd);

                    if(authtoken != null){
                        //success.
//                        startMainActivity();
                    }
                    else{

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                    finish();
                }

            }
        }).start();

    }
    /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    showMessage(account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }
    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    showMessage("Account was created");
                    Log.d(TAG, "AddNewAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());

                }
            }
        }, null);
        finish();
    }
    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void finishLogout(){
        Account myPrefAcc = getPrefAccount();
//        mAccountManager.clearPassword(myPrefAcc);
        if(myPrefAcc != null) mAccountManager.removeAccount(myPrefAcc,null,null);
        Intent i = new Intent(this, MainActivity.class);
//        i.putExtra("now_playing", now_playing);
//        i.putExtra("earned", earned);
        startActivity(i);
        // close this activity
        finish();
    }
   /* @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void removeAccountExp(Account myPrefAcc){
        mAccountManager.removeAccountExplicitly(myPrefAcc);
    }*/
}
