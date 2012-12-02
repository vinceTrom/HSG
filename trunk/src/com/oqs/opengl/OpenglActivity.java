package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;
import com.oqs.opengl.lib.MMXMLParser;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class OpenglActivity extends Activity {


	private Handler _handler = new Handler();

	private GLSurfaceView mGLSurfaceView;
	public static int _screenHeight;
	public static int _screenWidth;
	public ArrayList<GLAnim> backsSprites = new ArrayList<GLAnim>();
	public ArrayList<GLAnim> playerSprites = new ArrayList<GLAnim>();
	public ArrayList<GLAnim> foregroundSprites = new ArrayList<GLAnim>();
	private SimpleGLRenderer spriteRenderer;

	public static int GROUND_LEVEL;

	private int playerState=1;
	private static final int WALK = 1;
	private static final int JUMP = 2;
	private static final int JUMP_TWO = 5;
	private static final int FALL = 3;
	private static final int EXPLO = 4;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		_screenHeight = displaymetrics.heightPixels;
		_screenWidth = displaymetrics.widthPixels;

		setContentView(R.layout.glview);
		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gLSurfaceView);
		findViewById(R.id.jumpbtn).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {jump();Log.d("", "JUMP ARROUND");}		
		});
		findViewById(R.id.shootbtn).setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					stopShoot();
				}else{
					if(event.getAction() == MotionEvent.ACTION_DOWN)
						shoot();
				}
				return true;
			}
		});
		spriteRenderer = new SimpleGLRenderer(this);
		GLAnim.activity = this;

		// Clear out any old profile results.
		ProfileRecorder.sSingleton.resetAll();

		final Intent callingIntent = getIntent();
		// Allocate our sprites and add them to an array.
		//final int robotCount = 4;//ANIM.equals("all")?12:2;//callingIntent.getIntExtra("spriteCount", 10);
		final boolean animate = true;//callingIntent.getBooleanExtra("animate", true);
		final boolean useVerts = true;
		final boolean useHardwareBuffers = 
				callingIntent.getBooleanExtra("useHardwareBuffers", false);


		// We need to know the width and height of the display pretty soon,
		// so grab the information now.
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// This list of things to move. It points to the same content as the
		// sprite list except for the background.
		GROUND_LEVEL = (int) (0.3*_screenHeight);
		createLevelAnims();
		createPlayerAnims();
		initAnims();

		// Now's a good time to run the GC.  Since we won't do any explicit
		// allocation during the test, the GC should stay dormant and not
		// influence our results.
		Runtime r = Runtime.getRuntime();
		r.gc(); 

		ArrayList<GLAnim> all = new ArrayList<GLAnim>();
		all.addAll(backsSprites);
		all.addAll(playerSprites);
		all.addAll(foregroundSprites);
		GLAnim[] gl = new GLAnim[0];
		spriteRenderer.setSprites(backsSprites.toArray(gl), playerSprites.toArray(gl), foregroundSprites.toArray(gl), all.toArray(gl));
		spriteRenderer.setVertMode(useVerts, useHardwareBuffers);

		mGLSurfaceView.setRenderer(spriteRenderer);

		if (animate) {
			Mover simulationRuntime = new Mover(_screenHeight);
			simulationRuntime.setRenderables(all.toArray(gl));

			mGLSurfaceView.setEvent(simulationRuntime);
		}
	}

	private void initAnims() {
		for(int i = 0;i<playerSprites.size();i++){
			if(playerSprites.get(i).getResourceName().equals("walk"))
				playerSprites.get(i).mustDraw = true;
			if(playerSprites.get(i).getResourceName().equals("armfire")){
				playerSprites.get(i).x = getWalkAnim().x + getWalkAnim().textureWidth*0.07f;
				playerSprites.get(i).y = GROUND_LEVEL - getWalkAnim().textureHeight*0.55f;
			}
			if(playerSprites.get(i).getResourceName().equals("jump")){
				playerSprites.get(i).loop = false;
				//playerSprites.get(i).applyGravity = true;
			}
			if(playerSprites.get(i).getResourceName().equals("fall")){
				playerSprites.get(i).loop = false;
				//playerSprites.get(i).applyGravity = true;
			}
			if(playerSprites.get(i).getResourceName().equals("explo"))
				playerSprites.get(i).loop = false;
			if(playerSprites.get(i).getResourceName().equals("bullet"))
				playerSprites.get(i).loop = false;

			if(playerSprites.get(i).getResourceName().equals("walk") || playerSprites.get(i).getResourceName().equals("jump")||playerSprites.get(i).getResourceName().equals("fall")){
				playerSprites.get(i).x = (int) (0.22f*_screenHeight);
				playerSprites.get(i).y = GROUND_LEVEL;
			}
		}

	}

	
	private void createLevelAnims(){
		InputStream ss = null;
		try {
			ss = getAssets().open("level.xml");
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElement elem = parser.parseSynchronously().getRootElement();
		MMXMLElements elems = elem.getElementForKey("player").getElementForKey("animations").getElementsForKey("background");
		for(int i=0;i<elems.size();i++){
			String anim_name = elems.get(i).getAttributes().get("name");
			GLAnim anim = new GLLayerLoop(anim_name, true);
			GLUtils.createAnim(this,anim, elems.get(i));
			if(elems.get(i).getAttributes().containsKey("foreground") && elems.get(i).getAttributes().get("foreground").equals("true"))
				foregroundSprites.add(anim);
			else
				backsSprites.add(anim);
		}		
	}

	private void createPlayerAnims(){

		InputStream ss = null;
		try {
			ss = getAssets().open("gunner.xml");
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElement elem = parser.parseSynchronously().getRootElement();
		MMXMLElements elems = elem.getElementForKey("player").getElementForKey("animations").getElementsForKey("animation");
		for(int i=0;i<elems.size();i++){
			String anim_name = elems.get(i).getAttributes().get("name");
			GLAnim anim;
			if(anim_name.equals("bullet"))
				anim = new GLBullets(anim_name, true);
			else
				anim = new GLAnim(anim_name, true);
			GLUtils.createAnim(this, anim, elems.get(i));
			anim.mustDraw = false;
			playerSprites.add(anim);			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	private void jump() {
		if(playerState==WALK){
			playerState = JUMP;
			spriteRenderer.getAnim("walk").mustDraw = false;
			spriteRenderer.getAnim("jump").mustDraw = true;
			spriteRenderer.getAnim("jump").y=GROUND_LEVEL;
			spriteRenderer.getAnim("jump").applyGravity = true;
			spriteRenderer.getAnim("jump").setYVelocity(0.9f);
			spriteRenderer.getAnim("armfire").applyGravity = true;
			spriteRenderer.getAnim("armfire").setYVelocity(0.9f);
		}else{
			if(playerState==JUMP){
				playerState = JUMP_TWO;
				spriteRenderer.getAnim("jump").setYVelocity(0.9f);
				spriteRenderer.getAnim("armfire").setYVelocity(0.9f);
			}
		}
	}

	public void fall() {
		playerState = FALL;
		spriteRenderer.getAnim("jump").initAnim();
		spriteRenderer.getAnim("jump").mustDraw = false;
		spriteRenderer.getAnim("jump").applyGravity = false;
		spriteRenderer.getAnim("fall").mustDraw = true;
		spriteRenderer.getAnim("fall").y = spriteRenderer.getAnim("jump").y;
		spriteRenderer.getAnim("fall").applyGravity = true;
		spriteRenderer.getAnim("fall").setYVelocity(0f);
	}

	public void fallFinished() {
		playerState = WALK;
		spriteRenderer.getAnim("fall").initAnim();
		spriteRenderer.getAnim("fall").mustDraw = false;
		spriteRenderer.getAnim("walk").initAnim();
		spriteRenderer.getAnim("walk").mustDraw = true;
		spriteRenderer.getAnim("fall").applyGravity = false;
		spriteRenderer.getAnim("armfire").applyGravity = false;
		spriteRenderer.getAnim("armfire").setYVelocity(0f);

	}

	//private long lastshoot=0;
	private void shoot() {
		spriteRenderer.getAnim("armfire").mustDraw = true;
		
		spriteRenderer.getAnim("bullet").mustDraw = true;
		spriteRenderer.getAnim("bullet").setXVelocity(4f);
		_handler.post(shootRun);
	}
	
	private Runnable shootRun = new Runnable() {
		
		@Override
		public void run() {
			GLAnim player = getCurrentPlayerAnim();
			((GLBullets)spriteRenderer.getAnim("bullet")).newBullet((int) (player.x-player.getAnchor().first+player.textureWidth*1),(int) (player.y-player.getAnchor().second+player.textureHeight*0.38+Math.random()*_screenHeight/20));
				_handler.postDelayed(this, 170);
		}
	};

	private void stopShoot(){
		_handler.removeCallbacks(shootRun);
		spriteRenderer.getAnim("armfire").mustDraw = false;

	}

	private GLAnim getCurrentPlayerAnim(){
		switch (playerState) {
		case WALK:
			return spriteRenderer.getAnim("walk");
		case JUMP:
			return spriteRenderer.getAnim("jump");
		case JUMP_TWO:
			return spriteRenderer.getAnim("jump");
		case FALL:
			return spriteRenderer.getAnim("fall");
		default:
			spriteRenderer.getAnim("walk");
			break;
		}
		return null;
	}

	private GLAnim getWalkAnim(){
		for(int i=0;i<playerSprites.size();i++){
			if(playerSprites.get(i).getResourceName().equals("walk"))
				return playerSprites.get(i);
		}
		return null;
	}

}