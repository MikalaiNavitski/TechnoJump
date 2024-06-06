package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.multiplayer.ConnectionHandler;

import java.util.ArrayList;

public class MultiplayerGameScreen extends GameScreen{
    private OrthographicCamera camera;
    private float prevLowerBound;
    private int cameraWidth;
    private int cameraHeight;
    private World worldPhysics;
    public long startTime;
    private long sendingTime = 0;

    private com.mygdx.game.MultiplayerWorldClass world;
    private PlayerClass player;
    private PlayerClass opponent;
    private Box2DDebugRenderer debugRenderer;

    public ConnectionHandler connectionHandler;

    private boolean host;
    private String name;
    private String opponentName;
    private float opponentNextPositionX;
    private float opponentNextPositionY;
    private float opponentNextX = 0;
    private float opponentNextY = 0;
    private long frames = 0;


    public MultiplayerGameScreen(MyMobileGame2 game, Integer generationKey, long startTime, boolean host, ConnectionHandler connectionHandler, String name, String opponentName) {
        super(game);
        lowerBound = 0;

        this.connectionHandler = connectionHandler;
        this.startTime = startTime;
        this.host = host;
        this.name = name;
        this.opponentName = opponentName;

        sendingTime = startTime;

        worldPhysics = new World(new Vector2(0 ,0), true);
        worldPhysics.setContactListener(new GameContactListener(game));
        World emptyWorldPhysics = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

        cameraWidth = Gdx.graphics.getWidth();
        cameraHeight = Gdx.graphics.getHeight();
        ratio = 1f * cameraHeight / cameraWidth;
        camera = new OrthographicCamera(40f, 40f * ratio );

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        camera.update();
        batch = new SpriteBatch();
        System.out.println(batch);
        world = new com.mygdx.game.MultiplayerWorldClass(this.game, worldPhysics, generationKey, connectionHandler);

        if (host) {
            player = new PlayerClass("imgonline-com-ua-Resize-bvGOQJ76rUhPN9UR.png", this.game, this.worldPhysics, GameScreen.worldWidth / 3f - 2f, 20);
            opponent = new PlayerClass("merge_from_ofoct.png", this.game, emptyWorldPhysics, GameScreen.worldWidth / 3f * 2 - 2f, 20);
        }
        else{
            opponent = new PlayerClass("imgonline-com-ua-Resize-bvGOQJ76rUhPN9UR.png", this.game, emptyWorldPhysics, GameScreen.worldWidth / 3f - 2f, 20);
            player = new PlayerClass("merge_from_ofoct.png", this.game, this.worldPhysics, GameScreen.worldWidth / 3f * 2 - 2f, 20);
        }
        toDelete = new ArrayList<>();
    }


    @Override
    public void render(float delta){

        for(Body curBody : toDelete){
            this.worldPhysics.destroyBody(curBody);
        }

        toDelete.clear();

        player.move();
        if(System.currentTimeMillis() - sendingTime > 500) {
            connectionHandler.sendPosition(player.positionX, player.positionY);
            moveOpponent();
            sendingTime = System.currentTimeMillis();
        }
        if(System.currentTimeMillis() - frames > 500 / 60){
            opponent.setPosition(opponent.positionX + opponentNextPositionX, opponent.positionY + opponentNextPositionY);
            frames = System.currentTimeMillis();
        }
        lowerBound = (float) ((System.currentTimeMillis() - startTime - 5000) * 10) /1000;
        camera.position.set(camera.viewportWidth / 2f,  player.positionY, 0);
        camera.update();
        game.viewport.apply();

        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        world.render(delta, player.positionY - game.viewport.getScreenHeight(), player.positionY + game.viewport.getScreenHeight());
        player.render(delta);
        opponent.render(delta);
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

        if(player.positionY >= GameScreen.worldHeight * (this.world.etap) - 3 * GameScreen.worldWidth) {
            this.world.extend();
        }

        if(player.positionY < lowerBound){
            game.setScreen(new com.mygdx.game.MultiplayerDeathScreen(game, (int)score, host, name, opponentName, connectionHandler));
            connectionHandler.sendDeath(score);
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

    private void moveOpponent(){
        if(opponentNextX != 0){
            opponent.setPosition(opponentNextX, opponentNextY);
        }
        opponentNextPositionX = (connectionHandler.opponentLastPositionX() - opponent.positionX) / 60;
        opponentNextPositionY = (connectionHandler.opponentLastPositionY() - opponent.positionY) / 60;
        opponentNextX = connectionHandler.opponentLastPositionX();
        opponentNextY = connectionHandler.opponentLastPositionY();

        opponent.setPosition(opponent.positionX + opponentNextPositionX, opponent.positionY + opponentNextPositionY);
        frames = System.currentTimeMillis();
    }

}
