package barcodekey.bck_messager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class Main extends Activity {

    public final static String EXTRA_MESSAGE = "barcodekey.bck_messager.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void encrypt(View view) {
        Intent encryptAPI = new Intent("app.barcodekey");
        EditText editText = (EditText) findViewById(R.id.message);
        String message = editText.getText().toString();
        encryptAPI.putExtra(EXTRA_MESSAGE, message);
        sendBroadcast(encryptAPI);
    }

    public void decrypt(View view) {
        Intent decryptAPI = new Intent("app.barcodekey");
        EditText editText = (EditText) findViewById(R.id.message);
        String message = editText.getText().toString();
        decryptAPI.putExtra(EXTRA_MESSAGE, message);
        sendBroadcast(decryptAPI);
    }
}
