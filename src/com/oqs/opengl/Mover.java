package com.oqs.opengl;

import android.os.SystemClock;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity and bounce simulation.  The
 * sprites are jumbled with random velocities every once and a while.
 */
public class Mover implements Runnable {
	private Renderable[] mRenderables;
	private long mLastTime;

	static float SPEED_OF_GRAVITY = 150.0f;
	static final long JUMBLE_EVERYTHING_DELAY = 15 * 1000;
	static final float MAX_VELOCITY = 8000.0f;

	public Mover(int _screenHeight) {
		SPEED_OF_GRAVITY = (float) (0.9*_screenHeight);
	}

	public void run() {
		// Perform a single simulation step.
		if (mRenderables != null) {
			final long time = SystemClock.uptimeMillis();
			final long timeDelta = time - mLastTime;
			final float timeDeltaSeconds = 
					mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
					mLastTime = time;

					for (int x = 0; x < mRenderables.length; x++) {
						Renderable object = mRenderables[x];

						// Move.
						object.x = object.x + (object.velocityX * timeDeltaSeconds);
						object.y = object.y + (object.velocityY * timeDeltaSeconds);
						object.z = object.z + (object.velocityZ * timeDeltaSeconds);
						try{
							if(((GLAnim)object).getResourceName().equals("bullet")){
								object.x = object.x - (object.velocityX * timeDeltaSeconds);
								((GLBullet)object).updateBulletPos((int) (object.velocityX * timeDeltaSeconds));
							}
						}catch(Exception e){}

						// Apply Gravity.
						if(object.applyGravity){
							object.velocityY -= SPEED_OF_GRAVITY * timeDeltaSeconds;  
							if(object.velocityY<0)
								((GLAnim)object).activity.fall();
							if(object.y < OpenglActivity.GROUND_LEVEL)
								((GLAnim)object).activity.fallFinished();
						}
					}
		}

	}

	public void setRenderables(Renderable[] renderables) {
		mRenderables = renderables;
	}

}
