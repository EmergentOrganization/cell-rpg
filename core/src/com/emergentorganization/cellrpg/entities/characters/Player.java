package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.emergentorganization.cellrpg.components.player.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "char-player";  // ID for getting spritesheet and collider
    private static int HEIGHT = 400;  // scale for collider mesh
    private static final int FRAME_COLS = 10;  // # of cols in spritesheet
    private static final int FRAME_ROWS = 1;  //  # of rows in spritesheet
    private static final float TPF = 0.2f;  // time per frame of animation

    private OrthographicCamera camera;
    private MovementComponent moveComponent;

    public Player(){
        super(this.createAnimation());

        moveComponent = getMovementComponent();
        moveComponent.setWorldPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

        addComponent(new WeaponComponent());
    }

    @Override
    public void added() {
        super.added();

        camera = getScene().getGameCamera();
        addComponent(new PlayerInputComponent(camera));
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(), BodyLoader.fetch().generateBody(ID, HEIGHT), Tag.PLAYER);
        phys.enableDebugRenderer(true);
        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        camera.position.set(moveComponent.getWorldPosition(), 0);
        camera.update();
    }

    private Animation createAnimation(){
        // create the sprite animation
        Texture sheet = new Texture(Gdx.files.internal(ID + ".png"));
        TextureRegion[][] spriteFrames = TextureRegion.split(sheet, sheet.getWidth()/FRAME_COLS, sheet.getHeight()/FRAME_ROWS);
        TextureRegion[] firstAnimation = spriteFrames[0];  // assume exactly 1 animation per row
        return new Animation(TPF, firstAnimation);
    }

}
