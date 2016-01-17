package io.github.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.emergentorganization.engine.components.Bounds;
import io.github.emergentorganization.engine.components.InputComponent;
import io.github.emergentorganization.engine.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import io.github.emergentorganization.cellrpg.input.player.inputUtil;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.engine.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Click screen to shoot weapon.
 * <p/>
 * SETTINGS:
 * * excludeRadius : radius inside which clicks don't count
 */
public class ClickShoot extends iPlayerCtrl {
    private final String NAME = "Click to Shoot";
    private final EntityFactory entityFactory;
    private final Camera camera;
    private final EventManager eventManager;

    public ClickShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im) {
        super(world, im);
        this.entityFactory = entityFactory;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
        this.eventManager = world.getSystem(EventManager.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow) {
        final Preferences prefs = GameSettings.getPreferences();

        float EXCLUDE_RADIUS_DEFAULT = 1f;
        float EXCLUDE_RADIUS_MIN = 0f;
        float EXCLUDE_RADIUS_MAX = 5f;
        float EXCLUDE_RADIUS_DELTA = .05f;
        float exclusionRadius = prefs.getFloat(GameSettings.KEY_WEAPON_CLICKSHOOT_RADIUS, EXCLUDE_RADIUS_DEFAULT);
        VisLabel excludeRadiusLabel = new VisLabel("exclusion radius around bridge orb: ");
        menuTable.add(excludeRadiusLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        final VisLabel excludeRadiusValue = new VisLabel(Float.toString(exclusionRadius));
        menuTable.add(excludeRadiusValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider excludeRadiusSlider = new VisSlider(EXCLUDE_RADIUS_MIN, EXCLUDE_RADIUS_MAX, EXCLUDE_RADIUS_DELTA, false);
        excludeRadiusSlider.setValue(exclusionRadius);
        excludeRadiusSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        float newVal = excludeRadiusSlider.getValue();
                        prefs.putFloat(GameSettings.KEY_WEAPON_CLICKSHOOT_RADIUS, newVal);
                        excludeRadiusValue.setText(Float.toString(newVal));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(excludeRadiusSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();
    }

    @Override
    public void process(Entity player) {
        if (Gdx.input.justTouched()) { // LMB or RMB?
            Vector2 mouse = inputUtil.getMousePos(camera);
            Vector2 playerPos = player.getComponent(Position.class).getCenter(player.getComponent(Bounds.class), 0);
            if (isOutsideExclusionRadius(mouse, playerPos)) {
                WeaponUtil.shootTo(mouse, player, eventManager, entityFactory);
            }
        }
    }

    private boolean isOutsideExclusionRadius(Vector2 pos, Vector2 center) {
        final Preferences prefs = GameSettings.getPreferences();
        return pos.dst(center) > prefs.getFloat(GameSettings.KEY_WEAPON_CLICKSHOOT_RADIUS);
    }
}
