package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL10;


public class GLBullet extends Renderable{

	@Override
	protected void finalDraw(GL10 gl, Grid grid) {
		grid.draw(gl, true, false);
	}

}
