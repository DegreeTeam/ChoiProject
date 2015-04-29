package com.example.manager_ui;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler; 
    private ArrayList<BLEArr> mLeDevices;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private WifiManager wManager = null;
    
    class BLEArr{
    	BluetoothDevice device;
    	int rssi;
    	String uuid;
    	void setdevice(	BluetoothDevice device, int rssi, String uuid){
    		device = device;
    		rssi = rssi;
    		uuid = uuid;
    	}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	  mHandler = new Handler();
          setContentView(R.layout.activity_main);
          wManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
          wManager.setWifiEnabled(true);
          connectwifi();
          if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
              Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
              finish();
          }

          final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
          	mBluetoothAdapter = bluetoothManager.getAdapter();
          	mBluetoothAdapter.enable();

          if (mBluetoothAdapter == null) {
              Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
              finish();
              return;
          }
          setContentView(R.layout.activity_main);
          scanLeDevice(true);
    }   
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			break;
		case R.id.button2:
			break;
		case R.id.button3:
			break;
		case R.id.button4:
			break;
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mLeDevices = new ArrayList<BLEArr>();
        scanLeDevice(true);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDevices.clear();
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
 	
         	final String uuid = getUid(device,scanRecord);
         	final int Rssi = rssi;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	BLEArr bldevice = new BLEArr();
                    bldevice.setdevice(device, Rssi, uuid);
                    Log.i("BLEArr",uuid);
                }
            });
      
        }
    };
    
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    String getUid (BluetoothDevice device, byte[] scanRecord){
    	int startByte = 2;
        boolean patternFound = false;
        String uuid =new String();;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }
        
        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);
            
            //Here is your UUID
             uuid =  hexString.substring(0,8) + "-" + 
                    hexString.substring(8,12) + "-" + 
                    hexString.substring(12,16) + "-" + 
                    hexString.substring(16,20) + "-" + 
                    hexString.substring(20,32);
        
            //Here is your Major value
            int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);
            //Here is your Minor value
            int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
        }
		return uuid;
    }
    void connectwifi(){
    	String ssid = "GBSSM";
    	// 신호가 가장 쎈 bluetooth uuid 가져오기 
    	
    	WifiConfiguration wfc = new WifiConfiguration();
    	wfc.SSID = "\"".concat(ssid).concat("\"");
    	wfc.status = WifiConfiguration.Status.DISABLED;
    	wfc.priority = 40;

    	wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    	wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
    	wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
    	wfc.allowedAuthAlgorithms.clear();
    	wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
    	wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
    	wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
    	wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
    	wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

    	int networkId = wManager.addNetwork(wfc);
    	if (networkId != -1) {
    			wManager.enableNetwork(networkId, true);
    		}
    }
}

