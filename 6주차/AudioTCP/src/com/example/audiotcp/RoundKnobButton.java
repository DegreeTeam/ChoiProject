package com.example.audiotcp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/*
 File:              RoundKnobButton
 Version:           1.0.0
 Release Date:      November, 2013
 License:           GPL v2
 Description:	   A round knob button to control volume and toggle between two states

 ****************************************************************************
 Copyright (C) 2013 Radu Motisan  <radu.motisan@gmail.com>

 http://www.pocketmagic.net

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************/

public class RoundKnobButton extends RelativeLayout implements
		OnGestureListener {
	final String TAG = "Check Event";
	private Context context;
	private GestureDetector gestureDetector;
	private ImageView ivRotor;
	private Bitmap bmpRotorOn, bmpRotorOff;
	private boolean mState = true;
	private int m_nWidth = 0, m_nHeight = 0;

	interface RoundKnobButtonListener {
		public void onStateChange(boolean newstate);

		public void onRotate(int percentage);
	}

	private RoundKnobButtonListener m_listener;

	public void SetListener(RoundKnobButtonListener l) {
		m_listener = l;
	}

	public void SetState(boolean state) {
		mState = state;
		ivRotor.setImageBitmap(state ? bmpRotorOn : bmpRotorOff);
	}

	public RoundKnobButton(Context context, int rotoron, final int w,
			final int h) {
		super(context);
		// we won't wait for our size to be calculated, we'll just store out
		// fixed size
		this.context = context;
		m_nWidth = w;
		m_nHeight = h;
		// create stator
		// load rotor images
		Bitmap srcon = BitmapFactory.decodeResource(context.getResources(),
				rotoron);

		float scaleWidth = ((float) w) / srcon.getWidth();
		float scaleHeight = ((float) h) / srcon.getHeight();
		Log.i("flase", scaleWidth+"");
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		bmpRotorOn = Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(),
				srcon.getHeight(), matrix, true);
		
		// create rotor
		ivRotor = new ImageView(context);
		ivRotor.setImageBitmap(bmpRotorOn);

		RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(
				w, h);// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);

		addView(ivRotor, lp_ivKnob);
		// set initial state
		SetState(mState);
		// enable gesture detector
		gestureDetector = new GestureDetector(getContext(), this);
	}

	/**
	 * math..
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	// << 그림 그리기 >>
	private float cartesianToPolar(float x, float y) {
		Log.i(TAG, "carresianToPolar");
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}

	// << 그림 그리기 >>
	public void setRotorPosAngle(float deg) {
		Log.i(TAG, "setRotorPosAngle");
		if (deg >= 210 || deg <= 150) {
			if (deg > 180)
				deg = deg - 360;
			Matrix matrix = new Matrix();
			ivRotor.setScaleType(ScaleType.MATRIX);
			matrix.postRotate((float) deg, m_nWidth / 2, m_nHeight / 2);// getWidth()/2,
																		// getHeight()/2);
			ivRotor.setImageMatrix(matrix);
		}
	}

	// << 그림 그리기 >>
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.i(TAG, "onScroll");
		float x = e2.getX() / ((float) getWidth());
		float y = e2.getY() / ((float) getHeight());
		float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our
															// custom axis
															// direction

		if (!Float.isNaN(rotDegrees)) {
			// instead of getting 0-> 180, -180 0 , we go for 0 -> 360
			float posDegrees = rotDegrees;
			if (rotDegrees < 0)
				posDegrees = 360 + rotDegrees;

			// deny full rotation, start start and stop point, and get a linear
			// scale
			if (posDegrees > 210 || posDegrees < 150) {
				// rotate our imageview
				setRotorPosAngle(posDegrees);
				// get a linear scale
				float scaleDegrees = rotDegrees + 150; // given the current
														// parameters, we go
														// from 0 to 300
				// get position percent
				int percent = (int) (scaleDegrees / 3);
				if (m_listener != null)
					m_listener.onRotate(percent);
				return true; // consumed
			} else
				return false;
		} else
			return false; // not consumed
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Log.i(TAG,"onTouchEvent");

		// int action = event.getAction();
		// float x = event.getX();
		// float y = event.getY();
		//
		// switch (action) {
		// case MotionEvent.ACTION_UP:
		// // Log.i(TAG,"TOUCH_UP");
		// case MotionEvent.ACTION_DOWN:
		// // Log.i(TAG,"TOUCH_DOWN");
		// }

		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return super.onTouchEvent(event);
	}

	// mAngleDow 을 해야함
	public boolean onDown(MotionEvent event) {
		// Log.i(TAG,"onDown");
		// float x = event.getX() / ((float) getWidth());
		// float y = event.getY() / ((float) getHeight());
		// mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our
		// custom axis direction
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// Log.i(TAG,"onSingleTapup");
		// float x = e.getX() / ((float) getWidth());
		// float y = e.getY() / ((float) getHeight());
		// mAngleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom
		// axis direction

		// if we click up the same place where we clicked down, it's just a
		// button press
		// if (! Float.isNaN(mAngleDown) && ! Float.isNaN(mAngleUp) &&
		// Math.abs(mAngleUp-mAngleDown) < 10) {
		// SetState(!mState);
		// if (m_listener != null) m_listener.onStateChange(mState);
		// }
		return true;
	}

	// public void setRotorPercentage(int percentage) {
	// Log.i(TAG, "onRotorPercentage");
	// int posDegree = percentage * 3 - 150;
	// if (posDegree < 0)
	// posDegree = 360 + posDegree;
	// setRotorPosAngle(posDegree);
	// }

	public void onShowPress(MotionEvent e) {
		// 오래 눌렀을때 이벤트 발생 (onLongPress 보다 덜 길어도 가능)

	}

	// 방향 캐치
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// Log.i(TAG, "onFling");
		// Y값이 너무 많이 움직였으면 아무 것도 안함
		// if( Math.abs(arg0.getY() -arg1.getY()) >250){
		// return false;
		// }
		if (arg0.getX() - arg1.getX() > 120 && Math.abs(arg2) > 150) {
			Toast.makeText(context, "RIGHT -> LEFT", Toast.LENGTH_SHORT).show();
		} else if (arg1.getX() - arg0.getX() > 120 && Math.abs(arg2) > 150) {
			Toast.makeText(context, "LEFT -> RIGHT", Toast.LENGTH_SHORT).show();
		}

		return false;
	}

	public void onLongPress(MotionEvent e) {
		// 오래 눌렀을때 이벤트 발생
	}

}
