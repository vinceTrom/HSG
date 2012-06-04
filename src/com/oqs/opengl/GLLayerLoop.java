package com.oqs.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class GLLayerLoop extends GLAnim{

	public GLLayerLoop(String resourceName, boolean tiled) {
		super(resourceName, tiled);
	}

	@Override
	protected void finalDraw(GL10 gl, Grid grid){

		if(x+textureWidth<OpenglActivity._screenWidth){
			grid.draw(gl, true, false);

			float tx = x;
			x = textureWidth;
			gl.glTranslatef(x, 0, 0);
			grid.draw(gl, true, false);
			if(tx+2*textureWidth<OpenglActivity._screenWidth){
				x = textureWidth;
				gl.glTranslatef(x, 0, 0);
				grid.draw(gl, true, false);
			}
			x = tx;
			if(x+textureWidth<0){
				x =0 ;
			}
		}else
			grid.draw(gl, true, false);

	}

	

}
