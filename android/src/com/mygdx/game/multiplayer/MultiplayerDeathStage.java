package com.mygdx.game;

import android.util.Pair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.multiplayer.ConnectionHandler;

import java.util.ArrayList;

public class MultiplayerDeathStage implements StageWrapper{

    private Stage stage;

    private MyMobileGame2 game;

    private Image fallingChar;
    private Image backGround;
    private TextButton backMenu;
    private Label yourScore;
    private Label yourOpponentScore;
    private Label actualScore;
    private Label actualOpponentScore;
    private int tick = 0;
    private int score;
    private boolean actTwo = false;
    private boolean actThree = false;
    public String name;
    private ConnectionHandler connectionHandler;

    public MultiplayerDeathStage(MyMobileGame2 game, int score, boolean host, String name, String nameOpponent, ConnectionHandler connectionHandler){
        this.game = game;
        this.score = score;
        this.connectionHandler = connectionHandler;

        stage = new Stage(game.viewport);

        fallingChar = new Image(game.assetManager.get(host ? "deathBlue.png" : "DeathPink.png", Texture.class));
        fallingChar.setWidth(fallingChar.getWidth() / 2);
        fallingChar.setHeight(fallingChar.getHeight() / 2);
        fallingChar.setPosition((stage.getWidth() - fallingChar.getWidth()) / 2f, stage.getHeight() - fallingChar.getHeight());
        fallingChar.setOrigin(fallingChar.getWidth() / 2, fallingChar.getHeight() / 2);

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setWidth(stage.getWidth());
        backGround.setHeight(stage.getHeight());

        yourScore = new Label("YOUR SCORE :", game.skin,  "title");
        yourScore.setPosition((stage.getWidth() - yourScore.getWidth()) / 2f, stage.getHeight() * 2f / 3f);

        actualScore = new Label(Integer.toString(score), game.skin, "title");
        actualScore.setPosition((stage.getWidth() - actualScore.getWidth()) / 2f, yourScore.getY() - 75);

        yourOpponentScore = new Label("YOUR OPPONENT :", game.skin,  "title");
        yourOpponentScore.setPosition((stage.getWidth() - yourScore.getWidth()) / 2f, actualScore.getY() - 75);

        actualOpponentScore = new Label("0", game.skin, "title");
        actualOpponentScore.setPosition((stage.getWidth() - actualScore.getWidth()) / 2f, yourOpponentScore.getY() - 75);

        backMenu = new TextButton("Back to main menu", game.skin);
        backMenu.setPosition((getStage().getWidth() - backMenu.getWidth()) / 2, actualOpponentScore.getY() - 100);
        backMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(game.gameScreen instanceof MultiplayerGameScreen){
                    ((MultiplayerGameScreen) game.gameScreen).connectionHandler.getBluetoothService().closeAllConnections();
                }
                System.out.println("!!!!!!!!!!!!!!!" + game.mainMenuScreen.getClass().toString());
                game.databaseHelper.addBluetoothPlayerScore(name, nameOpponent, score, connectionHandler.opponentScore());
                game.mainMenuScreen = new MainMenuScreen(game);
                game.setScreen(game.mainMenuScreen);
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });

        stage.addActor(backGround);
        stage.addActor(fallingChar);
    }

    public void render(){
        tick += 7;
        tick %= 360;
        fallingChar.setPosition(fallingChar.getX(), fallingChar.getY() - 7f);
        fallingChar.setRotation(tick);

        if(fallingChar.getY() < stage.getHeight() / 2 && !actTwo){
            stage.addActor(yourScore);
            stage.addActor(actualScore);
            if (connectionHandler.opponentDead()){
                actualOpponentScore.setText(Integer.toString(Math.round(connectionHandler.opponentScore())));
                stage.addActor(yourOpponentScore);
                stage.addActor(actualOpponentScore);
            }
            actTwo = true;
        }

        if(fallingChar.getY() < stage.getHeight() / 3 && !actThree){
            stage.addActor(backMenu);
            actThree = true;
        }
        if(actTwo){
            if (connectionHandler.opponentDead()){
                actualOpponentScore.setText(Integer.toString(Math.round(connectionHandler.opponentScore())));
                stage.addActor(yourOpponentScore);
                stage.addActor(actualOpponentScore);
            }
        }

        stage.act();
        stage.draw();
    }


    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }
}