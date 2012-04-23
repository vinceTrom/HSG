package com.oqs.opengl;

import android.util.Pair;

public class Picture {

	public Pair<Integer,Integer> orig;
	public int width;
	public int height;
	public Pair<Integer, Integer> anchor;

	public Picture(int x, int y, int width, int height, int anchorX, int anchorY){
		orig = new Pair<Integer, Integer>(x, y);
		this.width = width;
		this.height = height;
		anchor = new Pair<Integer, Integer>(anchorX, anchorY);		
	}	
}
