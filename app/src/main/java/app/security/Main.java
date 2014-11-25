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
import android.widget.Toast;

import java.util.List;

import barcodekey.bck_messager.R;


public class Main extends Activity {

    public final static String EXTRA_MESSAGE = "barcodekey.bck_messager.MESSAGE";
    IRemoteService mService = null;
    private Boolean mIsBind = false;
    private byte[] result;
    private String type = "P-521";
    private String uri = "";

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
                result = mService.encrypt(type,message.getBytes(),uri);
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(getApplicationContext(),"encrypt: "+result,Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("encrypt:  "+ new String(result));
        }
    }

    public void decryptButton(View view) {
        EditText editText = (EditText) findViewById(R.id.message);

        String message = editText.getText().toString();

        if(mIsBind) {
            try {
                result = mService.decrypt(type, message.getBytes(), uri);
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(getApplicationContext(),"decrypt: "+result,Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("decrypt:   "+new String(result));
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
        return new String(result);
    }
}
