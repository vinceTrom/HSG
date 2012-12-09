package com.oqs.opengl;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

public class Colisioner {

	private ArrayList<GLAnim> _enemies = new ArrayList<GLAnim>();
	public void testColisionWithBulletAndEnemy(ArrayList<Integer[]> posList) {
		if (posList.isEmpty())
			return;
		for(int i=0;i<_enemies.size();i++)
			if(_enemies.get(i).mustDraw){
				for(int j =0;j<posList.size();j++){
					Rect r = _enemies.get(i).getBoundRect();
					//Log.d("", "left:"+r.left+ " bot:"+r.bottom+" right:"+r.right+" top:"+r.top+" has "+posList.get(j)[0] + "  "+posList.get(j)[1]);
					if(_enemies.get(i).getBoundRect().contains(posList.get(j)[0], posList.get(j)[1]))
						_enemies.get(i).getCharacter().isTouchedByBullet();
				}
			}
	}

	public void addEnemy(GLAnim enemy) {//anims d ennemis qui cours
		_enemies.add(enemy);
	}

}
