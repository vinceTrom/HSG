package com.oqs.opengl;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

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

	public void setXVelocity(double d){
		velocityX = Level1._screenHeight*d;
	}

	public void setYVelocity(float velocity){
		velocityY = Level1._screenHeight*velocity;

	}
	protected abstract void finalDraw(GL10 gl, Grid grid);
	
	public Rect getBoundRect() {
		return new Rect((int)x, (int)(y-height), (int)(x+width),(int) (y));
	}

	// Velocity.
	/**
	 * the speed, in screenheight/seconds
	 */
	public double velocityX;
	/**
	 *  the speed, in screenheight/seconds
	 */
	public double velocityY;
	public double velocityZ;

	// Size.
	public float width;
	public float height;

	public boolean musDrawThisAnim(String resourceName) {
		return true;
	}

	public abstract boolean mustDraw();

	public void animFinished(String resourceName) {
		
	}
}
