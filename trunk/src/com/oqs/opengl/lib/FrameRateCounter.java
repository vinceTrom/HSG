package com.oqs.opengl.lib;

import java.util.Timer;
import java.util.TimerTask;

public class FrameRateCounter {

	public interface FrameRateListener{
		public void frameRateUpdated(int framenb);
	}

	private static final int PERIOD = 500;

	private static int _frameCount = 0;
	private Timer _timer = null;
	private FrameRateListener _listener;


	public void setFrameRateListener(FrameRateListener listener){
		_listener = listener;
		if(_listener != null){
			if(_timer == null){
				_timer = new Timer();
				_timer.schedule(new TimerTask() {		
					@Override
					public void run() {
						updateUI();			
					}
				},PERIOD, PERIOD);
			}
		}else{
			_timer.cancel();
			_timer = null;
		}
	}

	public static void incrementFrameCount(){
		_frameCount++;
	}

	public int getFrameCount(){
		return _frameCount;
	}

	private void updateUI(){
		if(_listener!= null)
			_listener.frameRateUpdated((int) (_frameCount*(1000f/PERIOD)));
		_frameCount = 0;
	}
}
