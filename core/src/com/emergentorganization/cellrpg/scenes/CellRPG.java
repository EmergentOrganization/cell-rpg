package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.entities.characters.Player;

public class CellRPG extends Scene {
	Texture img;
	Player player;
	
	@Override
	public void create () {
		super.create();

		// TODO: Create renderable component
		img = new Texture("CellRPG.png");
		player = new Player();
		this.addEntity(player);
	}

	@Override
	public void render () {
		super.render();

		/* TODO: SUPER INEFFECIENT. Create sprite entity we can render this with instead
		* of rendering a batch to the screen twice
		*/
		SpriteBatch batch = getSpriteBatch();
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
