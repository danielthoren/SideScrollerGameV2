package com.sidescroller.com.sidescroller.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sidescroller.game.BodyEditorLoader;
import com.sidescroller.game.Draw;
import com.sidescroller.game.SideScrollerGameV2;

/**
 * Created by daniel on 2016-06-06.
 */
public class StaticShape implements Draw {

    private Texture texture;
    private Body body;
    private final long iD;

    public StaticShape(long iD, World world, Vector2 position, String pathToJSON, Texture texture, float friction) {
        this.iD = iD;
        this.texture = texture;
        createBody(position, pathToJSON, friction, world);
    }

    private void createBody(Vector2 position, String pathToJSON, float friction, World world){
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(Gdx.files.internal(pathToJSON));

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = friction;

        body = world.createBody(bodyDef);

        Long iDtemp = iD;
        bodyEditorLoader.attachFixture(body, "floor", fixtureDef, 1f);
    }

    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    public void draw(SpriteBatch batch){
        batch.draw(texture, body.getPosition().x, body.getPosition().y);
    }

    public long getId(){
        return iD;
    }
}
