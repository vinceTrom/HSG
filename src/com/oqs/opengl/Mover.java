package com.oqs.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	private static float timeDeltaSeconds =0;

	private static int loop1index = 0;
	private static int loop2index = 0;

	private static int renderNb = 0;
	private static int loop1Nb = 0;
	private static int bulletsNb = 0;
	private static int enemiesNb = 0;

	private Renderable[] _renderables;
	private long mLastTime;
	private Colisioner _colisioner;
	private ArrayList<Soldier> _enemies;
	private ArrayList<Renderable> _bulletList;
	
	private static HashMap<Renderable, SpecialAction> _actions = new HashMap<Renderable, Mover.SpecialAction>();

	static float SPEED_OF_GRAVITY = 150.0f;

	public Mover(int _screenHeight) {
		_colisioner = new Colisioner();
		SPEED_OF_GRAVITY = (float) (10*_screenHeight);
		time = 0;
		timeDelta = 0;
		timeDeltaSeconds =0;

		loop1index = 0;
		loop2index = 0;

		renderNb = 0;
		loop1Nb = 0;
		bulletsNb = 0;
		enemiesNb = 0;
	}

	public double dmin = 1000;
	public double dmax = 0;
	public void run() {
		// Perform a single simulation step.
		if (_renderables != null) {
			time = SystemClock.uptimeMillis();
			timeDelta = time - mLastTime;
			timeDeltaSeconds = mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
			//timeDeltaSeconds = (float) 0.012;
			mLastTime = time;

			renderNb = _renderables.length;
			bulletsNb = _bulletList.size();
			enemiesNb = _enemies.size();
			loop1Nb =  renderNb + bulletsNb + enemiesNb;

			loop1index = 0;
			if(renderNb == 0)
				return;
			while( loop1index < loop1Nb) {
				Renderable object = null;
				if(loop1index<renderNb)
					object = _renderables[loop1index];
				else if(loop1index < renderNb + enemiesNb)
					object = _enemies.get(loop1index - renderNb);
				else 
					object = _bulletList.get(loop1index - renderNb - enemiesNb);

				// Move.
				if(object.getClass() == GLLayerLoop.class){
					object.x = (float) (object.x + (object.velocityX * Constants.LEVEL_SPEED * timeDeltaSeconds));
					//Log.d("", "object.x:"+(object.velocityX * Constants.LEVEL_SPEED * timeDeltaSeconds) +"with timeDeltaSeconds:"+timeDeltaSeconds);
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
				
				if(_actions.containsKey(object))
					if(_actions.get(object).isTriggered()){
						_actions.get(object).launchOnTrigger().run();
						_actions.remove(object);
					}

				loop1index++;
			}

			loop2index = 0;
			for(int i=0;i<_bulletList.size();i++ ){
				if(_colisioner.testIfOutsideOfTheScreen(_bulletList.get(i))){
					_bulletList.remove(_bulletList.get(i));
				}else{
					//Test Colisions between bullet and enemies
					_colisioner.testColisionWithBulletAndEnemy(_bulletList.get(i));
				}
			}

			for(int i=0;i<_enemies.size();i++ ){
				if(_colisioner.testIfOutsideOfTheScreen(_enemies.get(i)))
					_enemies.remove(_enemies.get(i));
			}


		}

	}

	public void setRenderables(Renderable[] renderables, ArrayList<Soldier> enemies,  ArrayList<Renderable> bulletList) {
		_bulletList = bulletList;
		_enemies = enemies;
		_renderables = renderables;
		for(int i=0;i<_renderables.length;i++){
			_colisioner.addEnemies(enemies);
		}
	}
	
	public static void addTrigger(Renderable renderable, SpecialAction action){
		_actions.put(renderable, action);
	}
	
	public interface SpecialAction{
		public boolean isTriggered();
		public Runnable launchOnTrigger();
	}

}
