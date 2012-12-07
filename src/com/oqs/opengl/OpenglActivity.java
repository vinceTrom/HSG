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

	private GLSurfaceView mGLSurfaceView;
	public static int _screenHeight;
	public static int _screenWidth;
	public ArrayList<GLAnim> backsSprites = new ArrayList<GLAnim>();
	//public ArrayList<GLAnim> playerSprites = new ArrayList<GLAnim>();
	public ArrayList<GLAnim> foregroundSprites = new ArrayList<GLAnim>();
	private SimpleGLRenderer spriteRenderer;
	private Player _player;
private ArrayList<Enemy> _enemies = new ArrayList<Enemy>();
	




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
			public void onClick(View v) {_player.jump();Log.d("", "JUMP ARROUND");}		
		});
		findViewById(R.id.shootbtn).setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					_player.stopShoot();
				}else{
					if(event.getAction() == MotionEvent.ACTION_DOWN)
						_player.shoot();
				}
				return true;
			}
		});
		spriteRenderer = new SimpleGLRenderer(this);

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
		Constants.GROUND_LEVEL = (int) (0.3*_screenHeight);
		createLevelAnims();
		_player = new Player(this);
		_enemies.add(new Enemy(this));

		// Now's a good time to run the GC.  Since we won't do any explicit
		// allocation during the test, the GC should stay dormant and not
		// influence our results.
		Runtime r = Runtime.getRuntime();
		r.gc(); 

		ArrayList<GLAnim> all = new ArrayList<GLAnim>();
		all.addAll(backsSprites);
		all.addAll(_player.getSprites());
		all.addAll(_enemies.get(0).getSprites());
		all.addAll(foregroundSprites);
		GLAnim[] gl = new GLAnim[0];
		spriteRenderer.setSprites(backsSprites.toArray(gl), _player, _enemies, foregroundSprites.toArray(gl), all.toArray(gl));
		spriteRenderer.setVertMode(useVerts, useHardwareBuffers);

		mGLSurfaceView.setRenderer(spriteRenderer);

		if (animate) {
			Mover simulationRuntime = new Mover(this, _screenHeight);
			simulationRuntime.setRenderables(all.toArray(gl));

			mGLSurfaceView.setEvent(simulationRuntime);
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



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}


	public void fall() {
		_player.fall();		
	}
	public void fallFinished() {
		_player.fallFinished();		
	}




}