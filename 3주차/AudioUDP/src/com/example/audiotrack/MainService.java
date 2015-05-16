package com.example.audiotrack;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service{
	private byte[] recvBuf = new byte[1024];
	public static final String SERVER_NAME = "0.0.0.0";
	public static final int SERVERPORT = 2007;
	SocketAddress socketAddr = new InetSocketAddress(SERVERPORT);
	Boolean KeepGoing = false;
	final int SAMPLE_RATE = 32768;
	int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT);
	AudioTrack audioTrack =new AudioTrack(AudioManager.STREAM_MUSIC,
			SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT, minSize, AudioTrack.MODE_STREAM);;
    @Override
    public void onCreate() { 	

    	Client c = new Client();
		new Thread(c).start();
		audioTrack.play();
        super.onCreate();
    }
    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
    	if(intent.getBooleanExtra("keepgoing", true)) {
    	 	Client c = new Client();
    		new Thread(c).start();
    		audioTrack.play();
    		KeepGoing = true;
    	} else {
    		audioTrack.stop();
    		KeepGoing = false;
    	}
        return  super.onStartCommand(intent, flags, startId);	
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
		audioTrack.stop();
		audioTrack.release();
        super.onDestroy();
    }
       
	class Client implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(KeepGoing)
					UDPsendRecv();
			} catch (Exception e) {
				Log.e("UDP", "C: Error", e);
			}
		}

		public void UDPsendRecv() throws Exception {

			DatagramSocket socket = null;
			try {
				if (socket == null) {
					socket = new DatagramSocket(SERVERPORT);
					socket.setBroadcast(true);
					socket.setReuseAddress(true);
				}

				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

				socket.receive(packet);
				audioTrack.write(recvBuf, 0, recvBuf.length);

				Log.d("UDP", "C: Received: " + recvBuf);

				socket.close();
			} catch (Exception e) {
				socket.close();
				throw e;
			}
		}
	}
}
