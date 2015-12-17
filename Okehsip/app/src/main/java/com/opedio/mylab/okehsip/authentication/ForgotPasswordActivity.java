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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.opedio.mylab.okehsip.data.User;
import com.opedio.mylab.okehsip.OkehsipApplication;
import com.opedio.mylab.okehsip.R;
import com.opedio.mylab.okehsip.util.ServerGw;

import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity implements ServerGw.MyListener {
    private final static String TAG = "ForgetPasswordActivity";
    TextView txtViewErrorMsg;
    EditText userEmail;
    static OkehsipApplication apps;
    TextView accEmail;
    ServerGw vServerGw;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        user = new User();
        txtViewErrorMsg = (TextView) findViewById(R.id.error_msg);
        userEmail = (EditText) findViewById(R.id.accountName);

        accEmail = userEmail;
        if (getIntent().hasExtra(apps.KEY_MY_EMAIL)) accEmail.setText(getIntent().getStringExtra(apps.KEY_MY_EMAIL));
        else accEmail.setText(apps.getPreferredEmail());

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
                Intent i = new Intent(getApplicationContext(), NewPasswordActivity.class);
                if (user.isParamEmailOk()) i.putExtra(apps.KEY_MY_EMAIL, user.getEmail());
                startActivity(i);
                finish();
            }
        });

        vServerGw = ServerGw.newInstance(this);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
                if (user.isParamEmailOk()) vServerGw.SubmitData();
                else SetAlertMessage(getResources().getString(R.string.ALERT_MSG_MANDATORY_PARAM));
            }
        });
    }
    private void collectData(){
        user = new User();
        user.setEmail(((TextView) findViewById(R.id.accountName)).getText().toString());
        txtViewErrorMsg.setText("");
    }
    @Override
    public void SubmitData(Intent intent) {
        new ProcessSubmitData().execute();
    }
    @Override
    public void SetAlertMessage(String msg) {
        txtViewErrorMsg.setTextColor(getResources().getColor(R.color.red01));
        txtViewErrorMsg.setText(msg);
    }
    private class ProcessSubmitData extends AsyncTask<String, String, Intent> {
        private ProgressDialog pDialog;
        final String userEmail = user.getEmail();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Submit data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected Intent doInBackground(String... args) {
            Bundle data = new Bundle();
            apps.setVercode(apps.genRandomString());
            JSONObject jsonResp = vServerGw.forgotPasword(userEmail,apps.getVercode());
            try {
                //{err_code:"000",err_msg:"Success",notif:"Data berhasil di submit.",code:"dfafdsafdf",token:"dfafdsafdf"}
//                String token = jsonResp.getString(apps.KEY_TOKEN);
//                data.putString(apps.KEY_TOKEN, token);
                Log.d(TAG, jsonResp.toString());
                data.putString(apps.KEY_SERVER_NOTIF, jsonResp.getString(apps.KEY_SERVER_NOTIF));
                if(!"000".equalsIgnoreCase(jsonResp.getString(apps.KEY_ERROR_CODE)))
                    data.putString(apps.KEY_ERROR_MESSAGE, jsonResp.getString(apps.KEY_SERVER_NOTIF));
            }catch (Exception e){
//                data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                data.putString(apps.KEY_ERROR_MESSAGE,getResources().getString(R.string.SYSTEM_ERROR_MSG));
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
                pDialog.dismiss();
            }
            else{
                Toast.makeText(getBaseContext(), intent.getStringExtra(apps.KEY_SERVER_NOTIF), Toast.LENGTH_LONG).show();
                finishSubmit(intent);
                pDialog.dismiss();
            }

        }
    }
    private void finishSubmit(Intent intent){
        Intent i = new Intent(this, NewPasswordActivity.class);
        i.putExtra(apps.KEY_MY_EMAIL, user.getEmail());
        startActivity(i);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forget_password, menu);
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
