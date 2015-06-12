package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.tools.map.Map;
import com.emergentorganization.cellrpg.tools.map.MapLoader;
import org.dyn4j.geometry.Vector2;

public class Test extends Scene {
	private Player player;
	private Map map;

	@Override
	public void create() {
		super.create();

		Stage uiStage = getUiStage();
		Image cellRPG = new Image(new Texture("CellRPG.png"));
		uiStage.addActor(cellRPG);

		getWorld().setGravity(new Vector2(0, 0)); // defaults to -9.8 m/s
		getWorld().addListener(new PlayerCollisionListener()); // stops player from clipping through colliders

		player = new Player();
		this.addEntity(player);

		map = MapLoader.getMap("testMap.svg");
		map.load(this);
	}
	
	@Override
	public void show () {
		super.show();

		// check gameState for android-app-hiding instances
		if (player == null) {
			create();
		}
	}

	@Override
	public void hide() {
		// TODO
	}

	public Map getMap() {
		return map;
	}
}
