package com.emergentorganization.cellrpg.customParticleEffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by 7yl4r on 9/11/2015.
 */
public class WindParticleEffect extends ParticleEffect {
    private float x;
    private float y;
    private float dx;
    private float dy;

    public WindParticleEffect(float X, float Y, float dX, float dY){
        super();
        x = X;
        y = Y;
        dx = dX;
        dy = dY;
    }

    @Override
    public void update(float timeDelta){
        super.update(timeDelta);
        x += dx * timeDelta;
        y += dy * timeDelta;

        // TODO: convert x & y from screen-coords to world-coords?
        setPosition(x, y);

        // get pixels nearby (TODO: (optimization: in direction of travel only)
        final float size = 5f;
        try {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(
                    (int) (x - size),
                    (int) (y - size),
                    (int) (x + size),
                    (int) (y + size)
            );
            // TODO: analyze colors for each pixel
            //.getPixel(x, y);
            int i = (int)x;
            int j = (int)y;
            Color color = new Color(pixmap.getPixel(i, j));
            //System.out.println(color.r + " " + color.b + " " + color.g);

            // TODO: push pixel away from wind-collidable colors
        } catch (IllegalArgumentException err){
            // if off screen?
            return;
        } catch (GdxRuntimeException err){
            // if off screen and therefore the pixmap broke?
            return;
        }
    }
}
