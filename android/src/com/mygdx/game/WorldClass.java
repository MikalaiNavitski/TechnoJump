package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

public class WorldClass {

    public ArrayList<Platform> platforms = new ArrayList<>();
    public ArrayList<Sprite> backgrounds = new ArrayList<>();
    private MyMobileGame2 game;

    private World world;
    public WorldClass(MyMobileGame2 game, World world){
        this.game = game;
        this.world = world;

        int cury = 0;
        while(cury < GameScreen.worldHeight) {
            Sprite backGround = new Sprite(game.assetManager.get("BACKGROUND0001.png", Texture.class));
            backGround.setPosition(0, cury);
            backGround.setSize(GameScreen.worldWidth * 2, GameScreen.worldWidth * 3);
            backgrounds.add(backGround);
            cury += GameScreen.worldWidth * 3;
        }

        float rate = GameScreen.probabilityByDifficulty(0);

        float currentProbability = 0;

        platforms.add(new Platform("PLATFORM.png", this.game, this.world,  GameScreen.worldWidth / 2f - 4.5f,  1));

        int nxty = 8;

        while(nxty < GameScreen.worldHeight - 3){
            float rand = new Random().nextInt(1001) / 1000f;
            if(rand < currentProbability){
                float rand2  = new Random().nextInt(100);
                if(rand2 < 20) {
                    platforms.add(new DamagedPlatform("damaged.png", this.game, this.world,  new Random().nextInt(GameScreen.worldWidth - 9), nxty));
                }
                else  platforms.add(new Platform("PLATFORM.png", this.game, this.world,  new Random().nextInt(GameScreen.worldWidth - 9), nxty));
                nxty += 3;
                currentProbability = 0;
            }
            else{
                nxty ++;
                currentProbability += rate;
            }
        }
    }

    public WorldClass(MyMobileGame2 game, World world,  WorldClass previousWorld){
        this.game = game;
        this.world = world;

        int cury = 0;
        while(cury < GameScreen.worldHeight) {
            Sprite backGround = new Sprite(game.assetManager.get("BACKGROUND0001.png", Texture.class));
            backGround.setPosition(0, cury);
            backGround.setSize(GameScreen.worldWidth * 2, GameScreen.worldWidth * 3);
            backgrounds.add(backGround);
            cury += GameScreen.worldWidth * 3;
        }

        float rate = GameScreen.probabilityByDifficulty((int)this.game.gameScreen.score);
        int lasty = 0;

        for(Platform curPlatf : previousWorld.platforms){
            if(curPlatf.getY() > GameScreen.worldHeight - 3 * GameScreen.worldWidth){
                Platform newPlatform;
                if(curPlatf instanceof DamagedPlatform){
                    newPlatform = new DamagedPlatform("damaged.png",  game, this.world,  curPlatf.getX(), curPlatf.getY() - GameScreen.worldHeight + 3f * GameScreen.worldWidth);
                }
                else {
                    newPlatform = new Platform("PLATFORM.png", game, this.world, curPlatf.getX(), curPlatf.getY() - GameScreen.worldHeight + 3f * GameScreen.worldWidth);
                }
                this.platforms.add(newPlatform);
                lasty = Integer.max((int)newPlatform.getY(), lasty);
            }
        }
        lasty += 3;

        float currentProbability = (2 * GameScreen.worldHeight - lasty) * rate;

        while(lasty < GameScreen.worldHeight - 3){
            float rand = new Random().nextInt(1001) / 1000f;
            if(rand < currentProbability){
                float rand2  = new Random().nextInt(100);
                if(rand2 < 20) {
                    platforms.add(new DamagedPlatform("damaged.png", this.game, this.world,  new Random().nextInt(GameScreen.worldWidth - 9), lasty));
                }
                else  platforms.add(new Platform("PLATFORM.png", this.game, this.world, new Random().nextInt(GameScreen.worldWidth - 9), lasty));
                lasty += 3;
                currentProbability = 0;
            }
            else{
                lasty ++;
                currentProbability += rate;
            }
        }
        previousWorld.dispose();
    }


    public void render(float delta, float lower, float upper){
        for(Sprite curBack : this.backgrounds){
            if((curBack.getY() >= lower && curBack.getY() <= upper) || (curBack.getY() + curBack.getHeight() <= upper && curBack.getHeight() + curBack.getY() >= lower) || (curBack.getY() <= lower && curBack.getY() + curBack.getHeight() >= upper)){
                curBack.draw(game.gameScreen.batch);
            }
        }

        for(Platform curPlatform : this.platforms){
            if(curPlatform.getY() + curPlatform.sprite.getHeight() >= lower && curPlatform.getY() <= upper) {
                if(curPlatform instanceof DamagedPlatform){
                    if(((DamagedPlatform) curPlatform).destroyed()){
                        continue;
                    }
                }
                curPlatform.render(delta);
            }
        }

    }

    public void dispose(){
        for(Platform curPlatform : this.platforms){
            curPlatform.dispose();
        }
    }

}
