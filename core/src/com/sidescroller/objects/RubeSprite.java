package com.sidescroller.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;

/**
 * A container for image information that is used when creating game objects.
 */
public class RubeSprite {
    private RubeImage rubeImage;
    private Sprite sprite;

    /**
     * Creates a 'RubeSprite'
     * @param rubeImage A 'RubeImage' from wich to create a 'Sprite'.
     */
    public RubeSprite(RubeImage rubeImage) {
        this.rubeImage = rubeImage;

        sprite = new Sprite(new Texture(Gdx.files.internal(rubeImage.file)));
        sprite.setSize(rubeImage.width, rubeImage.height);
    }

    /**
     * Returns the RubeImage.
     * @return RubeImage
     */
    public RubeImage getRubeImage() {return rubeImage;}

    /**
     * Returns the Sprite
     * @return Sprite
     */
    public Sprite getSprite() {return sprite;}
}
