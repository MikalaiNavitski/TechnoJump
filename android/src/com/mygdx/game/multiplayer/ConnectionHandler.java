package com.mygdx.game.multiplayer;

import com.mygdx.game.bluetooth.BluetoothService;

public interface ConnectionHandler {

    void sendPosition(float x, float y);

    float opponentLastPositionX();

    float opponentLastPositionY();

    void sendDeath(float score);

    void sendPlatformDeath(float x, float y);

    boolean deletedPlatform(float x, float y);

    BluetoothService getBluetoothService();

    boolean opponentDead();

    float opponentScore();

}
