package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;

public interface StageWrapper {

    Stage getStage();

    void resize(int width, int height);

    void dispose();

}
