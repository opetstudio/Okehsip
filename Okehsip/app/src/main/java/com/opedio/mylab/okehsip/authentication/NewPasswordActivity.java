package com.opedio.mylab.okehsip.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class NewPasswordActivity extends AppCompatActivity implements ServerGw.MyListener{

    static OkehsipApplication apps;
    TextView txtViewErrorMsg;
    TextView accEmail;
    private User user;
    private ServerGw vServerGw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        user = new User();
        vServerGw = ServerGw.newInstance(this);
        txtViewErrorMsg = (TextView) findViewById(R.id.error_msg);
        ((TextView) findViewById(R.id.form_description)).setText(getResources().getString(R.string.activity_new_password_description));

        accEmail = ((TextView) findViewById(R.id.accountName));
        if (getIntent().hasExtra(apps.KEY_MY_EMAIL)) accEmail.setText(getIntent().getStringExtra(apps.KEY_MY_EMAIL));
        else accEmail.setText(apps.getPreferredEmail());

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                if(accEmail.getText().toString() != null && !"".equalsIgnoreCase(accEmail.getText().toString()))
                    i.putExtra(apps.KEY_MY_EMAIL, accEmail.getText().toString());
                startActivity(i);
                finish();
//                NetAsync(v);
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
                if(!user.isParamOk4()){
                    SetAlertMessage(getResources().getString(R.string.ALERT_MSG_MANDATORY_PARAM));
                }
                else if(!user.isConfPassOk()) {
                    SetAlertMessage(getResources().getString(R.string.ALERT_CONFPASS_NOTMATCH));
                }
                else{
                    vServerGw.SubmitData();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_password, menu);
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
        user.setVerificationCode(((TextView) findViewById(R.id.accountVerifingCode)).getText().toString());
        user.setPassword(((TextView) findViewById(R.id.accountNewPass)).getText().toString());
        user.setConfPassword(((TextView) findViewById(R.id.accountNewPassConf)).getText().toString());
        txtViewErrorMsg.setText("");
    }
    private void submit(){
        new AsyncTask<String, Void, Intent>() {
            private ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(NewPasswordActivity.this);
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Submit data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected Intent doInBackground(String... params) {
//                user.setEmail(((TextView) findViewById(R.id.accountName)).getText().toString());
//                user.setVerificationCode(((TextView) findViewById(R.id.accountVerifingCode)).getText().toString());
//                user.setPassword(((TextView) findViewById(R.id.accountNewPass)).getText().toString());
//                user.setConfPassword(((TextView) findViewById(R.id.accountNewPassConf)).getText().toString());
                Bundle data = new Bundle();
                final Intent res = new Intent();
                JSONObject jsonResp = vServerGw.createNewPasword(user.getEmail(), user.getVerificationCode(),user.getPassword(),user.getConfPassword(),apps.CONST_CLIENT);
                try {
                    data.putString(apps.KEY_SERVER_NOTIF, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                    data.putString(apps.KEY_ERROR_CODE, jsonResp.getString(apps.KEY_ERROR_CODE));
                    if(!"000".equalsIgnoreCase(jsonResp.getString(apps.KEY_ERROR_CODE)))
                        data.putString(apps.KEY_ERROR_MESSAGE, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                    //{err_code:"000",err_msg:"success",notif:"Berhasil update password"}
                }catch (Exception e){
//                    data.putString(apps.KEY_ERROR_MESSAGE, e.getMessage());
                    data.putString(apps.KEY_ERROR_MESSAGE, getResources().getString(R.string.SYSTEM_ERROR_MSG));
                }

                res.putExtras(data);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(apps.KEY_ERROR_MESSAGE)) {
                    SetAlertMessage(intent.getStringExtra(apps.KEY_SERVER_NOTIF));
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_SERVER_NOTIF), Toast.LENGTH_SHORT).show();
                } else{
//                    finishSubmit(intent);
                    Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_SERVER_NOTIF), Toast.LENGTH_LONG).show();
//                    SetSuccessAlertMessage(intent.getStringExtra(apps.KEY_SERVER_NOTIF));
                    finishSubmit();
                }

                pDialog.dismiss();
            }
        }.execute();
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
    public void SetSuccessAlertMessage(String msg) {
        txtViewErrorMsg.setTextColor(getResources().getColor(R.color.green01));
        txtViewErrorMsg.setText(msg);
    }
    private void finishSubmit(){
        finish();
    }
}
