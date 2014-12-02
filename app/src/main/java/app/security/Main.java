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
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
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
    private String phoneNumber = "";

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            System.out.println("onServiceConnected, laitetaan mService!");
            mService = IRemoteService.Stub.asInterface((IBinder)service);

            if(mService == null) {
                System.out.println("EI LÖYTYNYT");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("mainin onServiceDisconnected");
            mService = null;
            mIsBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("mainin onCreate");
        if (mConnection == null) System.out.println("mConnection null");

        super.onCreate(savedInstanceState);
        if (mService == null) System.out.println("mService null");
        if (mConnection == null) System.out.println("mConnection null");

        setContentView(R.layout.activity_main);
        if (mService == null) System.out.println("mService null");
        if (mConnection == null) System.out.println("mConnection null");

        bind();
        if (mService == null) System.out.println("mService null");
        if (mConnection == null) System.out.println("mConnection null");

    }




    public void encryptButton(View view) {
        System.out.println("mainin encryptButton");

        EditText editText = (EditText) findViewById(R.id.message);

        String message = editText.getText().toString();
        System.out.println("encryptataan message: " + message);
        if(mIsBind) {
            System.out.println("on sitoutunut");
            if (mService == null){
                System.out.println("ÄLÄMÖLÖÖÖÖ");
            }
            try {
                result = mService.encrypt(type, message.getBytes(), lookupKey);
                System.out.println("saatiin tulos: " + result);
            } catch (RemoteException e) {
                System.out.println("EI ONNISTUNUT");
                e.printStackTrace();
            }
            System.out.println("toustataan");
            Toast toast = Toast.makeText(getApplicationContext(),"encrypt: "+result,Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("encrypt:  " + new String(result));
            updateMessageField(new String(result));
        }
    }

    public void decryptButton(View view) {
        System.out.println("mainin decryptButton");

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
            updateMessageField(new String(result));
        }
    }

    public void sendButton(View view) {
        EditText editText = (EditText) findViewById(R.id.message);
        if (phoneNumber != null && !phoneNumber.equals("")){
            String message = editText.getText().toString();
         //   sendSMSMessage(message);
        }
    }

    protected void sendSMSMessage(String message) {
        System.out.println("Send SMS");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    public void bind() {
        System.out.println("mainin bind");
        Intent i = new Intent("app.security.RemoteService");
        i.putExtra("package_name", "perunaohjelma");
        i.putExtra(Intent.EXTRA_UID, Binder.getCallingUid());

        mIsBind =  bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("bindaus: " + mIsBind);

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

            phoneNumber = readMimetypeData2(lookupKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

            System.out.println("name: " + name);
            System.out.println("lookupkey: " + lookupKey);

            updatePickedContactLabel(name + " " + phoneNumber);
        }
    }

    public String readMimetypeData2(String lookupKey, String mimetype){
        String value;
        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data.DATA1},
                ContactsContract.Data.LOOKUP_KEY + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{lookupKey,mimetype},
                null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    value = cursor.getString(0);
                    cursor.close();
                    return value;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    private void updatePickedContactLabel(String name) {
        TextView textView = (TextView) findViewById(R.id.picked);
        textView.setText(name);
    }

    private void updateMessageField(String message) {
        EditText editText = (EditText) findViewById(R.id.message);
        editText.setText(message);
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
