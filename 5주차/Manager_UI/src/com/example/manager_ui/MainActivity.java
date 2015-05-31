package com.example.manager_ui;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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
    BLEArr bldevice;
    private EditText edittext1,edittext2,edittext3,edittext4;
    private RadioButton rbutton1, rbutton2,rbutton3;
    String editstr;

    class BLEArr{
   
    	BluetoothDevice device;
    	int rssi;
    	String uuid; 
    	
    	void setdevice (BluetoothDevice device, int rssi, String uuid){
    		device = device;
    		rssi = rssi;
    		uuid = uuid;
    	}
    	String getUUid(){
    		return uuid;
    	}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	  mHandler = new Handler();
          setContentView(R.layout.activity_main);
          
          edittext1 =  (EditText) findViewById(R.id.editText1);
          edittext2 =  (EditText) findViewById(R.id.editText2);
          edittext3 =  (EditText) findViewById(R.id.editText3);
          edittext4 =  (EditText) findViewById(R.id.editText4);
          rbutton1 = (RadioButton) findViewById(R.id.radioButton1);
          rbutton2 = (RadioButton) findViewById(R.id.radioButton2);
          rbutton3 = (RadioButton) findViewById(R.id.radioButton3);
          
       
          
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
   
          scanLeDevice(true);
    }   
	public void onClick(View v) {
		editstr = new String();
		switch (v.getId()) {
		case R.id.button1: 
			editstr = "1" + edittext1.getText().toString();
		    new Thread(new Client(editstr)).start(); 
	
			break;
		case R.id.Button2:
			editstr = "2" + edittext2.getText().toString();
		    new Thread(new Client(editstr)).start(); 
	
			break;
		case R.id.Button3:
			editstr = "3" + edittext3.getText().toString();
		    new Thread(new Client(editstr)).start(); 
	      
			break;
		case R.id.Button4:
			editstr = "4" + edittext4.getText().toString();
		    new Thread(new Client(editstr)).start(); 

			break;
		case R.id.Button5:
			editstr = "5";
			if(rbutton1.isChecked())
				editstr += "1";
			else if (rbutton2.isChecked())
				editstr += "2";
			else
				editstr += "3";	
		    new Thread(new Client(editstr)).start(); 
			break;

		}
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
                	connectwifi(uuid);
                	bldevice = new BLEArr();
                    bldevice.setdevice(device, Rssi, uuid);
                    
                    //Log.i("device",device.getName());
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
         String uuid =new String();
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
    void connectwifi(String uuid){
    	Log.i("bleuuid", uuid);
    	// 해당 AP로 연결하기 
    	String ssid = "degree123";
    	String password = "degree123";
    	
    	WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo winfo = manager.getConnectionInfo(); 
    	
    	String getSSID = winfo.getSSID(); // WifiInfo 에서 받아온 SSID정보
		boolean isConfigured = false;
		int networkId = -1;
		boolean wifiEnable = manager.isWifiEnabled(); // WIFI status

		if (wifiEnable == false) // WIFI on
		{
			manager.setWifiEnabled(true);
			// wifi 0n and 2.0 second wait
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (ssid != getSSID) {
			List<WifiConfiguration> configList = manager
					.getConfiguredNetworks(); // WIFI 정보
			// 설정된 wifiConfig 불러와서 있으면 network id를 가져옴
			for (WifiConfiguration wifiConfig : configList) {
				String str = wifiConfig.SSID;
				if (wifiConfig.SSID.equals("\"".concat(ssid).concat("\""))) {
					Log.d("wifi", "SSID : " + str);
					networkId = wifiConfig.networkId;
					isConfigured = true;
					break;
				}
			}
		
		}

	if (!isConfigured) {
			WifiConfiguration wfc = new WifiConfiguration();

			wfc.SSID = "\"".concat(ssid).concat("\"");
			wfc.status = WifiConfiguration.Status.DISABLED;
			wfc.priority = 40;

			// WPA/WPA2
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wfc.preSharedKey = "\"".concat(password).concat("\"");

			// wfc 값으로 설정하여 추가합니다.
			networkId = manager.addNetwork(wfc);
			// 설정한 값을 저장합니다.
			manager.saveConfiguration();
		}
		manager.enableNetwork(networkId, true);
	}
	class Client implements Runnable {
		
		protected static final String SERVIP = "192.168.42.1"; // server ip
		protected static final int PORT = 2012;
		String sendstr= new String();

		Client(String str){
			sendstr = str;
		}
		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
		        Toast.makeText(getApplicationContext(), "OK", 0).show();
			super.handleMessage(msg);
		}};
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Log.d("TCP", "server connecting");
				InetAddress serverAddr = InetAddress.getByName(SERVIP);
				Socket sock = new Socket(serverAddr, PORT);
				DataOutputStream output = new DataOutputStream(sock.getOutputStream());
				byte[] datafile = new byte[32];
			    datafile =sendstr.getBytes(); 
			    output.write(datafile);
		        sock.close();
		        handler.sendEmptyMessage(0);
			} catch (Exception e) {
				Log.e("TCP", "C: Error", e);
			}
		}
	
	}
}
