package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by BrianErikson on 6/3/2015.
 */
public class MovementComponent extends EntityComponent {

    public enum MoveState {
        NOT_MOVING,
        MOUSE_FOLLOW,
        PATH_FOLLOW
    }
    private MoveState moveState = MoveState.NOT_MOVING;

    private Matrix3 translation = new Matrix3();
    private Matrix3 rotation = new Matrix3();
    private Matrix3 scale = new Matrix3();
    private Matrix3 transform = new Matrix3();
    private boolean isDirty = false;

    private float speed = 200 * Scene.scale; // default scalar for player movement [px/s]
    private Vector2 velocity = new Vector2();

    public MovementComponent() {
        type = ComponentType.MOVEMENT;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        updateTransform();
        updateMovement();
    }

    public void updateTransform() {
        if (isDirty) {
            transform = new Matrix3().mul(scale).mul(rotation).mul(translation);
            isDirty = false;
        }
    }

    public void translate(Vector2 vec) {
        translation.translate(vec);
        isDirty = true;
    }

    public void rotateRad(float rads) {
        rotation.rotateRad(rads);
        isDirty = true;
    }

    public void rotate(float degs) {
        rotation.rotate(degs);
        isDirty = true;
    }

    public void rotate(Vector2 rotator) {
        rotation.rotateRad(rotator.angleRad());
        isDirty = true;
    }

    public void setLocalPosition(Vector2 position) {
        translation.setToTranslation(position);
        isDirty = true;
    }

    public void setWorldPosition(float x, float y) {
        setWorldPosition(new Vector2(x, y));
    }

    public void setWorldPosition(Vector2 position) {
        // TODO: World position
        setLocalPosition(position);
    }

    public void setRotation(Vector2 rotation) {
        rotation.setAngleRad(rotation.angleRad());
        isDirty = true;
    }


    public void setRotation(float angle){
        rotation.setToRotation(angle);
        isDirty = true;
    }

    /**
     * Returns a read-only copy of the positon
     * @return
     */
    public Vector2 getLocalPosition() {
        Vector2 pos = new Vector2();
        translation.getTranslation(pos);
        return pos;
    }

    public Vector2 getWorldPosition() {
        // TODO
        return getLocalPosition();
    }

    public float getRotation() {
        return rotation.getRotation();
    }

    public float getRotationRad() {
        return rotation.getRotationRad();
    }

    public Matrix3 getTransform() {
        updateTransform();
        return transform;
    }

    public Vector2 getScale() {
        Vector2 vec = new Vector2();
        scale.getScale(vec);
        return vec;
    }

    public void setScale(float xy) {
        setScale(new Vector2(xy, xy));
    }

    public void setScale(float x, float y) {
        setScale(new Vector2(x, y));
    }

    public void setScale(Vector2 vec) {
        scale.setToScaling(vec);
        isDirty = true;
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }

    public float getSpeed(){
        return speed;
    }

    public void setVelocity(Vector2 vel){
        velocity.set(vel);
    }

    public Vector2 getVelocity(){
        return velocity.cpy();
    }

    public void setMoveState(MoveState state) {
        if (state == MoveState.NOT_MOVING) {
            stopMoving();
            return;
        }

        this.moveState = state;
    }

    public MoveState getMoveState() {
        return moveState;
    }

    private void updateMovement(){
        Vector2 newPos = getWorldPosition();
        Vector2 move = getVelocity().scl(Gdx.graphics.getDeltaTime());

        newPos.add(move);
        setWorldPosition(newPos);
    }

    public void stopMoving() {
        setVelocity(Vector2.Zero);
        moveState = MoveState.NOT_MOVING;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
