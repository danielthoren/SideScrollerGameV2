package com.sidescroller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sidescroller.objects.Circle;
import com.sidescroller.objects.Shape;
import com.sidescroller.player.Player;

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
    private static final Vector2 GRAVITY = new Vector2(0, -10);

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
            Player player = new Player(map.getObjectID(), map.getWorld(), new Vector2(4,4), new Texture(Gdx.files.internal("badlogic.jpg")), 2f, 3f, 0.5f, 1f);
            map.addDrawObject(player, 0);
            map.addUpdateObject(player);
            map.addInputListener(player);

            Shape bottle = new Shape(map.getObjectID(), "test01", map.getWorld(), new Vector2(0, 3), true, "bottle.json", 2f, 4f);
            map.addDrawObject(bottle, 0);

            Circle circle = new Circle(map.getObjectID(), map.getWorld(), new Vector2(2f, 10f), false, 0.5f, 1f, 1f, 0.1f, new Texture(Gdx.files.internal("Coin.png")));
            map.addDrawObject(circle, 0);


            Shape floor = new Shape(map.getObjectID(), "floor", map.getWorld(), new Vector2(0, 0), true, "test.json", 2f, 15f);
            map.addDrawObject(floor, 1);

            maps.put(mapNumber, map);
        }
    }

    public float getPixPerMeter(){return PIX_PER_METER;}

    public Map getMap(int mapNumber){return maps.get(mapNumber);}


}
