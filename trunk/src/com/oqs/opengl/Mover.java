package com.oqs.opengl;

import android.os.SystemClock;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity and bounce simulation.  The
 * sprites are jumbled with random velocities every once and a while.
 */
public class Mover implements Runnable {
	private Renderable[] _renderables;
	private long mLastTime;
	private Colisioner _colisioner;
	private OpenglActivity _activity;

	static float SPEED_OF_GRAVITY = 150.0f;

	public Mover(OpenglActivity openglActivity, int _screenHeight) {
		_activity = openglActivity;
		_colisioner = new Colisioner();
		SPEED_OF_GRAVITY = (float) (0.9*_screenHeight);
	}

	public void run() {
		// Perform a single simulation step.
		if (_renderables != null) {
			final long time = SystemClock.uptimeMillis();
			final long timeDelta = time - mLastTime;
			final float timeDeltaSeconds = 
					mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
					mLastTime = time;

					for (int x = 0; x < _renderables.length; x++) {
						Renderable object = _renderables[x];

						// Move.
						object.x = object.x + (object.velocityX * timeDeltaSeconds);
						object.y = object.y + (object.velocityY * timeDeltaSeconds);
						object.z = object.z + (object.velocityZ * timeDeltaSeconds);
						try{
							if(((GLAnim)object).getResourceName().equals("bullet")){
								object.x = object.x - (object.velocityX * timeDeltaSeconds);
								((GLBullets)object).updateBulletsPos((int) (object.velocityX * timeDeltaSeconds));
								//Test Colisions between bullet and enemies
								_colisioner.testColisionWithBulletAndEnemy(((GLBullets) object).getPosList());
							}
						}catch(Exception e){e.printStackTrace();}
						
						

						// Apply Gravity.
						if(object.applyGravity){
							object.velocityY -= SPEED_OF_GRAVITY * timeDeltaSeconds;  
							if(object.velocityY<0  && ((GLAnim)object).getResourceName().equals("jump"))
								_activity.fall();
							if(object.y < Constants.GROUND_LEVEL && ((GLAnim)object).getResourceName().equals("fall"))
								_activity.fallFinished();
						}
					}
		}

	}

	public void setRenderables(Renderable[] renderables) {
		_renderables = renderables;
		for(int i=0;i<_renderables.length;i++){
			if(((GLAnim)_renderables[i]).getResourceName().equals("enemy/walk"))
			_colisioner.addEnemy((GLAnim)_renderables[i]);
		}
	}

}
