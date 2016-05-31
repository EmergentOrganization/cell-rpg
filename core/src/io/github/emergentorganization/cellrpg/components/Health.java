package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;


public class Health extends Component {
    public int health = Integer.MAX_VALUE;  // current health... Basically invincible by default
    public int maxHealth = health;  // max health achievable
}
