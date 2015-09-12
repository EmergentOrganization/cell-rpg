package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.customParticleEffects.WindParticleEffect;

import java.util.ArrayList;

/**
 * WindWaker creates constant wind particle effects in with given direction and speed.
 * Created by 7yl4r on 9/11/2015.
 */
public class WindWaker extends Entity{
    private static final int RENDERS_PER_INSERT = 200;

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
        Vector2 pos = getScene().getPlayer().getFirstComponentByType(MovementComponent.class).getWorldPosition();
        float x = pos.x - Gdx.graphics.getWidth()/2; // TODO: set +/- based on direction
        float y = pos.y - Gdx.graphics.getHeight()/2;
        float dx = 1f;  // TODO: base these on speed
        float dy = 1f;

        System.out.println("add new particle @ (" + x + ',' + y + ")");

        WindParticleEffect newEffect = new WindParticleEffect(x, y, dx, dy);
        // TODO: use pooling for loading: https://www.youtube.com/watch?v=3OwIiELYa70
        newEffect.load(Gdx.files.internal("particleEffects/wind.p"), Gdx.files.internal("particleEffects"));
        newEffect.setPosition(x, y);
        newEffect.start();
        effects.add(newEffect);
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
        for (WindParticleEffect windParticle : effects){
            windParticle.update(deltaTime);
        }
    }
}
