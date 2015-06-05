package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.components.messages.MoveToMessage;

/**
 * Created by BrianErikson on 6/3/2015.
 */
public class MovementComponent extends BaseComponent {

    private Matrix3 identity = new Matrix3();
    private Matrix3 translation = new Matrix3();
    private Matrix3 rotation = new Matrix3();
    private Matrix3 scale = new Matrix3();
    private Matrix3 transform = new Matrix3();
    private boolean isDirty = false;

    private Vector2 dir = new Vector2();
    private float velocity = 200;

    public MovementComponent() {
        type = ComponentType.MOVEMENT;
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

    public void setVelocity(float vel){
        velocity = vel;
    }

    public float getVelocity(){
        return velocity;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        updateTransform();
    }

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public void receiveMessage(BaseComponentMessage message) {
        super.receiveMessage(message);
    }

    /*
     * The math here is correct, but there is a problem with translating
     * input coordinates to world coordinates since LibGDX acts differently on Y axis.
     * Therefore, only the X axis responds correctly.
     *
     * http://gamedev.stackexchange.com/questions/73051/libgdx-converting-screen-click-coordinates-into-world-coordinates-for-2d-game
     */
    private void move(MoveToMessage moveTo){

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
