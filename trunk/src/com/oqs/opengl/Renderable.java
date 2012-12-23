package com.oqs.opengl;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;

/** 
 * Base class defining the core set of information necessary to render (and move
 * an object on the screen.  This is an abstract type and must be derived to
 * add methods to actually draw (see CanvasSprite and GLSprite).
 */
public abstract class Renderable {
	// Position.
	public float x;
	public float y;
	public float z;
	
	public boolean applyGravity = false;
	
	public HashMap<String,  RenderableAnimState> _state = new HashMap<String, RenderableAnimState>();

	public void setXVelocity(float velocity){
		velocityX = OpenglActivity._screenHeight*velocity;
	}

	public void setYVelocity(float velocity){
		velocityY = OpenglActivity._screenHeight*velocity;

	}
	protected abstract void finalDraw(GL10 gl, Grid grid);
	
	public Rect getBoundRect() {
		return new Rect((int)x, (int)(y-height), (int)(x+width),(int) (y));
	}

	// Velocity.
	/**
	 * the speed, in screenheight/seconds
	 */
	public float velocityX;
	/**
	 *  the speed, in screenheight/seconds
	 */
	public float velocityY;
	public float velocityZ;

	// Size.
	public float width;
	public float height;

	public boolean musDrawThisAnim(String resourceName) {
		return true;
	}
}
