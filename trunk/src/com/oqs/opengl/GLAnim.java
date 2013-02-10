package com.oqs.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

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
		synchronized(OpenglActivity.class){
			for(int i = 0;i<_renderables.size();i++){
				if(!_renderables.get(i)._state.containsKey(mResourceName)){
					_renderables.get(i)._state.put(mResourceName, new RenderableAnimState());
				}
				if(_renderables.get(i).musDrawThisAnim(getResourceName())){
					 RenderableAnimState state = _renderables.get(i)._state.get(mResourceName);
					 
					if(_tiled){
						if(System.currentTimeMillis()- _period >state.lastDraw){
							state.lastDraw = System.currentTimeMillis();
							if(loop)
								state.currentindex = (state.currentindex+1)%mGrid.length;
							else
								state.currentindex = Math.min(state.currentindex+1,mGrid.length-1);
						}
					}

					gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureName);
					// Draw using verts or VBO verts.
					gl.glPushMatrix();
					gl.glLoadIdentity();
					try{
						/*
						if(getResourceName().equals("walk")){
							Log.d("", "walk x=" +(_renderables.get(i).x + _offsetX- _frames.get(state.currentindex).imageAnchor.first));
							Log.d("","walk width: "+_renderables.get(i).width);
						}
						*/
						if(getResourceName().equals("armfire"))
							Log.d(""," armfire y: "+_renderables.get(i).y +_offsetY + _frames.get(state.currentindex).imageAnchor.second);

						gl.glTranslatef(
								_renderables.get(i).x + _offsetX- _frames.get(state.currentindex).imageAnchor.first,
								_renderables.get(i).y +_offsetY  + _frames.get(state.currentindex).imageAnchor.second , 
								0);

					}catch (Exception e){e.printStackTrace();}

					_renderables.get(i).finalDraw(gl, mGrid[state.currentindex]);
					gl.glPopMatrix();
				}
			}
		}
	}


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
	protected void finalDraw(GL10 gl, Grid grid){
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
