package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.momac.mmlib.xml.MMXMLElement;
import com.momac.mmlib.xml.MMXMLParser;
import com.momac.mmlib.xml.MMXMLElement.MMXMLElements;
import com.momac.mmlib.xml.MMXMLParser.MMXMLParserDelegate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class OpenglActivity extends Activity {
	private  static int SPRITE_WIDTH = 0;
	private static int SPRITE_HEIGHT = 0;


	private GLSurfaceView mGLSurfaceView;
	private static final String ANIM = "explo2";


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
		spriteArray[0] = background;


		// This list of things to move. It points to the same content as the
		// sprite list except for the background.
		Renderable[] renderableArray = new Renderable[robotCount]; 
		for (int x = 0; x < robotCount; x++) {
			GLAnim robot;
			// Our robots come in three flavors.  Split them up accordingly.
			robot = new GLAnim(ANIM+".png", true);

			BitmapFactory.Options opt = new Options();
			opt.inJustDecodeBounds = true;

			try {
				BitmapFactory.decodeStream(getAssets().open(ANIM+".png"),null,opt);
			} catch (IOException e) {e.printStackTrace();}
			SPRITE_WIDTH = opt.outWidth;
			SPRITE_HEIGHT = opt.outHeight;

			robot.width = SPRITE_WIDTH;
			robot.height = SPRITE_HEIGHT;

			robot.setGrids(createGrids(robot, ANIM));
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

	//float[] offsets = {33,256,472,638,846,1050,1266,1479,1710,1915,2120,2340,2450,2744,2935,3120};

	private Grid[] createGrids(GLAnim glanim, String animName){
		float picSizeOnScreen = 2;

		ArrayList<Picture> pictures = new ArrayList<Picture>();
		InputStream ss = null;
		try {
			ss = getAssets().open("gunner.xml");
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElements elems = parser.parseSynchronously().getElementForKey("player").getElementForKey("animations").getElementsForKey("animation");
		MMXMLElement anim = null;
		for(int i=0;i<elems.size();i++){
			if(elems.get(i).getAttributes().get("name").equals(animName))
				anim = elems.get(i);
		}
		int nbframes = anim.getElementsForKey("image").size();

		for(int i=0;i<nbframes;i++){
			int x = Integer.parseInt(anim.getElement(i).getAttributes().get("x"));
			int y = Integer.parseInt(anim.getElement(i).getAttributes().get("y"));
			int width = Integer.parseInt(anim.getElement(i).getAttributes().get("width"));
			int height = Integer.parseInt(anim.getElement(i).getAttributes().get("height"));
			int anchorx = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"));
			int anchory = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"));
			pictures.add(new Picture((int)(x),(int)(y),(int)(width),(int)(height),(int)(anchorx),(int)(anchory)));
		}
		glanim.setPictures(pictures);
		glanim.setPictureSizeOnScreen(picSizeOnScreen);


		Grid[] grids = new Grid[nbframes];

		for(int frameindex = 0;frameindex<nbframes;frameindex++){
			// Setup a quad for the sprites to use.
			float Xoffset = 0.0f+pictures.get(frameindex).orig.first/5328f;
			float Yoffset = 0.0f+pictures.get(frameindex).orig.second/492f;
			float Xratio = 1.0f*pictures.get(frameindex).width/5328f;
			float Yratio = 1.0f*pictures.get(frameindex).height/492f;
			Grid picGrid = new Grid(2, 2, false);
			picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
			picGrid.set(1, 0, picSizeOnScreen*SPRITE_WIDTH*Xratio, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
			picGrid.set(0, 1, 0.0f, picSizeOnScreen*SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
			picGrid.set(1, 1,picSizeOnScreen*SPRITE_WIDTH*Xratio,picSizeOnScreen* SPRITE_HEIGHT*Yratio, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);

			grids[frameindex] = picGrid;
		}
		return grids;
	}

}