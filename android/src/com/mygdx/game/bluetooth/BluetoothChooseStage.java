package com.mygdx.game;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.bluetooth.BluetoothService;
import com.mygdx.game.bluetooth.BluetoothStage;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothChooseStage implements StageWrapper{

    private final Stage stage;
    private final MyMobileGame2 game;

    private final Label mainLabel;
    private final Image backGround;

    private final ArrayList<TextButton> bluetoothes;

    @TargetApi(Build.VERSION_CODES.N)
    public BluetoothChooseStage(MyMobileGame2 game, BluetoothService bluetoothService){
        this.game = game;
        stage = new Stage(game.viewport);

        Gdx.input.setCatchBackKey(true);

        mainLabel = new Label("Accessible Connections", game.skin, "title");
        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 100);

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        bluetoothes = new ArrayList<>();

        stage.addActor(backGround);
        stage.addActor(mainLabel);

        Set<BluetoothDevice> devices = bluetoothService.getConnectedDevices();

        for(BluetoothDevice device : devices) {
                TextButton curButton = new TextButton(device.getName(), game.skin);
                curButton.setHeight((float) (curButton.getHeight() * 1.5));
                curButton.setWidth((float) (curButton.getWidth() * 1.5));
                curButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(bluetoothService.connectToDevice(device)){
                            game.mainMenuScreen.currentStage = new BluetoothStage(game);
                            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
                        }
                    }
                });
                bluetoothes.add(curButton);
        }

        Table scrollTable = new Table(game.skin);


        for(TextButton curBluetooth : bluetoothes){
            scrollTable.add(curBluetooth);
            scrollTable.row();
        }

        ScrollPane bluetoothList = new ScrollPane(scrollTable);

        bluetoothList.setHeight(stage.getHeight() / 4);
        bluetoothList.setWidth((float) (stage.getWidth() * 0.9));

        bluetoothList.setPosition(stage.getWidth() / 2 - bluetoothList.getWidth() / 2, stage.getHeight() - 100 - mainLabel.getHeight() - 20 - bluetoothList.getHeight());


        final Table table = new Table();
        table.setFillParent(true);
        table.add(bluetoothList).fill().expand();

        stage.addActor(bluetoothList);

    }


    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 50);
        float upper = stage.getHeight() - 50;
        for(TextButton curBluetooth : bluetoothes){
            upper -= 10;
            curBluetooth.setPosition(stage.getWidth() / 2 - curBluetooth.getWidth() / 2, upper);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.mainMenuScreen.currentStage = game.mainMenuScreen.mainMenuStage;
            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            Gdx.input.setCatchBackKey(false);
        }
    }
}
