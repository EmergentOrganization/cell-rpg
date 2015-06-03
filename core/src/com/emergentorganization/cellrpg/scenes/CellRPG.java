package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.entities.characters.Player;

public class CellRPG extends Scene {
	Player player;
	
	@Override
	public void create () {
		super.create();

		Stage uiStage = getUiStage();
		Image cellRPG = new Image(new Texture("CellRPG.png"));
		uiStage.addActor(cellRPG);

		player = new Player();
		this.addEntity(player);
	}
}
