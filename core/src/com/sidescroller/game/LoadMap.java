package com.sidescroller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sidescroller.objects.StaticShape;
import javafx.scene.image.Image;
import sun.applet.Main;

import java.util.*;

/**
 * The singelton that loads and holds the map. This is a singelton since there is no need for more than one maploader.
 * All maps and objects can be loaded from this one class. This saves system resources since there is no need for more than one
 * class that loads maps.
 */
public final class LoadMap {


    private final static LoadMap INSTANCE = new LoadMap();
    private AbstractMap<Integer, Map> maps;

    private final static float PIX_PER_METER = 100;

    private LoadMap() {
        maps = new HashMap<Integer, Map>();}

    public static LoadMap getInstance(){
        return INSTANCE;
    }
    //Todo Read value from file
    private static final Vector2 GRAVITY = new Vector2(0, 20);

    /**
     * Function loading specified map. At the moment it only instantiates hardcoded objects.
     * It was planed to keep this code in a file that would be loaded and thats why it holds so many magic numbers.
     * Todo make loadmap use serializable to load objects. Make saveMap function that saves a map using serializable.
     * @param mapNumber The number of the map to be loaded
     */
    public void loadMap(Integer mapNumber){

        if (!maps.containsKey(mapNumber)){

            Map map = new Map(GRAVITY, true);

            //Do add items to map here:
            StaticShape floor = new StaticShape(map.getObjectID(), "floor",  map.getWorld(), new Vector2(-3,0), "test.json", 2f, 5f);
            map.addDrawObject(floor);
            StaticShape bottle = new StaticShape(map.getObjectID(), "test01", map.getWorld(), new Vector2(0,-2), "bottle.json", 2f, 4f);
            map.addDrawObject(bottle);


            maps.put(mapNumber, map);
        }
    }

    /**
     * Tries to load images, if the path is invalid then load errortexture.
     * @param path The path to load from.
     * @param size The imSize of the image, if the vector is (0,0) then it loads the default imSize.
     * @return Returns a javafx image.
     */
    public Image loadImage(String path, Vector2 size){
        Image image;
        try{
            if (size.x == 0 || size.y == 0){
                image = new Image(Main.class.getResourceAsStream(path));
            }
            else {
                image = new Image(Main.class.getResourceAsStream(path), SideScrollerGameV2.metersToPix(size.x), SideScrollerGameV2.metersToPix(size.y), false, false);
            }
        }
        catch (NullPointerException e ){
            image = new Image(Main.class.getResourceAsStream("/textures/squareTextures/ErrorSquare.png"), SideScrollerGameV2.metersToPix(size.x), SideScrollerGameV2.metersToPix(size.y), false, false);
        }
        return image;
    }

    public float getPixPerMeter(){return PIX_PER_METER;}

    public Map getMap(int mapNumber){return maps.get(mapNumber);}


}
