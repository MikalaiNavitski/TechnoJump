package com.mygdx.game;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.bluetooth.BluetoothService;

public class StatisticsStage implements StageWrapper {

    private Stage stage;
    private MyMobileGame2 game;
    private Label nameLabel;
    private TextButton soloStatistics;
    private TextButton bluetoothStatistics;
    private Image backGround;

    public StatisticsStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);

        this.game.skin = new Skin(files.internal("skin/comic-ui.json"));

        nameLabel = new Label("Statistics", game.skin, "title");
        nameLabel.setHeight(nameLabel.getHeight() * 2);
        nameLabel.setWidth(nameLabel.getWidth() * 2);
        nameLabel.setFontScale(2);
        nameLabel.setPosition(stage.getWidth() / 2 - nameLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - nameLabel.getHeight() / 2);

        soloStatistics = new TextButton("Solo Statistics", game.skin);
        soloStatistics.setHeight(soloStatistics.getHeight() * 2);
        soloStatistics.setWidth(soloStatistics.getWidth() * 2);
        soloStatistics.getLabel().setFontScale(2);
        soloStatistics.setPosition(stage.getWidth() / 2 - soloStatistics.getWidth() / 2, stage.getHeight() / 2 - soloStatistics.getHeight()/ 2);

        soloStatistics.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.mainMenuScreen.currentStage = new SingleStatisticsStage(game);
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });

        bluetoothStatistics = new TextButton("Bluetooth Statistics", game.skin);
        bluetoothStatistics.setHeight(bluetoothStatistics.getHeight() * 2);
        bluetoothStatistics.setWidth(bluetoothStatistics.getWidth() * 2);
        bluetoothStatistics.getLabel().setFontScale(2);
        bluetoothStatistics.setPosition(stage.getWidth() / 2 - bluetoothStatistics.getWidth() / 2, soloStatistics.getY() - bluetoothStatistics.getHeight() - 100);

        bluetoothStatistics.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.mainMenuScreen.currentStage = new BluetoothStatisticsStage(game);
                Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            }
        });


        backGround = new Image(this.game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        stage.addActor(backGround);
        stage.addActor(nameLabel);
        stage.addActor(soloStatistics);
        stage.addActor(bluetoothStatistics);

    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        nameLabel.setPosition(stage.getWidth() / 2 - nameLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - nameLabel.getHeight() / 2);
        soloStatistics.setPosition(stage.getWidth() / 2 - soloStatistics.getWidth() / 2, stage.getHeight() / 2 - soloStatistics.getHeight()/ 2);
        bluetoothStatistics.setPosition(stage.getWidth() / 2 - bluetoothStatistics.getWidth() / 2, soloStatistics.getY()  - bluetoothStatistics.getHeight() - 100);

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
        Gdx.input.setCatchBackKey(true);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.mainMenuScreen.currentStage = game.mainMenuScreen.mainMenuStage;
            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            Gdx.input.setCatchBackKey(false);
        }
    }


}
