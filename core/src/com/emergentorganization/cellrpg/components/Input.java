package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 10/28/15.
 */
public class Input extends Component {
    public int controlScheme; // TODO placeholder
    public Vector2 direction = new Vector2();
    public float maxSpeed; // in m/s
    public float accelForce; // in kg/m^2
}
