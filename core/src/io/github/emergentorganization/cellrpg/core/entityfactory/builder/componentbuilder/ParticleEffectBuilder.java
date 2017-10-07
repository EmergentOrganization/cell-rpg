package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import io.github.emergentorganization.cellrpg.components.ParticleEffectComponent;
import io.github.emergentorganization.cellrpg.core.ParticleEff;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ParticleEffectBuilder extends BaseComponentBuilder {
    private ParticleEff effectName;

    public ParticleEffectBuilder(ParticleEff effectName) {
        super(Aspect.all(ParticleEffectComponent.class), 20);
        this.effectName = effectName;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        ParticleEffectComponent component = entity.getComponent(ParticleEffectComponent.class);
        AssetManager assetManager = entity.getWorld().getSystem(AssetManager.class);
        if (component != null) {
            logger.trace("init particle effect " + effectName);
            component.particleEffect = assetManager.getParticleEffect(effectName);

//            // set continuous?
//            for (int i = 0, n = particleEffect.getEmitters().size; i < n; i++) {
//                ParticleEmitter emitter = particleEffect.getEmitters().get(i);
//                emitter.setContinuous(true);
//            }

//            particleEffect.setDuration((int)particleEffect.getEmitters().get(0).duration);

            component.particleEffect.start();
        }
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return ParticleEffectComponent.class;
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
