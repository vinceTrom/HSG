package com.oqs.opengl;

import android.content.Context;

public class SplashSoldier extends Soldier{

	public static final int DRINK = 6;
	public static final int YAWN = 7;
	public static final int BIKE = 8;
	public static final int DANCE1 = 9;
	public static final int DANCE2 = 10;
	public static final int DANCE3 = 11;
	public static final int CALLING1 = 12;
	public static final int CALLING2 = 13;


	public static final String DRINKING_ANIM_PATH = "soldier/tanning";
	public static final String YAWNING_ANIM_PATH = "soldier/yawning";
	public static final String BIKING_ANIM_PATH = "soldier/biker";
	public static final String DANCING1_ANIM_PATH = "soldier/dancing1";
	public static final String DANCING2_ANIM_PATH = "soldier/dancing2";
	public static final String DANCING3_ANIM_PATH = "soldier/dancing3";
	public static final String CALLING1_ANIM_PATH = "soldier/calling";
	public static final String CALLING2_ANIM_PATH = "soldier/calling2";

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
		if(_playerState == DRINK && resourceName.equals(DRINKING_ANIM_PATH))
			return true;
		else if(_playerState == YAWN && resourceName.equals(YAWNING_ANIM_PATH))
			return true;
		else if(_playerState == BIKE && resourceName.equals(BIKING_ANIM_PATH))
			return true;	
		else if(_playerState == DANCE1 && resourceName.equals(DANCING1_ANIM_PATH))
			return true;
		else if(_playerState == DANCE2 && resourceName.equals(DANCING2_ANIM_PATH))
			return true;
		else if(_playerState == DANCE3 && resourceName.equals(DANCING3_ANIM_PATH))
			return true;
		else if(_playerState == CALLING1 && resourceName.equals(CALLING1_ANIM_PATH))
			return true;
		else if(_playerState == CALLING2 && resourceName.equals(CALLING2_ANIM_PATH))
			return true;
		return false;
	}

}
