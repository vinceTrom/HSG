package com.oqs.opengl;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;


public class GLSplash extends Scene{

	private Timer _timer;
	private SplashSoldier tanningSoldier;
	private SplashSoldier tanningSoldier2;
	private Handler _handler = new Handler();

	@Override
	public int getLayoutId() {
		return R.layout.glsplash;
	}

	@Override
	public void init() {

		_player.setVisibility(false);

		for(GLLayerLoop layer: _backsSprites)
			layer.velocityX = 0;

		for(GLLayerLoop layer: _foregroundSprites)
			layer.velocityX = 0;

		///////////////
		tanningSoldier = new SplashSoldier(this){
			@Override
			public void animFinished(String resourceName) {
				double random = Math.random();
				long delay = (long) (Math.random()*3000+2000); 
				if(random<0.4)
					_handler.postDelayed(launchDrink, delay);
				else
					_handler.postDelayed(launchYawn, delay);
			}
		};
		tanningSoldier._playerState = SplashSoldier.DRINK;
		tanningSoldier.x = _screenHeight*0.5f;
		tanningSoldier.y = _screenHeight*0.44f;
		
		tanningSoldier2 = new SplashSoldier(this){
			@Override
			public void animFinished(String resourceName) {
				double random = Math.random();
				long delay = (long) (Math.random()*3000+2000); 
				if(random<0.37)
					_handler.postDelayed(launchDrink2, delay);
				else
					_handler.postDelayed(launchYawn2, delay);
			}
		};
		tanningSoldier2._playerState = SplashSoldier.YAWN;
		tanningSoldier2.x = _screenHeight*1.52f;
		tanningSoldier2.y = _screenHeight*0.29f;
		
		
		SplashSoldier dancing3Soldier1 = new SplashSoldier(GLSplash.this);
		dancing3Soldier1._playerState = SplashSoldier.DANCE3;
		dancing3Soldier1.x = _screenHeight*0.45f;
		dancing3Soldier1.y = _screenHeight*0.2f;
		_enemies.add(dancing3Soldier1);
		
		
		SplashSoldier dancing3Soldier2 = new SplashSoldier(GLSplash.this);
		dancing3Soldier2._playerState = SplashSoldier.DANCE3;
		dancing3Soldier2.x = _screenHeight*1.3f;
		dancing3Soldier2.y = _screenHeight*0.47f;
		_enemies.add(dancing3Soldier2);
		
		SplashSoldier outhouseSoldier = new SplashSoldier(GLSplash.this);
		outhouseSoldier._playerState = SplashSoldier.OUTHOUSE;
		outhouseSoldier.x = _screenHeight*0.2f;
		outhouseSoldier.y = _screenHeight*0.2f;
		_enemies.add(outhouseSoldier);
		 
		
		_timer = new Timer();
		_timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				SplashSoldier bikingSoldier = new SplashSoldier(GLSplash.this);
				bikingSoldier._playerState = SplashSoldier.BIKE;
				bikingSoldier.x = _screenWidth;
				bikingSoldier.y = Constants.GROUND_LEVEL;
				bikingSoldier.setXVelocity(-0.35f);
				_enemies.add(bikingSoldier);
			}
		}, 1000, 10000);

		_enemies.add(tanningSoldier);
		_enemies.add(tanningSoldier2);

		launchLogoAnim();
	}

	private Runnable launchYawn  = new Runnable() {
		@Override
		public void run() {
			Log.d("","run: launchYawn1");
			tanningSoldier.getAnim(SplashSoldier.DRINKING_ANIM_PATH).initAnim(tanningSoldier);
			tanningSoldier.getAnim(SplashSoldier.YAWNING_ANIM_PATH).initAnim(tanningSoldier);
			tanningSoldier._playerState = SplashSoldier.YAWN;			
		}
	};
	
	private Runnable launchYawn2  = new Runnable() {
		@Override
		public void run() {
			Log.d("","run: launchYawn2");
			tanningSoldier2.getAnim(SplashSoldier.DRINKING_ANIM_PATH).initAnim(tanningSoldier2);
			tanningSoldier2.getAnim(SplashSoldier.YAWNING_ANIM_PATH).initAnim(tanningSoldier2);
			tanningSoldier2._playerState = SplashSoldier.YAWN;			
		}
	};

	private Runnable launchDrink  = new Runnable() {
		@Override
		public void run() {
			Log.d("","run: launchDrink1");
			tanningSoldier.getAnim(SplashSoldier.YAWNING_ANIM_PATH).initAnim(tanningSoldier);
			tanningSoldier.getAnim(SplashSoldier.DRINKING_ANIM_PATH).initAnim(tanningSoldier);
			tanningSoldier._playerState = SplashSoldier.DRINK;			
		}
	};
	
	private Runnable launchDrink2  = new Runnable() {
		@Override
		public void run() {
			Log.d("","run: launchDrink2");
			tanningSoldier2.getAnim(SplashSoldier.YAWNING_ANIM_PATH).initAnim(tanningSoldier2);
			tanningSoldier2.getAnim(SplashSoldier.DRINKING_ANIM_PATH).initAnim(tanningSoldier2);
			tanningSoldier2._playerState = SplashSoldier.DRINK;			
		}
	};

	private void launchLogoAnim(){
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				final View v = findViewById(R.id.gllogo);
				TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0);
				anim.setDuration(1200);
				anim.setFillAfter(true);
				anim.setInterpolator(new OvershootInterpolator(3.5f));
				anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						v.setVisibility(View.VISIBLE);			
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {}
				});
				v.startAnimation(anim);
				v.setVisibility(View.VISIBLE);
			}
		}, 3000);
	}

	@Override
	protected void onDestroy (){
		super.onDestroy();
		_timer.cancel();
	}

}
