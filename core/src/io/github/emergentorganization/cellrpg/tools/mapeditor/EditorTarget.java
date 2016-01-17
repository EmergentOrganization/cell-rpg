package io.github.emergentorganization.cellrpg.tools.mapeditor;

import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.tools.mapeditor.renderables.BoundsGizmo;


public class EditorTarget {
    private final BoundsGizmo boundsGizmo;
    private final Entity entity;

    public EditorTarget(final BoundsGizmo boundsGizmo, final Entity entity) {
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
