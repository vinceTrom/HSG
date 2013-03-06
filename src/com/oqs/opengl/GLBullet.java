package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL11;


public class GLBullet extends Renderable{

	@Override
	protected void finalDraw(GL11 gl, Grid grid) {
		grid.draw(gl, true, false);
	}

}
