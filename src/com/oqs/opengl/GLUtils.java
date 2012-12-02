package com.oqs.opengl;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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

		sprite.width = SPRITE_WIDTH;
		sprite.height = SPRITE_HEIGHT;
		sprite.x = 0;
		sprite.y = 0;

		sprite.setGrids(createGrids(sprite, animElem.getName(), animElem));
		//spriteList.add(sprite);
	}
	
	public static Grid[] createGrids(GLAnim glanim, String animName, MMXMLElement anim){

		float picSizeOnScreenRatio = 0.3f;//relative � la hauteur de l'�cran
		/*
		if(animName.equals("mainback")){
			picSizeOnScreenRatio = 1;
			glanim.x = 0;
			glanim.y = _screenHeight;
			glanim.setXVelocity(-0.5f);
		}
		 */
		float maxheightPic = 0f;

		ArrayList<Picture> pictures = new ArrayList<Picture>();

		int nbframes = anim.getElementsForKey("image").size();

		for(int i=0;i<nbframes;i++){
			int x = Integer.parseInt(anim.getElement(i).getAttributes().get("x"));
			int y = Integer.parseInt(anim.getElement(i).getAttributes().get("y"));
			int width = Integer.parseInt(anim.getElement(i).getAttributes().get("width"));
			int height = Integer.parseInt(anim.getElement(i).getAttributes().get("height"));
			int anchorx = Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"));//(int) ((height/(float) h/picSizeOnScreen)*(width - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorX"))));
			int anchory = height-Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"));//(int) ((height/(float)h/picSizeOnScreen)*(height - Integer.parseInt(anim.getElement(i).getAttributes().get("anchorY"))));
			pictures.add(new Picture((int)(x),(int)(y),(int)(width),(int)(height),(int)(anchorx),(int)(anchory)));
			if(height > maxheightPic)
				maxheightPic = height;
		}

		glanim.setPictures(pictures);
		if(anim.getAttributes().get("Xvelocity") != null){
			glanim.setXVelocity(Float.parseFloat(anim.getAttributes().get("Xvelocity")));
			glanim.x = 0;
		}

		if(anim.getAttributes().get("size") != null){
			picSizeOnScreenRatio = Float.parseFloat(anim.getAttributes().get("size"));
		}

		if(anim.getAttributes().get("y") != null){
			glanim.y = Float.parseFloat(anim.getAttributes().get("y"))*OpenglActivity._screenHeight;
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
			glanim.setTextureDimensions(texturewidth, textureheight);

			picGrid.set(0, 0,  0.0f, 0.0f, 0.0f, Xoffset+0.0f , 1.0f*Yratio+Yoffset, null);
			picGrid.set(1, 0, texturewidth, 0.0f, 0.0f, Xoffset+1.0f*Xratio, 1.0f*Yratio+Yoffset, null);
			picGrid.set(0, 1, 0.0f, textureheight, 0.0f, Xoffset+0.0f, 0.0f+Yoffset, null);
			picGrid.set(1, 1, texturewidth, textureheight, 0.0f, Xoffset+1.0f*Xratio, 0.0f+Yoffset, null);

			pictures.get(frameindex).anchor = new Pair<Integer, Integer>((int) (pictures.get(frameindex).anchor.first*ratio), (int) (pictures.get(frameindex).anchor.second*ratio));

			grids[frameindex] = picGrid;
		}
		return grids;
	}
}
