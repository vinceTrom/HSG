package com.oqs.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.content.Context;
import android.graphics.Rect;

public abstract class Character extends Renderable{

	protected abstract String getCharacterType();
	private static HashMap<String,ArrayList<GLAnim>> _savedSprites = new HashMap<String,ArrayList<GLAnim>>();
	protected ArrayList<GLAnim> _sprites = new ArrayList<GLAnim>();

	public int _playerState=1;
	
	public static void clearSprites(){
		_savedSprites.clear();
	}

	public Character(Context ctx, String fileName){
		createAnims(ctx, fileName);
		initAnims();
		if(!_savedSprites.containsKey(fileName))
			_savedSprites.put(fileName, _sprites);
		else{
			_sprites = _savedSprites.get(fileName);
			for(GLAnim anim:_sprites)
				anim.addCharacter(this);
		}
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
			_sprites.add(anim);			
		}
	}	
	protected abstract void initAnims();

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
				descs.add("currentAnim: "+anim.getResourceName() +" width:"+width+" height:"+height+ "x:"+x+" y:"+y+" speedX:"+velocityX+" speedy"+velocityY);
			
		String result = "";
		for(int i=0;i<descs.size();i++){
			result = result+"\n"+descs.get(i);
		}
		return getCharacterType()+": " +result;
	}

}
