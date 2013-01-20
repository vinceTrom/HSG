package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;


public class Player extends Character {

	public static final int WALK = 1;
	public static final int JUMP = 2;
	public static final int JUMP_TWO = 5;
	public static final int FALL = 3;

	private Handler _handler = new Handler();

	public Player(Context ctx) {
		super(ctx, "player.xml");
		_playerState = WALK;
	}

	@Override
	protected void createAnims(Context ctx, String fileName){
		super.createAnims(ctx, fileName);
		addBullet(ctx);		
	}

	private void addBullet(Context ctx) {
		InputStream ss = null;
		try {
			ss = ctx.getAssets().open("bullet.xml");
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElement elem = parser.parseSynchronously().getRootElement();
		MMXMLElement elem2 = elem.getElementForKey("player").getElementForKey("animations").getElementForKey("animation");

		//GLUtils.createAnim(ctx, anim, elem2);
		//anim.mustDraw = false;		
	}

	protected void initAnims() {
		x = (int) (0.22f*OpenglActivity._screenHeight);
		y = Constants.GROUND_LEVEL;
		for(int i=0;i<_sprites.size();i++){

			if(_sprites.get(i).getResourceName().equals("jump")){
				_sprites.get(i).loop = false;
			}
			if(_sprites.get(i).getResourceName().equals("fall")){
				_sprites.get(i).loop = false;
			}
			/*
			if(_sprites.get(i).getResourceName().equals("armfire")){
				Pair<Integer,Integer> p = getCurrentPlayerAnim().getFrames().get(_state.get(getCurrentAnimName()).currentindex).fireAnchor;
				
				int deltaX = (int) (x+p.first);
				int deltaY = (int) (y+p.second);
				_sprites.get(i).setOffsetPos(deltaX, deltaY);
			}
			*/
			 
		}
		/*
		for(int i = 0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("walk"))
				_sprites.get(i).mustDraw = true;
			if(_sprites.get(i).getResourceName().equals("armfire")){
				_sprites.get(i).x = getWalkAnim().x + getWalkAnim().textureWidth*0.07f;
				_sprites.get(i).y = Constants.GROUND_LEVEL - getWalkAnim().textureHeight*0.55f;
			}
			if(_sprites.get(i).getResourceName().equals("jump")){
				_sprites.get(i).loop = false;
				//playerSprites.get(i).applyGravity = true;
			}
			if(_sprites.get(i).getResourceName().equals("fall")){
				_sprites.get(i).loop = false;
				//playerSprites.get(i).applyGravity = true;
			}
			if(_sprites.get(i).getResourceName().equals("explo"))
				_sprites.get(i).loop = false;
			if(_sprites.get(i).getResourceName().equals("bullet"))
				_sprites.get(i).loop = false;

			if(_sprites.get(i).getResourceName().equals("walk") || _sprites.get(i).getResourceName().equals("jump")||_sprites.get(i).getResourceName().equals("fall")){
				_sprites.get(i).x = (int) (0.22f*OpenglActivity._screenHeight);
				_sprites.get(i).y = Constants.GROUND_LEVEL;
			}
		}
		 */

	}



	public void jump() {
		if(_playerState==WALK){
			_playerState = JUMP;
			applyGravity = true;
			setYVelocity(0.9f);
		}else{
			if(_playerState==JUMP){
				_playerState = JUMP_TWO;
				velocityY = 0.9f;
				setYVelocity(0.9f);
			}
		}
	}

	public void fall() {
		_playerState = FALL;
		getAnim("jump").initAnim();
	}

	public void fallFinished() {
		_playerState = WALK;
		setYVelocity(0);
		getAnim("fall").initAnim();
		getAnim("walk").initAnim();
		applyGravity = false;
		y = Constants.GROUND_LEVEL;
	}

	private boolean isshooting = false;
	public void shoot() {
		isshooting = true;
		_handler.post(shootRun);
	}

	private Runnable shootRun = new Runnable() {

		@Override
		public void run() {
			Log.e("", "shootrun "+y+ "  height:"+height);
			GLBullet bullet = new GLBullet();
			Pair<Integer,Integer> p = getCurrentPlayerAnim().getFrames().get(_state.get(getCurrentAnimName()).currentindex).fireAnchor;
			bullet.y = y+p.second;
			bullet.x = x+p.first-GLBullets.get().getSprite().getFrames().get(0).width;
			bullet.setXVelocity(4f);
			GLBullets.get().addBullet(bullet);
			//((GLBullets)getAnim("bullet")).newBullet((int) (player.x-player.getAnchor().first+player.textureWidth*1),(int) (player.y-player.getAnchor().second+player.textureHeight*0.38+Math.random()*OpenglActivity._screenHeight/20));
			_handler.postDelayed(this, 170);
		}
	};

	public void stopShoot(){
		isshooting = false;
		_handler.removeCallbacks(shootRun);
	}

	String getCurrentAnimName(){
		switch (_playerState) {
		case WALK:
			return "walk";
		case JUMP:
			return "jump";
		case JUMP_TWO:
			return "jump";
		case FALL:
			return "fall";
		default:
			return "walk";
		}
	}

	GLAnim getCurrentPlayerAnim(){
		return getAnim(getCurrentAnimName());
	}


	private GLAnim getWalkAnim(){
		for(int i=0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals("walk"))
				return _sprites.get(i);
		}
		return null;
	}

	@Override
	public void isTouchedByBullet() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getCharacterType() {
		return "Player";
	}

	@Override
	protected void finalDraw(GL10 gl, Grid grid) {
		grid.draw(gl, true, false);
	}

	@Override
	public boolean musDrawThisAnim(String resourceName) {
		if(_playerState == WALK && resourceName.equals("walk"))
			return true;
		else
			if((_playerState == JUMP || _playerState == JUMP_TWO) && resourceName.equals("jump"))
				return true;
			else
				if(_playerState == FALL && resourceName.equals("fall"))
					return true;
				else
					if(isshooting && resourceName.equals("armfire"))
						return true;

		return false;
	}
}
