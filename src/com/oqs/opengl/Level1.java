package com.oqs.opengl;

import java.util.Timer;
import java.util.TimerTask;

import com.oqs.opengl.lib.FrameRateCounter;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Level1 extends Scene {

	private Timer _timer;
	private FrameRateCounter _frameRateCounter = null;

	@Override
	protected void onDestroy (){
		super.onDestroy();
		_timer.cancel();
	}

	@Override
	public void onStop(){
		super.onStop();
		_frameRateCounter.setFrameRateListener(null);
	}

	@Override
	public void onResume(){
		super.onResume();
		TextView fpsUI = (TextView) findViewById(R.id.fpsviewer);
		final fpsRun fpsRun = new fpsRun();
		fpsRun.fpsUI = fpsUI;
		_frameRateCounter = new FrameRateCounter();
		/*
		_frameRateCounter.setFrameRateListener(new FrameRateListener() {

			@Override
			public void frameRateUpdated(int framenb) {
				fpsRun._frameNB = framenb;
				runOnUiThread(fpsRun);
			}
		});
		 */
	}

	@Override
	public void init() {

		findViewById(R.id.jumpbtn).setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					_player.stopJump();
				}else{
					if(event.getAction() == MotionEvent.ACTION_DOWN)
						_player.jump();
				}
				return true;
			}
		});
		/*.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				_player.jump();
				Log.d("", "JUMP ARROUND");
				}		
		});
		*/
		findViewById(R.id.shootbtn).setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					_player.stopShoot();
				}else{
					if(event.getAction() == MotionEvent.ACTION_DOWN)
						_player.shoot();
				}
				return true;
			}
		});

		_timer = new Timer();
		_timer.schedule(new TimerTask() {

			@Override
			public void run() {
				final Soldier soldier = new Soldier(Level1.this);
				soldier.x = 1300;
				soldier.y = Constants.GROUND_LEVEL;
				soldier.setXVelocity(-0.35f-0.35f*Constants.LEVEL_SPEED);
				_enemies.add(soldier);	

				Constants.LEVEL_SPEED = Constants.LEVEL_SPEED*1.05;
			}
		}, 5000, 4000);		
	}

	private StringBuilder stgbuild = new StringBuilder();
	private static final String txtheader = "FPS: ";
	private class fpsRun implements Runnable{

		public int _frameNB=0;
		public TextView fpsUI;
		public void run() {
			stgbuild.setLength(0);
			fpsUI.setText(stgbuild.append(txtheader).append(_frameNB));	

		}
	}
	@Override
	public int getLayoutId() {
		return R.layout.glview;
	}
}