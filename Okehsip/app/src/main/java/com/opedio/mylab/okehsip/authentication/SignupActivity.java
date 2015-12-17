package com.opedio.mylab.okehsip.authentication;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.opedio.mylab.okehsip.data.User;
import com.opedio.mylab.okehsip.OkehsipApplication;
import com.opedio.mylab.okehsip.R;
import com.opedio.mylab.okehsip.util.ServerGw;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity implements ServerGw.MyListener{

    private final String TAG = "SignupActivity";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public final static String PARAM_USER_PASS = "USER_PASS";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    TextView txtViewErrorMsg;


    private String mAuthTokenType;
    private AccountManager mAccountManager;
    private TextView userEmail;
    ServerGw vServerGw;
    static OkehsipApplication apps;

    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate");

        txtViewErrorMsg = (TextView) findViewById(R.id.error_msg);
        userEmail = (EditText) findViewById(R.id.accountName);

        mAccountManager = AccountManager.get(getBaseContext());
        vServerGw = ServerGw.newInstance(this);
        user = new User();


        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;


        if (getIntent().hasExtra(apps.KEY_MY_EMAIL)) userEmail.setText(getIntent().getStringExtra(apps.KEY_MY_EMAIL));
        else userEmail.setText(apps.getPreferredEmail());

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collect data
                collectData();
                //submit
                if(user.isParamOk()) vServerGw.SubmitData();
                else SetAlertMessage(getResources().getString(R.string.ALERT_MSG_MANDATORY_PARAM));
//                new NetCheck().execute();
            }
        });
        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignupVerifingActivity.class);
                if(userEmail.getText().toString() != null && !"".equalsIgnoreCase(userEmail.getText().toString()))
                    i.putExtra(apps.KEY_MY_EMAIL, userEmail.getText().toString());
                startActivity(i);
//                finish();
            }
        });


        /*final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length > 0) {
            ///finish
            Toast.makeText(getBaseContext(), "Only One Account is supported", Toast.LENGTH_SHORT).show();
            finish();
        }*/
    }
    private void collectData(){
        user = new User();
        user.setFullName(((TextView) findViewById(R.id.accountFullName)).getText().toString());
        user.setEmail(((TextView) findViewById(R.id.accountName)).getText().toString());
        user.setPassword(((TextView) findViewById(R.id.accountPassword)).getText().toString());
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
    public void submit() {
        Log.d(TAG,"submit");
        final String userName = user.getFullName();
        final String userEmail = user.getEmail();
        final String userPass = user.getPassword();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {
            private ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Submit data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected Intent doInBackground(String... params) {
                Log.d(TAG,"Started authenticating");
                apps.setVercode(apps.genRandomString());
                JSONObject jsonResp = vServerGw.userSignUp(userName, userEmail, userPass, apps.getVercode());
                Bundle data = new Bundle();
                if (jsonResp instanceof JSONObject){
                    try {
                        String err_code = jsonResp.getString(apps.KEY_ERROR_CODE);
                        Log.d(TAG,jsonResp.toString());
                        Log.d(TAG,"err_code: "+err_code);

                        //{err_code:"000",err_msg:"success",notif:"berhasil signup"}
//                    jsonResp = sServerAuthenticate.userSignUp(userName, userEmail, userPass, mAuthTokenType);
//                    String token = jsonResp.getString(apps.KEY_TOKEN);
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
//                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
//                    data.putString(AccountManager.KEY_AUTHTOKEN, token);

                        if(!"000".equalsIgnoreCase(err_code))
                            data.putString(apps.KEY_ERROR_MESSAGE, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                    }catch (Exception e){
//                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                        data.putString(apps.KEY_ERROR_MESSAGE,getResources().getString(R.string.SYSTEM_ERROR_MSG));
                    }
                }
                else{
                    data.putString(apps.KEY_ERROR_MESSAGE,getResources().getString(R.string.SYSTEM_ERROR_MSG));
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(apps.KEY_ERROR_MESSAGE)) {
                    SetAlertMessage(intent.getStringExtra(apps.KEY_ERROR_MESSAGE));
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                } else{
                    finishSubmit(intent);
                    pDialog.dismiss();
                }

            }
        }.execute();
    }

    private void finishSubmit(Intent intent) {
        Log.d(TAG, "finishSignup");
        /*String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        Log.d(TAG,"finishSignup > addAccountExplicitly");
        String authtokenType = mAuthTokenType;
        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        mAccountManager.addAccountExplicitly(account, accountPassword, null);
        mAccountManager.setAuthToken(account, authtokenType, authtoken);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);*/

        //start SplashScreenActivity
        Intent i = new Intent(this, SignupVerifingActivity.class);
        i.putExtra(apps.KEY_MY_EMAIL, user.getEmail());
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
}
