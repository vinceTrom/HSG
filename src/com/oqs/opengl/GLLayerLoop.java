package com.oqs.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class GLLayerLoop extends Renderable{

	protected GLAnim _sprite;

	public GLLayerLoop(String resourceName, boolean tiled) {
		_sprite = new GLAnim(resourceName, tiled, this);
	}

	@Override
	protected void finalDraw(GL10 gl, Grid grid){

		if(x+_sprite.textureWidth<OpenglActivity._screenWidth){
			grid.draw(gl, true, false);

			float tx = x;
			x = _sprite.textureWidth;
			gl.glTranslatef(x, 0, 0);
			grid.draw(gl, true, false);
			if(tx+2*_sprite.textureWidth<OpenglActivity._screenWidth){
				x = _sprite.textureWidth;
				gl.glTranslatef(x, 0, 0);
				grid.draw(gl, true, false);
			}
			x = tx;
			if(x+_sprite.textureWidth<0){
				x =0 ;
			}
		}else
			grid.draw(gl, true, false);

	}

	public GLAnim getSprite() {
		return _sprite;
	}

	

}
