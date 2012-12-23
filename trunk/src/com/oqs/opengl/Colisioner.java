package com.oqs.opengl;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

public class Colisioner {

	private ArrayList<Enemy> _enemies = new ArrayList<Enemy>();
	public void testColisionWithBulletAndEnemy(Renderable bullet) {

		for(int i=0;i<_enemies.size();i++){
			//if(_enemies.get(i).mustDraw){
			//for(int j =0;j<posList.size();j++){
			Rect r = _enemies.get(i).getBoundRect();
			//Log.d("", "left:"+r.left+ " bot:"+r.bottom+" right:"+r.right+" top:"+r.top+" has "+posList.get(j)[0] + "  "+posList.get(j)[1]);
			if(_enemies.get(i).getBoundRect().contains((int)bullet.x, (int)bullet.y)){
				GLBullets.get()._bulletList.remove(bullet);
				_enemies.get(i).isTouchedByBullet();
			}
			//}
			//}
		}

	}

	public void addEnemies(ArrayList<Enemy> enemies) {//anims d ennemis qui cours
		_enemies = enemies;
	}

	public boolean testIfOutsideOfTheScreen(Renderable renderable) {
		return renderable.x-200>OpenglActivity._screenWidth || renderable.x+200<0;
		
	}

}
