package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.global.DialogComponent;
import com.emergentorganization.cellrpg.entities.*;
import com.emergentorganization.cellrpg.entities.buildings.VyroidGenEntity;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import com.emergentorganization.cellrpg.scenes.regions.ArcadeRegion1;
import com.emergentorganization.cellrpg.scenes.regions.Region;
import com.emergentorganization.cellrpg.story.dialogue.ArcadeStory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArcadeScene extends CAScene implements arcadeScore {
	private final Logger logger = LogManager.getLogger(getClass());

	static final float POINTS_PER_SEC = 100f;  // # of points per second survived
	Entity cameraTarget;
	private int score = 0;
	private float pointBuffer = 0f;

	private long timeUntilNextGenerator = 10000; // ms until next generator spawns
	private long lastGenSpawnTime;
	private MovementComponent playerMoveComponent;

	public int getScore(){
		return score;
	}

	public void addPoints(final int amount){
		score += amount;
	}

	public void resetScore(){
		score = 0;
	}

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

	protected CALayer getGenInsertLayer(){
		// returns CALayer into which next new generator should spawn vyroids
		int chosenLayer = (int)Math.round(Math.random()*2);
		switch (chosenLayer){
			case 0:
				return CALayer.VYROIDS_MINI;
			case 1:
				return CALayer.VYROIDS_MEGA;
			default:
				return CALayer.VYROIDS;
		}
	}

	private VyroidGenEntity addGenerator(){
		// adds another vyroid generator
		float x = 0;
		float y = 0;

		VyroidGenEntity gen = new VyroidGenEntity(ZIndex.BUILDING, getGenInsertLayer());
		addArcadeEntity(gen, x, y, .0000001f, .0000001f);
		lastGenSpawnTime = System.currentTimeMillis();
		logger.info("new vyroid gen added!");

		// add weapon powerup TODO: at random position on screen (currently just near origin)
		x = (float)Math.random()*40-20;
		y = (float)Math.random()*40-20;
		WeaponPowerup wp = new WeaponPowerup();
		addArcadeEntity(wp, x, y, .01f, .01f);

		// add shield powerup
		x = (float)Math.random()*40-20;
		y = (float)Math.random()*40-20;
		ShieldPowerup sp = new ShieldPowerup();
		addArcadeEntity(sp, x, y, .01f, .01f);

		return gen;
	}

	private void setupArcadeScene(){
		// add player
		Player player = new Player();
		playerMoveComponent = player.getFirstComponentByType(MovementComponent.class);
		addArcadeEntity(player);

		// add 1st generator
		cameraTarget = addGenerator();
		addArcadeEntity(cameraTarget, 0, 0, .0000001f, .0000001f);

		addEntity(new ScoreHUD());
	}


	public Region getStartingRegion(){
		return new ArcadeRegion1(this);
	}

	@Override
	public void create() {
		super.create();

		DialogComponent dc = new DialogComponent();
		addComponent(dc);
		dc.loadDialogueSequence(new ArcadeStory()).init();

		getWorld().setGravity(new org.dyn4j.geometry.Vector2(0, 0)); // defaults to -9.8 m/s
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
		CellRpg.bgSoundController.start();
	}

	public void enforcePlayerBounds() {
		Vector3 camPos = getGameCamera().position;
		Vector2 pos = playerMoveComponent.getWorldPosition();
		float halfWidth = ((float)Gdx.graphics.getWidth() * Scene.scale) / 2f;
		float halfHeight = ((float)Gdx.graphics.getHeight() * Scene.scale) / 2f;

		if (pos.x < -halfWidth + camPos.x)
			pos.set(-halfWidth + camPos.x, pos.y);
		else if (pos.x > halfWidth + camPos.x)
			pos.set(halfWidth + camPos.x, pos.y);

		if (pos.y < -halfHeight + camPos.y)
			pos.set(pos.x, -halfHeight + camPos.y);
		else if (pos.y > halfHeight + camPos.y)
			pos.set(pos.x, halfHeight + camPos.y);

		playerMoveComponent.setWorldPosition(pos);
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

		enforcePlayerBounds();
		pointBuffer += delta*POINTS_PER_SEC;  // get points for each render you survive
		addPoints((int) pointBuffer);  // whole points added to score
		pointBuffer%=1f;  // then chop off whole points and save only decimal parts

		// increase difficulty by adding generator if time
		long now = System.currentTimeMillis();
		if (now - lastGenSpawnTime > timeUntilNextGenerator){
			// TODO: decrease timeUntilNextGenerator?
			addGenerator();
		}

		drawUI();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void dispose() {
		super.dispose();

		CellRpg.bgSoundController.stop();
	}
}
