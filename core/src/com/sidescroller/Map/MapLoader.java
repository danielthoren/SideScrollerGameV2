package com.sidescroller.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.sidescroller.Map.RubeLoader.gushikustudios.RubeScene;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.RubeSceneLoader;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.objects.Actions.ButtonTrigger;
import com.sidescroller.objects.Actions.SpawnAction;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.Shape;
import com.sidescroller.player.Player;

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
	private Json json;

	//TODO fix converter that fixes image paths
    private MapLoader() {
        loadedMaps = new HashMap<String, Map>(1);
        loader = new RubeSceneLoader();
		json = new Json();
    }

    public static MapLoader getInstance(){return instance;}

    public Map loadMap(String mapPath){
        if (!loadedMaps.containsKey(mapPath)){
            RubeScene scene = loader.loadScene(Gdx.files.internal(mapPath));

            //removes the '../' in each image filepath that the editor generates
            for (RubeImage rubeImage : scene.getImages()){
                rubeImage.file = rubeImage.file.substring(3);
            }

            Map map = new Map(scene.getWorld(), true, scene.velocityIterations, scene.positionIterations);

            //Adding all of the bodies to the map
            int p = 0;
            for (int x = 0; x < scene.getBodies().size; x++){
                Body body = scene.getBodies().get(x);
                Array<RubeImage> rubeImages = scene.getMappedImage(body);
                //Creating the arrays for the hashmap and adding images to the map if there are images then creating a Shape.
                Shape shape;
                if (rubeImages != null) {
                    Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
                    for (RubeImage rubeImage : rubeImages){
                        rubeSprites.add(new RubeSprite(rubeImage));
                    }
                    map.updateLayerDepth(rubeSprites);
                    shape = new Shape(map.getObjectID(), body, rubeSprites);
                }
                else{
                    shape = new Shape(map.getObjectID(), body);
                }

                //Checking for the type variable in custom and creating object accordingly
                String type = (String) scene.getCustom(body, "type");

				if (type == null){
					map.addDrawObject(shape);
				}
                else if (type.toLowerCase().equals("spawnaction")){
					try {
						int actionID = (Integer) scene.getCustom(body, "id");
						SpawnAction spawnAction = new SpawnAction(actionID, shape);
						map.getActionManager().addAction(spawnAction);
					}
					catch (ClassCastException e){
						System.out.println("Error corrected. ClassCastException when getting custom property 'id'");
						map.addDrawObject(shape);
					}
                }
                else if (type.toLowerCase().equals("buttontrigger")){
					try {
						int targetActionID = (Integer) scene.getCustom(body, "id");
						ButtonTrigger buttonTrigger = new ButtonTrigger(targetActionID, shape);
						map.getActionManager().addTrigger(buttonTrigger);
						map.addDrawObject(shape);
					}
					catch (ClassCastException e){
						System.out.println("Error corrected. ClassCastException when getting custom property 'id'");
						map.addDrawObject(shape);
					}
                }
            }

            //adding a player at specific position
            //@TODO Load playerinformation from file
            Player player = new Player(map.getObjectID(), scene.getWorld(), new Vector2(2f, 2f), new Texture(Gdx.files.internal("body.png")), 1f, 1f, 0.01f, 0.2f);
            map.addInputListener(player);
            map.addUpdateObject(player);
            map.addDrawObject(player);
            map.addCollisionListener(player);

            loadedMaps.put(mapPath, map);
        }
        return loadedMaps.get(mapPath);
    }
}
