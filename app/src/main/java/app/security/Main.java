package app.security;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import barcodekey.bck_messager.R;


public class Main extends Activity {

    public final static String EXTRA_MESSAGE = "barcodekey.bck_messager.MESSAGE";
    private static final int REQUEST_CODE_PICKER = 2;
    IRemoteService mService = null;
    private Boolean mIsBind = false;
    private byte[] result;
    private String type = "P-521";
    private String lookupKey = "";

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
                result = mService.encrypt(type,message.getBytes(),lookupKey);
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(getApplicationContext(),"encrypt: "+result,Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("encrypt:  " + new String(result));
        }
    }

    public void decryptButton(View view) {
        EditText editText = (EditText) findViewById(R.id.message);

        String message = editText.getText().toString();

        if(mIsBind) {
            try {
                result = mService.decrypt(type, message.getBytes(), lookupKey);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("kutsuttu Mainin onActivityResulttiin");
        System.out.println("requestCode: " + requestCode);
        System.out.println("resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICKER:
                    onActivityResultPicker(data);
                    break;
            }

        } else {
            // gracefully handle failure
            System.out.println("Warning: activity result not ok");
        }
    }

    private void onActivityResultPicker(Intent data) {
        Uri uri = data.getData();
        System.out.println("onActivityResultPicker saatu URI: " + uri);

        int idx;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        System.out.println("kursori");
        // Lets read the first row and only
        if (cursor.moveToFirst()) {
            System.out.println("kursorin eka rivi");
            idx = cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);
            this.lookupKey = cursor.getString(idx);

            idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String name = cursor.getString(idx);

            System.out.println("name: " + name);
            System.out.println("lookupkey: " + lookupKey);

            updatePickedContactLabel(name);
        }
    }

    private void updatePickedContactLabel(String name) {
        TextView textView = (TextView) findViewById(R.id.picked);
        textView.setText(name);
    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, REQUEST_CODE_PICKER);
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
