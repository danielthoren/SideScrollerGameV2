package com.sidescroller.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.sidescroller.game.Draw;
import com.sidescroller.game.InputListener;
import com.sidescroller.game.SideScrollerGameV2;

/**
 * Created by daniel on 2016-06-06.
 */
public class Player implements InputListener, Draw {

    private Vector2 acceleration;
    private Vector2 maxVelocity;
    private Body body;
    private Texture texture;
    private final long iD;

    public Player(long iD, FixtureDef[] fixtures, Vector2 position, Texture texture, float friction, float density, float restitution) {
        this.iD = iD;
        this.texture = texture;
        createBody(position, fixtures, friction, density, restitution);
    }

    private void createBody(Vector2 position, FixtureDef[] fixtureDefs, float friction, float density, float restitution){

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = SideScrollerGameV2.getCurrentMap().getWorld().createBody(bodyDef);
        for (FixtureDef fixtureDef : fixtureDefs){
            body.createFixture(fixtureDef);
        }
        body.setUserData(this);
        body.setActive(true);
        body.setSleepingAllowed(true);
    }

    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    @Override
    public void draw(SpriteBatch batch){
        batch.draw(texture, body.getPosition().x - texture.getWidth()/2, body.getPosition().y + texture.getHeight()/2);
    }

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public void keyDown(int keycode){
    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public void keyUp (int keycode){
    }

    /** Called when a key was typed
     *
     * @param character The character
     * @return whether the input was processed */
    public void keyTyped (char character){
    }

    public long getId(){
        return iD;
    }
}
