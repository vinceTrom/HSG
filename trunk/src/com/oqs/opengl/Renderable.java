package com.oqs.opengl;

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

	public void setXVelocity(float velocity){
		velocityX = OpenglActivity._screenHeight*velocity;
	}

	public void setYVelocity(float velocity){
		velocityY = OpenglActivity._screenHeight*velocity;

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
}
