package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.components.Charge;

public class ChargeBuilder extends BaseComponentBuilder {
    private int charge = 0;  // defaults to 0 charge unless other given.
    private final int maxCharge;
    private int recharge_per_s = 0;


    public ChargeBuilder(int maxCharge) {
        super(Aspect.all(Charge.class), 50);
        this.maxCharge = maxCharge;
    }

    public ChargeBuilder charge(int charge) {
        this.charge = charge;
        return this;
    }

    public ChargeBuilder rechargeRate( int rate ) {
        this.recharge_per_s = rate;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        Charge cha = entity.getComponent(Charge.class);
        cha.set(charge);
        cha.maxCharge = maxCharge;
        cha.recharge_per_s = recharge_per_s;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return Charge.class;
    }
}
