package com.oqs.opengl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.oqs.opengl.lib.FrameRateCounter;
import com.oqs.opengl.lib.FrameRateCounter.FrameRateListener;
import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;
import com.oqs.opengl.lib.MMXMLParser;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class OpenglActivity extends Activity {

	private Timer _timer;
	private GLSurfaceView mGLSurfaceView;
	public static int _screenHeight;
	public static int _screenWidth;
	public ArrayList<GLLayerLoop> backsSprites = new ArrayList<GLLayerLoop>();
	public ArrayList<GLLayerLoop> foregroundSprites = new ArrayList<GLLayerLoop>();
	private SimpleGLRenderer spriteRenderer;
	private Player _player;
	private ArrayList<Enemy> _enemies = new ArrayList<Enemy>();
	private FrameRateCounter _frameRateCounter = null;

	@Override
	protected void onDestroy (){
		super.onDestroy();
		_timer.cancel();
	}

	@Override
	public void onStop(){
		super.onStop();
		_frameRateCounter.setFrameRateListener(null);
		Character.clearSprites();
	}

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

		final Intent callingIntent = getIntent();
		// Allocate our sprites and add them to an array.
		//final int robotCount = 4;//ANIM.equals("all")?12:2;//callingIntent.getIntExtra("spriteCount", 10);
		final boolean useVerts = true;
		final boolean useHardwareBuffers = 
				callingIntent.getBooleanExtra("useHardwareBuffers", false);


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
		for(GLLayerLoop layer:backsSprites)
			all.add(layer.getSprite());
		all.addAll(_player.getSprites());
		all.add(bullet.getSprite());
		all.add(GLBullets.get().getSprite());

		_enemies.add(new Enemy(this));
		all.addAll(_enemies.get(0).getSprites());
		_timer = new Timer();
		_timer.schedule(new TimerTask() {

			@Override
			public void run() {
					_enemies.add(new Enemy(OpenglActivity.this));	
				
				Constants.LEVEL_SPEED = Constants.LEVEL_SPEED+0.02;
			}
		}, 5000, 4000);

		for(GLLayerLoop layer:foregroundSprites)
			all.add(layer.getSprite());
		GLAnim[] gl = new GLAnim[0];
		ArrayList<GLAnim> backs = new ArrayList<GLAnim>();
		for(GLLayerLoop layer:backsSprites)
			backs.add(layer.getSprite());
		ArrayList<GLAnim> fores = new ArrayList<GLAnim>();
		for(GLLayerLoop layer:foregroundSprites)
			fores.add(layer.getSprite());
		spriteRenderer.setSprites(backs.toArray(gl), _player, _enemies, fores.toArray(gl), all.toArray(gl));
		spriteRenderer.setVertMode(useVerts, useHardwareBuffers);

		mGLSurfaceView.setRenderer(spriteRenderer);

		Mover simulationRuntime = new Mover(this, _screenHeight);
		ArrayList<Renderable> allRender = new ArrayList<Renderable>();
		allRender.addAll(backsSprites);
		allRender.add(_player);

		allRender.addAll(foregroundSprites);
		Renderable[] gl2 = new Renderable[0];
		simulationRuntime.setRenderables(allRender.toArray(gl2),_enemies,GLBullets.get()._bulletList);

		mGLSurfaceView.setEvent(simulationRuntime);

	}

	@Override
	public void onResume(){
		super.onResume();
		TextView fpsUI = (TextView) findViewById(R.id.fpsviewer);
		final fpsRun fpsRun = new fpsRun();
		fpsRun.fpsUI = fpsUI;
		_frameRateCounter = new FrameRateCounter();
		_frameRateCounter.setFrameRateListener(new FrameRateListener() {

			@Override
			public void frameRateUpdated(int framenb) {
				fpsRun._frameNB = framenb;
				runOnUiThread(fpsRun);
			}
		});
	}
private StringBuilder stgbuild = new StringBuilder();
private static final String txtheader = "FPS: ";
	private class fpsRun implements Runnable{

		public int _frameNB=0;
		public TextView fpsUI;
		public void run() {
			stgbuild.setLength(0);
			fpsUI.setText(stgbuild.append(txtheader).append(_frameNB));	
			
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
			GLLayerLoop layer = new GLLayerLoop(anim_name, true);
			GLUtils.createAnim(this, layer.getSprite(), elems.get(i));
			if(elems.get(i).getAttributes().containsKey("foreground") && elems.get(i).getAttributes().get("foreground").equals("true"))
				foregroundSprites.add(layer);
			else
				backsSprites.add(layer);
		}		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}