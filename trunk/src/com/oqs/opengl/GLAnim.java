package com.oqs.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

/**
 * This is the OpenGL ES version of a sprite.  It is more complicated than the
 * CanvasSprite class because it can be used in more than one way.  This class
 * can draw using a grid of verts, a grid of verts stored in VBO objects, or
 * using the DrawTexture extension.
 */

public class GLAnim {
	// The OpenGL ES texture handle to draw.
	private int mTextureName;

	private ArrayList<Renderable> _renderables;
	// The id of the original resource that mTextureName is based on.
	private String mResourceName;
	protected Grid[] mGrid;
	//private int currentindex = 0;
	public boolean loop = true;
	//private long lastDraw=0;
	private boolean _tiled = true;
	private ArrayList<Picture> _frames = null;
	private int _period = 80;

	private int _averageWidth = 0;
	private int _averageHeight = 0;
	public int textureWidth = 0;
	public int textureHeight = 0;

	private int _offsetX = 0;
	private int _offsetY = 0;

	public static long _currentTimeMillis = 0;

	/*
	public GLAnim(String resourceName, boolean tiled) {
		super();
		this.mResourceName = resourceName;
		this._tiled = tiled;
	}
	 */
	public GLAnim(String resourceName, boolean tiled, Renderable character) {
		super();
		this.mResourceName = resourceName;
		this._tiled = tiled;	
		this._renderables  = new ArrayList<Renderable>();
		this._renderables.add(character);
	}

	public GLAnim(String resourceName, boolean tiled, ArrayList<Renderable> characters) {
		super();
		this.mResourceName = resourceName;
		this._tiled = tiled;	
		this._renderables  = characters;
	}

	public void setTextureDimensions(int w, int h){
		textureWidth = w;
		textureHeight = h;
	}

	public void setOffsetPos(int offsetX, int offsetY){
		_offsetX = offsetX;
		Log.d("","setOffsetPosY de armfire:"+offsetY);
		_offsetY = offsetY;
	}


	public void draw(GL10 gl) {
		for(int i = 0;i<_renderables.size();i++){
			final Renderable renderable = _renderables.get(i);

			if(!renderable._state.containsKey(mResourceName)){
				renderable._state.put(mResourceName, new RenderableAnimState());
			}

			if(renderable.musDrawThisAnim(getResourceName())){
				final RenderableAnimState state = renderable._state.get(mResourceName);

				if(_tiled){//TODO deplacer ça dans le mover
					int period = _period;
					if(getResourceName().equals("walk") || getResourceName().equals("soldier/walk"))
						period = (int) (period / Constants.LEVEL_SPEED);
					if(_currentTimeMillis - period > state.lastDraw){
						state.lastDraw = _currentTimeMillis;
						if(loop)
							state.currentindex = (state.currentindex+1)%mGrid.length;
						else
							state.currentindex = Math.min(state.currentindex+1,mGrid.length-1);
					}
				}

				gl.glBindTexture(GL11.GL_TEXTURE_2D, mTextureName);
				// Draw using verts or VBO verts.
				gl.glPushMatrix();
				gl.glLoadIdentity();

				final Picture pic = _frames.get(state.currentindex);
				gl.glTranslatef(
						renderable.x + _offsetX- pic.imageAnchor.first,
						renderable.y +_offsetY  - pic.floorPos , 
						0);
/*
				if(getResourceName().equals("back"))
					willdrawback(getResourceName());
				if(getResourceName().equals("mainback"))
					willdrawmainback(getResourceName());
				if(getResourceName().equals("first"))
					willdrawfirst(getResourceName());
*/
				renderable.finalDraw(gl, mGrid[state.currentindex]);
				gl.glPopMatrix();
			}
		}
	}
/*
	private void willdrawback(String s){
		int a = s.length();
	}

	private void willdrawmainback(String s){
		int a = s.length();
	}

	private void willdrawfirst(String s){
		int a = s.length();
	}
*/

	public void setPictures(ArrayList<Picture> ls){
		_frames = ls;
	}

	public ArrayList<Picture> getFrames(){
		return _frames;
	}

	public void setAnimPeriod(int period) {
		this._period = period;	
	}

	void setTextureName(int name) {
		mTextureName = name;
	}


	public int getTextureName() {
		return mTextureName;
	}

	public void setResourceId(String name) {
		mResourceName = name;
	}

	public String getResourceName() {
		return mResourceName;
	}


	public void setGrids(Grid[] grid) {
		mGrid = grid;
	}

	public Grid[] getGrids() {
		return mGrid;
	}

	/*
	protected void finalDraw(GL11 gl, Grid grid){
		grid.draw(gl, true, false);
	}
	 */
	public void initAnim(){
		getCharacter()._state.get(mResourceName).currentindex = 0;
	}



	public Renderable getCharacter() {
		return _renderables.get(0);
	}

	public void addCharacter(Renderable character) {
		_renderables.add(character);		
	}

	public void setAverageWidth(int width, int height) {
		_averageWidth = width;
		_averageHeight = height;
	}

	public float getAverageWidth() {
		return _averageWidth;
	}

	public float getAverageHeight() {
		return _averageHeight;
	}

	public String toString(){
		return getResourceName();
	}

}
