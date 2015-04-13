package com.example.broadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  new Thread(new Client()).start(); 
        Client c = new Client();
        new Thread(c).start(); 
        TextView tv = (TextView)findViewById(R.id.text);
        tv.setText(c.str);
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
	 
	 public static final String SERVER_NAME = "0.0.0.0";
	 public static final int SERVERPORT = 2007;
	 public static String str = null;
	 @Override
	 public void run() {
	  // TODO Auto-generated method stub
	        try {   
	            	str = UDPsendRecv().toString();
	        }catch (Exception e) {   
	        		Log.e("UDP", "C: Error", e);   
	        } 
	 }
	 
	 public byte[] UDPsendRecv() throws Exception
	 {

	  byte[] recvBuf = null;
	  DatagramSocket socket = null ;
	  try {
		  		socket = new DatagramSocket(SERVERPORT, InetAddress.getByName(SERVER_NAME));
		  		socket.setBroadcast(true);
		  
		  		recvBuf = new byte[1024];
		     	DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
		     	socket.receive(packet);

		     	Log.d("UDP", "C: Received: '" + recvBuf + "'");
	            socket.close();
	  		}catch (Exception e) {   
		  		throw e;
	  		} 
		 return  recvBuf;
	 }
 }



