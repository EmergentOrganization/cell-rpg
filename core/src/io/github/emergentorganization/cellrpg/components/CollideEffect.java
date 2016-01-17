package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * describes the potential effect of collisions with other objects
 */
public class CollideEffect extends Component {
    public int damage = 0;  // damage to entity I'm colliding with
    public int selfDamage = 0;  // damage to me (caused by self, in addition to damage caused by other entity)
}
