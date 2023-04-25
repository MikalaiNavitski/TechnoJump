package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class DamagedPlatform extends Platform{

    private boolean destroyed = false;

    public DamagedPlatform(String texturePath, MyMobileGame2 game, World world, float x, float y) {
        super(texturePath, game, world, x, y);
    }
    @Override
    public void fallTop(PlayerClass player){
        Vector2 newVelocity = new Vector2(player.body.getLinearVelocity().x, 38);
        player.body.setLinearVelocity(newVelocity);
        destroyed = true;
    }

    public boolean destroyed(){
        return destroyed;
    }

}
