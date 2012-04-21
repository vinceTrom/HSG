package com.oqs.opengl;


import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class OpenglActivity extends Activity {
	private final static int SPRITE_WIDTH = 256;
	private final static int SPRITE_HEIGHT = 256;


	private GLSurfaceView mGLSurfaceView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GLSurfaceView(this);
		SimpleGLRenderer spriteRenderer = new SimpleGLRenderer(this);

		// Clear out any old profile results.
		ProfileRecorder.sSingleton.resetAll();

		final Intent callingIntent = getIntent();
		// Allocate our sprites and add them to an array.
		final int robotCount = 1;//callingIntent.getIntExtra("spriteCount", 10);
		final boolean animate = true;//callingIntent.getBooleanExtra("animate", true);
		final boolean useVerts = true;
		final boolean useHardwareBuffers = 
				callingIntent.getBooleanExtra("useHardwareBuffers", false);

		// Allocate space for the robot sprites + one background sprite.
		GLAnim[] spriteArray = new GLAnim[robotCount + 1];    

		// We need to know the width and height of the display pretty soon,
		// so grab the information now.
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		GLAnim background = new GLAnim("abg.png");
		// BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.bg);
		Bitmap backgoundBitmap = null;
		try {
			backgoundBitmap = BitmapFactory.decodeStream(getAssets().open("abg.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		spriteArray[0] = background;

		/*
	        Grid[] spriteGrids = new Grid[1];
	        Grid picGrid = null;
	        if (useVerts) {
	            // Setup a quad for the sprites to use.  All sprites will use the
	            // same sprite grid intance.
	        	float Xoffset = 0.0f;
	        	float Yoffset = 0.0f;
	        	float Xratio = 1.0f;
	        	float Yratio = 1.0f;
	            picGrid = new Grid(2, 2, false);
	            picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
	            picGrid.set(1, 0, SPRITE_WIDTH*Xratio, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
	            picGrid.set(0, 1, 0.0f, SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
	            picGrid.set(1, 1, SPRITE_WIDTH*Xratio, SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);
	        }
	        spriteGrids[0] = picGrid;
		 */


		// This list of things to move. It points to the same content as the
		// sprite list except for the background.
		Renderable[] renderableArray = new Renderable[robotCount]; 
		final int robotBucketSize = robotCount / 3;
		for (int x = 0; x < robotCount; x++) {
			GLAnim robot;
			// Our robots come in three flavors.  Split them up accordingly.
			if (x < robotBucketSize) {
				robot = new GLAnim("aexplo.png");
			} else if (x < robotBucketSize * 2) {
				robot = new GLAnim("aexplo.png");
			} else {
				robot = new GLAnim("aexplo.png");
			}

			robot.width = SPRITE_WIDTH;
			robot.height = SPRITE_HEIGHT;

			// Pick a random location for this sprite.
			robot.x = (float)(Math.random() * dm.widthPixels);
			robot.y = (float)(Math.random() * dm.heightPixels);

			// All sprites can reuse the same grid.  If we're running the
			// DrawTexture extension test, this is null.

			robot.setGrids(createGrids());

			// Add this robot to the spriteArray so it gets drawn and to the
			// renderableArray so that it gets moved.
			spriteArray[x + 1] = robot;
			renderableArray[x] = robot;
		}


		// Now's a good time to run the GC.  Since we won't do any explicit
		// allocation during the test, the GC should stay dormant and not
		// influence our results.
		Runtime r = Runtime.getRuntime();
		r.gc();

		spriteRenderer.setSprites(spriteArray);
		spriteRenderer.setVertMode(useVerts, useHardwareBuffers);

		mGLSurfaceView.setRenderer(spriteRenderer);

		if (animate) {
			Mover simulationRuntime = new Mover();
			simulationRuntime.setRenderables(renderableArray);

			simulationRuntime.setViewSize(dm.widthPixels, dm.heightPixels);
			mGLSurfaceView.setEvent(simulationRuntime);
		}
		setContentView(mGLSurfaceView);
	}

	private Grid[] createGrids(){
		int nbframes = 1;
		float texturewidth = 128f;
		float textureheight = 128f;
		
		Grid[] grids = new Grid[nbframes];

		for(int frameindex = 0;frameindex<nbframes;frameindex++){
			// Setup a quad for the sprites to use.
			float Xoffset = 0.0f;
			float Yoffset = 0.0f;
			float Xratio = 1.0f;
			float Yratio = 1.0f;
			Grid picGrid = new Grid(2, 2, false);
			picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
			picGrid.set(1, 0, SPRITE_WIDTH*Xratio, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
			picGrid.set(0, 1, 0.0f, SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
			picGrid.set(1, 1, SPRITE_WIDTH*Xratio, SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);

			grids[frameindex] = picGrid;
		}
		return grids;
	}
}