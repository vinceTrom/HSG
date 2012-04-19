package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL10;


/**
 * This is the OpenGL ES version of a sprite.  It is more complicated than the
 * CanvasSprite class because it can be used in more than one way.  This class
 * can draw using a grid of verts, a grid of verts stored in VBO objects, or
 * using the DrawTexture extension.
 */

public class GLAnim extends Renderable {
	// The OpenGL ES texture handle to draw.
	private int mTextureName;
	// The id of the original resource that mTextureName is based on.
	private String mResourceName;
	// If drawing with verts or VBO verts, the grid object defining those verts.
	private Grid[] mGrid;
	private int currentindex = 0;

	public GLAnim(String resourceName) {
		super();
		mResourceName = resourceName;
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
	float textureCoordinates[] = {0.0f, 0.5f,
			0.5f, 0.5f,
			0.0f, 0.0f,
			0.5f, 0.0f };

	public void draw(GL10 gl) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureName);
		// Draw using verts or VBO verts.
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		
		
			gl.glTranslatef(
					x, 
					y, 
					z);
		 
		
		mGrid[currentindex].draw(gl, true, false);

		gl.glPopMatrix();

	}
}
