package com.emergentorganization.cellrpg.entities.backgrounds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;

import java.util.ArrayList;

/**
 * Created by 7yl4r on 9/7/2015.
 */
public class Wind extends EntityComponent{
    private static final int RENDERS_PER_INSERT = 5;

    private float speed;
    private float direction;
    private Pixmap pxMap;
    private ArrayList<Vector2> activePixels = new ArrayList<Vector2>();
    private ArrayList<Color> colorsOfTheWind = new ArrayList<Color>();
    private int renderN = 0;

    public Wind(float windSpeed, float windDirection){
        super();
        speed = windSpeed;
        direction = windDirection;
        System.out.println("WIND INIT?");
    }

    @Override
    public void added(){
        super.added();
        pxMap = new Pixmap(
                1,
                1,
                Pixmap.Format.RGBA4444
        );

    }

    private void addWindPixel(){
        // add new active pixel
        Vector2 pos = getEntity().getScene().getPlayer().getFirstComponentByType(MovementComponent.class).getWorldPosition();
        activePixels.add(new Vector2(
                (int) (pos.x - 60),  // TODO: should use screen size here (600x400?)
                (int) (pos.y + 50*Math.random() - 25)
        ));
        colorsOfTheWind.add(new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random()));
    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);

        batch.enableBlending();
        if (renderN % RENDERS_PER_INSERT == 0){
            addWindPixel();
        }
        renderN += 1;

        //System.out.println(activePixels.size() + " wind px");

        ArrayList<Vector2> newPix = new ArrayList<Vector2>();
        ArrayList<Color> newColors = new ArrayList<Color>();
        float scale = getEntity().getScene().scale * 10;
        for(int i = 0; i < activePixels.size(); i++) {
            Vector2 pixel = activePixels.get(i);
            Color color = colorsOfTheWind.get(i);
            pxMap.setColor(color);
            pxMap.drawPixel(0, 0);

            batch.draw(new Texture(pxMap), (int) pixel.x, (int) pixel.y, scale, scale );

            pixel.x += 1;  // movement TODO: use direction & speed to get x & y movement
            pixel.y += Math.random() - .5;
            color.a -= .009f; // pixel decay

            if (color.a > .01){
                newPix.add(pixel);
                newColors.add(color);
            }
        }
        activePixels.clear();    // clear to prevent memory leak?
        colorsOfTheWind.clear();
        activePixels = newPix;
        colorsOfTheWind = newColors;

    }

    @Override
    public boolean shouldRender(){
        return true;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

    }
}
