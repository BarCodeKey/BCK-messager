package app.security;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import barcodekey.bck_messager.R;


public class Main extends Activity {

    public final static String EXTRA_MESSAGE = "barcodekey.bck_messager.MESSAGE";
    IRemoteService mService = null;
    private Boolean mIsBind = false;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
    }

    public void encryptButton(View view) {

        EditText editText = (EditText) findViewById(R.id.message);

        String message = editText.getText().toString();
        if(mIsBind) {
            try {
                result = mService.encrypt(message.getBytes());
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }

            System.out.println("encrypt:  "+result);
        }
    }

    public void decryptButton(View view) {
        EditText editText = (EditText) findViewById(R.id.message);

        String message = editText.getText().toString();

        if(mIsBind) {
            try {
                result = mService.decrypt(message.getBytes());
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }
            System.out.println("decrypt:   "+result);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = IRemoteService.Stub.asInterface((IBinder)service);

            if(mService == null) {
                System.out.println("EI LÖYTYNYT");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mIsBind = false;
        }
    };

    public void bind() {
        Intent i = new Intent("app.security.RemoteService");
        mIsBind =  bindService(i,
                mConnection, Context.BIND_AUTO_CREATE);

    }
    public void unBind(){
        unbindService(mConnection);
        mIsBind = false;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
    }
    public String getResult(){
        return result;
    }
}
