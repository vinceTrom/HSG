package com.oqs.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class Scene extends Activity{

	
	private GLSurfaceView mGLSurfaceView;
	public static int _screenHeight;
	public static int _screenWidth;
	public ArrayList<GLLayerLoop> _backsSprites = new ArrayList<GLLayerLoop>();
	public ArrayList<GLLayerLoop> _foregroundSprites = new ArrayList<GLLayerLoop>();
	private SimpleGLRenderer spriteRenderer;
	protected Player _player;
	protected ArrayList<Soldier> _enemies = new ArrayList<Soldier>();
	
	public abstract int getLayoutId();
	public abstract void init();

	

	@Override
	public void onStop(){
		super.onStop();
		Character.clearSprites();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		_screenHeight = displaymetrics.heightPixels;
		_screenWidth = displaymetrics.widthPixels;

		setContentView(getLayoutId());
		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gLSurfaceView);

		spriteRenderer = new SimpleGLRenderer(this);

		final boolean useVerts = true;
		final boolean useHardwareBuffers = true;


		// We need to know the width and height of the display pretty soon,
		// so grab the information now.
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// This list of things to move. It points to the same content as the
		// sprite list except for the background.
		Constants.LEVEL_SPEED = 1;
		Constants.GROUND_LEVEL = (int) (0.07f*_screenHeight);
		createLevelAnims();
		_player = new Player(this);
		GLBullets bullet = new GLBullets(this);

		// Now's a good time to run the GC.  Since we won't do any explicit
		// allocation during the test, the GC should stay dormant and not
		// influence our results.
		Runtime r = Runtime.getRuntime();
		r.gc(); 

		ArrayList<GLAnim> all = new ArrayList<GLAnim>();
		for(GLLayerLoop layer:_backsSprites)
			all.add(layer.getSprite());
		all.addAll(_player.getSprites());
		all.add(bullet.getSprite());
		all.add(GLBullets.get().getSprite());

		//_enemies.add();
		final Soldier soldier = new Soldier(this){
			@Override
			public boolean musDrawThisAnim(String resourceName) {
				return false;
			}
		};
		all.addAll(soldier.getSprites());

		for(GLLayerLoop layer:_foregroundSprites)
			all.add(layer.getSprite());
		GLAnim[] gl = new GLAnim[0];
		ArrayList<GLAnim> backs = new ArrayList<GLAnim>();
		for(GLLayerLoop layer:_backsSprites)
			backs.add(layer.getSprite());
		ArrayList<GLAnim> fores = new ArrayList<GLAnim>();
		for(GLLayerLoop layer:_foregroundSprites)
			fores.add(layer.getSprite());
		spriteRenderer.setSprites(backs.toArray(gl), _player, _enemies, fores.toArray(gl), all.toArray(gl));
		spriteRenderer.setVertMode(useVerts, useHardwareBuffers);

		mGLSurfaceView.setRenderer(spriteRenderer);

		Mover simulationRuntime = new Mover(_screenHeight);
		ArrayList<Renderable> allRender = new ArrayList<Renderable>();
		allRender.addAll(_backsSprites);
		allRender.add(_player);

		allRender.addAll(_foregroundSprites);
		Renderable[] gl2 = new Renderable[0];
		simulationRuntime.setRenderables(allRender.toArray(gl2),_enemies,GLBullets.get()._bulletList);

		mGLSurfaceView.setEvent(simulationRuntime);
		
		init();
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
			GLLayerLoop layer = new GLLayerLoop(anim_name, true);
			GLUtils.createAnim(this, layer.getSprite(), elems.get(i));
			if(elems.get(i).getAttributes().containsKey("foreground") && elems.get(i).getAttributes().get("foreground").equals("true"))
				_foregroundSprites.add(layer);
			else
				_backsSprites.add(layer);
		}		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}
