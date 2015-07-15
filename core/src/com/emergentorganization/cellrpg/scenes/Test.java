package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.components.global.DialogComponent;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import com.emergentorganization.cellrpg.tools.mapeditor.map.Map;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import org.dyn4j.geometry.Vector2;

public class Test extends PausableScene {

	@Override
	public void create() {
		super.create();

		DialogComponent dc = new DialogComponent();
		addComponent(dc);
		//dc.setEnabled(true);
		dc.setTypewriterText("This is a test message.", 0.3f);

		Stage uiStage = getUiStage();
		Image cellRPG = new Image(new Texture("CellRPG.png"));
		uiStage.addActor(cellRPG);

		getWorld().setGravity(new Vector2(0, 0)); // defaults to -9.8 m/s
		getWorld().addListener(new PlayerCollisionListener()); // stops player from clipping through colliders
		Map map = MapTools.importMap("TestMap");
		addEntities(map.getEntities());

		addEntityListener(new EntityActionListener(Player.class) {
			private FollowingCamera followingCamera;

			@Override
			public void onAdd() {
				followingCamera = new FollowingCamera(getPlayer());
				addEntity(followingCamera);
			}

			@Override
			public void onRemove() {
				removeEntity(followingCamera);
			}
		});
	}

	@Override
	public void show () {
		super.show();

		// check gameState for android-app-hiding instances
		if (getUiStage() == null) {
			create();
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		drawUI();
	}

	@Override
	public void hide() {
		// TODO
	}
}
