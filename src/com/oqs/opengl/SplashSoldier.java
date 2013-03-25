package com.oqs.opengl;

import android.content.Context;

public class SplashSoldier extends Soldier{

	public static final int TANN = 6;
	public static final int YAWN = 7;
	public static final int BIKE = 8;

	public static final String TANNING_ANIM_PATH = "soldier/tanning";
	public static final String YAWNING_ANIM_PATH = "soldier/yawning";
	public static final String BIKING_ANIM_PATH = "soldier/biker";

	public SplashSoldier(Context ctx) {
		super(ctx);
		for(GLAnim anim : _sprites){
			if(anim.getResourceName().contains("tann"))
				anim.loop = false;
			if(anim.getResourceName().contains("yawn"))
				anim.loop = false;
		}
	}

	@Override
	public boolean musDrawThisAnim(String resourceName) {
		if(_playerState == TANN && resourceName.equals(TANNING_ANIM_PATH))
			return true;
		else if(_playerState == YAWN && resourceName.equals(YAWNING_ANIM_PATH))
			return true;
		else if(_playerState == BIKE && resourceName.equals(BIKING_ANIM_PATH))
			return true;
		return false;
	}

}
