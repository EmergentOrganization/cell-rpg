package com.emergentorganization.cellrpg.customParticleEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;

/**
 * Created by 7yl4r on 9/11/2015.
 */
public class WindParticleEffect extends ParticleEffect {
    private float x;
    private float y;
    private float dx;
    private float dy;
    private float collideForceX=0f;  // used to add temporary push to dx/dy
    private float collideForceY=0f;
    private Camera cam;

    public WindParticleEffect(float X, float Y, float dX, float dY, Camera camera){
        super();
        x = X;
        y = Y;
        dx = dX;
        dy = dY;
        cam = camera;
    }

    @Override
    public void update(float timeDelta){
        super.update(timeDelta);

        x += (dx + collideForceX) * timeDelta;
        y -= (dy + collideForceY) * timeDelta;

        collideForceX *= .9f;
        collideForceY *= .9f;

        // TODO: convert x & y from screen-coords to world-coords?
        setPosition(x, y);

        // get pixels nearby TODO: (optimization) in direction of travel only
        Vector3 pos = new Vector3(cam.project(new Vector3(x, y, 0)));
        final int size = 1;
        int x1 = (int)pos.x - size;
        int x2 = (int)pos.x + size;
        int y1 = (int)pos.y - size;
        int y2 = (int)pos.y + size;
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        if (x1 > 0 && x2 < w && y1 > 0 && y2 < h) {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(
                    x1,
                    y1,
                    x2,
                    y2
            );
            // TODO: what about the other pixels?

            if (collidesWithPixel(0, 1, pixmap)) {
                // push pixel away from wind-collidable colors
                collideForceX = -2*dx;  // bounce off in x direction
            }
            if (collidesWithPixel(1, 0, pixmap)) {
                collideForceY = -2*dy;
            }
            pixmap.dispose();
        }
        /*
        } catch (IllegalArgumentException err) {
            // if off screen?
            return;
        } catch (GdxRuntimeException err) {
            // if off screen and therefore the pixmap broke?
            return;
        }
        */
    }

    private boolean collidesWithPixel(int i, int j, Pixmap pixmap){
        Color color = new Color(pixmap.getPixel(i, j));
        return (color.r > .9 && color.g > .9 && color.b > .9);
    }
}
