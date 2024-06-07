package com.mygdx.game;

import com.badlogic.gdx.Input;

public class NickInput implements Input.TextInputListener {

    private final DeathStage whereToSave;

    public NickInput(DeathStage stage){
        whereToSave = stage;
    }

    @Override
    public void input (String text) {
        whereToSave.name = text;
    }

    @Override
    public void canceled () {
        whereToSave.name = "";
    }
}