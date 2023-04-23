package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Entity {

    protected Texture texture;
    protected MyMobileGame2 game;
    protected float positionX;
    protected float positionY;

    protected Sprite sprite;
    protected Body body;
    protected Fixture fixture;
    protected World world;


    public Entity(String texturePath, MyMobileGame2 game, World world, float x, float y){
        texture = game.assetManager.get(texturePath, Texture.class);
        sprite = new Sprite(texture);
        this.game = game;
        this.world = world;
        positionY = y;
        positionX = x;
    }

    public float getX(){
        return positionX;
    }

    public float getY(){
        return positionY;
    }

    public void setPosition(float x, float y){
        positionX = x; positionY = y;
    }

    public Body getBody(){
        return this.body;
    }

    public abstract void render(float delta);

    public void dispose() {
    }

    protected abstract void definePhysics();


}
