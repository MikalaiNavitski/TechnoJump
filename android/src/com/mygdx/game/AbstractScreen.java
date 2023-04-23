package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;

public abstract class AbstractScreen extends ScreenAdapter {

    protected MyMobileGame2 game;

    public AbstractScreen(MyMobileGame2 game){
        this.game = game;
    }

    public void render(float delta){
        super.render(delta);
    }

}
