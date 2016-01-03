package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 10/28/15.
 */
public class InputComponent extends Component {
    public final Vector2 direction = new Vector2();
    public int controlScheme; // TODO placeholder
    public float speed; // in.. some kind of unit
}
