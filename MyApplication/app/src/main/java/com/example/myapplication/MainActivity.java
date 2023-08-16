package com.example.myapplication;

//edit in github test
//edit in android studio
//edit in android studio in home
//edit in office
//edit by github 2023-08-08 8:47

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    final String TAG = "ZZN";
    private final ActivityResultLauncher<String> mactivityresultlauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                Toast.makeText(MainActivity.this, "required ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
    private BluetoothManager mbluetoothmanager;
    BluetoothAdapter mbluetoothadapter;
    BluetoothLeScanner mbluetoothLeScanner;
    Handler mhandler;
    Button btnScan;
    ListView mlvbtdev;
    mybtArrayAdapter mmybtarrayadapter;
    ArrayList<BluetoothDevice> marraylistbtdev;
    BluetoothDevice mserverisconnected ;
    private boolean scanning = false;
    private Context mct = MainActivity.this;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This function is called when the user accepts or decline the permission.
        // Request Code is used to check which permission called this function.
        // This request code is provided when the user is prompt for permission.

        btnScan = findViewById(R.id.bntscan);
        mlvbtdev = findViewById(R.id.lvbtdev);
        mlvbtdev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mserverisconnected = marraylistbtdev.get(i)  ;
                if (connecttoserver(mserverisconnected)) {

                }


                //Toast.makeText(mct, i + ":" + marraylistbtdev.get(i).getAddress(), Toast.LENGTH_SHORT).show();
            }
        });


        marraylistbtdev = new ArrayList<>();

        btnScan.setOnClickListener(view -> {
            requirepermission();
            scanLeDevice();
            Toast.makeText(MainActivity.this, "开始扫描附近的蓝牙设备", Toast.LENGTH_SHORT).show();
        });

    }

    ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice mbtdev;
            super.onScanResult(callbackType, result);
            //Log.e(TAG, "onScanResult: " );
            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                mbtdev = result.getDevice();
                if (addbtdev(mbtdev)) {
                    mmybtarrayadapter = new mybtArrayAdapter(mct, 0, marraylistbtdev);
                    mlvbtdev.setAdapter(mmybtarrayadapter);
                    String strAddr = mbtdev.getAddress();
                    //Toast.makeText(MainActivity.this, "Found device: " + "Addr:" + strAddr, Toast.LENGTH_LONG).show();
                }

            }
        }
    };


    private final BluetoothGattCallback mybluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                //Toast.makeText(mct,"connected", Toast.LENGTH_SHORT).show();
                MainActivity.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Coonected", Toast.LENGTH_LONG).show();
                    }
                }) ;
                BluetoothGatt locgatt =gatt ;
                locgatt.discoverServices() ;

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt,int status){

                Log.d(TAG,"onServicesDiscovered") ;
            List<BluetoothGattService> listservice = gatt.getServices();
            for ( BluetoothGattService service : listservice) {
                List<BluetoothGattCharacteristic> listcharacteristic = service.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic:listcharacteristic){
                    UUID locuuid = characteristic.getUuid();
                    Log.d(TAG, locuuid.toString()) ;
                }
            }
        }

    } ;

    private boolean connecttoserver(BluetoothDevice parabledevice ) {

        BluetoothDevice the_device = parabledevice ;
        the_device.connectGatt(mct,false,mybluetoothGattCallback) ;
       return true ;
    }




    private void requirepermission() {

        mct = this;
        if (mct.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mct, "BLUETOOTH PERMISSION is GRANTED", Toast.LENGTH_SHORT).show();
//             Log.e(TAG, "BLUETOOTH :PERMISSION_GRANTED  ");
        } else {
            mactivityresultlauncher.launch(Manifest.permission.BLUETOOTH);
        }

        if (mct.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mct, "ACCESS_FINE_LOCATION PERMISSION is GRANTED", Toast.LENGTH_SHORT).show();
            //           Log.e(TAG, "BLUETOOTH :PERMISSION_GRANTED  ");
        } else {
            mactivityresultlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (mct.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mct, "BLUETOOTH_ADMIN PERMISSION is GRANTED", Toast.LENGTH_SHORT).show();
            //           Log.e(TAG, "BLUETOOTH :PERMISSION_GRANTED  ");
        } else {
            mactivityresultlauncher.launch(Manifest.permission.BLUETOOTH_ADMIN);
        }


    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {

        mct = MainActivity.this;
        mbluetoothmanager = (BluetoothManager) mct.getSystemService(Context.BLUETOOTH_SERVICE);

        mbluetoothadapter = mbluetoothmanager.getAdapter();
        mbluetoothLeScanner = mbluetoothadapter.getBluetoothLeScanner();


        mhandler = new Handler();

        if (!scanning) {
            // Stops scanning after a predefined scan period.
            mhandler.postDelayed(() -> {
                scanning = false;
                mbluetoothLeScanner.stopScan(leScanCallback);
                //btnScan.setText("开始扫描");
                btnScan.setEnabled(true);
               // Toast.makeText(mct, "SCAN STOPPED", Toast.LENGTH_SHORT).show();
            }, SCAN_PERIOD);
            scanning = true;
            mbluetoothLeScanner.startScan(leScanCallback);
            //btnScan.setText("停止扫描");
            btnScan.setEnabled(false);
        } else {
            scanning = false;
            mbluetoothLeScanner.stopScan(leScanCallback);
            //btnScan.setText("开始扫描");
            btnScan.setEnabled(true);

            //Toast.makeText(mct, "SCAN STOPPED", Toast.LENGTH_SHORT).show();
        }
    }

    boolean addbtdev(BluetoothDevice parambtdev) {

        boolean retval = false;
        if (parambtdev != null) {
            if (!marraylistbtdev.contains(parambtdev)) {
                marraylistbtdev.add(parambtdev);
                retval = true;
            }
        }
        return retval;
    }
}
