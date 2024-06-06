package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class GameScreen extends AbstractScreen{
    public SpriteBatch batch;
    private OrthographicCamera camera;
    public float lowerBound;
    private float prevLowerBound;
    private int cameraWidth;
    private int cameraHeight;
    public static final int worldWidth = 40;
    public static final int worldHeight = 800;
    public float ratio;
    private World worldPhysics;

    public float score = 0;

    private WorldClass world;
    private PlayerClass player;
    private Box2DDebugRenderer debugRenderer;
    public ArrayList<Body> toDelete;

    public GameScreen(MyMobileGame2 game) {
        super(game);
        lowerBound = 0;

        worldPhysics = new World(new Vector2(0 ,0), true);
        worldPhysics.setContactListener(new GameContactListener(game));
        debugRenderer = new Box2DDebugRenderer();

        cameraWidth = Gdx.graphics.getWidth();
        cameraHeight = Gdx.graphics.getHeight();
        ratio = 1f * cameraHeight / cameraWidth;
        camera = new OrthographicCamera(40f, 40f * ratio );

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        camera.update();
        batch = new SpriteBatch();
        world = new WorldClass(this.game, worldPhysics);
        player = new PlayerClass("imgonline-com-ua-Resize-bvGOQJ76rUhPN9UR.png", this.game, this.worldPhysics, GameScreen.worldWidth / 2f - 2f, 4);

        toDelete = new ArrayList<>();
    }


    @Override
    public void render(float delta){
        super.render(delta);

        for(Body curBody : toDelete){
            this.worldPhysics.destroyBody(curBody);
        }

        toDelete.clear();

        player.move();
        prevLowerBound = lowerBound;
        lowerBound = Math.max(lowerBound, (player.positionY - 20f * ratio));
        score += lowerBound - prevLowerBound;
        camera.position.set(camera.viewportWidth / 2f,  lowerBound + ( 40f * ratio) / 2f, 0);
        camera.update();
        game.viewport.apply();

        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        world.render(delta, lowerBound, (lowerBound + 40f * ratio));
        player.render(delta);
        batch.end();
        debugRenderer.render(this.getWorld(), camera.combined);

        Vector2 prevPositionPlayer = new Vector2(player.body.getPosition().x, player.body.getPosition().y);

        worldPhysics.step(1/60f, 6, 1);

        player.setPosition(player.getX() + (player.body.getPosition().x - prevPositionPlayer.x),  player.getY() + (player.body.getPosition().y - prevPositionPlayer.y));
        if(player.positionX + player.sprite.getWidth() < 0){
            player.setPosition(40f, player.positionY);
            player.body.setTransform(player.positionX, player.positionY, 0);
            player.body.setAwake(true);
        }
        else if(player.positionX > 40f){
            player.setPosition(0 + player.sprite.getWidth(), player.positionY);
            player.body.setTransform(player.positionX, player.positionY, 0);
            player.body.setAwake(true);
        }

        if(lowerBound >= GameScreen.worldHeight - 3 * GameScreen.worldWidth) {
            this.world = new WorldClass(game, worldPhysics,  this.world);
            lowerBound = lowerBound - GameScreen.worldHeight + 3 * GameScreen.worldWidth;
            player.setPosition(player.positionX, player.positionY - GameScreen.worldHeight + 3 * GameScreen.worldWidth);
            player.body.setTransform(player.positionX, player.positionY, 0);
            player.body.setAwake(true);
        }

        if(player.positionY < lowerBound){
            game.setScreen(new DeathScreen(game, (int)score));
        }

    }

    @Override
    public void resize(int width, int height) {
        this.camera.viewportWidth = 40f;
        this.camera.viewportHeight = 40f * ratio;
        this.camera.update();
    }

    public static float probabilityByDifficulty(int score){
        if(score < 1079){
            return 0.05f;
        }
        else if (score < 2149){
            return 0.025f;
        }
        else{
            return 0.0125f;
        }
    }

    public World getWorld(){
        return this.worldPhysics;
    }

    public void dispose(){
        player.dispose();
        world.dispose();
        worldPhysics.dispose();
        batch.dispose();
    }

}
