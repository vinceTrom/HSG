package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.content.Context;
import android.os.Handler;


public class Player extends Character {

	private Handler _handler = new Handler();

	private int playerState=1;
	private static final int WALK = 1;
	private static final int JUMP = 2;
	private static final int JUMP_TWO = 5;
	private static final int FALL = 3;
	private static final int EXPLO = 4;


	public Player(Context ctx) {
		super(ctx, "player.xml");
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
		
		GLAnim anim = new GLBullets("bullet", true);
		_sprites.add(anim);
		GLUtils.createAnim(ctx, anim, elem2);
		anim.mustDraw = false;		
	}

	protected void initAnims() {
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

	}

	public GLAnim getAnim(String animName) {
		for(int i=0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals(animName))
				return _sprites.get(i);
		}
		return null;

	}

	public void jump() {
		if(playerState==WALK){
			playerState = JUMP;
			getAnim("walk").mustDraw = false;
			getAnim("jump").mustDraw = true;
			getAnim("jump").y = Constants.GROUND_LEVEL;
			getAnim("jump").applyGravity = true;
			getAnim("jump").setYVelocity(0.9f);
			getAnim("armfire").applyGravity = true;
			getAnim("armfire").setYVelocity(0.9f);
		}else{
			if(playerState==JUMP){
				playerState = JUMP_TWO;
				getAnim("jump").setYVelocity(0.9f);
				getAnim("armfire").setYVelocity(0.9f);
			}
		}
	}

	public void fall() {
		playerState = FALL;
		getAnim("jump").initAnim();
		getAnim("jump").mustDraw = false;
		getAnim("jump").applyGravity = false;
		getAnim("fall").mustDraw = true;
		getAnim("fall").y = getAnim("jump").y;
		getAnim("fall").applyGravity = true;
		getAnim("fall").setYVelocity(0f);
	}

	public void fallFinished() {
		playerState = WALK;
		getAnim("fall").initAnim();
		getAnim("fall").mustDraw = false;
		getAnim("walk").initAnim();
		getAnim("walk").mustDraw = true;
		getAnim("fall").applyGravity = false;
		getAnim("armfire").applyGravity = false;
		getAnim("armfire").setYVelocity(0f);

	}

	//private long lastshoot=0;
	public void shoot() {
		getAnim("armfire").mustDraw = true;

		getAnim("bullet").mustDraw = true;
		getAnim("bullet").setXVelocity(4f);
		_handler.post(shootRun);
	}

	private Runnable shootRun = new Runnable() {

		@Override
		public void run() {
			GLAnim player = getCurrentPlayerAnim();
			((GLBullets)getAnim("bullet")).newBullet((int) (player.x-player.getAnchor().first+player.textureWidth*1),(int) (player.y-player.getAnchor().second+player.textureHeight*0.38+Math.random()*OpenglActivity._screenHeight/20));
			_handler.postDelayed(this, 170);
		}
	};

	public void stopShoot(){
		_handler.removeCallbacks(shootRun);
		getAnim("armfire").mustDraw = false;

	}

	private GLAnim getCurrentPlayerAnim(){
		switch (playerState) {
		case WALK:
			return getAnim("walk");
		case JUMP:
			return getAnim("jump");
		case JUMP_TWO:
			return getAnim("jump");
		case FALL:
			return getAnim("fall");
		default:
			getAnim("walk");
			break;
		}
		return null;
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
}
