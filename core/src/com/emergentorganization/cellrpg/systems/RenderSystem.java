package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Rotation;
import com.emergentorganization.cellrpg.components.Scale;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.managers.AssetManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class RenderSystem extends BaseEntitySystem {

    private ComponentMapper<Visual> vm;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Scale> sm;
    private ComponentMapper<Rotation> rm;

    private CameraSystem cameraSystem;

    private AssetManager assetManager; // being a registered system, it is injected on runtime

    private SpriteBatch batch;
    private ArrayList<Integer> sortedEntityIds;

    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Position.class, Rotation.class, Scale.class, Visual.class));

        this.batch = batch;
        sortedEntityIds = new ArrayList<Integer>();
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.getGameCamera().combined);
        batch.begin();
    }

    @Override
    protected void processSystem() {
        for (Integer id : sortedEntityIds) {
            process(id);
        }
    }

    protected void process(int entityId) {
        Visual v = vm.get(entityId);
        Position p = pm.get(entityId);
        Scale s = sm.get(entityId);
        Rotation r = rm.get(entityId);

        TextureRegion t = assetManager.getCurrentRegion(v);
        if (t != null) {
            if (v.isAnimation) {
                v.stateTime += world.getDelta();
            }
            batch.draw(t, p.position.x, p.position.y, 0, 0, t.getRegionWidth(), t.getRegionHeight(), s.scale, s.scale, r.angle);
        }
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void inserted(int entityId) {
        sortedEntityIds.add(entityId);
        Collections.sort(sortedEntityIds, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                Visual v1 = vm.get(o1);
                Visual v2 = vm.get(o2);
                return v1.index.ordinal() - v2.index.ordinal();
            }
        });
    }

    @Override
    protected void removed(final int entityId) {
        sortedEntityIds.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer - entityId == 0;
            }
        });
    }
}
