package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.global.DialogComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.buildings.VyroidGenerator;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import org.dyn4j.geometry.Vector2;

public class ArcadeScene extends CAScene {

	Entity cameraTarget;

	private void addArcadeEntity(Entity ent, float x, float y, float scaleX, float scaleY){
		MovementComponent move = ent.getFirstComponentByType(MovementComponent.class);
		move.setScale(new com.badlogic.gdx.math.Vector2(scaleX, scaleY));
		move.setRotation(0);
		move.setWorldPosition(new com.badlogic.gdx.math.Vector2(x, y));
		addEntity(ent);
	}

	private void addArcadeEntity(Entity ent, float x, float y){
		addArcadeEntity(ent, x, y, .1f, .1f);
	}

	private void addArcadeEntity(Entity ent){
		addArcadeEntity(ent, 0, 0);
	}

	private void addGenerator(){
		// adds another vyroid generator
		float x = 0;
		float y = 0;
		addArcadeEntity(new VyroidGenerator(), x, y, .0000001f, .0000001f);
	}

	private void setupArcadeScene(){
		// add player
		addArcadeEntity(new Player());

		// add 1st generator
		cameraTarget = new VyroidGenerator();
		addArcadeEntity(cameraTarget, 0, 0, .0000001f, .0000001f);

		// TODO: add score HUD

		// TODO: initiate difficulty ramp which adds generators as score goes up (use addGenerator)
	}

	@Override
	public void create() {
		super.create();

		DialogComponent dc = new DialogComponent();
		addComponent(dc);
		//dc.setEnabled(true);
		//dc.setTypewriterText("This is a test message.", 0.3f);

		Stage uiStage = getUiStage();
		Image cellRPG = new Image(new Texture("CellRPG.png"));
		uiStage.addActor(cellRPG);

		getWorld().setGravity(new Vector2(0, 0)); // defaults to -9.8 m/s
		getWorld().addListener(new PlayerCollisionListener()); // stops player from clipping through colliders

		setupArcadeScene();

		addEntityListener(new EntityActionListener(Player.class) {
			private FollowingCamera followingCamera;

			@Override
			public void onAdd() {

				followingCamera = new FollowingCamera(cameraTarget);
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
