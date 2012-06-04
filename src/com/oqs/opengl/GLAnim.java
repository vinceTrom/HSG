package com.oqs.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Pair;

/**
 * This is the OpenGL ES version of a sprite.  It is more complicated than the
 * CanvasSprite class because it can be used in more than one way.  This class
 * can draw using a grid of verts, a grid of verts stored in VBO objects, or
 * using the DrawTexture extension.
 */

public class GLAnim extends Renderable {
	// The OpenGL ES texture handle to draw.
	protected int mTextureName;
	
	public static OpenglActivity activity;
	// The id of the original resource that mTextureName is based on.
	private String mResourceName;
	protected Grid[] mGrid;
	private int currentindex = 0;
	public boolean loop = true;
	private long lastDraw=0;
	private boolean _tiled = true;
	private ArrayList<Picture> _frames = null;
	private int _period = 80;
	public boolean mustDraw = true;
	
	public int textureWidth = 0;
	public int textureHeight = 0;


	public GLAnim(String resourceName, boolean tiled) {
		super();
		this.mResourceName = resourceName;
		this._tiled = tiled;
	}
	
	public void setTextureDimensions(int w, int h){
		textureWidth = w;
		textureHeight = h;
	}


	public void draw(GL10 gl) {
		if(mustDraw){

			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureName);
			// Draw using verts or VBO verts.
			gl.glPushMatrix();
			gl.glLoadIdentity();

			if(_tiled){
				if(System.currentTimeMillis()- _period >lastDraw){
					lastDraw = System.currentTimeMillis();
					if(loop)
						currentindex = (currentindex+1)%mGrid.length;
					else
						currentindex = Math.min(currentindex+1,mGrid.length-1);
					//Log.d("", "currentindex: "+currentindex+"  length: "+mGrid.length+" posX= "+x);
				}

				gl.glTranslatef(
						x - _frames.get(currentindex).anchor.first, 
						y - _frames.get(currentindex).anchor.second, 
						z);
			}
			finalDraw(gl, mGrid[currentindex]);
			gl.glPopMatrix();
		}
	}
	
	public Pair<Integer, Integer> getAnchor(){
		int moyX = 0;
		int moyY = 0;
		for(int i = 0;i<_frames.size();i++){
			moyX = moyX + _frames.get(i).anchor.first;
			moyY = moyY + _frames.get(i).anchor.second;
		}
		Pair p = new Pair<Integer, Integer>(moyX/_frames.size(), moyY/_frames.size());
		return p;
	}






	public void setPictures(ArrayList<Picture> ls){
		_frames = ls;
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


	protected void finalDraw(GL10 gl, Grid grid){
		grid.draw(gl, true, false);
	}
	
	public void initAnim(){
		currentindex = 0;
	}

}
