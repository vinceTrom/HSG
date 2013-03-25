package com.oqs.opengl;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;


public class GLSplash extends Scene{

	private Timer _timer;
	private SplashSoldier tanningSoldier;
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

				if(resourceName.equals(SplashSoldier.TANNING_ANIM_PATH))
					_handler.postDelayed(launchYawn, 2000);
				if(resourceName.equals(SplashSoldier.YAWNING_ANIM_PATH))
					_handler.postDelayed(launchDrink, 3000);

			}
		};
		tanningSoldier._playerState = SplashSoldier.TANN;
		tanningSoldier.x = 300;
		tanningSoldier.y = 300;
		/*
		SplashSoldier yawningSoldier = new SplashSoldier(this);
		yawningSoldier._playerState = SplashSoldier.YAWN;
		yawningSoldier.x = 500;
		yawningSoldier.y = 300;
		 */
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
		//_enemies.add(yawningSoldier);

		launchLogoAnim();
	}

	private Runnable launchYawn  = new Runnable() {
		@Override
		public void run() {
			tanningSoldier._playerState = SplashSoldier.YAWN;			
		}
	};

	private Runnable launchDrink  = new Runnable() {
		@Override
		public void run() {
			tanningSoldier._playerState = SplashSoldier.TANN;			
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
