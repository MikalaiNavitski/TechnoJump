package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class DeathScreen extends AbstractScreen {

    private final DeathStage stage;
    public DeathScreen(MyMobileGame2 game, int score) {
        super(game);
        game.gameScreen.dispose();
        stage = new DeathStage(game, score);
        Gdx.input.setInputProcessor(stage.getStage());
    }
    @Override
    public void render(float delta){
        super.render(delta);
        stage.render();
    }

    public void resize(int width, int height){
        stage.resize(width, height);
    }

    public void dispose(){
        stage.dispose();
    }

}
