package com.emergentorganization.cellrpg.tools.mapeditor;

import com.artemis.Entity;
import com.emergentorganization.cellrpg.tools.mapeditor.renderables.BoundsGizmo;

/**
 * Created by brian on 12/12/15.
 */
public class EditorTarget {
    private BoundsGizmo boundsGizmo;
    private Entity entity;

    public EditorTarget(BoundsGizmo boundsGizmo, Entity entity) {
        this.boundsGizmo = boundsGizmo;
        this.entity = entity;
    }

    public BoundsGizmo getBoundsGizmo() {
        return boundsGizmo;
    }

    public Entity getEntity() {
        return entity;
    }
}
