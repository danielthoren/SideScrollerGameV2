package com.sidescroller.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.sidescroller.game.BodyEditorLoader;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollerGameV2;
import com.badlogic.gdx.utils.JsonReader;

/**
 * Created by daniel on 2016-06-06.
 */
public class Shape implements Draw {

    private Sprite sprite;
    private Body body;
    private final long iD;
    private final Vector2 spriteOrigin;

    public Shape(long iD, String nameOfBodyInJson, World world, Vector2 position, boolean isStatic, String pathToJSON, float friction, float bodyWidth)throws NullPointerException {
        this.iD = iD;
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(Gdx.files.internal(pathToJSON));
        spriteOrigin = bodyEditorLoader.getOrigin(nameOfBodyInJson, bodyWidth);

        String pathToImage = bodyEditorLoader.getImagePath(nameOfBodyInJson);
        Texture texture = new Texture(Gdx.files.internal(pathToImage));
        sprite = new Sprite(texture);
        //Sizing the sprite so that it fits on the body.
        sprite.setSize(bodyWidth, bodyWidth * ((float)texture.getHeight() / (float)texture.getWidth()));

        createBody(bodyEditorLoader, position, friction, world, bodyWidth, nameOfBodyInJson, isStatic);

        body.setUserData(this);
    }

    private void createBody(BodyEditorLoader bodyEditorLoader, Vector2 position, float friction, World world, float bodyWidth, String nameOfBdyInJson, boolean isStatic){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        if (isStatic) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        }
        else{
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = friction;

        body = world.createBody(bodyDef);

        bodyEditorLoader.attachFixture(body, nameOfBdyInJson, fixtureDef, bodyWidth);
    }

    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    public void draw(SpriteBatch batch){
        sprite.setPosition(body.getPosition().x, body.getPosition().y);
        sprite.setOrigin(spriteOrigin.x, spriteOrigin.y);
        sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle()));
        sprite.draw(batch);
    }

    public long getId(){
        return iD;
    }

    /**
     * Overridden version of Equals that ensures that both object pointers are the exact same instantiation of
     * its class.
     * @param obj The object to compare to.
     * @return True if they are equal, else false.
     */
    @Override
    public boolean equals(Object obj) {
        try{
            GameObject gameObject = (GameObject) obj;
            return gameObject.getId() == this.getId();
        }
        catch (Exception e){
            return false;
        }
    }
}
