package de.androidcrypto.bleandroidthingsserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import de.androidcrypto.bleandroidthingsserver.common.Ints;

public class MainActivity extends AppCompatActivity {

    // article: http://nilhcem.com/android-things/bluetooth-low-energy
    // code: https://github.com/Nilhcem/blefun-androidthings

    private AwesomenessCounter mAwesomenessCounter;
    private final LuckyCat mLuckyCat = new LuckyCat();
    private final GattServer mGattServer = new GattServer();

    /**
     * This block is for requesting permissions up to Android 12+
     *
     */

    private static final int PERMISSIONS_REQUEST_CODE = 191;
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestBlePermissions(this, PERMISSIONS_REQUEST_CODE);

        mAwesomenessCounter = new AwesomenessCounter(this);

        mLuckyCat.onCreate();
        mLuckyCat.updateCounter(mAwesomenessCounter.getCounterValue());

        mGattServer.onCreate(this, new GattServer.GattServerListener() {
            @Override
            public byte[] onCounterRead() {
                return Ints.toByteArray(mAwesomenessCounter.getCounterValue());
            }

            @Override
            public void onInteractorWritten() {
                int count = mAwesomenessCounter.incrementCounterValue();
                mLuckyCat.movePaw();
                mLuckyCat.updateCounter(count);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGattServer.onDestroy();
        mLuckyCat.onDestroy();
    }
}