package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

import javax.microedition.khronos.opengles.GL10;

public class MainMenuScreen extends AbstractScreen{

    private MainMenuStage mainMenuStage;
    public GameScreen gameScreen;
    private Stage currentStage;
    public MainMenuScreen(MyMobileGame2 game) {
        super(game);
        mainMenuStage = new MainMenuStage(game);
        currentStage = mainMenuStage.getStage();
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void render(float delta){
        super.render(delta);

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        currentStage.act();
        currentStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mainMenuStage.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mainMenuStage.dispose();
    }

}
