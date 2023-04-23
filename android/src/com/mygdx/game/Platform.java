package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Platform extends Entity{

    public Platform(String texturePath, MyMobileGame2 game, World world,  float x, float y) {
        super(texturePath, game,world, x, y);
        sprite.setBounds(x, y, 9, 3);
        definePhysics();
    }

    @Override
    public void render(float delta) {
        sprite.draw(game.gameScreen.batch);
    }

    @Override
    protected void definePhysics() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(this.getX() + this.sprite.getWidth() / 2, this.getY());
        body = this.world.createBody(bodyDef);

        PolygonShape platformPhysics = new PolygonShape();
        platformPhysics.setAsBox(this.sprite.getWidth() / 2, this.sprite.getHeight() / 4);
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = platformPhysics ;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0f;

        fixture = this.body.createFixture(fixtureDef);
        fixture.setUserData(this);
        platformPhysics.dispose();
    }

    @Override
    public void dispose(){
        super.dispose();
        world.destroyBody(this.body);
    }

    public void fallTop(PlayerClass player){
        Vector2 newVelocity = new Vector2(player.body.getLinearVelocity().x, 50);
        player.body.setLinearVelocity(newVelocity);
    }

}
