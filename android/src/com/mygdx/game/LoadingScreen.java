package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import javax.microedition.khronos.opengles.GL10;

public class LoadingScreen extends AbstractScreen {

    private com.badlogic.gdx.scenes.scene2d.Stage stage;
    private Texture loadingFrame;
    private Texture backgroundFrame;
    private Image loadingImage;
    private Image backgroundImage;
    private int tick = 0;
    private float width = 400;
    private float height = 800;

    public LoadingScreen(MyMobileGame2 game){
        super(game);
    }

    private void addToManager(){
        this.game.assetManager.load("ads.png", Texture.class);
        this.game.assetManager.load("BACKGROUND0001.png", Texture.class);
        this.game.assetManager.load("PLATFORM.png", Texture.class);
        this.game.assetManager.load("imgonline-com-ua-Resize-bvGOQJ76rUhPN9UR.png", Texture.class);
        this.game.assetManager.load("deathBlue.png", Texture.class);
        this.game.assetManager.load("damaged.png", Texture.class);
        this.game.assetManager.load("merge_from_ofoct.png", Texture.class);
        this.game.assetManager.load("Lava.png", Texture.class);
        this.game.assetManager.load("DeathPink.png", Texture.class);
        this.game.assetManager.load("img.png", Texture.class);
        this.game.assetManager.load("2946386-200 (1).png", Texture.class);
    }

    @Override
    public void show() {

        this.game.assetManager.load("DeathPink.png", Texture.class);
        this.game.assetManager.load("BackGroundLoad.png", Texture.class);
        this.game.assetManager.finishLoading();

        addToManager();

        stage = new Stage(game.viewport);

        loadingFrame = this.game.assetManager.get("DeathPink.png", Texture.class);
        loadingImage = new Image(loadingFrame);
        loadingImage.setOrigin(loadingImage.getWidth() / 2, loadingImage.getHeight() / 2);
        backgroundFrame = this.game.assetManager.get("BackGroundLoad.png", Texture.class);
        backgroundImage = new Image(backgroundFrame);

        stage.addActor(backgroundImage);
        stage.addActor(loadingImage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        tick ++;
        tick %= 360;

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if(this.game.assetManager.update()){
            this.game.mainMenuScreen = new MainMenuScreen(this.game);
            this.game.setScreen(this.game.mainMenuScreen);
        }
        loadingImage.setRotation(tick);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.width = stage.getWidth();
        this.height = stage.getHeight();

        backgroundImage.setPosition(0, 0);
        backgroundImage.setHeight(stage.getHeight());
        backgroundImage.setWidth(stage.getWidth());

        loadingImage.setPosition(this.width / 2 - loadingImage.getWidth() / 2, this.height / 2 - loadingImage.getHeight() / 2);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        this.game.assetManager.unload("DeathPink.png");
        this.game.assetManager.unload("BackGroundLoad.png");
    }

    @Override
    public void dispose() {
        loadingFrame.dispose();
        backgroundFrame.dispose();
        stage.dispose();
    }
}
