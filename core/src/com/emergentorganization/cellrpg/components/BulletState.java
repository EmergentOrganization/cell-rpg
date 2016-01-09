package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Created by brian on 11/21/15.
 */
public class BulletState extends Component {
    public int starting_bounces = 3;  // number of bounces the bullet starts with
    public int bounces = starting_bounces;  // number of bounces remaining in bullet
}
