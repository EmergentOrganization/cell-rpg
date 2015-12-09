package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Rotation;
import com.emergentorganization.cellrpg.components.Scale;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.tools.postprocessing.TronShader;

import java.util.*;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class RenderSystem extends BaseEntitySystem {

    private final TextureRegion fboRegion;
    private TronShader tronShader;
    private final FrameBuffer frameBuffer;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Scale> sm;
    private ComponentMapper<Rotation> rm;

    private CameraSystem cameraSystem;

    private AssetManager assetManager; // being a registered system, it is injected on runtime

    private final SpriteBatch batch;
    private final LinkedList<Integer> sortedEntityIds;
    private boolean tronShaderEnabled = false;
    private Batch outBatch;

    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Position.class, Rotation.class, Scale.class, Visual.class));

        this.batch = batch;
        this.outBatch = new SpriteBatch();
        sortedEntityIds = new LinkedList<Integer>();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Texture cb = frameBuffer.getColorBufferTexture();
        fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left
    }

    @Override
    protected  void begin() {
        frameBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(cameraSystem.getGameCamera().combined);
        batch.begin();
    }

    @Override
    protected  void processSystem() {
        for (Integer id : sortedEntityIds) {
            process(id);
        }
    }

    protected  void process(int entityId) {
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
        frameBuffer.end();

        if (tronShaderEnabled) {
            tronShader.render(frameBuffer);
        }

        // Render final texture to screen
        outBatch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        outBatch.draw(fboRegion, 0, 0);
        outBatch.end();
    }

    @Override
    protected  void inserted(int entityId) {
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
    protected void removed(int entityId) {
        ListIterator<Integer> iter = sortedEntityIds.listIterator();

        while (iter.hasNext()) {
            Integer id = iter.next();
            if (id - entityId == 0) {
                iter.remove();
            }
        }
    }

    public List<Integer> getSortedEntityIds() {
        return Collections.unmodifiableList(sortedEntityIds);
    }

    /**
     * Enables the Tron glow shader
     * @return The RenderSystem for shader chaining
     */
    public RenderSystem setTronShader(TronShader tronShader) {
        this.tronShader = tronShader;
        this.tronShaderEnabled = this.tronShader != null;
        return this;
    }
}
