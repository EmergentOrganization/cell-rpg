package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Weapon.Powerup;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 */
public class Weapon extends Equipment {

    // power-up constants:
    private static final long FIRE_RATE_DELAY_DELTA = 100;
    private static final long FIRE_RATE_LEN = 3;
    private static final long FIRE_RATE_CHARGE_BOOST = 100;
    private final int MAX_CHARGE = 100;
    public final int SHOT_CHARGE_COST = 10;
    private final Logger logger = LogManager.getLogger(getClass());
    // public:
    public long delay = 100;  // required delay between shots
    public int charge = 100;  // how much charge stored in weapon
    private final int recharge_per_s = 30;
    // private:
    public long lastShot;  // time of last weapon fire
    public boolean charge_changed;
    // list of applied powerups and corresponding power-down timers
    private final ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private final ArrayList<Long> powerup_timers = new ArrayList<Long>();

    public Weapon(int parentId, String name, String description, int baseEnergy, int energySlots, int attackStat) {
        super(parentId, name, description, baseEnergy, energySlots);
        this.type = EquipmentType.WEAPON;

        this.attackStat = attackStat;
    }

    public void create(World world, Vector2 pos) {
        // Shield  TODO: build weapon entity
//        final Entity weapon = new EntityBuilder(world, EntityFactory.object, name, EntityID.PLAYER_SHIELD.toString(), pos)
//                .tag("shield")
//                .addBuilder(new VisualBuilder()
//                                .texture(Resources.ANIM_PLAYER_SHIELD.get(MAX_SHIELD_STATE))
//                                .renderIndex(RenderIndex.PLAYER_SHIELD)
//                )
//                .build();
    }

    public void updatePosition(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper) {
        // TODO: re-enable once we have a weapon entity
//        if (this.shieldEntity >= 0) {
//            Bounds shieldBounds = boundsMapper.get(this.shieldEntity);
//            Bounds ownerBounds = boundsMapper.get(parentId);
//            Position parentPos = posMapper.get(parentId);
//            posMapper.get(this.shieldEntity)
//                    .position.set(parentPos.position)
//                    .sub(
//                            shieldBounds.width * 0.5f - ownerBounds.width * 0.5f,
//                            shieldBounds.height * 0.5f - ownerBounds.height * 0.5f
//                    );
//        }
    }

    @Override
    public void recharge() {
        // recharge weapon
        if (isPowered() && charge < MAX_CHARGE) {
            charge += recharge_per_s * powerLevel();
            logger.trace("recharge weapon");
        }

        checkForPowerDown(1);
    }

    private void checkForPowerDown(int FREQ) {
        // power-down powerups if timers are up
        for (int i = powerup_timers.size() - 1; i > -1; i--) {
            // NOTE: goes max->0 to avoid index issues when removing
            if (powerup_timers.get(i) < 0) {  // if timer expired
                powerDown(i);
            } else {
                powerup_timers.set(i, powerup_timers.get(i) - FREQ);
            }
        }
    }

    @Override
    public void powerUp(Powerup pow) {
        // applies powerup. adds to powerups and powerup_timers lists automatically
        logger.debug("applying powerup " + pow);
        switch (pow) {
            case FIRE_RATE:
                delay -= FIRE_RATE_DELAY_DELTA;
                charge += FIRE_RATE_CHARGE_BOOST;
                powerups.add(pow);
                powerup_timers.add(FIRE_RATE_LEN);
                break;
        }
    }

    private void powerDown(int i) {
        // un-applies powerup in position i in powerups[] and deletes powerup + timer.
        _powerDown(powerups.get(i));
        powerup_timers.remove(i);
        powerups.remove(i);
    }

    private void _powerDown(Powerup pow) {
        // un-applies powerup. powerup and timer must be manually removed from powerups and powerup_timers separately.
        logger.debug("powering down " + pow);
        switch (pow) {
            case FIRE_RATE:
                delay += FIRE_RATE_DELAY_DELTA;
                break;
        }
    }
}
