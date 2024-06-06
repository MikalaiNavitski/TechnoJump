package com.mygdx.game;

import static com.badlogic.gdx.Gdx.files;

import android.util.Pair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.bluetooth.BluetoothService;
import com.mygdx.game.bluetooth.BluetoothStage;

import java.util.ArrayList;

public class MainMenuStage implements StageWrapper {

    private Stage stage;
    private MyMobileGame2 game;
    private Label nameLabel;
    private TextButton playSinglePlayer;
    private TextButton playMultiPlayerBluetootth;
    private TextButton statistics;
    private TextButton settings;
    private Image backGround;

    public MainMenuStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);

        this.game.skin = new Skin(files.internal("skin/comic-ui.json"));

        nameLabel = new Label("Techno Jump", game.skin, "title");
        nameLabel.setHeight(nameLabel.getHeight() * 2);
        nameLabel.setWidth(nameLabel.getWidth() * 2);
        nameLabel.setFontScale(2);
        nameLabel.setPosition(stage.getWidth() / 2 - nameLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - nameLabel.getHeight() / 2);

        playSinglePlayer = new TextButton("Play Solo", game.skin);
        playSinglePlayer.setHeight(playSinglePlayer.getHeight() * 2);
        playSinglePlayer.setWidth(playSinglePlayer.getWidth() * 2);
        playSinglePlayer.getLabel().setFontScale(2);
        playSinglePlayer.setPosition(stage.getWidth() / 2 - playSinglePlayer.getWidth() / 2, stage.getHeight() / 2 - playSinglePlayer.getHeight()/ 2);

        playSinglePlayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                Gdx.input.setInputProcessor(null);
            }
        });

        playMultiPlayerBluetootth = new TextButton("Play by Bluetooth", game.skin);
        playMultiPlayerBluetootth.setHeight(playMultiPlayerBluetootth.getHeight() * 2);
        playMultiPlayerBluetootth.setWidth(playMultiPlayerBluetootth.getWidth() * 2);
        playMultiPlayerBluetootth.getLabel().setFontScale(2);
        playMultiPlayerBluetootth.setPosition(stage.getWidth() / 2 - playMultiPlayerBluetootth.getWidth() / 2, playSinglePlayer.getY() - playMultiPlayerBluetootth.getHeight() - 100);

        playMultiPlayerBluetootth.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.androidLauncher.bluetoothConnectionProlong();
                game.mainMenuScreen.currentStage = new com.mygdx.game.BluetoothChooseStage(game, BluetoothService.getInstance());
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });


                backGround = new Image(this.game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

/*        settings = new TextButton("...", game.skin);
        settings.setHeight(settings.getHeight() * 2);
        settings.setWidth(settings.getWidth() * 2);
        settings.getLabel().setFontScale(3);
        settings.setPosition(stage.getWidth() - settings.getWidth(), stage.getHeight() - settings.getHeight());*/

        statistics = new TextButton("Your Statistics", game.skin);
        statistics.setHeight(statistics.getHeight() * 2);
        statistics.setWidth(statistics.getWidth() * 2);
        statistics.getLabel().setFontScale(2);
        statistics.setPosition(stage.getWidth()/ 2 - statistics.getWidth() / 2, playMultiPlayerBluetootth.getY() - statistics.getHeight() - 100);

        statistics.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.mainMenuScreen.currentStage = new StatisticsStage(game);
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });

        stage.addActor(backGround);
        stage.addActor(nameLabel);
        stage.addActor(playSinglePlayer);
        stage.addActor(playMultiPlayerBluetootth);
       // stage.addActor(settings);
        stage.addActor(statistics);

    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        nameLabel.setPosition(stage.getWidth() / 2 - nameLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - nameLabel.getHeight() / 2);
        playSinglePlayer.setPosition(stage.getWidth() / 2 - playSinglePlayer.getWidth() / 2, stage.getHeight() / 2 - playSinglePlayer.getHeight()/ 2);
        playMultiPlayerBluetootth.setPosition(stage.getWidth() / 2 - playMultiPlayerBluetootth.getWidth() / 2, playSinglePlayer.getY()  - playMultiPlayerBluetootth.getHeight() - 100);
        //settings.setPosition(stage.getWidth() - settings.getWidth(), stage.getHeight() - settings.getHeight());
        statistics.setPosition(stage.getWidth()/ 2 - statistics.getWidth() / 2, playMultiPlayerBluetootth.getY() - statistics.getHeight() - 100);

        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }


}
