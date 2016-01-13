package com.emergentorganization.cellrpg.components;

import com.artemis.Component;


public class WeaponComponent extends Component {
    // public:
    public long delay;  // required delay between shots
    public int charge;  // how much charge stored in weapon
    public int SHOT_CHARGE_COST;

    // private:
    public long lastShot;  // time of last weapon fire
    public boolean charge_changed;

}
