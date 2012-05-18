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

public class GLAnim extends Renderable {
	// The OpenGL ES texture handle to draw.
	protected int mTextureName;
	// The id of the original resource that mTextureName is based on.
	private String mResourceName;
	protected Grid[] mGrid;
	private int currentindex = 0;
	private long lastDraw=0;
	private boolean _tiled = true;
	private ArrayList<Picture> _frames = null;
	private int _period = 80;
	public boolean mustDraw = true;


	public GLAnim(String resourceName, boolean tiled) {
		super();
		this.mResourceName = resourceName;
		this._tiled = tiled;
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
					currentindex = (currentindex+1)%mGrid.length;
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
}
