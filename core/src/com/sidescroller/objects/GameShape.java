package com.sidescroller.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.game.TypeOfGameObject;

/**
 * Creates a 'GameShape' object that contains a physical body with fixtures etc. Also may contain many textures in the form of
 * 'RubeSprite' that is drawn on the map.
 */
public class GameShape implements Draw {

    private Array<RubeSprite> rubeSprites;
    private Body body;
    private final long id;

    /**
     * Creates a GameShape object.
     * @param id The unique id of the object.
     * @param body The body of the object.
     * @param rubeSprites The 'RubeSprite' of the object (contains a 'RubeImage' and a 'Sprite').
     */
    public GameShape(long id, Body body, Array<RubeSprite> rubeSprites) {
        this.id = id;
        this.rubeSprites = rubeSprites;
        body.setUserData(this);
        this.body = body;
    }

    /**
     * Cteates a GameShape object.
     * @param id The unique id of the object.
     * @param body The body of the object.
     */
    public GameShape(long id, Body body) {
        this.id = id;
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
                //If the current RubeSprite is on the layer that is being drawn then draw it
                if (rubeSprite.getRubeImage().renderOrder == layer) {
					Sprite sprite = rubeSprite.getSprite();
					RubeImage rubeImage = rubeSprite.getRubeImage();
                    //rubeImage.center is <></>he variable given by the RUBE enviroment. This does not completely work with
                    //openGL since images in openGL is drawn with their bottom left corner as center.
                    sprite.setPosition(body.getPosition().x + rubeImage.center.x - (rubeImage.width / 2),
                                       body.getPosition().y + rubeImage.center.y - (rubeImage.height / 2));
					sprite.setOrigin(rubeImage.width/2 - rubeImage.center.x, rubeImage.height/2 - rubeImage.center.y);
					sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle() + rubeSprite.getRubeImage().angleInRads));
                    if (rubeImage.flip) {
                        sprite.flip(true, false);
                    }
                    sprite.draw(batch);
                }
            }
        }
    }

    public Body getBody(){return body;}

    public long getId(){return id;}

    public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.SHAPE;}

    public Array<RubeSprite> getRubeSprites(){
        return rubeSprites;
    }

    public void setGroupIndex(short index){
        Filter filter = new Filter();
        filter.groupIndex = index;
        for (Fixture fixture : body.getFixtureList()){
            fixture.setFilterData(filter);
        }
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
            return gameObject.getId() == id;
        }
        catch (Exception e){
            return false;
        }
    }
}
