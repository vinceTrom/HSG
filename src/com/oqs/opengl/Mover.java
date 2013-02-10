package com.oqs.opengl;

import java.util.ArrayList;

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
	private ArrayList<Enemy> _enemies;
	private ArrayList<Renderable> _bulletList;

	static float SPEED_OF_GRAVITY = 150.0f;

	public Mover(OpenglActivity openglActivity, int _screenHeight) {
		_colisioner = new Colisioner();
		SPEED_OF_GRAVITY = (float) (0.9*_screenHeight);
	}

	public void run() {
		// Perform a single simulation step.
		if (_renderables != null) {
			synchronized(OpenglActivity.class){
				final long time = SystemClock.uptimeMillis();
				final long timeDelta = time - mLastTime;
				final float timeDeltaSeconds = 
						mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
						mLastTime = time;

						for (int x = 0; x < _renderables.length+_enemies.size()+_bulletList.size(); x++) {
							Renderable object = null;
							if(x<_renderables.length)
								object = _renderables[x];
							else if(x<_renderables.length+_enemies.size())
								object = _enemies.get(x-_renderables.length);
							else 
								object = _bulletList.get(x-_renderables.length-_enemies.size());
							//object = x<_renderables .length?_renderables[x]:_bulletList.get(x-_renderables.length);

							// Move.
							object.x = object.x + (object.velocityX * timeDeltaSeconds);
							object.y = object.y + (object.velocityY * timeDeltaSeconds);
							object.z = object.z + (object.velocityZ * timeDeltaSeconds);

							// Apply Gravity.
							if(object.applyGravity){
								Player player = (Player)object;
								player.velocityY -= SPEED_OF_GRAVITY * timeDeltaSeconds;  
								if(player.velocityY<0  && (player._playerState == Player.JUMP || player._playerState == Player.JUMP_TWO))
									player.fall();
								if(player.y < Constants.GROUND_LEVEL && player._playerState == Player.FALL)
									player.fallFinished();
							}
						}

						for(int i =0;i<_bulletList.size();i++){
							if(_colisioner.testIfOutsideOfTheScreen(_bulletList.get(i))){
								_bulletList.remove(_bulletList.get(i));
							}else{
								//Test Colisions between bullet and enemies
								_colisioner.testColisionWithBulletAndEnemy(_bulletList.get(i));
							}
						}

						for(int i =0;i<_enemies.size();i++){
							if(_colisioner.testIfOutsideOfTheScreen(_enemies.get(i)))
								_enemies.remove(_enemies.get(i));
						}
			}
		}

	}

	public void setRenderables(Renderable[] renderables, ArrayList<Enemy> enemies,  ArrayList<Renderable> bulletList) {
		_bulletList = bulletList;
		_enemies = enemies;
		_renderables = renderables;
		for(int i=0;i<_renderables.length;i++){
			_colisioner.addEnemies(enemies);
		}
	}

}
