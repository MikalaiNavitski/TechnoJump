package com.mygdx.game.bluetooth;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Pair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.GameScreen;
import com.mygdx.game.MultiplayerGameScreen;
import com.mygdx.game.MyMobileGame2;
import com.mygdx.game.StageWrapper;
import com.mygdx.game.multiplayer.BluetoothConnectionHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public class BluetoothStage implements StageWrapper {

    private Stage stage;
    private MyMobileGame2 game;

    private Label firstPlayerLabel;
    private Label secondPlayerLabel;
    private Image backGround;
    String firstPlayerName = "None";
    String secondPlayerName = "None";
    boolean secondPlayerConnected = false;
    boolean firstPlayerReady = false;
    boolean secondPlayerReady = false;
    private boolean startedInput = false;

    private TextButton ready;
    private NickInputBluetooth playerNickInput;

    private BluetoothService bluetoothService;

    private int opponentRandom = 0;
    private int myRandom = 0;

    private long delta = 0;

    @TargetApi(Build.VERSION_CODES.N)
    public BluetoothStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);
        delta = System.currentTimeMillis();

        bluetoothService = BluetoothService.getInstance(); // Initialize the Bluetooth service

        Gdx.input.setCatchBackKey(true);

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        firstPlayerLabel = new Label("Connected: " + firstPlayerName, game.skin, "title");
        firstPlayerLabel.setHeight((float) (stage.getHeight() * 0.2));
        firstPlayerLabel.setWidth((float) (stage.getWidth() * 0.8));
        firstPlayerLabel.setFontScale(1);
        firstPlayerLabel.setPosition(stage.getWidth() / 2 - firstPlayerLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - firstPlayerLabel.getHeight() / 2);

        secondPlayerLabel = new Label(!secondPlayerConnected ? "Not Connected: " : "Connected" + secondPlayerName, game.skin, "title");
        secondPlayerLabel.setHeight((float) (stage.getHeight() * 0.2));
        secondPlayerLabel.setWidth((float) (stage.getWidth() * 0.8));
        secondPlayerLabel.setFontScale(1);
        secondPlayerLabel.setPosition(stage.getWidth() / 2 - secondPlayerLabel.getWidth() / 2, stage.getHeight() / 4 * 2 - secondPlayerLabel.getHeight() / 2);

        ready = new TextButton("Ready", game.skin);
        ready.setHeight((float) (stage.getHeight() * 0.4));
        ready.setWidth((float) (stage.getWidth() * 0.8));
        ready.getLabel().setFontScale(2);
        ready.setPosition(stage.getWidth() / 2 - ready.getWidth() / 2, stage.getHeight() / 4  - ready.getHeight()/ 2);

        ready.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!firstPlayerReady) {
                    firstPlayerReady = true;
                    ready.setText("Unready");
                }
                else{
                    firstPlayerReady = false;
                    ready.setText("Ready");
                }
                sendReadyMessage(firstPlayerReady);
            }
        });

        stage.addActor(backGround);
        stage.addActor(firstPlayerLabel);
        stage.addActor(secondPlayerLabel);
        stage.addActor(ready);

    }

    private void sendNameMessage(String name) {
        if(bluetoothService != null) {
            bluetoothService.sendMessageToAll("PLAYER_NAME:" + name);
        }
    }

    private void sendReadyMessage(boolean ready) {
        if(bluetoothService != null) {
            bluetoothService.sendMessageToAll("PLAYER_READY:" + (ready ? "true" : "false"));
        }
    }

    private void sendConnectedMessage(){
        if(bluetoothService != null) {
            bluetoothService.sendMessageToAll("PLAYER_CONNECTED");
        }
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        firstPlayerLabel.setPosition(stage.getWidth() / 2 - firstPlayerLabel.getWidth() / 2, stage.getHeight() / 4 * 3 - firstPlayerLabel.getHeight() / 2);
        secondPlayerLabel.setPosition(stage.getWidth() / 2 - secondPlayerLabel.getWidth() / 2, stage.getHeight() / 4 * 2 - secondPlayerLabel.getHeight() / 2);
        ready.setPosition(stage.getWidth() / 2 - ready.getWidth() / 2, stage.getHeight() / 4  - ready.getHeight()/ 2);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render() {
        if (firstPlayerReady ){
            game.gameScreen = new MultiplayerGameScreen(game, myRandom, System.currentTimeMillis(), true, new BluetoothConnectionHandler(bluetoothService), firstPlayerName, secondPlayerName);
            game.setScreen(game.gameScreen);
            Gdx.input.setInputProcessor(null);
            /*if(myRandom == 0) {
                myRandom = Math.abs(new Random().nextInt()) + 1;
            }

            bluetoothService.sendMessageToAll("RANDOM: " + myRandom);
            if(opponentRandom != 0){
                if(opponentRandom > myRandom){
                    game.gameScreen = new MultiplayerGameScreen(game, myRandom, System.currentTimeMillis(), true, new BluetoothConnectionHandler(bluetoothService), firstPlayerName, secondPlayerName);
                    game.setScreen(game.gameScreen);
                    Gdx.input.setInputProcessor(null);
                }
                else{
                    game.gameScreen = new MultiplayerGameScreen(game, opponentRandom, System.currentTimeMillis(), false, new BluetoothConnectionHandler(bluetoothService), firstPlayerName, secondPlayerName);
                    game.setScreen(game.gameScreen);
                    Gdx.input.setInputProcessor(null);
                }
            }*/

        }
        if(System.currentTimeMillis() - delta > 1000) {
            delta = System.currentTimeMillis();
            sendNameMessage(firstPlayerName);
            sendReadyMessage(firstPlayerReady);
            sendConnectedMessage();
        }
        goThroughMessages();
        if (!bluetoothService.checkSocketConnection()) {
            secondPlayerConnected = false;
        }
        firstPlayerLabel.setText("Connected: " + firstPlayerName + (!firstPlayerReady ? ": Not Ready" : ": Ready"));
        secondPlayerLabel.setText(!secondPlayerConnected ? "Not Connected: " : "Connected: " + secondPlayerName + (!secondPlayerConnected ? "" : !secondPlayerReady ? ": Not Ready" : ": Ready"));
        stage.act();
        if(!startedInput){
            startedInput = true;
            playerNickInput = new NickInputBluetooth(this);
            Gdx.input.getTextInput(playerNickInput, "Your Name", "", "Put your nick here");
        }
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.mainMenuScreen.currentStage = game.mainMenuScreen.mainMenuStage;
            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            bluetoothService.closeAllConnections();
            Gdx.input.setCatchBackKey(false);
        }
    }

    public void setFirstPlayerName(String name) {
        this.firstPlayerName = name;
        sendNameMessage(name);
    }

    public void setSecondPlayerName(String name) {
        this.secondPlayerName = name;
    }

    private void goThroughMessages(){
        ArrayList<String> messages = bluetoothService.getLastMessages();

        for(String message : messages){
            if(message.contains("PLAYER_NAME")){
                setSecondPlayerName(message.substring(12));
            }
            else if(message.contains("PLAYER_READY")){
                if(message.substring(13).equals("true")){
                    secondPlayerReady = true;
                }
                else{
                    secondPlayerReady = false;
                }
            }
            else if(message.contains("PLAYER_CONNECTED")){
                secondPlayerConnected = true;
            }
            else if(message.contains("RANDOM: ")){
                opponentRandom = Integer.parseInt(message.substring(8));
            }
        }
    }

    // Rest of the class...
}