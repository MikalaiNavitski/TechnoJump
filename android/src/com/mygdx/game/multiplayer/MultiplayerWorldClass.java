package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.multiplayer.ConnectionHandler;

import java.util.ArrayList;
import java.util.Random;

public class MultiplayerWorldClass {

    public ArrayList<Platform> platforms = new ArrayList<>();
    public ArrayList<Sprite> backgrounds = new ArrayList<>();
    private MyMobileGame2 game;
    private Sprite lava;

    Integer generationKey = 0;
    private int generatedY = 0;
    public int etap = 0;

    private Random random;

    private World world;
    private ConnectionHandler connectionHandler;
    public MultiplayerWorldClass(MyMobileGame2 game, World world, Integer generationKey, ConnectionHandler connectionHandler) {
        this.game = game;
        this.world = world;
        this.generationKey = generationKey;
        this.connectionHandler = connectionHandler;


        random = new Random(generationKey);

        int cury = - GameScreen.worldWidth;
        while(cury < GameScreen.worldHeight) {
            Sprite backGround = new Sprite(game.assetManager.get("BACKGROUND0001.png", Texture.class));
            backGround.setPosition(0, cury);
            backGround.setSize(GameScreen.worldWidth * 2, GameScreen.worldWidth * 3);
            backgrounds.add(backGround);
            cury += GameScreen.worldWidth * 3;
        }

        lava = new Sprite(game.assetManager.get("Lava.png", Texture.class));
        lava.setSize(GameScreen.worldWidth * 2, GameScreen.worldWidth * 2);
        lava.setPosition(0, GameScreen.worldWidth * 2);


        etap = 1;

        generatedY = cury;

        float rate = GameScreen.probabilityByDifficulty(0);

        float currentProbability = 0;

        platforms.add(new Platform("PLATFORM.png", this.game, this.world,  GameScreen.worldWidth / 3f - 4.5f,  17));
        platforms.add(new Platform("PLATFORM.png", this.game, this.world,  GameScreen.worldWidth / 3f * 2f - 4.5f,  17));

        int nxty = 20;

        while(nxty < GameScreen.worldHeight - 3){
            float rand = random.nextInt(1001) / 1000f;
            if(rand < currentProbability){
                float rand2  = random.nextInt(100);
                if(rand2 < 20) {
                    platforms.add(new DamagedPlatform("damaged.png", this.game, this.world,  random.nextInt(GameScreen.worldWidth - 9), nxty));
                }
                else  platforms.add(new Platform("PLATFORM.png", this.game, this.world,  random.nextInt(GameScreen.worldWidth - 9), nxty));
                nxty += 3;
                currentProbability = 0;
            }
            else{
                nxty ++;
                currentProbability += rate;
            }
        }
    }

    public void extend(){

        System.out.println("!!!!!!!!!!!Extending");

        for(Platform curPlatform : this.platforms){
            if(curPlatform.getY() < this.game.gameScreen.lowerBound){
                curPlatform.dispose();
            }
        }

        int cury = generatedY;
        while(cury < GameScreen.worldHeight + GameScreen.worldHeight * etap) {
            Sprite backGround = new Sprite(game.assetManager.get("BACKGROUND0001.png", Texture.class));
            backGround.setPosition(0, cury);
            backGround.setSize(GameScreen.worldWidth * 2, GameScreen.worldWidth * 3);
            backgrounds.add(backGround);
            cury += GameScreen.worldWidth * 3;
        }

        float rate = GameScreen.probabilityByDifficulty(GameScreen.worldHeight * etap);
        float currentProbability = (2 * GameScreen.worldHeight - generatedY) * rate;

        int nxty = generatedY;

        while(nxty < GameScreen.worldHeight + GameScreen.worldHeight * etap - 3){
            float rand = random.nextInt(1001) / 1000f;
            if(rand < currentProbability){
                float rand2  = random.nextInt(100);
                if(rand2 < 20) {
                    platforms.add(new DamagedPlatform("damaged.png", this.game, this.world,  random.nextInt(GameScreen.worldWidth - 9), nxty));
                }
                else  platforms.add(new Platform("PLATFORM.png", this.game, this.world,  random.nextInt(GameScreen.worldWidth - 9), nxty));
                nxty += 3;
                currentProbability = 0;
            }
            else{
                nxty ++;
                currentProbability += rate;
            }
        }

        this.etap += 1;
    }


    public void render(float delta, float lower, float upper){

        int calc = 0;

        for(Platform curPlatform : this.platforms){
            if(curPlatform.getY() < this.game.gameScreen.lowerBound){
                curPlatform.dispose();
                calc += 1;
            }
            else{
                break;
            }
        }

        while(calc > 0){
            this.platforms.remove(0);
            calc -= 1;
        }

        for(Sprite curBack : this.backgrounds){
            if((curBack.getY() >= lower && curBack.getY() <= upper) || (curBack.getY() + curBack.getHeight() <= upper && curBack.getHeight() + curBack.getY() >= lower) || (curBack.getY() <= lower && curBack.getY() + curBack.getHeight() >= upper)){
                curBack.draw(game.gameScreen.batch);
            }
        }
        for(Platform curPlatform : this.platforms){
            if(curPlatform.getY() > this.game.gameScreen.lowerBound && curPlatform.getY() + curPlatform.sprite.getHeight() >= lower && curPlatform.getY() <= upper) {
                if(curPlatform instanceof DamagedPlatform){
                    if(connectionHandler.deletedPlatform(curPlatform.getX(), curPlatform.getY())){
                        ((DamagedPlatform) curPlatform).setDestroyed(true);
                    }
                    if(((DamagedPlatform) curPlatform).destroyed()){
                        continue;
                    }
                }
                curPlatform.render(delta);
            }
        }

        lava.setPosition(0, this.game.gameScreen.lowerBound - GameScreen.worldWidth * 2);
        lava.draw(game.gameScreen.batch);


    }

    public void dispose(){
        for(Platform curPlatform : this.platforms){
            curPlatform.dispose();
        }
    }

}
