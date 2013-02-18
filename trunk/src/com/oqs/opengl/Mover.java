package com.oqs.opengl;

import java.util.ArrayList;

import android.os.SystemClock;
import android.util.Log;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity and bounce simulation.  The
 * sprites are jumbled with random velocities every once and a while.
 */
public class Mover implements Runnable {

	private static long time = 0;
	private static long timeDelta = 0;
	private static double timeDeltaSeconds =0;

	private static int loop1index = 0;
	private static int loop2index = 0;
	private static int loop3index = 0;

	private static int renderNb = 0;
	private static int loop1Nb = 0;
	private static int bulletsNb = 0;
	private static int enemiesNb = 0;

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

	public double dmin = 1000;
	public double dmax = 0;
	public void run() {
		// Perform a single simulation step.
		if (_renderables != null) {
			synchronized(OpenglActivity.class){
				time = SystemClock.uptimeMillis();
				timeDelta = time - mLastTime;
				timeDeltaSeconds = 
						mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
						mLastTime = time;

						renderNb = _renderables.length;
						bulletsNb = _bulletList.size();
						enemiesNb = _enemies.size();
						loop1Nb =  renderNb + bulletsNb + enemiesNb;

						loop1index = 0;
						while( loop1index < loop1Nb) {
							Renderable object = null;
							if(loop1index<renderNb)
								object = _renderables[loop1index];
							else if(loop1index < renderNb + enemiesNb)
								object = _enemies.get(loop1index - renderNb);
							else 
								object = _bulletList.get(loop1index - renderNb - enemiesNb);
							//object = x<_renderables .length?_renderables[x]:_bulletList.get(x-_renderables.length);

							// Move.
							if(object.getClass() == GLLayerLoop.class){
								object.x = (float) (object.x + (object.velocityX * Constants.LEVEL_SPEED * timeDeltaSeconds));
								/*
								delta = Math.abs(delta);
								if(delta>0){
									dmin = Math.min(delta, dmin);
									dmax = Math.max(delta, dmax);
									Log.e("", "LEVEL dX:"+delta+ " in "+timeDeltaSeconds+"ms.min:"+dmin+" max:"+dmax);
								}
								 */
							}else
								object.x = (float) (object.x + (object.velocityX * timeDeltaSeconds));
							object.y = (float) (object.y + (object.velocityY * timeDeltaSeconds));
							object.z = (float) (object.z + (object.velocityZ * timeDeltaSeconds));

							// Apply Gravity.
							if(object.applyGravity){
								Player player = (Player)object;
								player.velocityY -= SPEED_OF_GRAVITY * timeDeltaSeconds;  
								if(player.velocityY<0  && (player._playerState == Player.JUMP || player._playerState == Player.JUMP_TWO))
									player.fall();
								if(player.y < Constants.GROUND_LEVEL && player._playerState == Player.FALL)
									player.fallFinished();
							}

							loop1index++;
						}

						loop2index = 0;
						while(loop2index<bulletsNb){
							if(_colisioner.testIfOutsideOfTheScreen(_bulletList.get(loop2index))){
								_bulletList.remove(_bulletList.get(loop2index));
							}else{
								//Test Colisions between bullet and enemies
								_colisioner.testColisionWithBulletAndEnemy(_bulletList.get(loop2index));
							}
							loop2index++;
						}

						loop3index = 0;
						while(loop3index<enemiesNb){
							if(!_enemies.isEmpty() && _colisioner.testIfOutsideOfTheScreen(_enemies.get(loop3index)))
								_enemies.remove(_enemies.get(loop3index));
							else
								loop3index++;
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
