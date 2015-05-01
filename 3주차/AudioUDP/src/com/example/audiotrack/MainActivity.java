package com.example.audiotrack;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

import android.app.Activity;
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
	
	AudioSynthesisTask audioSynth;
	boolean keepGoing = false;
	private byte[] recvBuf;
	
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

	@Override
	public void onPause() {
		super.onPause();
		keepGoing = false;

		endSound.setEnabled(false);
		startSound.setEnabled(true);
	}

	public void onClick(View v) {
		if (v == startSound) {
			keepGoing = true;

			audioSynth = new AudioSynthesisTask();
			audioSynth.execute();

			endSound.setEnabled(true);
			startSound.setEnabled(false);
		} else if (v == endSound) {
			keepGoing = false;

			endSound.setEnabled(false);
			startSound.setEnabled(true);
		}
	}

	private class AudioSynthesisTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			final int SAMPLE_RATE = 44100;
			
			int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_8BIT);

		//	recvBuf = new byte[minSize*4];
			recvBuf = new byte[44];	
		    Client c = new Client();
		        new Thread(c).start(); 
		        
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_8BIT, minSize*4,
					AudioTrack.MODE_STREAM);

			audioTrack.play();
		
		while (keepGoing) {
					audioTrack.write(recvBuf , 0, recvBuf .length);
		//			Arrays.fill(recvBuf,(byte)0);
					String str = new String();
					for(int i=0; i<recvBuf.length;i++)
						str += recvBuf[i] + " ";
						
					Log.v("log", str);	
			}
		return null;
	}
	

	 }
	class Client implements Runnable  {
		 
		 public static final String SERVER_NAME = "0.0.0.0";
		 public static final int SERVERPORT = 2007;
		 SocketAddress socketAddr =new InetSocketAddress(SERVERPORT); 

		@Override
		 public void run() {
		  // TODO Auto-generated method stub
		        try {   
		        		while(true)
		        			UDPsendRecv();
		        }catch (Exception e) {   
		        		Log.e("UDP", "C: Error", e);   
		        } 
		 }
		 
		 public void UDPsendRecv() throws Exception
		 {

		  DatagramSocket socket = null ;
		  try {
			  		if(socket == null) {
			  			 socket = new DatagramSocket(SERVERPORT);
			   			 socket.setBroadcast(true);
			  			 socket.setReuseAddress(true);
			  		}
			     	DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			    	socket.receive(packet);
			
			     	Log.d("UDP", "C: Received: " + recvBuf);
			     
			     	byte[] by = packet.getData();
			     	String str = new String();
			     	for(int i=0;i<by.length;i++)
			     		str += by[i]+" ";
			     	Log.v("log", str);
		            socket.close();
		  		}catch (Exception e) {   
		  			socket.close();
			  		throw e;
		  		} 
		 }
	}
}