package com.oqs.opengl;

import android.content.Context;
import android.util.Log;

public class Enemy extends Character{

	private boolean _isdead = false;
	private static int _count = -1;

	public Enemy(Context ctx) {
		super(ctx, "enemy/soldier.xml");		
	}

	@Override
	protected void initAnims() {
		_count++;
		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("enemy/walk")){
				_sprites.get(i).textureHeight =(int) (0.5f*OpenglActivity._screenHeight);
				_sprites.get(i).x = (int) ((3.6+2*_count)*OpenglActivity._screenHeight);
				_sprites.get(i).y = (int) (0.22*OpenglActivity._screenHeight);
				_sprites.get(i).setXVelocity(-0.7f);
				_sprites.get(i).mustDraw = true;
			}
			if(_sprites.get(i).getResourceName().equals("enemy/die1") ||
					_sprites.get(i).getResourceName().equals("enemy/die2")
					||_sprites.get(i).getResourceName().equals("enemy/die3")){
				_sprites.get(i).loop = false;
			}
		}
	}

	private void die(){
		if(_isdead)
			return;
		_isdead = true;
		String dieAnim = "die"+((int)(1+Math.random()*3));
		Log.d("", "dieAnim:"+dieAnim);
		getAnim("enemy/walk").mustDraw = false;
		getAnim("enemy/"+dieAnim).mustDraw = true;
		getAnim("enemy/"+dieAnim).x = getAnim("enemy/walk").x;
		getAnim("enemy/"+dieAnim).y = getAnim("enemy/walk").y;
		getAnim("enemy/"+dieAnim).velocityX = getAnim("enemy/walk").velocityX;
	}

	@Override
	public void isTouchedByBullet() {
		Log.e("", "isTouchedByBullet");		
		die();
	}
	
}
