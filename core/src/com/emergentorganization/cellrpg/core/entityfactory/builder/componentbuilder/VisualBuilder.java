package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import io.github.emergentorganization.engine.components.Bounds;
import io.github.emergentorganization.engine.components.Name;
import io.github.emergentorganization.engine.components.Visual;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.managers.AssetManager;

import java.util.List;


public class VisualBuilder extends BaseComponentBuilder {
    private RenderIndex renderIndex = RenderIndex.BUILDING;
    private boolean isAnimation = false;
    private float frameDuration = 0;
    private List<String> frames;
    private Animation.PlayMode playMode = Animation.PlayMode.LOOP_PINGPONG;
    private String texturePath = null;

    public VisualBuilder() {
        super(Aspect.all(Visual.class, Name.class, Bounds.class), 100);
    }

    public VisualBuilder animation(List<String> frames, Animation.PlayMode playMode, float frameDuration) {
        if (texturePath != null)
            throw new RuntimeException("ERROR: Cannot define both an animation and a texture");
        this.isAnimation = true;
        this.frames = frames;
        this.playMode = playMode;
        this.frameDuration = frameDuration;
        return this;
    }

    public VisualBuilder texture(String texturePath) {
        if (frames != null)
            throw new RuntimeException("ERROR: Cannot define both a texture and an animation");
        this.texturePath = texturePath;
        return this;
    }

    /**
     * Defaults to RenderIndex.BUILDING
     */
    public VisualBuilder renderIndex(RenderIndex renderIndex) {
        this.renderIndex = renderIndex;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        Visual v = entity.getComponent(Visual.class);
        if (v != null) {
            v.index = renderIndex;

            String entityId = entity.getComponent(Name.class).internalID;
            if (isAnimation) {
                v.setAnimation(entityId);
                Animation animation = entity.getWorld().getSystem(AssetManager.class).defineAnimation(
                        entityId,
                        frameDuration,
                        frames.toArray(new String[frames.size()]),
                        playMode
                );
                entity.getComponent(Bounds.class).setFromRegion(animation.getKeyFrames()[0]);
            } else if (texturePath != null) {
                v.setTexture(texturePath);
                entity.getComponent(Bounds.class).setFromRegion(
                        entity.getWorld().getSystem(AssetManager.class).getRegion(texturePath)
                );
            } else {
                throw new RuntimeException("ERROR: Need to set a texture or animation on entity " + entityId);
            }
        }
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return Visual.class;
    }
}
