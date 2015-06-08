package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

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

    private float speed = 500; // default scalar for player movement

    private Vector2 velocity = new Vector2();
    private Vector2 dest = new Vector2();
    private boolean hasDest = false;

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

    public void setDest(float x, float y){
        dest.set(x, y);
        velocity.set(dest.cpy().sub(getWorldPosition()).nor().scl(speed));
        hasDest = true;
    }

    public void removeDest(){
        hasDest = false;
        setVelocity(Vector2.Zero);
    }

    public Vector2 getDest() {
        if(!hasDest)
            return null;

        return dest;
    }

    private void updateMovement(){
        Vector2 newPos = getWorldPosition();

        if(hasDest && dest.dst(newPos) <= 10) {
            System.out.println("Arrived to dest.");
            removeDest();
        }

        if (!getVelocity().isZero()) {
            Vector2 move = getVelocity().scl(Gdx.graphics.getDeltaTime());

            newPos.add(move);
            setWorldPosition(newPos);
        }
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        updateTransform();
        updateMovement();
    }

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public void receiveMessage(BaseComponentMessage message) {
        super.receiveMessage(message);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
