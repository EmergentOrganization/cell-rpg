package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.entities.buildings.BuildingLarge1;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.PlayerCollisionListener;
import org.dyn4j.geometry.Vector2;

public class CellRPG extends Scene {
	Player player;
	
	@Override
	public void create () {
		super.create();

		Stage uiStage = getUiStage();
		Image cellRPG = new Image(new Texture("CellRPG.png"));
		uiStage.addActor(cellRPG);

		getWorld().setGravity(new Vector2(0,0)); // defaults to -9.8 m/s
		getWorld().addListener(new PlayerCollisionListener()); // stops player from clipping through colliders

		player = new Player();
		this.addEntity(player);

        BuildingLarge1 bldg = new BuildingLarge1();
        bldg.getMovementComponent().setWorldPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		this.addEntity(bldg);
	}
}
