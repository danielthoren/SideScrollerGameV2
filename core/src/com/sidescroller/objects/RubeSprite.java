package com.sidescroller.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;

/**
 * Created by daniel on 2016-06-26.
 */
public class RubeSprite {
    private RubeImage rubeImage;
    private Sprite sprite;

    public RubeSprite(RubeImage rubeImage) {
        this.rubeImage = rubeImage;

        sprite = new Sprite(new Texture(Gdx.files.internal(rubeImage.file)));
        sprite.setSize(rubeImage.width, rubeImage.height);
        System.out.print(rubeImage.width);
        System.out.print("       ");
        System.out.println(rubeImage.height);
    }

    public RubeImage getRubeImage() {return rubeImage;}

    public Sprite getSprite() {return sprite;}
}
