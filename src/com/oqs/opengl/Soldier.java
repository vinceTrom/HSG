package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

public class Soldier extends Character{

	public static final int WALK = 1;
	public static final int DIE1 = 2;
	public static final int DIE2 = 5;
	public static final int DIE3 = 3;
	
	
	private static final String WALK_ANIM_PATH = "soldier/walk";
	private static final String DIE1_ANIM_PATH = "soldier/die1";
	private static final String DIE2_ANIM_PATH = "soldier/die2";
	private static final String DIE3_ANIM_PATH = "soldier/die3";
	
	

	private boolean _isdead = false;
	private static final String FILENAME = "soldier/soldier.xml";
	private Rect _boundRect = new Rect();

	public Soldier(Context ctx) {
		super(ctx, FILENAME);	
		_playerState = WALK;
	}

	@Override
	public Rect getBoundRect() {

		GLAnim anim = getCurrentAnim();
		int width =0;
		int height = 0;

		try{
			width = anim.getFrames().get(_state.get(anim.getResourceName()).currentindex).width;
			height = anim.getFrames().get(_state.get(anim.getResourceName()).currentindex).height;
		}catch(Exception e){}
		_boundRect.set((int)x,(int) y, (int)(x+width),(int)(y+height));
		return _boundRect; 
	}

	@Override
	protected void initAnims() {
		
		
		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("soldier/walk")){
				_sprites.get(i).textureHeight =(int) (0.5f*Level1._screenHeight);
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

		switch(dieValue){
		case 1:_playerState = DIE1;break;
		case 2:_playerState = DIE2;break;
		case 3:_playerState = DIE3;break;
		}

		setXVelocity(-0.35*Constants.LEVEL_SPEED);
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
	protected void finalDraw(GL10 gl, Grid grid) {
		grid.draw(gl, true, false);		
	}

	@Override
	public boolean musDrawThisAnim(String resourceName) {
		if(_playerState == WALK && resourceName.equals(WALK_ANIM_PATH))
			return true;
		else
			if(_playerState == DIE1 && resourceName.equals(DIE1_ANIM_PATH))
				return true;
			else
				if(_playerState == DIE2 && resourceName.equals(DIE2_ANIM_PATH))
					return true;
				else
					if(_playerState == DIE3 && resourceName.equals(DIE3_ANIM_PATH))
						return true;
					
		return false;
	}

	private GLAnim getCurrentAnim(){
		String anim = "";
		switch (_playerState) {
		case WALK:anim = WALK_ANIM_PATH;break;
		case DIE1:anim = DIE1_ANIM_PATH;break;
		case DIE2:anim = DIE2_ANIM_PATH;break;
		case DIE3:anim = DIE3_ANIM_PATH;break;
		default:
			break;
		}
		for(int i = 0;i<_sprites.size();i++)
			if(_sprites.get(i).getResourceName().equals(anim))
				return _sprites.get(i);
		return null;
	}

	@Override
	public boolean mustDraw() {
		return true;
	}

}
