package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.*;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;
import com.emergentorganization.cellrpg.scenes.arcadeScore;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private final Logger logger = LogManager.getLogger(getClass());

    private static final String ID = "char-player";  // ID for getting spritesheet and collider
    private static final float TPF = 0.2f;  // time per frame of animation

    private MovementComponent moveComponent;

    // map editor camera:
    private OrthographicCamera camera;

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public Player(){
        super(getAssetNames(), TPF);

        moveComponent = getFirstComponentByType(MovementComponent.class);

        initPlayer();
    }

    public Player(Vector2 position) {
        super(getAssetNames(), TPF);

        moveComponent = getFirstComponentByType(MovementComponent.class);
        moveComponent.setWorldPosition(position);

        initPlayer();
    }

    public static String[] getAssetNames() {
        return new String[] {
                "game/char-player/0",
                "game/char-player/1",
                "game/char-player/2",
                "game/char-player/3",
                "game/char-player/4",
                "game/char-player/5",
                "game/char-player/6",
                "game/char-player/7",
                "game/char-player/8",
                "game/char-player/9"
        };
    }

    private void initPlayer(){
        addComponent(new WeaponComponent());
    }

    private void initCAGrid(){
        int[][] pattern = {
                {1,0,1,0,1},
                {0,0,0,0,0},
                {1,0,0,0,1},
                {0,0,0,0,0},
                {1,0,1,0,1}
        };
        addComponent(new GridSeedComponent(
                pattern,
                1,
                new Vector2(0,0),  // TODO: place this in center of img
                CALayer.ENERGY
        ));
    }

    @Override
    public void added() {
        super.added();

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        float scale = currentFrame.getRegionWidth(); // Why does this work without scaling?
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.PLAYER);

        if (!getScene().isEditor()) {
            camera = getScene().getGameCamera();
            camera.position.set(moveComponent.getWorldPosition()
                    .sub(camera.viewportWidth /2f, camera.viewportHeight /2f), 0f);

            PlayerInputComponent playerInput = new PlayerInputComponent(camera);
            addComponent(playerInput);
            phys.setUserData(new PlayerUserData(this, playerInput));
        }
        //phys.enableDebugRenderer(true);

        addComponent(phys);
        addComponent(new ShieldComponent());

        //addComponent(new DialogComponent());

        CAScene scene =  (CAScene) getScene();
        if (scene instanceof CAScene) {
            // NOTE: this uses only x-dimension; assumes width ~= height
            int collideRadius = 0; //TODO: (int) (getGraphicsComponent().getSize().x*scale);
            int collideGrid;
            try {
                collideGrid = scene.getLayer(CALayer.VYROIDS).getCellSize();
            } catch(NullPointerException err){
                collideGrid = 1;
            }
            initCAGrid();

            CACollisionComponent cacc = new CACollisionComponent(CALayer.VYROIDS);
            // bullet trail energy layer effect
            cacc.addCollision(
                    1,
                    EntityEvents.VYROID_DAMAGE,
                    collideRadius,
                    collideGrid
            );
            cacc.addCollision(
                    1,
                    new int[][]{
                            {0, 0, 0},
                            {0, 0, 0},
                            {0, 0, 0}
                    },
                    CALayer.VYROIDS,
                    collideRadius,
                    collideGrid
            );
            addComponent(cacc);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void fireEvent(EntityEvents event){
        super.fireEvent(event);
        switch(event){
            case SHIELD_DOWN:
                logger.info("game over");
                // player is dead!
                String message;
                if (getScene() instanceof arcadeScore){
                    int score = ((arcadeScore) getScene()).getScore();
                    message = "bridge to planiverse collapsed. \nFinal score: " + Integer.toString(score);
                } else {
                    String hash = Long.toHexString(Double.doubleToLongBits(Math.random())).toUpperCase().substring(0, 6);
                    message = "bridge to planiverse collapsed. \nSpatiotemporal hash: " + hash;
                }
                CellRpg.fetch().setScreen(new MainMenu(message));
                break;
            case VYROID_DAMAGE:
                getFirstComponentByType(ShieldComponent.class).damage();
                break;
        }
    }
}
