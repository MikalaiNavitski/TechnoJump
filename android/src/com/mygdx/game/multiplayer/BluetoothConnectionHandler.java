package com.mygdx.game.multiplayer;

import android.util.Pair;

import com.mygdx.game.bluetooth.BluetoothService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BluetoothConnectionHandler implements ConnectionHandler{


    private float opponentPositionX = 0;
    private float opponentPositionY = 0;

    private float opponentScore = 0;
    private boolean opponentDead = false;
    private Set<Pair<Float, Float> > deletedPlatform = new HashSet<>();

    private BluetoothService bluetoothService;

    public BluetoothConnectionHandler(BluetoothService bluetoothService){
        this.bluetoothService = bluetoothService;
    }

    @Override
    public void sendPosition(float x, float y) {
        bluetoothService.sendMessageToAll("CURRENT STATE: " + x + ", " + y);
    }

    @Override
    public float opponentLastPositionX() {
        handleMessages();
        return opponentPositionX;
    }

    @Override
    public float opponentLastPositionY() {
        handleMessages();
        return opponentPositionY;
    }

    @Override
    public void sendPlatformDeath(float x, float y) {
        bluetoothService.sendMessageToAll("PLATFORM: " + x + ", " + y);
    }

   @Override
    public boolean deletedPlatform(float x, float y) {
        boolean result = deletedPlatform.contains(new Pair<>(x, y));
        if(result) {
            deletedPlatform.remove(new Pair<>(x, y));
        }
        return result;
    }

    @Override
    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    @Override
    public void sendDeath(float score) {
        bluetoothService.sendMessageToAll("DEATH: " + score);
    }

    @Override
    public boolean opponentDead() {
        handleMessages();
        return opponentDead;
    }

    @Override
    public float opponentScore() {
        handleMessages();
        return opponentScore;
    }

    private void handleMessages(){
        ArrayList<String> messages = bluetoothService.getLastMessages();
        for (String message : messages) {
            if (message.startsWith("CURRENT STATE: ")) {
                try {
                    String[] split = message.substring(15).split(", ");
                    opponentPositionX = Float.parseFloat(split[0]);
                    opponentPositionY = Float.parseFloat(split[1]);
                } catch (Exception e){

                }
            }
            else if(message.startsWith("DEATH: ")){
                try {
                    opponentScore = Float.parseFloat(message.substring(7));
                    opponentDead = true;
                } catch (Exception e){

                }
            }
            else if(message.startsWith("PLATFORM: ")){
                try {
                    String[] split = message.substring(10).split(", ");
                    float x = Float.parseFloat(split[0]);
                    float y = Float.parseFloat(split[1]);
                    deletedPlatform.add(new Pair<>(x, y));
                } catch (Exception e){

                }
            }
        }
    }
}
