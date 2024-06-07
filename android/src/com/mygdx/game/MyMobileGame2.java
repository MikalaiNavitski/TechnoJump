package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class MyMobileGame2 extends Game {

    public ScreenViewport viewport = new ScreenViewport();
    public com.badlogic.gdx.assets.AssetManager assetManager = new com.badlogic.gdx.assets.AssetManager();
    public MainMenuScreen mainMenuScreen;
    public AndroidLauncher androidLauncher;
    public GameScreen gameScreen;
    public DatabaseHelper databaseHelper;
    public Skin skin;

    @Override
    public void create(){
        Skin skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        setScreen(new LoadingScreen(this));
    }


    public void setAndroidLauncher(AndroidLauncher androidLauncher){
        this.androidLauncher = androidLauncher;
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }

}
