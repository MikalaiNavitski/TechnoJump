package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerClass extends Entity{

    private float stateTime;
    private final Animation<TextureRegion> mainAnimation;
    public PlayerClass(String texturePath, MyMobileGame2 game, World world, float x, float y){
        super(texturePath, game, world,  x, y);
        sprite.setBounds(x, y, 2, 3);

        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / 12,
                texture.getHeight());
        System.out.println(tmp.length);
        mainAnimation = new Animation<>(1f / 12f,  tmp[0]);

        definePhysics();
        stateTime = 0;
        body.setLinearVelocity(0, -5);
    }
    @Override
    public void render(float delta) {
        TextureRegion currentFrame = mainAnimation.getKeyFrame(stateTime, true);
        System.out.println(this.game.gameScreen.batch);
        this.game.gameScreen.batch.draw(currentFrame, this.positionX - 2, this.positionY - 3f, 4, 6);
    }

    @Override
    protected void definePhysics() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.getX(), this.getY());
        body = world.createBody(bodyDef);

        PolygonShape platformPhysics = new PolygonShape();
        platformPhysics.setAsBox(this.sprite.getWidth(), this.sprite.getHeight());
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = platformPhysics ;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        fixture = this.body.createFixture(fixtureDef);
        fixture.setUserData(this);
        platformPhysics.dispose();
    }

    public void move(){
        float oXMovement = 5 * Gdx.input.getAccelerometerX() * Gdx.input.getAccelerometerX();
        if(Gdx.input.getAccelerometerX() > 0){
            oXMovement *= -1;
        }
        float oYMovement = this.body.getLinearVelocity().y;

        stateTime += Gdx.graphics.getDeltaTime();

        oYMovement -= 0.5f;

        Vector2 newVelocity = new Vector2(oXMovement, oYMovement);

        body.setLinearVelocity(newVelocity);
    }

    public void move(Boolean jump){
        float oXMovement = 5 * Gdx.input.getAccelerometerX() * Gdx.input.getAccelerometerX();
        if(Gdx.input.getAccelerometerX() > 0){
            oXMovement *= -1;
        }
        float oYMovement = this.body.getLinearVelocity().y;

        stateTime += Gdx.graphics.getDeltaTime();

        oYMovement -= 0.5f;

        if (jump) {
           oYMovement += 250f;
            ((MultiplayerGameScreen) game.gameScreen).jumpIs = Boolean.FALSE;
        }

        Vector2 newVelocity = new Vector2(oXMovement, oYMovement);

        body.setLinearVelocity(newVelocity);
    }
    @Override
    public void dispose(){
        world.destroyBody(this.body);
    }
}
