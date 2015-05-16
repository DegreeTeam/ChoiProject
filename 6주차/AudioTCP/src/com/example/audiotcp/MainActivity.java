package com.example.audiotcp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity implements OnClickListener {

	ToggleButton playbtn;
	SharedPreferences mPref;
	SharedPreferences.Editor editor;
	Button leftbtn;
	Button rightbtn;
	TextView nameview;
	View btnView;
	Singleton m_Inst = Singleton.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Drawable play = getResources().getDrawable(R.drawable.play);
		play.setBounds(0, 0, play.getIntrinsicWidth(),
				play.getIntrinsicHeight());
		final Drawable stop = getResources().getDrawable(R.drawable.stop);
		stop.setBounds(0, 0, stop.getIntrinsicWidth(),
				stop.getIntrinsicHeight());
		final Drawable left = getResources().getDrawable(R.drawable.left);
		left.setBounds(0, 0, left.getIntrinsicWidth(),
				left.getIntrinsicHeight());
		final Drawable right = getResources().getDrawable(R.drawable.right);
		right.setBounds(0, 0, right.getIntrinsicWidth(),
				right.getIntrinsicHeight());
		final Drawable nameimg = getResources()
				.getDrawable(R.drawable.titleimg);
		right.setBounds(0, 0, right.getIntrinsicWidth(),
				right.getIntrinsicHeight());

		// 중간 title
		LinearLayout title = new LinearLayout(this);
		LinearLayout.LayoutParams tparam = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tparam.gravity = Gravity.CENTER_HORIZONTAL;
		title.setLayoutParams(tparam);
		nameview = new TextView(this);
		nameview.setBackground(nameimg);
		nameview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		title.addView(nameview);

		// 버튼 상단
		RelativeLayout btn = new RelativeLayout(this);

		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = mPref.edit();
		Boolean checked = mPref.getBoolean("check", false);

		playbtn = new ToggleButton(this);
		playbtn.setId(2);
		playbtn.setText("");

		if (checked)
			playbtn.setBackground(stop);
		else
			playbtn.setBackground(play);

		leftbtn = new Button(this);
		leftbtn.setBackground(left);
		leftbtn.setId(1);
		rightbtn = new Button(this);
		rightbtn.setBackground(right);
		rightbtn.setId(3);

		// Button 3
		RelativeLayout.LayoutParams middleButton = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		middleButton.addRule(RelativeLayout.CENTER_HORIZONTAL);
		playbtn.setLayoutParams(middleButton);

		RelativeLayout.LayoutParams leftButton = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		leftbtn.setLayoutParams(leftButton);
		leftButton.addRule(RelativeLayout.LEFT_OF, 2);

		RelativeLayout.LayoutParams rightButton = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rightbtn.setLayoutParams(rightButton);
		rightButton.addRule(RelativeLayout.RIGHT_OF, 2);

		btn.addView(playbtn);
		btn.addView(leftbtn);
		btn.addView(rightbtn);

		// circle
		m_Inst.InitGUIFrame(this);
		RelativeLayout circle = new RelativeLayout(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RoundKnobButton rv = new RoundKnobButton(this, R.drawable.circle, m_Inst.Scale(1200), m_Inst.Scale(1200));
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		circle.addView(rv, lp);
		
		// 전체 Linear

		LinearLayout whole = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		whole.setOrientation(LinearLayout.VERTICAL);
		whole.setLayoutParams(params);

		whole.addView(btn);
		whole.addView(title);
		whole.addView(circle);
		
		setContentView(whole);

		playbtn.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			Intent Service = new Intent(MainActivity.this, MainService.class);

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
					Service.putExtra("btn", "start");
					startService(Service);
					playbtn.setBackground(stop);
					playbtn.setTextOn("");
					editor.putBoolean("check", true);
				} else {
					Service.putExtra("btn", "stop");
					startService(Service);
					playbtn.setBackground(play);
					editor.putBoolean("check", false);
					playbtn.setTextOff("");
				}
				editor.commit();
				// TODO Auto-generated method stub
			}
		});
	}

	public void onResume() {
		super.onResume();
	}

	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		editor.clear();
		editor.commit();
		stopService(new Intent(MainActivity.this, MainService.class));
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

}