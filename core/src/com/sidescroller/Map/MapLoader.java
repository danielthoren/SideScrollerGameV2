package com.sidescroller.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.sidescroller.Map.RubeLoader.gushikustudios.RubeDefaults;
import com.sidescroller.Map.RubeLoader.gushikustudios.RubeScene;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.RubeSceneLoader;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.objects.Actions.ButtonTrigger;
import com.sidescroller.objects.Actions.BodyAction;
import com.sidescroller.objects.Actions.SensorTrigger;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.Shape;
import com.sidescroller.objects.Turret;
import com.sidescroller.player.Player;

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

    private MapLoader() {
        loadedMaps = new HashMap<String, Map>(1);
        loader = new RubeSceneLoader();
		json = new Json();
    }

    public static MapLoader getInstance(){return instance;}

    public Map loadMap(String mapPath){
        //If the map is not yet loaded, then load it
        if (!loadedMaps.containsKey(mapPath)){
            RubeScene scene = loader.loadScene(Gdx.files.internal(mapPath));

			convertFilePath(scene);

            Map map = new Map(scene.getWorld(), true, scene.velocityIterations, scene.positionIterations);

            //Adding all of the bodies to the map
            int p = 0;
            for (int x = 0; x < scene.getBodies().size; x++){
                Body body = scene.getBodies().get(x);
                Array<RubeImage> rubeImages = scene.getMappedImage(body);
                //Creating the arrays for the hashmap and adding images to the map if there are images then creating a Shape.
                Shape shape;
                if (rubeImages != null) {
					shape = new Shape(map.getObjectID(), body, createRubeSprites(rubeImages, map));
                }
                else{
                    shape = new Shape(map.getObjectID(), body);
                }

                //Checking for the type variable in custom and creating object accordingly
                Object typeObj = scene.getCustom(body, "type");
                String type;
                try{
                    if (typeObj != null) {
                        type = (String) typeObj;
                    }
                    else {
                        type = "";
                    }
                }
                catch (ClassCastException e){
                    System.out.println("Error corrected. ClassCastException when getting custom property! (type)");
                    type = "";
                }

                //Creating the object that the 'type' variable specifies.
                if (type.toLowerCase().equals("bodyaction")){
                    createBodyAction(shape, scene, map);
                }
                else if (type.toLowerCase().equals("buttontrigger")){
                    createButtonTrigger(shape, scene, map);
                }
                else if (type.toLowerCase().equals("sensortrigger")){
                    createSensorTrigger(shape, scene, map);
                }
				else if (type.toLowerCase().equals("turret")){
					createTurret(map, shape, scene);
				}
                else{
                    map.addDrawObject(shape);
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

	private void convertFilePath(RubeScene scene){
		//removes the '../' in each image filepath that the editor generates
		//TODO fix converter that fixes image paths
		for (RubeImage rubeImage : scene.getImages()){
			rubeImage.file = rubeImage.file.substring(3);
		}
	}

	private Array<RubeSprite> createRubeSprites(Array<RubeImage> rubeImages, Map map){
		Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
		for (RubeImage rubeImage : rubeImages){
			rubeSprites.add(new RubeSprite(rubeImage));
		}
		map.updateLayerDepth(rubeSprites);
		return  rubeSprites;
	}

	private void createTurret(Map map, Shape shape, RubeScene scene){
		RubeSceneLoader turretLoader = new RubeSceneLoader(scene.getWorld());
		RubeScene turretScene;
		turretScene = turretLoader.loadScene(Gdx.files.internal("turret.json"));

		Body turretBaseBody = null;
		Body barrelBody = null;

		for (Body body : turretScene.getBodies()){
			Object partObj = turretScene.getCustom(body, "part");
			try{
				String part = (String) partObj;
				if (part.toLowerCase().equals("barrel")){
					barrelBody = body;
				}
				else if (part.toLowerCase().equals("turretbase")){
					turretBaseBody = body;
				}
			}
			catch (ClassCastException e){
				System.out.println("Error corrected. ClassCastException when getting custom property! (Turret)");
				map.addDrawObject(shape);
			}
			catch (NullPointerException e){
				System.out.println("Error corrected. NullPointerException when getting custom property! (Turret)");
				map.addDrawObject(shape);
			}
		}

		if(turretBaseBody != null && barrelBody != null) {
			convertFilePath(turretScene);
			Shape turretBase = new Shape(map.getObjectID(), turretBaseBody, createRubeSprites(turretScene.getMappedImage(turretBaseBody), map));
			Shape barrel = new Shape(map.getObjectID(), barrelBody, createRubeSprites(turretScene.getMappedImage(barrelBody), map));
			turretBase.getBody().setTransform(shape.getBody().getPosition(), shape.getBody().getAngle());
			barrel.getBody().setTransform(shape.getBody().getPosition(), shape.getBody().getAngle());

			System.out.println(shape.getBody().getPosition());

			Turret turret = new Turret(map.getObjectID(), barrel, turretBase);

			map.addDrawObject(barrel);
			map.addDrawObject(turretBase);
			map.addUpdateObject(turret);
		}
	}

    /**
     * Creates a 'ButtonTrigger' of specified type and adding it to the world.
     * @param shape The shape that belongs to the object.
     * @param scene The scene from wich to create the object.
     * @param map The map to add the object to.
     */
    private void createButtonTrigger(Shape shape, RubeScene scene, Map map){

        Object actionIDObj = scene.getCustom(shape.getBody(), "id");
        try{
            int actionID = (Integer) actionIDObj;
            ButtonTrigger buttonTrigger = new ButtonTrigger(map.getObjectID(), actionID, shape);
            map.getActionManager().addTrigger(buttonTrigger);
            map.addDrawObject(shape);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (ButtonTrigger)");
            map.addDrawObject(shape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (ButtonTrigger)");
            map.addDrawObject(shape);
        }
    }

    public void createSensorTrigger(Shape shape, RubeScene scene, Map map){

        Object actionIDObj = scene.getCustom(shape.getBody(), "id");
        try{
            int actionID = (Integer) actionIDObj;
            SensorTrigger sensorTrigger = new SensorTrigger(map.getObjectID(), actionID, shape);
            map.getActionManager().addTrigger(sensorTrigger);
            map.addDrawObject(shape);
            map.addCollisionListener(sensorTrigger);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (SensorTrigger)");
            map.addDrawObject(shape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (SensorTrigger)");
            map.addDrawObject(shape);
        }

    }

    /**
     * Creating a object of type 'BodyAction' and adding it to the world.
     * @param shape The shape that belongs to the object.
     * @param scene The scene from wich to construct the object.
     * @param map The map to add the object to.
     */
    private void createBodyAction(Shape shape, RubeScene scene, Map map){

        Object subTypeObj = scene.getCustom(shape.getBody(), "subtype");
        Object actionIDObj = scene.getCustom(shape.getBody(), "id");
        Object drawObj = scene.getCustom(shape.getBody(), "draw");
        try {
            String subType = (String) subTypeObj;
            int actionID = (Integer) actionIDObj;
            boolean draw;
            //the draw parameter is not absolutely needed to create this object, though it is preffered.
            if (drawObj != null) {
                draw = (Boolean) drawObj;
            }
            else {
                draw = true;
            }
            BodyAction.TypeOfBodyAction typeOfBodyAction;

            if (subType.equals("make_dynamic")){
                typeOfBodyAction = BodyAction.TypeOfBodyAction.MAKE_DYNAMIC;
            }
            else if (subType.equals("remove")){
                typeOfBodyAction = BodyAction.TypeOfBodyAction.REMOVE;
            }
            else {
                typeOfBodyAction = BodyAction.TypeOfBodyAction.SPAWN;
            }

            //Needs '==' since the value may be null.
            if (!subType.equals("spawn") && draw){
                map.addDrawObject(shape);
            }
            BodyAction bodyAction = new BodyAction(actionID, shape, typeOfBodyAction);
            map.getActionManager().addAction(bodyAction);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (BodyAction)'");
            map.addDrawObject(shape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (BodyAction)'");
            map.addDrawObject(shape);
        }
    }
}



