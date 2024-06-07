package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.multiplayer.ConnectionHandler;

import java.util.ArrayList;

public class MultiplayerGameScreen extends GameScreen{
    private OrthographicCamera camera;
    private float prevLowerBound;
    private int cameraWidth;
    private int cameraHeight;
    private World worldPhysics;
    private World emptyWorldPhysics;
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

    private boolean powerUpReady1 = false;
    private boolean powerUpReady2 = false;
    private int powerUpCount1 = 0;
    private int powerUpCount2 = 0;

    private ImageButton buttonUp;
    private Sprite buttonUpUn;
    public Boolean jumpIs = false;

    private Stage stage;

    public MultiplayerGameScreen(MyMobileGame2 game, Integer generationKey, long startTime, boolean host, ConnectionHandler connectionHandler, String name, String opponentName) {
        super(game);
        stage = new Stage(game.viewport);

        lowerBound = 0;

        this.connectionHandler = connectionHandler;
        this.startTime = startTime;
        this.host = host;
        this.name = name;
        this.opponentName = opponentName;

        sendingTime = startTime;

        worldPhysics = new World(new Vector2(0 ,0), true);
        worldPhysics.setContactListener(new GameContactListener(game));
        emptyWorldPhysics = new World(new Vector2(0, 0), true);
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

        buttonUpUn = new Sprite(this.game.assetManager.get("img.png", Texture.class));
        buttonUpUn.setSize(10, 10);
        buttonUpUn.setPosition(10, 100);

        buttonUp = new ImageButton(this.game.skin);
        buttonUp.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(this.game.assetManager.get("2946386-200 (1).png", Texture.class)));
        buttonUp.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(this.game.assetManager.get("2946386-200 (1).png", Texture.class)));

        buttonUp.setPosition(10, 10);
        buttonUp.setHeight(buttonUp.getHeight() * 2);
        buttonUp.setWidth(buttonUp.getWidth() * 2);
        buttonUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                powerUpReady1 = false;
                jumpIs = true;
            }
        });

        stage.addActor(buttonUp);

        toDelete = new ArrayList<>();
    }


    @Override
    public void render(float delta){

        for(Body curBody : toDelete){
            this.worldPhysics.destroyBody(curBody);
        }

        toDelete.clear();



        player.move(jumpIs);
        score = player.getY();
        if(System.currentTimeMillis() - sendingTime > 100) {
            connectionHandler.sendPosition(player.positionX, player.positionY);
            moveOpponent();
            sendingTime = System.currentTimeMillis();
            if(player.positionY < opponent.positionY){
                if(powerUpReady1 == false) {
                    powerUpCount1 += 1;
                }
                if(powerUpReady2 == false) {
                    powerUpCount2 += 1;
                }
            }
        }

        if(powerUpCount1 == 100){
            powerUpReady1 = true;
            powerUpCount1 = 0;
        }

        if(powerUpCount2 == 100){
            powerUpReady2 = true;
            powerUpCount2 = 0;
        }


        if(frames != 0 && System.currentTimeMillis() - frames > (100 / 50)) {

            while (System.currentTimeMillis() - frames > (100 / 50)){
                opponent.setPosition(opponent.positionX + opponentNextPositionX, opponent.positionY + opponentNextPositionY);
                frames += (100 / 50);
            }
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
        if (powerUpReady1){
            Gdx.input.setInputProcessor(stage);
            stage.act();
            stage.draw();
        }
        else{
            buttonUpUn.setPosition(0, player.positionY - (float) camera.viewportHeight / 2 + 5);
            buttonUpUn.draw(batch);
        }
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
        opponentNextPositionX = (connectionHandler.opponentLastPositionX() - opponent.positionX) / 50;
        opponentNextPositionY = (connectionHandler.opponentLastPositionY() - opponent.positionY) / 50;
        opponentNextX = connectionHandler.opponentLastPositionX();
        opponentNextY = connectionHandler.opponentLastPositionY();

        opponent.setPosition(opponent.positionX + opponentNextPositionX, opponent.positionY + opponentNextPositionY);
        frames = System.currentTimeMillis();
    }

}
