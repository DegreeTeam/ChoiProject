package com.example.audiotrack;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button startSound;
	Button endSound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startSound = (Button) this.findViewById(R.id.StartSound);
		startSound.setOnClickListener(this);

		endSound = (Button) this.findViewById(R.id.EndSound);
		endSound.setOnClickListener(this);

		endSound.setEnabled(false);
	}
    public void onResume(){
    	startServiceMethod();
    	super.onResume();
    }
	public void onClick(View v) {
		
	    Intent Service = new Intent(this, MainService.class);
	    
		if (v == startSound) {
			Service.putExtra("keepgoing", true);
			startService(Service);
		    endSound.setEnabled(true);
			startSound.setEnabled(false);
			
		} else if (v == endSound) {
	//		stopServiceMethod();

			Service.putExtra("keepgoing", false);
			startService(Service);
			endSound.setEnabled(false);
			startSound.setEnabled(true);
		}
	}
	public void startServiceMethod(){
		 Intent Service = new Intent(this, MainService.class);
	     startService(Service);
	}
	
	public void stopServiceMethod(){
	    Intent Service = new Intent(this, MainService.class);
	    stopService(Service);
	}
	public void onDestroy(){
		stopServiceMethod();
		super.onDestroy();
	}

}