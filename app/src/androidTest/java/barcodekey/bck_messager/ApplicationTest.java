package barcodekey.bck_messager;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import app.security.Main;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<Main> {
    public ApplicationTest() {
        super(Main.class);
    }
    public void testEncrypt(){
        System.out.println("testaa");
    }
    public void testDecrypt(){
        System.out.println("testaa");
    }
}