package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import com.emergentorganization.cellrpg.tools.mapeditor.map.Map;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import org.dyn4j.geometry.Vector2;

public class Test extends Scene {
	private CAGrid ca_grid;
	private final int RENDERS_PER_GLIDER_INSERT = 10;  // TODO: temporary for testing only!
	private int render_n = 0;  // TODO: temporary for testing only!

	@Override
	public void create() {
		super.create();

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
				ca_grid = new CAGrid(3, ZIndex.CHARACTER);  // TODO: what is the proper ZIndex?
				addEntity(ca_grid);

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

		if (render_n > RENDERS_PER_GLIDER_INSERT){
			render_n = 0;
			int[][] testPattern = {
					{0,1,0},
					{0,0,1},
					{1,1,1}
			};
			int x = (int)(Math.random()*(ca_grid.getSizeX() - testPattern.length));
			int y = (int)(Math.random()*(ca_grid.getSizeY() - testPattern[0].length));
			ca_grid.stampState(testPattern, x, y);
			System.out.println("inserting glider @ " + x + "," + y);
			drawUI();
		} else {
			render_n++;
		}
	}

	@Override
	public void hide() {
		// TODO
	}
}
