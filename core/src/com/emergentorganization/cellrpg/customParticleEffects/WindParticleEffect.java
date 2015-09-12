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
    private WindDirection direction;
    private Camera cam;

    public WindParticleEffect(float X, float Y, WindDirection windDirection, float speed, Camera camera){
        super();
        direction = windDirection;
        x = X;
        y = Y;
        dx = windDirection.getX()*speed;
        dy = windDirection.getY()*speed;
        cam = camera;
    }

    @Override
    public void update(float timeDelta){
        super.update(timeDelta);

        x += (dx + collideForceX) * timeDelta;
        y -= (dy + collideForceY) * timeDelta;

        collideForceX *= .9f;
        collideForceY *= .9f;

        setPosition(x, y);

        // get pixels nearby TODO: (optimization) in direction of travel only
        Vector3 pos = new Vector3(cam.project(new Vector3(x, y, 0)));  // convert from world-coords to screen coords
        final int radius = 1;
        int W = radius * 2 + 1;
        int H = W;
        int X, Y;
        switch(direction){
            case UP_LEFT:
                X = (int)pos.x - radius;
                Y = (int)pos.y - radius;
                break;
            default:
                X = 0;  // TODO: should be unreachable; throw err here
                Y = 0;
        }

        // check not out-of-screen-bounds
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        if (X > 0 && X+W < w && Y > 0 && Y+H < h) {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(
                    X,
                    Y,
                    W,
                    H
            );

            Color right = new Color(pixmap.getPixel(radius+1, radius));
            Color left = new Color(pixmap.getPixel(radius-1, radius));
            Color above = new Color(pixmap.getPixel(radius, radius+1));
            Color below  = new Color(pixmap.getPixel(radius, radius-1));
            float ab = above.r + above.g + above.b;
            float be = below.r + below.g + below.b;
            float ri = right.r + right.g + right.b;
            float le = left.r + left.g + left.b;
            float gradX = ri - le;
            float gradY = ab - be;
            collideForceX -= ((float)Math.exp(gradX)-1)*dx;
            collideForceY -= ((float)Math.exp(gradY)-1)*dy;

            /*
            if (collidesWithPixel(0, 1, pixmap)) {
                // push pixel away from wind-collidable colors
                collideForceX = -2*dx;  // bounce off in x direction
            }
            if (collidesWithPixel(1, 0, pixmap)) {
                collideForceY = -2*dy;
            }
            */
            pixmap.dispose();
        }
    }

    private boolean collidesWithPixel(int i, int j, Pixmap pixmap){
        Color color = new Color(pixmap.getPixel(i, j));
        return (color.r > .9 && color.g > .9 && color.b > .9);
    }
}
