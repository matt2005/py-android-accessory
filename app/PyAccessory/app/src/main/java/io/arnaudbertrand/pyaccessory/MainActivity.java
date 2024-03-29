package io.arnaudbertrand.pyaccessory;



import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbAccessory;

import com.arnaudbertrand.pyaccessory.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView mText;
    ParcelFileDescriptor mFileDescriptor;
    String mMessage;
    String mMessage2;
    String TAG = "DebugPy";

    UsbManager mUsbManager;
    UsbAccessory mAccessory;

    FileInputStream mInputStream;
    FileOutputStream mOutputStream;

    Runnable mUpdateUI = new Runnable() {
        @Override
        public void run() {
            mText.append(mMessage);
        }
    };

    Runnable mUpdateUI2 = new Runnable() {
        @Override
        public void run() {
            mText.append(mMessage2);
        }
    };

    /** Called when the user clicks the Decrease or Increase button */
    public void sendMessage(View view) {

        char direction = 0;

        switch (view.getId()) {
            case R.id.actuator_decrease:
                direction = '1';
                break;
            case R.id.actuator_increase:
                direction = '0';
                break;
        }

        byte[] buffer = new byte[5];
        buffer[0] = (byte) 'A';
        for (int i = 1; i < 5; i++) {
            buffer[i] = (byte) direction;
        }

        mMessage2 = "<<< ";
        try {
            mOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            mMessage2 += "Send error";
        }
        String msg = new String(buffer);
        mMessage2 += msg + System.getProperty("line.separator");
        mText.post(mUpdateUI2);

    }

    Runnable mListenerTask = new Runnable() {
        @Override
        public void run() {

            byte[] buffer = new byte[5];
            int ret;

            try {
                mMessage = ">>> ";
                ret = mInputStream.read(buffer);
                if (ret == 5) {
                    String msg = new String(buffer);
                    mMessage += msg;
                } else {
                    mMessage += "Read error";
                }

            } catch (IOException e) {
                e.printStackTrace();
                mMessage += "Read error";
            }

            mMessage += System.getProperty("line.separator");
            mText.post(mUpdateUI);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new Thread(this).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.display_area);
        mText.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

        if (mAccessory == null) {
            mText.append("Not started by the accessory directly" +
                    System.getProperty("line.separator"));
            return;
        }

        Log.v(TAG, mAccessory.toString());
        mFileDescriptor = mUsbManager.openAccessory(mAccessory);
        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
        }
        Log.v(TAG, mFileDescriptor.toString());
        new Thread(mListenerTask).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}