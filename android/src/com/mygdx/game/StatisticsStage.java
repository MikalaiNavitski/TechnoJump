package com.mygdx.game;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Pair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Comparator;

public class StatisticsStage implements StageWrapper{

    private Stage stage;
    private MyMobileGame2 game;

    private Label mainLabel;
    private Image backGround;
    private ScrollPane scoresList;

    private ArrayList<Label> scores;

    @TargetApi(Build.VERSION_CODES.N)
    public StatisticsStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);

        Gdx.input.setCatchBackKey(true);

        mainLabel = new Label("Your Statistics", game.skin, "title");
        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 100);

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        scores = new ArrayList<>();

        stage.addActor(backGround);
        stage.addActor(mainLabel);

        ArrayList<Pair<Integer, String>> valueScore = game.mainMenuScreen.getRecords();
        if(valueScore.size() > 0) {
            valueScore.sort(new Comparator<Pair<Integer, String>>() {
                @Override
                public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
                    if (o1.first < o2.first) {
                        return 1;
                    } else if (o1.first > o2.first) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            //float upper = stage.getHeight() - 100 - mainLabel.getHeight() - 20;

            for (int i = 0; i < valueScore.size(); i++) {
                StringBuilder string = new StringBuilder(valueScore.get(i).second);
                string.append(" : ");
                string.append(valueScore.get(i).first.toString());
                Label curLabel = new Label(string.toString(), game.skin, "title");
               // upper -= 100f;
                //curLabel.setPosition(stage.getWidth() / 2 - curLabel.getWidth() / 2, upper);
                scores.add(curLabel);
                System.out.println(string.toString());
                //stage.addActor(curLabel);
            }

            Table scrollTable = new Table(game.skin);

            for(Label curLabel : scores){
                scrollTable.add(curLabel);
                scrollTable.row();
            }

            scoresList = new ScrollPane(scrollTable);

            scoresList.setHeight(stage.getHeight() / 4);
            scoresList.setWidth(stage.getWidth() / 2);

            scoresList.setPosition(stage.getWidth() / 2 - scoresList.getWidth() / 2, stage.getHeight() - 100 - mainLabel.getHeight() - 20 - scoresList.getHeight());


            final Table table = new Table();
            table.setFillParent(true);
            table.add(scoresList).fill().expand();

            stage.addActor(scoresList);

        }

    }


    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 50);
        float upper = stage.getHeight() - 50;
        for(Label curLabel : scores){
            upper -= 10;
            curLabel.setPosition(stage.getWidth() / 2 - curLabel.getWidth() / 2, upper);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.mainMenuScreen.currentStage = game.mainMenuScreen.mainMenuStage;
            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            Gdx.input.setCatchBackKey(false);
        }
    }
}
