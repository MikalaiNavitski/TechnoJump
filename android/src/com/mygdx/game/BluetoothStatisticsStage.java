package com.mygdx.game;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class BluetoothStatisticsStage implements StageWrapper{

    private Stage stage;
    private MyMobileGame2 game;

    private Label mainLabel;
    private Label historyLabel;
    private Image backGround;
    private ScrollPane scoresList;

    private ArrayList<Label> scores;

    @TargetApi(Build.VERSION_CODES.N)
    public BluetoothStatisticsStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);

        Gdx.input.setCatchBackKey(true);

        mainLabel = new Label("Your Single Player Statistics", game.skin, "title");
        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 100);

        historyLabel = new Label("History", game.skin, "title");
        historyLabel.setPosition(stage.getWidth() / 2 - historyLabel.getWidth() / 2, mainLabel.getY() - 100 - historyLabel.getHeight());

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        scores = new ArrayList<>();

        stage.addActor(backGround);
        stage.addActor(mainLabel);
        stage.addActor(historyLabel);

        Cursor result = game.databaseHelper.getLast20BluetoothPlayerScores();

        while(result.moveToNext()) {
            StringBuilder string = new StringBuilder(result.getString(1));
            string.append(" : ");
            string.append(Float.toString(result.getFloat(3)));
            string.append("     ");
            string.append(result.getString(2));
            string.append(" : ");
            string.append(Float.toString(result.getFloat(4)));
            Label curLabel = new Label(string.toString(), game.skin, "title");
            scores.add(curLabel);
            System.out.println(string.toString());
        }

        Table scrollTable = new Table(game.skin);

        for(Label curLabel : scores){
            scrollTable.add(curLabel);
            scrollTable.row();
        }

        scoresList = new ScrollPane(scrollTable);

        scoresList.setHeight(stage.getHeight() / 2);
        scoresList.setWidth((float) (stage.getWidth() * 0.8));

        scoresList.setPosition(stage.getWidth() / 2 - scoresList.getWidth() / 2, historyLabel.getY() - 100 - scoresList.getHeight());


        final Table table = new Table();
        table.setFillParent(true);
        table.add(scoresList).fill().expand();

        stage.addActor(scoresList);
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
        historyLabel.setPosition(stage.getWidth() / 3 - historyLabel.getWidth() / 2, mainLabel.getY() - 100 - historyLabel.getHeight());

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
            game.mainMenuScreen.currentStage = new StatisticsStage(game);
            Gdx.input.setInputProcessor(game.mainMenuScreen.getStage());
            Gdx.input.setCatchBackKey(false);
        }
    }
}
