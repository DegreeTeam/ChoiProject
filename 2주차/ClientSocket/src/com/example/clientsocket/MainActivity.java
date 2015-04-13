package com.example.clientsocket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Client()).start(); 

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
}

 class Client implements Runnable  {
//	 public static final String SERVER_NAME = "10.10.2.183";
	 public static final String SERVER_NAME = "192.168.42.1";
	 public static final int SERVERPORT = 2007;
	 @Override
	 public void run() {
	  // TODO Auto-generated method stub
	        try {   
	            byte[] buf = ("Hello from Client\n\r").getBytes();
	           for(int i = 0; i < 5; i ++)
	            	UDPsendRecv(buf);
	            	
	        }catch (Exception e) {   
	            Log.e("UDP", "C: Error", e);   
	        } 
	 }
	 
	 public String UDPsendRecv(byte[] sendData) throws Exception
	 {
	  String recvData = null;
	  DatagramSocket socket = null ;
	  try {
	          socket = new DatagramSocket() ;
	          InetAddress host = InetAddress.getByName( SERVER_NAME );   
	          DatagramPacket packet = new DatagramPacket( sendData, sendData.length, host, SERVERPORT );
	
	          socket.send( packet ) ;

	          socket.setSoTimeout( 20000 ) ;
	          packet.setData( new byte[100] ) ;
	          socket.receive( packet ) ;
	          
	          recvData = new String(packet.getData()).trim(); // getData() 로만 끝내면 이상한 값이 찍힌다
	
	          Log.d("UDP", "C: Received: '" + recvData + "'");
	          
	          socket.close();
	  }catch (Exception e) {   
	   throw e;
	        } 
	  return recvData;
	 }


 }



