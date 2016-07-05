package com.sidescroller.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.Map.RubeLoader.gushikustudios.RubeScene;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.RubeSceneLoader;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.Shape;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singelton class that loads and unloads maps.
 */
public class MapLoader {
    public static final int LAYERS = 16;

    private static MapLoader instance = new MapLoader();
    private RubeSceneLoader loader;
    private HashMap<String, Map> loadedMaps;

    private MapLoader() {
        loadedMaps = new HashMap<String, Map>(1);
        loader = new RubeSceneLoader();
    }

    public static MapLoader getInstance(){return instance;}

    public Map loadMap(String mapPath){
        if (!loadedMaps.containsKey(mapPath)){
            RubeScene scene = loader.loadScene(Gdx.files.internal(mapPath));

            Map map = new Map(scene.getWorld(), true, scene.velocityIterations, scene.positionIterations);

            //Adding all of the bodies to the map
            int p = 0;
            for (int x = 0; x < scene.getBodies().size; x++){
                Body body = scene.getBodies().get(x);
                Array<RubeImage> rubeImages = scene.getMappedImage(body);
                //Creating the arrays for the hashmap and adding images to the map if there are images
                Shape shape;
                if (rubeImages != null) {
                    Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
                    for (RubeImage rubeImage : rubeImages){
                        rubeSprites.add(new RubeSprite(rubeImage));
                    }
                    shape = new Shape(map.getObjectID(), body, rubeSprites);
                }
                else{
                    shape = new Shape(map.getObjectID(), body);
                }
            }

            loadedMaps.put(mapPath, map);
        }
        return loadedMaps.get(mapPath);
    }


}
