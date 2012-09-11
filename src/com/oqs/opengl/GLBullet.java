package com.oqs.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class GLBullet extends GLAnim{

	public ArrayList<Integer[]> _posList = new ArrayList<Integer[]>();

	public GLBullet(String resourceName, boolean tiled) {
		super(resourceName, tiled);
	}

	@Override
	protected void finalDraw(GL10 gl, Grid grid){
		for(int j=0;j<_posList.size();j++){
			if(_posList.get(j)[0]>OpenglActivity._screenWidth){
				_posList.remove(j);
			}else{
				gl.glTranslatef( _posList.get(j)[0], _posList.get(j)[1], 0);
				grid.draw(gl, true, false);
				gl.glTranslatef( -1*_posList.get(j)[0], -1*_posList.get(j)[1], 0);
			}
		}
	}

	public void updateBulletPos(int deltaX){
		//Log.d("", "updatebulletpos:"+deltaX);
		for(int i = 0;i<_posList.size();i++){
			_posList.get(i)[0] = _posList.get(i)[0] + deltaX;
		}
	}

	public void newBullet(int x, int y){
		Log.d("", "new bullet:"+x + "  "+y);
		Integer[] tab = {x,y};
		_posList.add(tab);
	}

}