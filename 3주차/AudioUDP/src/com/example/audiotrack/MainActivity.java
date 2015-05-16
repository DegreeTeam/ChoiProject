package com.example.audiotrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	     startService(new Intent(this, MainService.class));
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

			Service.putExtra("keepgoing", false);
			startService(Service);
			endSound.setEnabled(false);
			startSound.setEnabled(true);
		}
	}
	
	public void onDestroy(){
	    stopService(new Intent(this, MainService.class));
		super.onDestroy();
	}

}