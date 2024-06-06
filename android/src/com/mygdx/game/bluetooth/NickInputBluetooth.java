package com.mygdx.game.bluetooth;

import com.badlogic.gdx.Input;
import com.mygdx.game.DeathStage;

public class NickInputBluetooth implements Input.TextInputListener {

    private BluetoothStage whereToSave;

    public NickInputBluetooth(BluetoothStage stage){
        whereToSave = stage;
    }

    @Override
    public void input (String text) {
        //System.out.println(text);
        whereToSave.setFirstPlayerName(text);
    }

    @Override
    public void canceled () {
        whereToSave.setFirstPlayerName("You");
    }

}