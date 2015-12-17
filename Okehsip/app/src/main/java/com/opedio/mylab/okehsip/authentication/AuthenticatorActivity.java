package com.opedio.mylab.okehsip.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opedio.mylab.okehsip.data.User;
import com.opedio.mylab.okehsip.MainActivity;
import com.opedio.mylab.okehsip.OkehsipApplication;
import com.opedio.mylab.okehsip.R;
import com.opedio.mylab.okehsip.util.ServerGw;

import org.json.JSONObject;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements ServerGw.MyListener {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

//    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
//    private String mAuthTokenType;

//    private Toolbar toolbar;
    static ActionBar actionBar = null;
    TextView txtViewErrorMsg;
    private User user;
    private ServerGw vServerGw;
    static OkehsipApplication apps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        user = new User();
        vServerGw = ServerGw.newInstance(this);

        Log.d(TAG,"onCreate");

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
       /* if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
//            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);

            actionBar = getSupportActionBar();
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Please Login");

        }*/

        txtViewErrorMsg = (TextView) findViewById(R.id.error_msg);

        mAccountManager = AccountManager.get(getBaseContext());
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
//        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        if (accountName != null) {
            ((TextView)findViewById(R.id.accountName)).setText(accountName);
        }


        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            //cek jumlah account dulu....skarang support 1 akun aja
            final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
            if (availableAccounts.length > 0) {
                ///finish
                Toast.makeText(getBaseContext(), "Only One Account is supported", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
                if(user.isParamOk3()) vServerGw.SubmitData();
                else SetAlertMessage(getResources().getString(R.string.ALERT_MSG_MANDATORY_PARAM));
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start MainActivity
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                i.putExtra("param1", "");
                startActivity(i);
            }
        });
        findViewById(R.id.btnAccVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start MainActivity
                Intent i = new Intent(getApplicationContext(), SignupVerifingActivity.class);
                i.putExtra("param1", "");
                startActivity(i);
            }
        });

        findViewById(R.id.btnForgotPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start MainActivity
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                i.putExtra("param1", "");
                startActivity(i);
            }
        });




    }
    public void submit() {
        Log.d(TAG,"submit");
        final String userName = user.getEmail();
        final String userPass = user.getPassword();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {
            private ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(AuthenticatorActivity.this);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Submit data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected Intent doInBackground(String... params) {
//                UserFunctions userFunction = new UserFunctions();
                Log.d(TAG,"Started authenticating");
//                String authtoken = null;
                Bundle data = new Bundle();
                JSONObject jsonResp = vServerGw.userSignIn(userName, userPass, user.getAuthTokenType(), apps.CONST_CLIENT);

                try {
                    String err_code = jsonResp.getString(apps.KEY_ERROR_CODE);
//                        authtoken = sServerAuthenticate.userSignIn(userName, userPass, user.getAuthTokenType());
//                    authtoken = jsonResp.getString(apps.KEY_AUTH_TOKEN);
                    Log.d(TAG,jsonResp.toString());
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, jsonResp.getString(apps.KEY_AUTH_TOKEN));
                        data.putString(PARAM_USER_PASS, userPass);
                        if(!"000".equalsIgnoreCase(err_code))
                            data.putString(apps.KEY_ERROR_MESSAGE, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                    } catch (Exception e) {
                        data.putString(apps.KEY_ERROR_MESSAGE, getResources().getString(R.string.SYSTEM_ERROR_MSG));
                    }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(apps.KEY_ERROR_MESSAGE)) {
                    txtViewErrorMsg.setTextColor(getResources().getColor(R.color.red01));
                    txtViewErrorMsg.setText(intent.getStringExtra(apps.KEY_ERROR_MESSAGE));
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
                pDialog.dismiss();
            }
        }.execute();

    }

    private void finishLogin(Intent intent) {
        Log.d(TAG, "finishLogin");
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG,"finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = user.getAuthTokenType();

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);

            //start MainActivity
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("now_playing", "");
            i.putExtra("earned", "");
            startActivity(i);
        } else {
            Log.d(TAG,"finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
//


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_authenticator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void collectData(){
        user = new User();
        user.setEmail(((TextView) findViewById(R.id.accountName)).getText().toString());
        user.setPassword(((TextView) findViewById(R.id.accountPassword)).getText().toString());
        user.setAuthTokenType(getIntent().getStringExtra(ARG_AUTH_TYPE));
        if(!(user.getAuthTokenType() != null && !"".equalsIgnoreCase(user.getAuthTokenType()))){
            //  mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
            user.setAuthTokenType(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
        }

        txtViewErrorMsg.setText("");
    }

    @Override
    public void SubmitData(Intent intent) {
        submit();
    }

    @Override
    public void SetAlertMessage(String msg) {
        txtViewErrorMsg.setTextColor(getResources().getColor(R.color.red01));
        txtViewErrorMsg.setText(msg);
    }


}
