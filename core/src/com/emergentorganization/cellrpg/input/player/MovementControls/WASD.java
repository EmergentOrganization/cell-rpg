package com.emergentorganization.cellrpg.input.player.MovementControls;


import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.input.InputProcessor;

/**
 * Created by orelb on 10/29/2015.
 */
public class WASD extends InputProcessor {

    public WASD(World world, ComponentMapper<InputComponent> im) {
        super(world, im);
    }

    @Override
    public void process(int playerId) {
        InputComponent input = im.get(playerId);
        if (input != null) {
            Vector2 dir = input.direction.setZero();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                dir.add(0, 1f);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                dir.add(0, -1f);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                dir.add(-1f, 0);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                dir.add(1f, 0);
            }
            input.direction.set(dir);
        }
    }
}
