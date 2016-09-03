package com.sidescroller.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.game.Update;
import com.sidescroller.Map.RubeLoader.gushikustudios.RubeScene;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.RubeSceneLoader;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.game.Draw;
import com.sidescroller.game.InputListener;
import com.sidescroller.objects.Door;
import com.sidescroller.objects.Ladder;
import com.sidescroller.objects.actions.ButtonTrigger;
import com.sidescroller.objects.actions.BodyAction;
import com.sidescroller.objects.actions.BodyAction.TypeOfBodyAction;
import com.sidescroller.objects.actions.SensorTrigger;
import com.sidescroller.objects.GameShape;
import com.sidescroller.objects.JointData;
import com.sidescroller.objects.turret.PlayerTurret;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.turret.Turret;
import com.sidescroller.Character.Player;

import java.util.HashMap;

/**
 * Singelton class that loads and unloads maps.
 */
@SuppressWarnings("ProhibitedExceptionCaught")
public final class MapLoader {

    private RubeSceneLoader loader;
	//Needs to be 'HashMap'. 'Map' does not accept type parameters.
    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
	private HashMap<String, Map> loadedMaps;
	private AssetManager assetManager;
	private SideScrollGameV2 sideScrollGameV2;

	private static final String ERROR_SQUARE = "textures/ErrorSquare.png";
	private static final float SIZE_OF_ERROR_SQUARE = 0.5f;

    public MapLoader(SideScrollGameV2 sideScrollGameV2) {
		this.sideScrollGameV2 = sideScrollGameV2;
		assetManager = sideScrollGameV2.getAssetManager();
        loadedMaps = new HashMap<String, Map>(1);
        loader = new RubeSceneLoader();
    }

	public Map getMap(String mapPath){
		return loadedMaps.get(mapPath);
	}

	public boolean isLoaded(String mapPath){
		return loadedMaps.containsKey(mapPath);
	}

    public void loadMap(String mapPath){
		//@TODO This is a temporary fix for the door. When trying to reopen a world a nullpointerexception is thrown. Thus all worlds are reloaded all the time.
		loadedMaps.clear();

		sideScrollGameV2.getAssetManager().load(ERROR_SQUARE, Texture.class);
        //If the map is not yet loaded, then load it
        if (!loadedMaps.containsKey(mapPath)){
            RubeScene scene = loader.loadScene(Gdx.files.internal(mapPath));

			loadRubeImages(scene);
            Map map = new Map(scene.getWorld(), true, scene.velocityIterations, scene.positionIterations);

			setJointData(scene, map);

            //Adding all of the bodies to the map
            for (int x = 0; x < scene.getBodies().size; x++){
                Body body = scene.getBodies().get(x);
                Array<RubeImage> rubeImages = scene.getMappedImage(body);
                //Creating the arrays for the hashmap and adding images to the map if there are images then creating a GameShape.
                GameShape gameShape;
                if (rubeImages != null) {
					gameShape = new GameShape(map.getObjectID(), body, createRubeSprites(rubeImages, map));
                }
                else{
					gameShape = new GameShape(map.getObjectID(), body);
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
                    createBodyAction(gameShape, scene, map);
                }
                else if (type.toLowerCase().equals("buttontrigger")){
                    createButtonTrigger(gameShape, scene, map);
                }
                else if (type.toLowerCase().equals("sensortrigger")){
                    createSensorTrigger(gameShape, scene, map);
                }
				else if (type.toLowerCase().equals("turret")){
					createTurret(map, gameShape, scene);
				}
				else if (type.toLowerCase().equals("door")){
					createDoor(gameShape, scene, map);
				}
				else if (type.toLowerCase().equals("ladder")){
					createLadder(gameShape, map);
				}
                else{
                    map.addDrawObject(gameShape);
                }
            }

            loadedMaps.put(mapPath, map);
        }
    }

	private void createDoor(GameShape shape, RubeScene scene, Map map){
		Object worldToLoadObj;
		Object idCustomObj;

		worldToLoadObj = scene.getCustom(shape.getBody(), "worldfile");
		idCustomObj = scene.getCustom(shape.getBody(), "idCustom");
		try{
			String worldToLoad = (String) worldToLoadObj;
			int idCustom = (Integer) idCustomObj;

			Door door = new Door(map.getObjectID(), sideScrollGameV2, idCustom, worldToLoad, shape);
			map.addDrawObject(shape);
		}
		catch (ClassCastException e){
			System.out.println("ClassCastException in 'createDoor'. custom properties 'worldfile' and/or 'doorid' might be of the wrong type!");
			putErrorSquare(map, shape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
		}
		catch (NullPointerException e){
			System.out.println("NullPointerException in 'createDoor'. Custom properties 'worldfile' and/or 'doorid' not set up!");
			putErrorSquare(map, shape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
		}
	}

	/**
	 * Loads a player in to the world. Currently only one specific player.
	 * @param map The map to load the player to.
	 */
	public Player loadPlayer(Map map){
		//adding a player at specific position
		//@TODO Load playerinformation from file
		Player player = new Player(map.getObjectID(), map, sideScrollGameV2, new Vector2(2, 2), new Texture(Gdx.files.internal("textures/body.png")), 1, 1, 0.01f, 0.3f);
		map.addInputListener(player);
		map.addUpdateObject(player);
		map.addDrawObject(player);
		map.addCollisionListener(player);
		return player;
	}

	/**
	 * Creating a 'JointData' container for those joinst that have an id assigned to them. This is used when creating actions
     * relating to joints.
	 * @param scene The scene containing the joints.
	 * @param map The map to add the joints with a 'id' parameter to.
	 */
	private void setJointData(RubeScene scene, Map map){
		if (scene.getJoints() != null) {
			for (Joint joint : scene.getJoints()) {
				JointData jointData = new JointData();
				Object id;
				Object name;
				id = scene.getCustom(joint, "id");
				name = scene.getCustom(joint, "name");

				if (id != null) {
					try {
						jointData.setJointId((Integer) id);
						joint.setUserData(jointData);

						//TODO add theese joints to the actionManager in the map (create a system for joint actions in the ActionManager)
					} catch (ClassCastException e) {
						System.out.println(
								"Error corrected. ClassCastException when getting custom property! (joint id. Wrong input in map editor!)");
					}
				}
				if (name != null) {
					try {
						jointData.setName((String) name);
						joint.setUserData(jointData);
					} catch (ClassCastException e) {
						System.out.println(
								"Error corrected. ClassCastException when getting custom property! (joint name. Wrong input in map editor!)");
					}
				}
			}
		}
	}

	/**
	 * Converts the filepaths that the editor outputs when the the json file is exported in a certain way. May become
	 * absolete as we start setting the enviroment up in another way.
	 * @param scene The scene in wich to change the filepaths.
	 */
	private void loadRubeImages(RubeScene scene){
		//Loading the images in to the assetmanager
		for (RubeImage rubeImage : scene.getImages()){
			assetManager.load(rubeImage.file, Texture.class);
		}
		assetManager.update();
		assetManager.finishLoading();
	}

	/**
	 * Creates an array of 'RubeSprites' from a array of 'RubeImages'. Used when creating shapes and other things that
	 * needs drawing.
	 * @param rubeImages RubeImages loaded from json file, one of the parameters contained in a 'RubeSprite'.
	 * @param map The map so that the layer depth can be updated.
	 * @return Returns an array of 'RubeSprites'.
	 */
	private Array<RubeSprite> createRubeSprites(Iterable<RubeImage> rubeImages, Map map){
		if (rubeImages != null) {
			Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
			for (RubeImage rubeImage : rubeImages) {
				rubeSprites.add(new RubeSprite(rubeImage, assetManager.get(rubeImage.file, Texture.class)));
			}
			map.updateLayerDepth(rubeSprites);
			return rubeSprites;
		}
		else {
			return null;
		}
	}

	/**
	 * Creates a turret object.
	 * @param map The map to add the object to.
	 * @param gameShape The gameShape created with the body specifying this objects creation.
	 * @param scene The scene from wich to create the object.
	 */
	private void createTurret(Map map, GameShape gameShape, RubeScene scene){
		RubeSceneLoader turretLoader = new RubeSceneLoader(scene.getWorld());
		RubeScene turretScene;
		turretScene = turretLoader.loadScene(Gdx.files.internal("jsonFiles/turret.json"));

		setJointData(turretScene, map);

		//getting the needed bodies
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
				putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
			}
			catch (NullPointerException e){
				System.out.println("Error corrected. NullPointerException when getting custom property! (Turret)");
				putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
			}
		}

		//Getting the needed joints
        RevoluteJoint barrelRevoluteJoint = null;
		try {
			for (JointEdge jointEdge : turretBaseBody.getJointList()) {

				if (jointEdge.joint.getUserData() != null) {
					try {
						JointData jointData = (JointData) jointEdge.joint.getUserData();
						String name = jointData.getName();
						if (name.toLowerCase().equals("barreljoint")) {
							barrelRevoluteJoint = (RevoluteJoint) jointEdge.joint;
						}
					} catch (ClassCastException e) {
						System.out.println(
								"Error corrected. NullPointerException when getting custom property! (Turret joint (joint 'UserData' not a object of type 'JointData' !)");
						putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
					} catch (NullPointerException e) {
						System.out.println(
								"Error corrected. NullPointerException when getting custom property! (Turret joint (joint 'UserData' not )");
						putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
					}
				}
			}
		}
		catch (NullPointerException e) {
			System.out.println(
					"Error corrected. NullPointerException when getting custom property! (turretBaseBody.getJointList returned zero)");
			putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
		}

		String subType = null;
		try{
			Object subTypeObj = scene.getCustom(gameShape.getBody(), "subtype");
			if (subTypeObj != null) {
				subType = (String) subTypeObj;
			}
		}
		catch (ClassCastException e){
			System.out.println(
								"Error corrected. NullPointerException when getting custom property! ('subtype' property not a string, error in editor!)");
		}

		if(turretBaseBody != null && barrelBody != null && barrelRevoluteJoint != null) {
			loadRubeImages(turretScene);
			GameShape turretBase = new GameShape(map.getObjectID(), turretBaseBody, createRubeSprites(turretScene.getMappedImage(turretBaseBody), map));
			GameShape barrel = new GameShape(map.getObjectID(), barrelBody, createRubeSprites(turretScene.getMappedImage(barrelBody), map));
			turretBase.getBody().setTransform(gameShape.getBody().getPosition(), gameShape.getBody().getAngle());
			barrel.getBody().setTransform(gameShape.getBody().getPosition(), gameShape.getBody().getAngle());

			//Creating the type of turret specified in the subtype property
			if (subType == null){
				Turret turret = new Turret(map.getObjectID(), sideScrollGameV2, barrel, turretBase, barrelRevoluteJoint);
			}
			else if (subType.equals("manual")){
				InputListener playerTurret = new PlayerTurret(map.getObjectID(), sideScrollGameV2, barrel, turretBase, barrelRevoluteJoint);
				map.addInputListener(playerTurret);
				//TODO fix this without casting!!!!!
				map.addUpdateObject((Update) playerTurret);
			}
			else{
				Turret turret = new Turret(map.getObjectID(), sideScrollGameV2, barrel, turretBase, barrelRevoluteJoint);
			}

			map.addDrawObject(barrel);
			map.addDrawObject(turretBase);
		}
		else{
			for (Body body : turretScene.getBodies()){
				scene.getWorld().destroyBody(body);
			}
			putErrorSquare(map, gameShape.getBody().getPosition(), new Vector2(SIZE_OF_ERROR_SQUARE, SIZE_OF_ERROR_SQUARE));
			System.out.println("Error corrected. Error when creating turret!");
		}
		//Removing the body giving the position and creation information to the turret. Otherwise nullpointerException is thrown.
		map.removeDrawObject(gameShape);
		map.removeBody(gameShape.getBody());
	}

	private void putErrorSquare(Map map, Vector2 pos, Vector2 size){
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(pos);
		bodyDef.type = BodyType.StaticBody;
		RubeImage rubeImage = new RubeImage();
		rubeImage.angleInRads = 0;
		rubeImage.renderOrder = 0;
		rubeImage.file = ERROR_SQUARE;
		rubeImage.width = size.x;
		rubeImage.height = size.y;
		Array<RubeImage> rubeImages = new Array<RubeImage>(1);
		rubeImages.add(rubeImage);
		Array<RubeSprite> rubeSprites = createRubeSprites(rubeImages, map);
		Draw gameShape = new GameShape(map.getObjectID(), map.createBody(bodyDef), rubeSprites);
		map.addDrawObject(gameShape);
	}

    /**
     * Creates a 'ButtonTrigger' of specified type and adding it to the world.
     * @param gameShape The gameShape that belongs to the object.
     * @param scene The scene from wich to create the object.
     * @param map The map to add the object to.
     */
    private void createButtonTrigger(GameShape gameShape, RubeScene scene, Map map){

        Object actionIDObj = scene.getCustom(gameShape.getBody(), "idCustom");
        try{
            int actionID = (Integer) actionIDObj;
            ButtonTrigger buttonTrigger = new ButtonTrigger(map.getObjectID(), actionID, gameShape);
            map.getActionManager().addTrigger(buttonTrigger);
            map.addDrawObject(gameShape);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (ButtonTrigger)");
            map.addDrawObject(gameShape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (ButtonTrigger)");
            map.addDrawObject(gameShape);
        }
    }

    public void createSensorTrigger(GameShape gameShape, RubeScene scene, Map map){

        Object actionIDObj = scene.getCustom(gameShape.getBody(), "idCustom");
        try{
            int actionID = (Integer) actionIDObj;
            SensorTrigger sensorTrigger = new SensorTrigger(map.getObjectID(), actionID, gameShape);
            map.getActionManager().addTrigger(sensorTrigger);
            map.addDrawObject(gameShape);
            map.addCollisionListener(sensorTrigger);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (SensorTrigger)");
            map.addDrawObject(gameShape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (SensorTrigger)");
            map.addDrawObject(gameShape);
        }

    }

    /**
     * Creating a object of type 'BodyAction' and adding it to the world.
     * @param gameShape The gameShape that belongs to the object.
     * @param scene The scene from wich to construct the object.
     * @param map The map to add the object to.
     */
    private void createBodyAction(GameShape gameShape, RubeScene scene, Map map){

        Object subTypeObj = scene.getCustom(gameShape.getBody(), "subtype");
        Object actionIDObj = scene.getCustom(gameShape.getBody(), "idCustom");
        Object drawObj = scene.getCustom(gameShape.getBody(), "draw");
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
            TypeOfBodyAction typeOfBodyAction;

            if (subType.equals("make_dynamic")){
                typeOfBodyAction = TypeOfBodyAction.MAKE_DYNAMIC;
            }
            else if (subType.equals("remove")){
                typeOfBodyAction = TypeOfBodyAction.REMOVE;
            }
            else {
                typeOfBodyAction = TypeOfBodyAction.SPAWN;
            }

            //Needs '==' since the value may be null.
            if (!subType.equals("spawn") && draw){
                map.addDrawObject(gameShape);
            }
            BodyAction bodyAction = new BodyAction(map.getObjectID(), sideScrollGameV2,  actionID, gameShape, typeOfBodyAction);
            map.getActionManager().addAction(bodyAction);
        }
        catch (ClassCastException e){
            System.out.println("Error corrected. ClassCastException when getting custom property! (BodyAction)'");
            map.addDrawObject(gameShape);
        }
        catch (NullPointerException e){
            System.out.println("Error corrected. NullPointerException when getting custom property! (BodyAction)'");
            map.addDrawObject(gameShape);
        }
    }

	/**
	 * creates a ladder.
	 * @param gameShape
	 * @param map
	 */
	private void createLadder(GameShape gameShape, Map map){
		Ladder ladder = new Ladder(map.getObjectID(), sideScrollGameV2, gameShape);
		map.addUpdateObject(ladder);
		map.addCollisionListener(ladder);
	}
}



