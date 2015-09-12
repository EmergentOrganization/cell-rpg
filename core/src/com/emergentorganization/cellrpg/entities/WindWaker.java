package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.customParticleEffects.WindParticleEffect;

import java.util.ArrayList;

/**
 * WindWaker creates constant wind particle effects in with given direction and speed.
 * Created by 7yl4r on 9/11/2015.
 */
public class WindWaker extends Entity{
    private static final int RENDERS_PER_INSERT = 50;  // TODO: (improvement) more inserts for more intense wind

    private int renderN = 0;
    private float speed;
    private float direction;
    private ArrayList<WindParticleEffect> effects = new ArrayList<WindParticleEffect>();

    public WindWaker(){  // TODO: init with wind-map file?
        super();
        float windSpeed = 1;
        float windDirection = 1;
        speed = windSpeed;
        direction = windDirection;
        System.out.println("WIND INIT?");
    }

    private void addWindPixel(){
        // add new active pixel
        Camera cam = getScene().getGameCamera();
        // TODO: set width/height or 0 based on direction
        float xp = Gdx.graphics.getWidth();
        float yp = Gdx.graphics.getHeight();
        if (Math.random() > .5){
            xp *= (float)Math.random();
        } else {
            yp *= (float)Math.random();
        }

        Vector3 ppp = cam.unproject(new Vector3(
                xp,
                yp,
                0
        ));
        float x =  ppp.x;
        float y =  ppp.y;

        float dx = -10f;  // TODO: base these on speed
        float dy = -10f;

        System.out.println("add new particle @ (" + x + ',' + y + ")");
        WindParticleEffect newEffect = new WindParticleEffect(x, y, dx, dy);
        // TODO: use pooling for loading: https://www.youtube.com/watch?v=3OwIiELYa70
        newEffect.load(Gdx.files.internal("particleEffects/wind.p"), Gdx.files.internal("particleEffects"));
//        newEffect.setPosition(x, y);
        newEffect.scaleEffect(getScene().scale);
        newEffect.start();
        effects.add(newEffect);
        System.out.println("particle count:" + effects.size());
    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);

        if (renderN % RENDERS_PER_INSERT == 0){
            addWindPixel();
        }
        renderN += 1;

        for (WindParticleEffect windParticle : effects){
            windParticle.draw(batch);
        }
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        ArrayList<WindParticleEffect> effectsBuffer = new ArrayList<WindParticleEffect>(effects);
        for (WindParticleEffect windParticle : effectsBuffer){
            windParticle.update(deltaTime);
            if (windParticle.isComplete()){
                effects.remove(windParticle);
            }
        }
    }
}
