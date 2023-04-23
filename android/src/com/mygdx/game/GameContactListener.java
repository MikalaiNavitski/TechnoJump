package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {
    MyMobileGame2 game;
    public GameContactListener(MyMobileGame2 game){
        this.game = game;

    }
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if(fa == null || fb == null){
            return;
        }

        if(fa.getUserData() instanceof PlayerClass && fb.getUserData() instanceof Platform){
            PlayerClass curPlayer = (PlayerClass) fa.getUserData();
            Platform curPlatform = (Platform) fb.getUserData();
            if(curPlayer.body.getLinearVelocity().y <= 0) {
                curPlatform.fallTop(curPlayer);
            }
        }

        if(fa.getUserData() instanceof Platform && fb.getUserData() instanceof PlayerClass){
            PlayerClass curPlayer = (PlayerClass) fb.getUserData();
            Platform curPlatform = (Platform) fa.getUserData();
            if(curPlayer.body.getLinearVelocity().y <= 0) {
                curPlatform.fallTop(curPlayer);
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if(fa == null || fb == null){
            return;
        }

        if(fa.getUserData() instanceof PlayerClass && fb.getUserData() instanceof Platform){
            PlayerClass curPlayer = (PlayerClass) fa.getUserData();
            Platform curPlatform = (Platform) fb.getUserData();
            if(curPlayer.positionY <= curPlatform.getY() + curPlatform.sprite.getHeight()){
                contact.setEnabled(false);
            }
        }

        if(fa.getUserData() instanceof Platform && fb.getUserData() instanceof PlayerClass){
            PlayerClass curPlayer = (PlayerClass) fb.getUserData();
            Platform curPlatform = (Platform) fa.getUserData();
            if(curPlayer.positionY <= curPlatform.getY() + curPlatform.sprite.getHeight()){
                contact.setEnabled(false);
            }
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
