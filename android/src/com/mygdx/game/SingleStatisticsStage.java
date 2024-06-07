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

public class SingleStatisticsStage implements StageWrapper{

    private Stage stage;
    private MyMobileGame2 game;

    private Label mainLabel;
    private Label historyLabel;
    private Label bestScoreLabel;
    private Image backGround;
    private ScrollPane scoresList;
    private ScrollPane scoresListBest;

    private ArrayList<Label> scores;
    private ArrayList<Label> best;

    @TargetApi(Build.VERSION_CODES.N)
    public SingleStatisticsStage(MyMobileGame2 game){
        this.game = game;
        stage = new Stage(game.viewport);

        Gdx.input.setCatchBackKey(true);

        mainLabel = new Label("Your Single Player Statistics", game.skin, "title");
        mainLabel.setPosition(stage.getWidth() / 2 - mainLabel.getWidth() / 2, stage.getHeight() - 100);

        historyLabel = new Label("History", game.skin, "title");
        historyLabel.setPosition(stage.getWidth() / 3 - historyLabel.getWidth() / 2 - 50, mainLabel.getY() - 100 - historyLabel.getHeight());

        bestScoreLabel = new Label("Best Results", game.skin, "title");
        bestScoreLabel.setPosition(stage.getWidth() / 3 * 2 - bestScoreLabel.getWidth() / 2 + 50, mainLabel.getY() - 100 - bestScoreLabel.getHeight());

        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
        backGround.setPosition(0, 0);
        backGround.setHeight(stage.getHeight());
        backGround.setWidth(stage.getWidth());

        scores = new ArrayList<>();

        stage.addActor(backGround);
        stage.addActor(mainLabel);
        stage.addActor(historyLabel);
        stage.addActor(bestScoreLabel);

        Cursor result = game.databaseHelper.getLast20SinglePlayerScores();

        while(result.moveToNext()) {
            StringBuilder string = new StringBuilder(result.getString(1));
            string.append(" : ");
            string.append(Float.toString(result.getFloat(2)));
            Label curLabel = new Label(string.toString(), game.skin, "title");
            scores.add(curLabel);
            curLabel.setScale(0.5f);
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

        scoresList.setPosition(stage.getWidth() / 3 - scoresList.getWidth() / 2 - 50, historyLabel.getY() - 100 - scoresList.getHeight());


        final Table table = new Table();
        table.setFillParent(true);
        table.add(scoresList).fill().expand();

        stage.addActor(scoresList);

        Cursor resultBest = game.databaseHelper.getDistinctSinglePlayerScoresSortedByScore();

        best = new ArrayList<Label>();

        while(resultBest.moveToNext()) {
            StringBuilder string = new StringBuilder(resultBest.getString(0));
            string.append(" : ");
            string.append(Float.toString(resultBest.getFloat(1)));
            Label curLabel = new Label(string.toString(), game.skin, "title");
            best.add(curLabel);
            curLabel.setScale(0.5f);
            System.out.println(string.toString());
            //stage.addActor(curLabel);
        }

        Table scrollTableBest = new Table(game.skin);

        for(Label curLabel : best){
            scrollTableBest.add(curLabel);
            scrollTableBest.row();
        }

        scoresListBest = new ScrollPane(scrollTableBest);

        scoresListBest.setHeight(stage.getHeight() / 4);
        scoresListBest.setWidth(stage.getWidth() / 2);

        scoresListBest.setPosition(stage.getWidth() / 3 * 2 - scoresListBest.getWidth() / 2 + 50, historyLabel.getY() - 100 - scoresListBest.getHeight());


        final Table tableBest = new Table();
        tableBest.setFillParent(true);
        tableBest.add(scoresListBest).fill().expand();

        stage.addActor(scoresListBest);

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
        bestScoreLabel.setPosition(stage.getWidth() / 3 * 2 - bestScoreLabel.getWidth() / 2, mainLabel.getY() - 100 - bestScoreLabel.getHeight());

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
