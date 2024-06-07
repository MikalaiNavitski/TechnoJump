package com.mygdx.game.bluetooth;

import com.badlogic.gdx.Input;

public class NickInputBluetooth implements Input.TextInputListener {

    private final BluetoothStage whereToSave;

    public NickInputBluetooth(BluetoothStage stage){
        whereToSave = stage;
    }

    @Override
    public void input (String text) {
        whereToSave.setFirstPlayerName(text);
    }

    @Override
    public void canceled () {
        whereToSave.setFirstPlayerName("You");
    }

}