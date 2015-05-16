package com.example.audiotcp;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

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

public class Singleton extends Application {
	private final static String					LOG_TAG				= "Singleton";
	private static 		Singleton				m_Instance;

	static	final		boolean					SET_DEBUG			= true;
	// Appscreen metrics
	public				float					m_fFrameS			= 0;	
	public				int						m_nFrameW			= 0,
												m_nFrameH			= 0,
												m_nTotalW			= 0,
												m_nTotalH			= 0,
												m_nPaddingX			= 0,
												m_nPaddingY			= 0;
		
	public Singleton() {
		super();
		m_Instance = this;
	}
	
	public static Singleton getInstance() {
		if(m_Instance == null) {
			synchronized(Singleton.class) {
				if(m_Instance == null) new Singleton();
			}
		}
		return m_Instance;
	}

	@Override public void onCreate() {
		super.onCreate();
	
	}
	
	
	public static void Debug(String tag, String message) {
		if (SET_DEBUG) {
			Log.d(tag, message);
		}
	}
	
	//must be called in every oncreate 
	public void InitGUIFrame(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		m_nTotalW = dm.widthPixels;
		m_nTotalH = dm.heightPixels;
		
		// scale factor
		m_fFrameS = (float)m_nTotalW / 1280.0f;
		// compute our frame
		m_nFrameW = m_nTotalW;
		m_nFrameH = (int) (960.0f * m_fFrameS);
		// compute padding for our frame inside the total screen size
		
		m_nPaddingY = 0;
		m_nPaddingX = (m_nTotalW - m_nFrameW) / 2;
		
		Debug(LOG_TAG, "InitGUIFrame: frame:"+m_nFrameW+"x"+m_nFrameH+ " Scale:"+m_fFrameS);
		
	}
	
	//  알거 같음 ㅋㅋ
	public int Scale(int v) {    //스케일 정하는 함수 !! <설정 할때>
		float s = (float)v * m_fFrameS; int rs = 0;
		if (s - (int)s >= 0.5) rs= ((int)s)+1; else rs= (int)s;
		return rs;
	}
	

	
}

