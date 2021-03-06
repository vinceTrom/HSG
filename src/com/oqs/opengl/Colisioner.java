package com.oqs.opengl;

import java.util.ArrayList;


public class Colisioner {

	private ArrayList<Soldier> _enemies = new ArrayList<Soldier>();
	public void testColisionWithBulletAndEnemy(Renderable bullet) {

		for(int i=0;i<_enemies.size();i++){
			if(_enemies.get(i).getBoundRect().contains((int)bullet.x, (int)bullet.y)){
				GLBullets.get()._bulletList.remove(bullet);
				_enemies.get(i).isTouchedByBullet();
			}
		}

	}

	public void addEnemies(ArrayList<Soldier> enemies) {//anims d ennemis qui cours
		_enemies = enemies;
	}

	public boolean testIfOutsideOfTheScreen(Renderable renderable) {
		return renderable.x-300>Level1._screenWidth || renderable.x+200<0;

	}

}
