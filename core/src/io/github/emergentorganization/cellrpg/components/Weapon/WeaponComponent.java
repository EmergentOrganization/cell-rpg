package io.github.emergentorganization.cellrpg.components.Weapon;

import com.artemis.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class WeaponComponent extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    // public:
    public long delay = 200;  // required delay between shots
    public int charge = 100;  // how much charge stored in weapon
    public int SHOT_CHARGE_COST = 10;
    public int recharge_per_s = 10;

    // private:
    public long lastShot;  // time of last weapon fire
    public boolean charge_changed;

    // list of applied powerups and corresponding power-down timers
    public ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    public ArrayList<Long> powerup_timers = new ArrayList<Long>();

    // power-up constants:
    static final long FIRE_RATE_DELAY_DELTA = 100;
    static final long FIRE_RATE_LEN = 5;
    static final long FIRE_RATE_CHARGE_BOOST = 100;

    public void powerUp(Powerup pow){
        // applies powerup. adds to powerups and powerup_timers lists automatically
        logger.debug("applying powerup " + pow);
        switch (pow){
            case FIRE_RATE:
                delay -= FIRE_RATE_DELAY_DELTA;
                charge += FIRE_RATE_CHARGE_BOOST;
                powerups.add(pow);
                powerup_timers.add(FIRE_RATE_LEN);
                break;
        }
    }

    public void powerDown(int i){
        // un-applies powerup in position i in powerups[] and deletes powerup + timer.
        _powerDown(powerups.get(i));
        powerup_timers.remove(i);
        powerups.remove(i);
    }

    private void _powerDown(Powerup pow){
        // un-applies powerup. powerup and timer must be manually removed from powerups and powerup_timers separately.
        logger.debug("powering down " + pow);
        switch (pow){
            case FIRE_RATE:
                delay += FIRE_RATE_DELAY_DELTA;
                break;
        }
    }
}
