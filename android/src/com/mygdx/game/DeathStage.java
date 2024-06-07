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

import java.util.ArrayList;

public class DeathStage implements StageWrapper{

    private Stage stage;

    private MyMobileGame2 game;

    private Image fallingChar;
    private Image backGround;
    private TextButton backAgain;
    private TextButton backMenu;
    private Label yourScore;
    private Label actualScore;
    private int tick = 0;
    private int score;
    private boolean actTwo = false;
    private boolean actThree = false;
    private boolean nameCheck = false;
    public String name;

    private ArrayList<Pair<Integer, String>> scores;

    public DeathStage(MyMobileGame2 game, int score){
        this.game = game;
        this.score = score;

        stage = new Stage(game.viewport);

        fallingChar = new Image(game.assetManager.get("deathBlue.png", Texture.class));
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

        backAgain = new TextButton("Try again", game.skin);
        backAgain.setPosition((getStage().getWidth() - backAgain.getWidth()) / 2, (getStage().getHeight() - backAgain.getHeight()) / 2);
        backAgain.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                Gdx.input.setInputProcessor(null);
            }
        });

        backMenu = new TextButton("Back to main menu", game.skin);
        backMenu.setPosition((getStage().getWidth() - backMenu.getWidth()) / 2, backAgain.getY() - 100);
        backMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.gameScreen.
                game.setScreen(game.mainMenuScreen);
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });

        scores = game.mainMenuScreen.getRecords();

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
            actTwo = true;
        }

        if(fallingChar.getY() < stage.getHeight() / 3 && !actThree){
            stage.addActor(backAgain);
            stage.addActor(backMenu);
            actThree = true;
            NickInput listener = new NickInput(this);
            Gdx.input.getTextInput(listener, "Save Score", "", "Put your nick here");
            System.out.println(name);
        }

        if(name != null && !name.equals("") && !nameCheck){
            game.databaseHelper.addSinglePlayerScore(name, score);
/*            int cur = 1;
            boolean presented = false;
            boolean better = false;
            for(Pair<Integer, String> curScore : scores){
                if(name.equals(curScore.second)){
                    presented = true;
                    if(score > curScore.first){
                        better = true;
                    }
                    break;
                }
                cur ++;
            }
            if(!presented) {
                Preferences scoresPref = Gdx.app.getPreferences("scores");
                int sizeContains = scoresPref.getInteger("size");
                sizeContains++;
                scoresPref.putInteger("size", sizeContains);

                StringBuilder keyInt = new StringBuilder("int");
                keyInt.append(sizeContains);
                StringBuilder keyStr = new StringBuilder("str");
                keyStr.append(sizeContains);

                scoresPref.putInteger(keyInt.toString(), score);
                scoresPref.putString(keyStr.toString(), name);
                scoresPref.flush();
            }
            else if(presented && better){
                Preferences scoresPref = Gdx.app.getPreferences("scores");
                StringBuilder keyInt = new StringBuilder("int");
                keyInt.append(cur);
                scoresPref.putInteger(keyInt.toString(), score);
                scoresPref.flush();
            }*/
            nameCheck = true;
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
