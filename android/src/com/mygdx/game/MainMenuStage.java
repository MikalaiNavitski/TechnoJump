package com.mygdx.game;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.bluetooth.BluetoothService;

public class MainMenuStage implements StageWrapper {

    private final Stage stage;
    private final Label nameLabel;
    private final TextButton playSinglePlayer;
    private final TextButton playMultiPlayerBluetooth;
    private final TextButton statistics;
    private final Image backGround;

    public MainMenuStage(MyMobileGame2 game){
        stage = new Stage(game.viewport);

        game.skin = new Skin(files.internal("skin/comic-ui.json"));

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

        playMultiPlayerBluetooth = new TextButton("Play by Bluetooth", game.skin);
        playMultiPlayerBluetooth.setHeight(playMultiPlayerBluetooth.getHeight() * 2);
        playMultiPlayerBluetooth.setWidth(playMultiPlayerBluetooth.getWidth() * 2);
        playMultiPlayerBluetooth.getLabel().setFontScale(2);
        playMultiPlayerBluetooth.setPosition(stage.getWidth() / 2 - playMultiPlayerBluetooth.getWidth() / 2, playSinglePlayer.getY() - playMultiPlayerBluetooth.getHeight() - 100);

        playMultiPlayerBluetooth.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.androidLauncher.bluetoothConnectionProlong();
                game.mainMenuScreen.currentStage = new com.mygdx.game.BluetoothChooseStage(game, BluetoothService.getInstance());
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });


                backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        statistics = new TextButton("Your Statistics", game.skin);
        statistics.setHeight(statistics.getHeight() * 2);
        statistics.setWidth(statistics.getWidth() * 2);
        statistics.getLabel().setFontScale(2);
        statistics.setPosition(stage.getWidth()/ 2 - statistics.getWidth() / 2, playMultiPlayerBluetooth.getY() - statistics.getHeight() - 100);

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
        stage.addActor(playMultiPlayerBluetooth);
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
        playMultiPlayerBluetooth.setPosition(stage.getWidth() / 2 - playMultiPlayerBluetooth.getWidth() / 2, playSinglePlayer.getY()  - playMultiPlayerBluetooth.getHeight() - 100);
        statistics.setPosition(stage.getWidth()/ 2 - statistics.getWidth() / 2, playMultiPlayerBluetooth.getY() - statistics.getHeight() - 100);

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
