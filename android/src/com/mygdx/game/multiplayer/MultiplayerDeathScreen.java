package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.multiplayer.ConnectionHandler;

public class MultiplayerDeathScreen extends AbstractScreen {
    private final com.mygdx.game.MultiplayerDeathStage stage;
    public MultiplayerDeathScreen(MyMobileGame2 game, int score, boolean host, String name, String opponentName, ConnectionHandler connectionHandler) {
        super(game);
        game.gameScreen.dispose();
        stage = new com.mygdx.game.MultiplayerDeathStage(game, score, host,  name, opponentName, connectionHandler);
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
