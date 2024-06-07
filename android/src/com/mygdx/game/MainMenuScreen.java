package com.mygdx.game;

import android.util.Pair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class MainMenuScreen extends AbstractScreen{

    public MainMenuStage mainMenuStage;
    public GameScreen gameScreen;
    private SingleStatisticsStage statisticsStage;
    public StageWrapper currentStage;
    public MainMenuScreen(MyMobileGame2 game) {
        super(game);


        mainMenuStage = new MainMenuStage(game);
        currentStage = mainMenuStage;
        Gdx.input.setInputProcessor(currentStage.getStage());
    }

    @Override
    public void render(float delta){
        super.render(delta);

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        currentStage.render();
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

    public Stage getStage(){
        return this.currentStage.getStage();
    }

    public ArrayList<Pair<Integer, String>> getRecords(){
        ArrayList<Pair<Integer, String>> result = new ArrayList<>();
        Preferences scoresPref = Gdx.app.getPreferences("scores");
        if(scoresPref.contains("size")){
            int sizeContains = scoresPref.getInteger("size");
            int cur = 1;
            while(cur <= sizeContains){
                StringBuilder keyInt = new StringBuilder("int");
                keyInt.append(cur);
                StringBuilder keyStr = new StringBuilder("str");
                keyStr.append(cur);
                String string = scoresPref.getString(keyStr.toString());
                int number = scoresPref.getInteger(keyInt.toString());
                result.add(new Pair<>(number, string));
                cur++;
            }
        }
        scoresPref.flush();
        return result;
    }

}
