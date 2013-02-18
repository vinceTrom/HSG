package com.oqs.opengl;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.util.Pair;

import com.oqs.opengl.lib.MMXMLElement;

public class GLUtils {

	private  static float SPRITE_WIDTH = 0;
	private static float SPRITE_HEIGHT = 0;



	public static void createAnim(Context ctx, GLAnim sprite, MMXMLElement animElem){
		BitmapFactory.Options opt = new Options();
		opt.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeStream(ctx.getAssets().open(sprite.getResourceName()+".png"),null,opt);
		} catch (IOException e) {e.printStackTrace();}
		SPRITE_WIDTH = opt.outWidth;
		SPRITE_HEIGHT = opt.outHeight;

		sprite.setGrids(createGrids(sprite, sprite.getResourceName(), animElem));
	}

	public static Grid[] createGrids(GLAnim glanim, String animName, MMXMLElement anim){
		/*
		if(_gridsMap.containsKey(animName)){
			try{
				glanim.setAnimPeriod(Integer.parseInt(anim.getAttributes().get("period")));
			}catch(Exception e){}
			glanim.setPictures(_picturesMap.get(animName));
			return _gridsMap.get(animName);
		}
		 */

		float picSizeOnScreenRatio = 0.3f;//relative � la hauteur de l'�cran

		float maxheightPic = 0f;

		ArrayList<Picture> pictures = new ArrayList<Picture>();
		if(animName.equals("armfire"))
			Log.d("","ici");
		int nbframes = anim.getElementsForKey("image").size();
		int anchorYmin = 99999;
		for(int i=0;i<nbframes;i++){
			int x = Integer.parseInt(anim.getElement(i).getAttributes().get("x"));
			int y = Integer.parseInt(anim.getElement(i).getAttributes().get("y"));
			int width = Integer.parseInt(anim.getElement(i).getAttributes().get("width"));
			int height = Integer.parseInt(anim.getElement(i).getAttributes().get("height"));
			int anchorx = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"));//(int) ((height/(float) h/picSizeOnScreen)*(width - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"))));
			int anchory = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY")) - height;//(int) ((height/(float)h/picSizeOnScreen)*(height - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"))));
			anchorYmin = Math.min(anchorYmin, anchory);
			if(animName.equals("armfire"))
				anchorYmin=0;
			int fireAnchorX = 0;
			int fireAnchorY = 0;
			int floorPos =0;
			try{
				fireAnchorX = Integer.parseInt(anim.getElement(i).getAttributes().get("fireanchorX"));
				fireAnchorY= Integer.parseInt(anim.getElement(i).getAttributes().get("fireanchorY"));
				floorPos = Integer.parseInt(anim.getElement(i).getAttributes().get("floorpos"));
			}catch(Exception e){}
			pictures.add(new Picture((int)(x),(int)(y),(int)(width),(int)(height),(int)(anchorx),(int)(anchory), fireAnchorX, fireAnchorY, floorPos));
			if(height > maxheightPic)
				maxheightPic = height;
		}
		glanim.setPictures(pictures);

		if(anim.getAttributes().get("Xvelocity") != null){
			glanim.getCharacter().setXVelocity(Double.parseDouble(anim.getAttributes().get("Xvelocity")));
			glanim.getCharacter().x = 0;
		}


		if(anim.getAttributes().get("size") != null){
			picSizeOnScreenRatio = Float.parseFloat(anim.getAttributes().get("size"));
		}

		if(anim.getAttributes().get("y") != null){
			glanim.getCharacter().y = Float.parseFloat(anim.getAttributes().get("y"))*OpenglActivity._screenHeight;
		}

		/*
		glanim.x=200;
		glanim.y=200;
		glanim.setXVelocity(0.33f);
		glanim.setYVelocity(0.1f);
		 */

		try{
			glanim.setAnimPeriod(Integer.parseInt(anim.getAttributes().get("period")));
		}catch(Exception e){}


		Grid[] grids = new Grid[nbframes];
		float averageWidth = 0;
		float averageHeight = 0;
		for(int frameindex = 0;frameindex<nbframes;frameindex++){
			// Setup a quad for the sprites to use.
			float Xoffset = pictures.get(frameindex).orig.first/SPRITE_WIDTH;
			float Yoffset = pictures.get(frameindex).orig.second/SPRITE_HEIGHT;
			float Xratio = pictures.get(frameindex).width/SPRITE_WIDTH;
			float Yratio = pictures.get(frameindex).height/SPRITE_HEIGHT;
			Grid picGrid = new Grid(2, 2, false);

			int textureheight = (int) ((pictures.get(frameindex).height/maxheightPic)*picSizeOnScreenRatio*OpenglActivity._screenHeight);
			float ratio = (textureheight/(float)pictures.get(frameindex).height);
			int texturewidth = (int) (ratio*pictures.get(frameindex).width);
			averageWidth = averageWidth+texturewidth;
			averageHeight = averageHeight+textureheight;
			glanim.setTextureDimensions(texturewidth, textureheight);

			picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
			picGrid.set(1, 0, texturewidth, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
			picGrid.set(0, 1, 0.0f, textureheight, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
			picGrid.set(1, 1, texturewidth, textureheight, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);

			pictures.get(frameindex).width = (int) (pictures.get(frameindex).width * ratio);
			pictures.get(frameindex).height = (int) (pictures.get(frameindex).height * ratio);
			pictures.get(frameindex).imageAnchor = new Pair<Integer, Integer>((int) (pictures.get(frameindex).imageAnchor.first*ratio), (int) ((pictures.get(frameindex).imageAnchor.second-anchorYmin)*ratio));

			pictures.get(frameindex).fireAnchor = new Pair<Integer, Integer>((int) (pictures.get(frameindex).fireAnchor.first*ratio), (int) ((pictures.get(frameindex).fireAnchor.second)*ratio));
			pictures.get(frameindex).floorPos = (int) (pictures.get(frameindex).floorPos * ratio);

			grids[frameindex] = picGrid;
		}
		averageWidth = averageWidth/nbframes;
		averageHeight = averageHeight/nbframes;
		glanim.setAverageWidth((int) averageWidth, (int) averageHeight);

		return grids;
	}
}
