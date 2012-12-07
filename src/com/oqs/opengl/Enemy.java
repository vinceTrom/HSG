package com.oqs.opengl;

import android.content.Context;
import android.util.Log;

public class Enemy extends Character{

	public Enemy(Context ctx) {
		super(ctx, "enemy/soldier.xml");
	}

	@Override
	protected void initAnims() {
		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("enemy/walk")){
				_sprites.get(i).textureHeight =(int) (0.5f*OpenglActivity._screenHeight);
				_sprites.get(i).x = (int) (3.6*OpenglActivity._screenHeight);
				_sprites.get(i).y = (int) (0.22*OpenglActivity._screenHeight);
				_sprites.get(i).setXVelocity(-0.7f);
				_sprites.get(i).mustDraw = true;
			}
		}
	}
	
	private void die(){
		
	}

	@Override
	public void isTouchedByBullet() {
Log.e("", "isTouchedByBullet");		
	}

}
