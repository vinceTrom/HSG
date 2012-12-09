package com.oqs.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.content.Context;

public abstract class Character {

	protected ArrayList<GLAnim> _sprites = new ArrayList<GLAnim>();
	
	public Character(Context ctx, String fileName){
		createAnims(ctx, fileName);
		initAnims();
	}
	
	protected void createAnims(Context ctx, String fileName){

		InputStream ss = null;
		try {
			ss = ctx.getAssets().open(fileName);
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElement elem = parser.parseSynchronously().getRootElement();
		MMXMLElements elems = elem.getElementForKey("player").getElementForKey("animations").getElementsForKey("animation");
		for(int i=0;i<elems.size();i++){
			String anim_name = elems.get(i).getAttributes().get("name");
			GLAnim anim = new GLAnim(anim_name, true, this);
			GLUtils.createAnim(ctx, anim, elems.get(i));
			anim.mustDraw = false;
			_sprites.add(anim);			
		}
	}	protected abstract void initAnims();
	
	public ArrayList<GLAnim> getSprites(){
		return _sprites;
	}

	public abstract void isTouchedByBullet();
	
	public GLAnim getAnim(String animName) {
		for(int i=0;i<_sprites.size();i++){
			if(_sprites.get(i).getResourceName().equals(animName))
				return _sprites.get(i);
		}
		return null;

	}
	
	@Override
	public String toString(){
		ArrayList<String> descs = new ArrayList<String>();
		for(GLAnim anim :_sprites)
			if(anim.mustDraw){
				descs.add("currentAnim: "+anim.getResourceName() +" width:"+anim.width+" height:"+anim.height+ "x:"+anim.x+" y:"+anim.y+" speedX:"+anim.velocityX+" speedy"+anim.velocityY);
			}
		String result = "";
		for(int i=0;i<descs.size();i++){
			result = result+"\n"+descs.get(i);
		}
		return result;
	}
	
}
