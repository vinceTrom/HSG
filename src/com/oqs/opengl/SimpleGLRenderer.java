package com.oqs.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.oqs.opengl.lib.FrameRateCounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.Pair;

/**
 * An OpenGL ES renderer based on the GLSurfaceView rendering framework.  This
 * class is responsible for drawing a list of renderables to the screen every
 * frame.  It also manages loading of textures and (when VBOs are used) the
 * allocation of vertex buffer objects.
 */
public class SimpleGLRenderer implements GLSurfaceView.Renderer {
	// Specifies the format our textures should be converted to upon load.
	private static BitmapFactory.Options sBitmapOptions
	= new BitmapFactory.Options();
	// An array of things to draw every frame.
	private GLAnim[] mSprites;
	private GLAnim[] mbackgrounds;
	private GLAnim[] mplayer;
	private ArrayList<Soldier> _enemies; //We have a tab of ennemies,so a tab of ennemies of tab of anims 
	public ArrayList<GLBullets> _bullets = new ArrayList<GLBullets>();

	private GLAnim[] mforegrounds;
	// Pre-allocated arrays to use at runtime so that allocation during the
	// test can be avoided.
	private int[] mTextureNameWorkspace;
	private int[] mCropWorkspace;
	// A reference to the application context.
	private Context mContext;
	// Determines the use of vertex arrays.
	private boolean mUseVerts;
	// Determines the use of vertex buffer objects.
	private boolean mUseHardwareBuffers;

	public SimpleGLRenderer(Context context) {
		// Pre-allocate and store these objects so we can use them at runtime
		// without allocating memory mid-frame.
		mTextureNameWorkspace = new int[1];
		mCropWorkspace = new int[4];

		// Set our bitmaps to 16-bit, 565 format.
		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		mContext = context;
	}

	public int[] getConfigSpec() {
		// We don't need a depth buffer, and don't care about our
		// color depth.
		int[] configSpec = { EGL11.EGL_DEPTH_SIZE, 0, EGL11.EGL_NONE };
		return configSpec;
	}

	public void setSprites(GLAnim[] backs, Player _player,ArrayList<Soldier> _enemies2, GLAnim[] foreground, GLAnim[] total) {
		GLAnim[] gl = new GLAnim[0];
		mbackgrounds = backs;
		mplayer =  _player.getSprites().toArray(gl);
		_enemies = _enemies2;
		mforegrounds = foreground;
		mSprites = total;
	}

	/** 
	 * Changes the vertex mode used for drawing.  
	 * @param useVerts  Specifies whether to use a vertex array.  If false, the
	 *     DrawTexture extension is used.
	 * @param useHardwareBuffers  Specifies whether to store vertex arrays in
	 *     main memory or on the graphics card.  Ignored if useVerts is false.
	 */
	public void setVertMode(boolean useVerts, boolean useHardwareBuffers) {
		mUseVerts = useVerts;
		mUseHardwareBuffers = useVerts ? useHardwareBuffers : false;
	}

	/** Draws the sprites. */
	@Override
	public void drawFrame(GL10 gl) {
		if (mSprites != null) {
			GLAnim._currentTimeMillis = System.currentTimeMillis();

			gl.glMatrixMode(GL11.GL_MODELVIEW);

			if (mUseVerts) {
				Grid.beginDrawing(gl, true, false);
			}
			//Log.d("", "mbackgrounds length:"+mbackgrounds.length);
			for (int x = 0; x < mbackgrounds.length; x++) {
				mbackgrounds[x].draw(gl);
			}		

			if(_enemies != null){
				//Log.d("", "ENEMIES NUMBER: "+_enemies.size());
				for(int x = 0; x < _enemies.size(); x++) {

					for(int y =0;y<_enemies.get(x).getSprites().size();y++)
						_enemies.get(x).getSprites().get(y).draw(gl);

				}
			}


			//Log.d("", "mplayer length:"+mplayer.length);
			for (int x = 0; x < mplayer.length; x++) {
				if( !mplayer[x].getResourceName().equals("armfire"))
					mplayer[x].draw(gl);
			}	

			GLBullets.get().getSprite().draw(gl);


			final Player player= ((Player) mplayer[0].getCharacter());			
			if(player.isShooting()){
				final Picture currentPlayerPic = player.getCurrentPlayerAnim().getFrames().get(player._state.get(player.getCurrentAnimName()).currentindex);
				final Pair<Integer,Integer> p =currentPlayerPic .fireAnchor;

				int deltaX = (int) (p.first - currentPlayerPic.imageAnchor.first);
				int deltaY =0;
				if(player._state.get("armfire")!= null){
					deltaY = deltaY + currentPlayerPic.floorPos;
				}
				getAnim("armfire").setOffsetPos(deltaX, - deltaY);
				getAnim("armfire").draw(gl);
			}


			//Log.d("", "mforegrounds length:"+mforegrounds.length);

			for (int x = 0; x < mforegrounds.length; x++) {
				mforegrounds[x].draw(gl);
			}

			if (mUseVerts) {
				Grid.endDrawing(gl);
			}
			//Log.d("", "_______________");
			FrameRateCounter.incrementFrameCount();
		}

	}

	/* Called when the size of the window changes. */
	@Override
	public void sizeChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL11.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);

		gl.glShadeModel(GL11.GL_FLAT);
		gl.glEnable(GL11.GL_BLEND);
		gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
		gl.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Called whenever the surface is created.  This happens at startup, and
	 * may be called again at runtime if the device context is lost (the screen
	 * goes to sleep, etc).  This function must fill the contents of vram with
	 * texture data and (when using VBOs) hardware vertex arrays.
	 */
	@Override
	public void surfaceCreated(GL10 gl) {


		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		gl.glShadeModel(GL11.GL_FLAT);
		gl.glDisable(GL11.GL_DEPTH_TEST);
		gl.glEnable(GL11.GL_TEXTURE_2D);
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		gl.glDisable(GL11.GL_DITHER);
		gl.glDisable(GL11.GL_LIGHTING);

		gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (mSprites != null) {

			// If we are using hardware buffers and the screen lost context
			// then the buffer indexes that we recorded previously are now
			// invalid.  Forget them here and recreate them below.
			if (mUseHardwareBuffers) {
				for (int x = 0; x < mSprites.length; x++) {
					// Ditch old buffer indexes.
					for(int j =0;j<mSprites[x].getGrids().length;j++){
						mSprites[x].getGrids()[j].invalidateHardwareBuffers();
					}
				}
			}

			// Load our texture and set its texture name on all sprites.

			// To keep this sample simple we will assume that sprites that share
			// the same texture are grouped together in our sprite list. A real
			// app would probably have another level of texture management, 
			// like a texture hash.

			String lastLoadedResource = "";
			int lastTextureId = -1;

			for (int x = 0; x < mSprites.length; x++) {
				String resource = mSprites[x].getResourceName();
				if (resource != lastLoadedResource) {
					lastTextureId = loadBitmap(mContext, gl, resource);
					lastLoadedResource = resource;
				}
				mSprites[x].setTextureName(lastTextureId);
				if (mUseHardwareBuffers) {
					for(int j =0;j<mSprites[x].getGrids().length;j++){
						Grid currentGrid = mSprites[x].getGrids()[j];
						if (!currentGrid.usingHardwareBuffers()) {
							currentGrid.generateHardwareBuffers(gl);
						}
					}
					//mSprites[x].getGrid().generateHardwareBuffers(gl);
				}
			}
		}
	}

	/**
	 * Called when the rendering thread shuts down.  This is a good place to
	 * release OpenGL ES resources.
	 * @param gl
	 */
	public void shutdown(GL11 gl) {
		if (mSprites != null) {

			String lastFreedResource = "";
			int[] textureToDelete = new int[1];

			for (int x = 0; x < mSprites.length; x++) {
				String resource = mSprites[x].getResourceName();
				if (resource != lastFreedResource) {
					textureToDelete[0] = mSprites[x].getTextureName();
					gl.glDeleteTextures(1, textureToDelete, 0);
					mSprites[x].setTextureName(0);
				}
				if (mUseHardwareBuffers) {
					for(int j =0;j<mSprites[x].getGrids().length;j++){
						mSprites[x].getGrids()[j].releaseHardwareBuffers(gl);
					}
				}
			}
		}
	}

	/** 
	 * Loads a bitmap into OpenGL and sets up the common parameters for 
	 * 2D texture maps. 
	 */
	protected int loadBitmap(Context context, GL10 gl, String resourceName) {
		int textureName = -1;
		if (context != null && gl != null) {
			gl.glGenTextures(1, mTextureNameWorkspace, 0);

			textureName = mTextureNameWorkspace[0];
			gl.glBindTexture(GL11.GL_TEXTURE_2D, textureName);

			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

			gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
			int error = gl.glGetError();
			if (error != GL11.GL_NO_ERROR) {
				Log.e("SpriteMethodTest", "Texture Load GLError1: " + error);
			}
			Log.d("", "resourceID: "+resourceName);
			//InputStream is = context.getResources().openRawResource(resourceId);
			InputStream is = null;
			Bitmap bitmap;
			try {
				try {
					is = mContext.getAssets().open(resourceName);
				} catch (IOException e) {
					try {
						is = mContext.getAssets().open(resourceName+".png");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("",e.getMessage());
					// Ignore.
				}
			}

			error = gl.glGetError();
			if (error != GL11.GL_NO_ERROR) {
				Log.e("SpriteMethodTest", "Texture Load GLError1.5: " + error);
			}
			int[] maxTextureSize = new int[1];
			gl.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
			Log.d("", "width: "+bitmap.getWidth());
			Log.d("", "height: "+bitmap.getHeight()+"   max:"+maxTextureSize[0]);

			int[] tmp = new int[1];
			gl.glGenTextures(1, tmp, 0);

			gl.glBindTexture(GL11.GL_TEXTURE_2D, textureName);

			GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);


			//gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

			//gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			error = gl.glGetError();
			if (error != GL11.GL_NO_ERROR) {
				Log.e("SpriteMethodTest", "Texture Load GLError2: " + error);
			}

			mCropWorkspace[0] = 0;
			mCropWorkspace[1] = bitmap.getHeight();
			mCropWorkspace[2] = bitmap.getWidth();
			mCropWorkspace[3] = -bitmap.getHeight();

			bitmap.recycle();

			((GL11) gl).glTexParameteriv(GL11.GL_TEXTURE_2D, 
					GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);


			error = gl.glGetError();
			if (error != GL11.GL_NO_ERROR) {
				Log.e("SpriteMethodTest", "Texture Load GLError: " + error);
			}

		}

		return textureName;
	}



	public GLAnim getAnim(String animName) {
		for(int i=0;i<mplayer.length;i++){
			if(mplayer[i].getResourceName().equals(animName))
				return mplayer[i];
		}
		return null;

	}





}
