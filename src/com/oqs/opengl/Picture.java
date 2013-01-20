package com.oqs.opengl;

import android.util.Pair;
/**
 * Used to define a pic in an animation
 * @author Vince
 *
 */
public class Picture {

	public Pair<Integer,Integer> orig;
	public int width;
	public int height;
	public Pair<Integer, Integer> imageAnchor;
	public Pair<Integer, Integer> fireAnchor;
	public int floorPos;

	public Picture(int x, int y, int width, int height, int anchorX, int anchorY, int fireAnchorX,int fireAnchorY, int floorPosition){
		orig = new Pair<Integer, Integer>(x, y);
		this.width = width;
		this.height = height;
		imageAnchor = new Pair<Integer, Integer>(anchorX, anchorY);	
		fireAnchor = new Pair<Integer, Integer>(fireAnchorX, fireAnchorY);	
		floorPos = floorPosition;
	}	
}
