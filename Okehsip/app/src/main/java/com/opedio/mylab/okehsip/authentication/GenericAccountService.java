package com.opedio.mylab.okehsip.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GenericAccountService extends Service {
    public GenericAccountService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

//        throw new UnsupportedOperationException("Not yet implemented");
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}
