package com.opedio.mylab.okehsip.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opedio.mylab.okehsip.data.User;
import com.opedio.mylab.okehsip.OkehsipApplication;
import com.opedio.mylab.okehsip.R;
import com.opedio.mylab.okehsip.util.ServerGw;

import org.json.JSONObject;

public class SignupVerifingActivity extends AppCompatActivity implements ServerGw.MyListener{
    private final String TAG = "SignupVerifingActivity";
    TextView txtViewErrorMsg;
    TextView userEmail;
    static OkehsipApplication apps;
    private User user;
    private ServerGw vServerGw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_verifing);
        user = new User();
        vServerGw = ServerGw.newInstance(this);
        txtViewErrorMsg = (TextView) findViewById(R.id.error_msg);
        TextView accEmail = ((TextView) findViewById(R.id.accountName));
        userEmail = accEmail;
        if (getIntent().hasExtra(apps.KEY_MY_EMAIL)) accEmail.setText(getIntent().getStringExtra(apps.KEY_MY_EMAIL));
        else accEmail.setText(apps.getPreferredEmail());

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
                if(user.isParamOk2()) vServerGw.SubmitData();
                else SetAlertMessage(getResources().getString(R.string.ALERT_MSG_MANDATORY_PARAM));
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
//                if(userEmail.getText().toString() != null && !"".equalsIgnoreCase(userEmail.getText().toString()))
//                    i.putExtra(apps.KEY_MY_EMAIL, userEmail.getText().toString());
//                startActivity(i);
                finish();
            }
        });


    }
    private void collectData(){
        user = new User();
        user.setEmail(((TextView) findViewById(R.id.accountName)).getText().toString());
        user.setVerificationCode(((TextView) findViewById(R.id.accountVerifingCode)).getText().toString());
        txtViewErrorMsg.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ferify_code, menu);
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

    public void submit() {
        Log.d(TAG,"submit");
        final String verifingCode = user.getVerificationCode();
        final String userEmail = user.getEmail();


        new AsyncTask<String, Void, Intent>() {
            private ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(SignupVerifingActivity.this);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Submit data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected Intent doInBackground(String... params) {
                JSONObject jsonResp = vServerGw.accountVerifing(userEmail, verifingCode);
                Bundle data = new Bundle();
                if (jsonResp instanceof JSONObject) {
                    try {
                        String err_code = jsonResp.getString(apps.KEY_ERROR_CODE);
                        Log.d(TAG, "err_code:" + err_code);
                        Log.d(TAG, "jsonResp: " + jsonResp.toString());
                        //{err_code:"000",err_msg:"success",notif:"Account berhasil diverifikasi. Silahkan login. Terima kasih."}
                        data.putString(apps.KEY_SERVER_NOTIF, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                        if (!"000".equalsIgnoreCase(err_code))
                            data.putString(apps.KEY_ERROR_MESSAGE, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                    } catch (Exception e) {
                        data.putString(apps.KEY_ERROR_MESSAGE, getResources().getString(R.string.SYSTEM_ERROR_MSG));
                    }
                }
                else{
                    data.putString(apps.KEY_ERROR_MESSAGE, getResources().getString(R.string.SYSTEM_ERROR_MSG));
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(apps.KEY_ERROR_MESSAGE)) {
                    SetAlertMessage(intent.getStringExtra(apps.KEY_SERVER_NOTIF));
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_SERVER_NOTIF), Toast.LENGTH_SHORT).show();
                } else {
                    finishSubmit(intent);
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_SERVER_NOTIF), Toast.LENGTH_LONG).show();
                }
                pDialog.dismiss();
            }
        }.execute();
    }


    private void finishSubmit(Intent intent) {
        finish();
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
