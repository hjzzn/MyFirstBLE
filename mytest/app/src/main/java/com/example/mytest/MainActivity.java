package com.example.mytest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BluetoothManager mbluetoothManager;

    private BluetoothAdapter mbluetoothAdapter;
    private BluetoothLeScanner mbluetoothLeScanner;
    private boolean scanning = false;

    // Stops scanning after 10 seconds.
    private final long SCAN_PERIOD = 10000;
    private Handler handler = new Handler();
    private LayoutInflater mInflator;
    final String TAG = "ZZN";

    //private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mbtnScan = findViewById(R.id.btnscan);
        BluetoothManager mbluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter mbluetoothAdapter = mbluetoothManager.getAdapter();
        if (mbluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "onCreate: bluetooth is not supported");

        }

        // Permission to get photo from gallery, gets permission and produce boolean
        ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            Log.e(TAG, "onActivityResult: PERMISSION GRANTED");
                            //如果允许，则开始扫描
                            scanLeDevice();
                        } else {
                            Log.e(TAG, "onActivityResult: PERMISSION DENIED");
                        }
                    }
                });

        mbtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick Scan button: ");
                mPermissionResult.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
        });
    }


    private void scanLeDevice() {

        mbluetoothLeScanner = mbluetoothAdapter.getBluetoothLeScanner();
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mbluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mbluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mbluetoothLeScanner.stopScan(leScanCallback);
        }
    }


    // Device scan callback.
    final private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d(TAG, "onScanResult: Scan is goning");
                    //leDeviceListAdapter.addDevice(result.getDevice());
                    //leDeviceListAdapter.notifyDataSetChanged();
                }
            };







}



