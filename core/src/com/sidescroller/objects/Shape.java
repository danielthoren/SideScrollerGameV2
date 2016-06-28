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

/**
 * Created by daniel on 2016-06-06.
 */
public class Shape implements Draw {

    private HashMap<Integer, Array<RubeSprite>> rubeSpriteMap;
    private Body body;
    private final long iD;

    public Shape(long iD, Body body, HashMap<Integer, Array<RubeSprite>> rubeSpriteMap) {
        this.iD = iD;
        this.rubeSpriteMap = rubeSpriteMap;
        body.setUserData(this);
        this.body = body;
    }

    public Shape(long iD, Body body) {
        this.iD = iD;
        rubeSpriteMap = null;
        body.setUserData(this);
        this.body = body;
    }

    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    public void draw(SpriteBatch batch){
        if (rubeSpriteMap != null) {
            //TODO: Create function that checks wich is the lowest level so that no arrays before that is needed.
            for (int layers = 0; layers < rubeSpriteMap.size(); layers++) {
                Array<RubeSprite> rubeSprites = rubeSpriteMap.get(layers);

                for (RubeSprite rubeSprite : rubeSprites) {
                    Sprite sprite = rubeSprite.getSprite();
                    RubeImage rubeImage = rubeSprite.getRubeImage();

                    sprite.setOrigin(rubeImage.width/2, rubeImage.height/2);
                    sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle() + rubeImage.angleInRads));
                    sprite.setSize(rubeImage.width, rubeImage.height);
                    //rubeImage.center is the variable given by the RUBE enviroment. This does not completely work with
                    //openGL since images in openGL is drawn with their bottom left corner as center.
                    //sprite.setPosition(body.getPosition().x + rubeImage.center.x - (rubeImage.width/2), body.getPosition().y + rubeImage.center.y - (rubeImage.height/2));
                    sprite.setPosition(body.getPosition().x - rubeImage.width/2, body.getPosition().y - rubeImage.height/2);
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
