package com.sidescroller.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.game.BodyEditorLoader;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollerGameV2;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by daniel on 2016-06-06.
 */
public class Shape implements Draw {

    private Array<RubeSprite> rubeSprites;
    private Body body;
    private final long iD;

    public Shape(long iD, Body body, Array<RubeSprite> rubeSprites) {
        this.iD = iD;
        this.rubeSprites = rubeSprites;
        body.setUserData(this);
        this.body = body;
    }

    public Shape(long iD, Body body) {
        this.iD = iD;
        rubeSprites = null;
        body.setUserData(this);
        this.body = body;
    }

    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    public void draw(SpriteBatch batch, int layer){
        if (rubeSprites != null) {
            for (RubeSprite rubeSprite : rubeSprites) {
                Sprite sprite = rubeSprite.getSprite();
                RubeImage rubeImage = rubeSprite.getRubeImage();
                //If the current RubeSprite is on the layer that is being drawn then draw it
                if (rubeImage.filter == layer) {
                    //rubeImage.center is <></>he variable given by the RUBE enviroment. This does not completely work with
                    //openGL since images in openGL is drawn with their bottom left corner as center.
                    sprite.setPosition(body.getPosition().x + rubeImage.center.x - (rubeImage.width / 2),
                                       body.getPosition().y + rubeImage.center.y - (rubeImage.height / 2));
                    if (rubeImage.flip) {
                        sprite.flip(true, false);
                    }
                    sprite.draw(batch);
                }
            }
        }
    }

    public long getId(){return iD;}

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
