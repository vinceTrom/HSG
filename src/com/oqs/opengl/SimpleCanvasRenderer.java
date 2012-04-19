package com.oqs.opengl;

import com.oqs.opengl.CanvasSurfaceView.Renderer;

import android.graphics.Canvas;


/**
 * An extremely simple renderer based on the CanvasSurfaceView drawing
 * framework.  Simply draws a list of sprites to a canvas every frame.
 */
public class SimpleCanvasRenderer implements Renderer {

    private CanvasSprite[] mSprites;
    
    public void setSprites(CanvasSprite[] sprites) {
        mSprites = sprites;
    }
    
    public void drawFrame(Canvas canvas) {
        if (mSprites != null) {

            for (int x = 0; x < mSprites.length; x++) {
                mSprites[x].draw(canvas);
            }
        }
        
    }

    public void sizeChanged(int width, int height) {
        
    }

}
