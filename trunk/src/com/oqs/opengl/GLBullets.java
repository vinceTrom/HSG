package com.oqs.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.oqs.opengl.lib.MMXMLElement;
import com.oqs.opengl.lib.MMXMLParser;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

import android.content.Context;
import android.util.Log;

public class GLBullets {

	private GLAnim _sprite;
	public ArrayList<Renderable> _bulletList = new ArrayList<Renderable>();

	private static GLBullets _me;
	
	public GLBullets(Context ctx) {
		_me = this;
		_sprite = new GLAnim("bullet", false, _bulletList);
		InputStream ss = null;
		try {
			ss = ctx.getAssets().open("bullet.xml");
		} catch (IOException e1) {e1.printStackTrace();}
		MMXMLParser parser = MMXMLParser.createMMXMLParser(ss,null);
		MMXMLElement elem = parser.parseSynchronously().getRootElement();
		MMXMLElements elems = elem.getElementForKey("player").getElementForKey("animations").getElementsForKey("animation");
		for(int i=0;i<elems.size();i++){
			String anim_name = elems.get(i).getAttributes().get("name");
			GLUtils.createAnim(ctx, _sprite, elems.get(i));
		}
	}
	
	public static GLBullets get(){
		return _me;
	}
/*
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
	*/
	public GLAnim getSprite(){
		return _sprite;
	}

	public void addBullet(GLBullet bullet) {
		bullet.width = _sprite.getAverageWidth();
		bullet.height = 10;
		_bulletList.add(bullet);
	}

}
