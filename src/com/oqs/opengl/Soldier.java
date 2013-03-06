package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

public class Soldier extends Character{

	public static final int WALK = 1;
	public static final int DIE1 = 2;
	public static final int DIE2 = 5;
	public static final int DIE3 = 3;

	private boolean _isdead = false;
	private static final String FILENAME = "soldier/soldier.xml";

	public Soldier(Context ctx) {
		super(ctx, FILENAME);	
		_playerState = WALK;
	}

	@Override
	public Rect getBoundRect() {
		/*
		GLAnim anim = null;

		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("enemy/walk")){
				anim = _sprites.get(i);
			}
		}
		 */
		GLAnim anim = getCurrentAnim();
		int width =0;
		int height = 0;

		try{
			width = anim.getFrames().get(_state.get(anim.getResourceName()).currentindex).width;
			height = anim.getFrames().get(_state.get(anim.getResourceName()).currentindex).height;
		}catch(Exception e){}
		return new Rect((int)x,(int) y, (int)(x+width),(int)(y+height));
	}

	@Override
	protected void initAnims() {
		x = 1300;//(int) ((1*_count)*OpenglActivity._screenHeight);
		//Log.e("", "new enemy: "+x);
		y = Constants.GROUND_LEVEL;
		setXVelocity(-0.35f-0.35f*Constants.LEVEL_SPEED);
		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("soldier/walk")){
				_sprites.get(i).textureHeight =(int) (0.5f*OpenglActivity._screenHeight);
			}
			if(_sprites.get(i).getResourceName().equals("soldier/die1") ||
					_sprites.get(i).getResourceName().equals("soldier/die2")
					||_sprites.get(i).getResourceName().equals("soldier/die3")){
				_sprites.get(i).loop = false;
			}
		}
	}

	private void die(){
		if(_isdead)
			return;
		_isdead = true;
		int dieValue = (int)(1+Math.random()*3);
		String dieAnim = "die"+(dieValue);

		switch(dieValue){
		case 1:_playerState = DIE1;break;
		case 2:_playerState = DIE2;break;
		case 3:_playerState = DIE3;break;
		}

		Log.d("", "dieAnim:"+dieAnim);
		//getAnim("enemy/walk").mustDraw = false;
		//getAnim("enemy/"+dieAnim).mustDraw = true;
		setXVelocity(-0.35*Constants.LEVEL_SPEED);
		//getAnim("enemy/"+dieAnim).x = getAnim("enemy/walk").x;
		//getAnim("enemy/"+dieAnim).y = getAnim("enemy/walk").y;
		//getAnim("enemy/"+dieAnim).velocityX = getAnim("enemy/walk").velocityX;
	}

	@Override
	public void isTouchedByBullet() {
		Log.e("", "isTouchedByBullet");		
		die();
	}

	@Override
	protected String getCharacterType() {
		return "Enemy";
	}

	@Override
	protected void finalDraw(GL11 gl, Grid grid) {
		grid.draw(gl, true, false);		
	}

	@Override
	public boolean musDrawThisAnim(String resourceName) {
		if(_playerState == WALK && resourceName.equals("soldier/walk"))
			return true;
		else
			if(_playerState == DIE1 && resourceName.equals("soldier/die1"))
				return true;
			else
				if(_playerState == DIE2 && resourceName.equals("soldier/die2"))
					return true;
				else
					if(_playerState == DIE3 && resourceName.equals("soldier/die3"))
						return true;
		return false;
	}

	private GLAnim getCurrentAnim(){
		String anim = "";
		switch (_playerState) {
		case WALK:anim = "soldier/walk";break;
		case DIE1:anim = "soldier/die1";break;
		case DIE2:anim = "soldier/die2";break;
		case DIE3:anim = "soldier/die3";break;
		default:
			break;
		}
		for(int i = 0;i<_sprites.size();i++)
			if(_sprites.get(i).getResourceName().equals(anim))
				return _sprites.get(i);
		return null;

	}

}
