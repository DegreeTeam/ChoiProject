package com.example.audiotcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	Boolean KeepGoing = false;
	final int SAMPLE_RATE = 32768;
	Socket sock;
	int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT);
	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT, minSize, AudioTrack.MODE_STREAM);;
	Client c = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		c = new Client();
		new Thread(c).start();
		audioTrack.play();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String str = intent.getStringExtra("btn");
		if (str.equals("start"))
			KeepGoing = true;
		else if (str.equals("stop"))
			KeepGoing = false;

		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {

		// TODO Auto-generated method stub
		audioTrack.release();
		try {
			sock.close();
			Log.i("false", "closesock");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	class Client implements Runnable {
		private byte[] datafile = new byte[32];
		public static final String SERVER_NAME = "192.168.42.1";
		protected static final int PORT = 2008;

		DataInputStream input;
		InetAddress serverAddr;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				serverAddr = InetAddress.getByName(SERVER_NAME);
				sock = new Socket(serverAddr, PORT);
				input = new DataInputStream(sock.getInputStream());
				while (true) {
						Recv();
	
				}
			} catch (Exception e) {
				Log.e("UDP", "C: Error", e);
			}
		}

		public void Recv() throws Exception {
			
			input.read(datafile);
			if(KeepGoing)
				audioTrack.write(datafile, 0, datafile.length);
			Arrays.fill(datafile, (byte)0);
			Log.d("tcp", datafile.toString());
		}
	}
}
