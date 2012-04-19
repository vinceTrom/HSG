package com.oqs.opengl;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class OpenglActivity extends Activity {
	 private final static int SPRITE_WIDTH = 64;
	    private final static int SPRITE_HEIGHT = 64;
	    
	    
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
	        final int robotCount = callingIntent.getIntExtra("spriteCount", 10);
	        final boolean animate = callingIntent.getBooleanExtra("animate", true);
	        final boolean useVerts = 
	            callingIntent.getBooleanExtra("useVerts", false);
	        final boolean useHardwareBuffers = 
	            callingIntent.getBooleanExtra("useHardwareBuffers", false);
	        
	        // Allocate space for the robot sprites + one background sprite.
	        GLSprite[] spriteArray = new GLSprite[robotCount + 1];    
	        
	        // We need to know the width and height of the display pretty soon,
	        // so grab the information now.
	        DisplayMetrics dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        
	        GLSprite background = new GLSprite(R.drawable.bg);
	        BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.bg);
	        Bitmap backgoundBitmap = backgroundImage.getBitmap();
	        background.width = backgoundBitmap.getWidth()*2;
	        background.height = backgoundBitmap.getHeight()*2;
	        if (useVerts) {
	            // Setup the background grid.  This is just a quad.
	            Grid backgroundGrid = new Grid(2, 2, false);
	            backgroundGrid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f, 1.0f, null);
	            backgroundGrid.set(1, 0, background.width, 0.0f, 0.0f, 1.0f, 1.0f, null);
	            backgroundGrid.set(0, 1, 0.0f, background.height, 0.0f, 0.0f, 0.0f, null);
	            backgroundGrid.set(1, 1, background.width, background.height, 0.0f, 
	                    1.0f, 0.0f, null );
	            background.setGrid(backgroundGrid);
	        }
	        spriteArray[0] = background;
	        
	        
	        Grid spriteGrid = null;
	        if (useVerts) {
	            // Setup a quad for the sprites to use.  All sprites will use the
	            // same sprite grid intance.
	            spriteGrid = new Grid(2, 2, false);
	            spriteGrid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f , 1.0f, null);
	            spriteGrid.set(1, 0, SPRITE_WIDTH, 0.0f, 0.0f, 1.0f, 1.0f, null);
	            spriteGrid.set(0, 1, 0.0f, SPRITE_HEIGHT, 0.0f, 0.0f, 0.0f, null);
	            spriteGrid.set(1, 1, SPRITE_WIDTH, SPRITE_HEIGHT, 0.0f, 1.0f, 0.0f, null);
	        }
	        
	        
	        
	        // This list of things to move. It points to the same content as the
	        // sprite list except for the background.
	        Renderable[] renderableArray = new Renderable[robotCount]; 
	        final int robotBucketSize = robotCount / 3;
	        for (int x = 0; x < robotCount; x++) {
	            GLSprite robot;
	            // Our robots come in three flavors.  Split them up accordingly.
	            if (x < robotBucketSize) {
	                robot = new GLSprite(R.drawable.skate1);
	            } else if (x < robotBucketSize * 2) {
	                robot = new GLSprite(R.drawable.skate1);
	            } else {
	                robot = new GLSprite(R.drawable.skate1);
	            }
	        
	            robot.width = SPRITE_WIDTH;
	            robot.height = SPRITE_HEIGHT;
	            
	            // Pick a random location for this sprite.
	            robot.x = (float)(Math.random() * dm.widthPixels);
	            robot.y = (float)(Math.random() * dm.heightPixels);
	            
	            // All sprites can reuse the same grid.  If we're running the
	            // DrawTexture extension test, this is null.
	            robot.setGrid(spriteGrid);
	            
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
	}