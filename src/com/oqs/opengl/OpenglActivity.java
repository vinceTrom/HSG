package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.momac.mmlib.xml.MMXMLElement;
import com.momac.mmlib.xml.MMXMLParser;
import com.momac.mmlib.xml.MMXMLElement.MMXMLElements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class OpenglActivity extends Activity {
	private  static float SPRITE_WIDTH = 0;
	private static float SPRITE_HEIGHT = 0;

	private RelativeLayout _mainLayout;
	private GLSurfaceView mGLSurfaceView;
	private static  String ANIM = "";
	public static int _screenHeight;
	public static int _screenWidth;
	public ArrayList<GLAnim> backsSprites = new ArrayList<GLAnim>();
	public ArrayList<GLAnim> playerSprites = new ArrayList<GLAnim>();
	public ArrayList<GLAnim> foregroundSprites = new ArrayList<GLAnim>();
	private SimpleGLRenderer spriteRenderer;

	public static int GROUND_LEVEL;

	private int playerState=1;
	private static int WALK = 1;
	private static int JUMP = 2;
	private static int FALL = 3;
	private static int EXPLO = 4;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		_screenHeight = displaymetrics.heightPixels;
		_screenWidth = displaymetrics.widthPixels;

		ANIM = getIntent().getStringExtra("anim");
		_mainLayout = new RelativeLayout(this);
		mGLSurfaceView = new GLSurfaceView(this);
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

		GLAnim background = new GLAnim("abg.png", false);
		// BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.bg);
		Bitmap backgoundBitmap = null;
		try {
			backgoundBitmap = BitmapFactory.decodeStream(getAssets().open("abg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		} // backgroundImage.getBitmap();
		background.width = backgoundBitmap.getWidth()*2;//HACK
		background.height = backgoundBitmap.getHeight()*2;
		if (useVerts) {
			// Setup the background grid.  This is just a quad.
			Grid[] backgroundGrids = new Grid[1];
			Grid backgroundGrid = new Grid(2, 2, false);
			backgroundGrid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f, 1.0f, null);
			backgroundGrid.set(1, 0, background.width, 0.0f, 0.0f, 1.0f, 1.0f, null);
			backgroundGrid.set(0, 1, 0.0f, background.height, 0.0f, 0.0f, 0.0f, null);
			backgroundGrid.set(1, 1, background.width, background.height, 0.0f, 
					1.0f, 0.0f, null );
			backgroundGrids[0] = backgroundGrid;
			background.setGrids(backgroundGrids);
		}


		// This list of things to move. It points to the same content as the
		// sprite list except for the background.
		GROUND_LEVEL = (int) (0.3*_screenHeight);
		createLevelAnims();
		createPlayerAnims();
		initAnims();
		/*
		for (int x = 0; x < robotCount; x++) {
			//String[] array = getResources().getStringArray(R.array.anims_array);
			String LOCAL_ANIM ;//= ANIM.equals("all")?array[((x)%(array.length-1))+1]:ANIM;//x%2==0?"walk":"explo";
			if(x==0)
				LOCAL_ANIM = "back";
			else if(x==1)
				LOCAL_ANIM = "mainback";
			else if(x == 2)
				LOCAL_ANIM = "walk";
			else 
				LOCAL_ANIM = "first";

			GLAnim tiledSprite = null;
			// Our robots come in three flavors.  Split them up accordingly.
			if(LOCAL_ANIM.equals("mainback") || LOCAL_ANIM.equals("back") || LOCAL_ANIM.equals("first")){
				tiledSprite = new GLLayerLoop(LOCAL_ANIM+".png", true);
			}else
				tiledSprite = new GLAnim(LOCAL_ANIM+".png", true);

		 */


		// Now's a good time to run the GC.  Since we won't do any explicit
		// allocation during the test, the GC should stay dormant and not
		// influence our results.
		Runtime r = Runtime.getRuntime();
		r.gc(); 
		//spriteArray[0] = background;
		/*
		Renderable[] renderableArray = new Renderable[spriteList.size()]; 


		for(int j = 0; j<backsSprites.size(); j++){
			spriteArray[j+1] = backsSprites.get(j);
			renderableArray[j] = backsSprites.get(j);
		}
		 */
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

		_mainLayout.addView(mGLSurfaceView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		addButtons();

		setContentView(_mainLayout);
	}

	private void initAnims() {
		for(int i = 0;i<playerSprites.size();i++){
			if(playerSprites.get(i).getResourceName().equals("walk"))
				playerSprites.get(i).mustDraw = true;
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
		}

	}

	private void addButtons() {
		ImageView img = new ImageView(this);
		img.setImageResource(R.drawable.jumpbutt);
		img.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {jump();Log.d("", "JUMP ARROUND");}		
		});
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutt, opt);
		lp.topMargin = _screenHeight- opt.outHeight;
		lp.leftMargin = 100;
		_mainLayout.addView(img, lp);

		ImageView img2 = new ImageView(this);
		img2.setImageResource(R.drawable.shootbutt);
		img2.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {Log.d("", "SHOOOOOOT");}
		});
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		BitmapFactory.Options opt2 = new BitmapFactory.Options();
		opt2.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.shootbutt, opt2);
		lp2.topMargin = _screenHeight- opt2.outHeight;
		lp2.leftMargin = _screenWidth-opt2.outWidth;
		_mainLayout.addView(img2, lp2);
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
			createAnim(anim, elems.get(i));
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
			GLAnim anim = new GLAnim(anim_name, true);
			createAnim(anim, elems.get(i));
			anim.mustDraw = false;
			playerSprites.add(anim);

			if(anim.equals("walk"))
				WALK = i;
			else if(anim.equals("jump"))
				JUMP = i;
			else
				if(anim.equals("fall"))
					FALL = i;
				else if(anim.equals("explo"))
					EXPLO = i;			
		}
	}

	private void createAnim(GLAnim sprite, MMXMLElement animElem){
		BitmapFactory.Options opt = new Options();
		opt.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeStream(getAssets().open(sprite.getResourceName()+".png"),null,opt);
		} catch (IOException e) {e.printStackTrace();}
		SPRITE_WIDTH = opt.outWidth;
		SPRITE_HEIGHT = opt.outHeight;

		sprite.width = SPRITE_WIDTH;
		sprite.height = SPRITE_HEIGHT;

		sprite.x = (int) (0.22f*_screenHeight);
		sprite.y = GROUND_LEVEL;

		sprite.setGrids(createGrids(sprite, animElem.getName(), animElem));
		//spriteList.add(sprite);
	}

	private Grid[] createGrids(GLAnim glanim, String animName, MMXMLElement anim){

		float picSizeOnScreenRatio = 0.3f;//relative � la hauteur de l'�cran
		/*
		if(animName.equals("mainback")){
			picSizeOnScreenRatio = 1;
			glanim.x = 0;
			glanim.y = _screenHeight;
			glanim.setXVelocity(-0.5f);
		}
		 */
		float maxheightPic = 0f;

		ArrayList<Picture> pictures = new ArrayList<Picture>();

		int nbframes = anim.getElementsForKey("image").size();

		for(int i=0;i<nbframes;i++){
			int x = Integer.parseInt(anim.getElement(i).getAttributes().get("x"));
			int y = Integer.parseInt(anim.getElement(i).getAttributes().get("y"));
			int width = Integer.parseInt(anim.getElement(i).getAttributes().get("width"));
			int height = Integer.parseInt(anim.getElement(i).getAttributes().get("height"));
			int anchorx = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"));//(int) ((height/(float) h/picSizeOnScreen)*(width - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"))));
			int anchory = height-Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"));//(int) ((height/(float)h/picSizeOnScreen)*(height - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"))));
			pictures.add(new Picture((int)(x),(int)(y),(int)(width),(int)(height),(int)(anchorx),(int)(anchory)));
			if(height > maxheightPic)
				maxheightPic = height;
		}

		glanim.setPictures(pictures);
		if(anim.getAttributes().get("Xvelocity") != null){
			glanim.setXVelocity(Float.parseFloat(anim.getAttributes().get("Xvelocity")));
			glanim.x = 0;
		}

		if(anim.getAttributes().get("size") != null){
			picSizeOnScreenRatio = Float.parseFloat(anim.getAttributes().get("size"));
		}

		if(anim.getAttributes().get("y") != null){
			glanim.y = Float.parseFloat(anim.getAttributes().get("y"))*_screenHeight;
		}

		/*
		glanim.x=200;
		glanim.y=200;
		glanim.setXVelocity(0.33f);
		glanim.setYVelocity(0.1f);
		 */

		try{
			glanim.setAnimPeriod(Integer.parseInt(anim.getAttributes().get("period")));
		}catch(Exception e){}


		Grid[] grids = new Grid[nbframes];

		for(int frameindex = 0;frameindex<nbframes;frameindex++){
			// Setup a quad for the sprites to use.
			float Xoffset = pictures.get(frameindex).orig.first/SPRITE_WIDTH;
			float Yoffset = pictures.get(frameindex).orig.second/SPRITE_HEIGHT;
			float Xratio = pictures.get(frameindex).width/SPRITE_WIDTH;
			float Yratio = pictures.get(frameindex).height/SPRITE_HEIGHT;
			Grid picGrid = new Grid(2, 2, false);

			int textureheight = (int) ((pictures.get(frameindex).height/maxheightPic)*picSizeOnScreenRatio*_screenHeight);
			float ratio = (textureheight/(float)pictures.get(frameindex).height);
			int texturewidth = (int) (ratio*pictures.get(frameindex).width);
			try{
				((GLLayerLoop)glanim).setTextureWidth(texturewidth);
			}catch(Exception e){}

			picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
			picGrid.set(1, 0, texturewidth, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
			picGrid.set(0, 1, 0.0f, textureheight, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
			picGrid.set(1, 1, texturewidth, textureheight, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);

			pictures.get(frameindex).anchor = new Pair<Integer, Integer>((int) (pictures.get(frameindex).anchor.first*ratio), (int) (pictures.get(frameindex).anchor.second*ratio));

			grids[frameindex] = picGrid;
		}
		return grids;
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
		}
	}

	public void fall() {
		playerState = FALL;
		spriteRenderer.getAnim("jump").initAnim();
		spriteRenderer.getAnim("jump").mustDraw = false;
		spriteRenderer.getAnim("fall").mustDraw = true;
		spriteRenderer.getAnim("fall").y = spriteRenderer.getAnim("jump").y;
		spriteRenderer.getAnim("fall").applyGravity = true;
		//spriteRenderer.getAnim("fa").setYVelocity(0.5f);
	}

	public void fallFinished() {
		playerState = WALK;
		spriteRenderer.getAnim("fall").initAnim();
		spriteRenderer.getAnim("fall").mustDraw = false;
		spriteRenderer.getAnim("walk").mustDraw = true;
		spriteRenderer.getAnim("fall").applyGravity = false;
	}

	private void shoot() {
		// TODO Auto-generated method stub

	}

}