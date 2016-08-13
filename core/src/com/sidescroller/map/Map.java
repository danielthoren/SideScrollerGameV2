package com.sidescroller.map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.*;
import com.sidescroller.objects.RubeSprite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the different values of the map. Having more than one instance of the map may be importaint in the future when
 * multiple maps may be needed for one session (doors and such).
 */
public class Map
{
    private ContactListenerGame contactListenerGame;
    private InputHandler inputHandler;

    private Box2DDebugRenderer debugRenderer;
    private ActionManager actionManager;

    private List<Draw> drawObjects;
    private List<Update> updateObjects;
    private List<InputListener> inputListenerList;
    private List<CollisionListener> collisionListenerList;

    private List<Long> drawObjectsStagedForRemoval;
    private List<Long> updateObjectsStagedForRemoval;
    private List<Long> inputListenersStagedForRemoval;
    private List<Long> collisionListenersStagedForRemoval;
    private List<Body> bodiesStagedForRemoval;

    private List<Draw> drawObjectsStagedForAddition;
    private List<Update> updateObjectsStagedForAddition;
    private List<InputListener> inputListenersStagedForAddition;
    private List<CollisionListener> collisionListenersStagedForAddition;

    private Array<Body> tmpBodies;

    private final int velocityIterations;
    private final int positionIterations;

    private final float updateTime = 1f / 60f;

    private float updateTimer;
    private long objectID;
    private int layerCount;

    private World world;

    /**
     * Creates an instance of 'Map' wich is a container for all of the world objects. It also contains an abstractionlayer
     * for removing and adding new objects to the world, preventing the program from crashing.
     */
    public Map(World world, boolean doSleep, int velocityIterations, int positionIterations) {
        this.world = world;
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
        objectID = 0;
        layerCount = 1;
        updateTimer = 0;
        debugRenderer = new Box2DDebugRenderer();
        drawObjects = new ArrayList<Draw>(layerCount);
        updateObjects = new ArrayList<Update>(10);
        collisionListenerList = new ArrayList<CollisionListener>(10);
        inputListenerList = new ArrayList<InputListener>(10);
        drawObjectsStagedForRemoval = new ArrayList<Long>(2);
        updateObjectsStagedForRemoval = new ArrayList<Long>(2);
        inputListenersStagedForRemoval = new ArrayList<Long>(2);
        collisionListenersStagedForRemoval = new ArrayList<Long>(2);
        bodiesStagedForRemoval = new ArrayList<Body>(2);
        drawObjectsStagedForAddition = new ArrayList<Draw>(layerCount);
        updateObjectsStagedForAddition = new ArrayList<Update>(2);
        inputListenersStagedForAddition = new ArrayList<InputListener>(2);
        collisionListenersStagedForAddition = new ArrayList<CollisionListener>(2);
		actionManager = new ActionManager();
        tmpBodies = new Array<Body>(10);
        contactListenerGame = new ContactListenerGame(this);
        inputHandler = new InputHandler(this);
        world.setContactListener(contactListenerGame);
    }

    /**
     * Removes objects staged for removal and clears the 'StagedForRemoval' lists. Theese lists are a buffer to prevent
     * instantanious removal of objects during runtime, if objects were removed during iteration over lists containing theese
     * objects or during the world step then the program would crash.
     */
    public void removeStagedOBjects (){
        //List used to keep track of wich id:s bodies have been removed. Used to prevent multiple removals of the same body.
        //If a item that will be staged for removal when collision occurs with a type of object then two such bodies will
        //be added to the 'stagedForRemoval' if the item collides with two objects of said type between one step.
        //This will cause an error when trying to remove the body already removed. Thus this prevents that from happening.
        Collection<Long> bodyIDRemoved = new ArrayList<Long>();
        //Destroying all of the bodies that are staged for removal
        for (Body body : bodiesStagedForRemoval){
            if (!bodyIDRemoved.contains(((GameObject)body.getUserData()).getId())) {
                bodyIDRemoved.add(((GameObject)body.getUserData()).getId());
                world.destroyBody(body);
            }
        }

        //Removing all of the 'Draw' objects from the maps global list
        for (long drawObjectRemove : drawObjectsStagedForRemoval){
            for (Iterator<Draw> iterator = drawObjects.iterator(); iterator.hasNext();){
                Draw object = iterator.next();
                if (drawObjectRemove == object.getId()){
                    iterator.remove();
                }
            }

        }

        //Removing all of the 'Update' objects from the maps global list
        for (long objectRemove : updateObjectsStagedForRemoval){
            for (Iterator<Update> iterator = updateObjects.iterator(); iterator.hasNext();){
                Update object = iterator.next();
                if (objectRemove == object.getId()){
                    iterator.remove();
                }
            }
        }
        //Removing all of the 'gamelogic.InputListener' objects from the maps global list
        for (long listenerRemova : inputListenersStagedForRemoval){
            for (Iterator<InputListener> iterator = inputListenerList.iterator(); iterator.hasNext();) {
                InputListener inputListener = iterator.next();
                if (inputListener.getId() == listenerRemova){
                    iterator.remove();
                }
            }
        }
        //Removing all of the 'gamelogic.CollisionListener' objects from the maps global list
        for (long collisionListenerRemove : collisionListenersStagedForRemoval){
            for (Iterator<CollisionListener> iterator = collisionListenerList.iterator(); iterator.hasNext();){
                CollisionListener collisionListener = iterator.next();
                if (collisionListener.getId() == collisionListenerRemove) {
                    iterator.remove();
                }
            }
        }
        //Clearing all of the 'StagedForRemoval' lists
        bodiesStagedForRemoval.clear();
        updateObjectsStagedForRemoval.clear();
        drawObjectsStagedForRemoval.clear();
        inputListenersStagedForRemoval.clear();
        collisionListenersStagedForRemoval.clear();
}

    /**
     * Adding objects staged for addition and clears the 'StagedForAddition' lists. Theese lists are a buffer to prevent
     * instantanious addition of objects during runtime, if objects were removed during iteration over lists containing theese
     * objects or during the world step then the program would crash.
     */
    public void addStagedObjects (){
        //Adding all of the staged 'Draw' objects to the maps global list
        for (Draw object : drawObjectsStagedForAddition){
            drawObjects.add(object);
        }
        //Adding all of the staged 'Update' objects to the maps global list
        for (Update object : updateObjectsStagedForAddition){
            updateObjects.add(object);
        }
        //Adding all of the staged 'gamelogic.InputListener' objects to the maps global list
        for (InputListener listener : inputListenersStagedForAddition){
            inputListenerList.add(listener);
        }
        //Adding all of the staged 'gamelogic.CollisionListener' objects to the maps global list
        for (CollisionListener collisionListener : collisionListenersStagedForAddition){
            collisionListenerList.add(collisionListener);
        }
        //Clearing the objects staged for addition
        drawObjectsStagedForAddition.clear();
        updateObjectsStagedForAddition.clear();
        inputListenersStagedForAddition.clear();
        collisionListenersStagedForAddition.clear();
    }

    public void update(){
        //Updating all of the objects
        for (Update updateObj : updateObjects){
            updateObj.update();
        }
        actionManager.update();
        updateTimer += updateTime;
        if (updateTimer >= 5){
            boundryCheck();
            updateTimer = 0;
        }

        addStagedObjects();
        removeStagedOBjects();
    }

    public void draw(SpriteBatch batch){
        //Drawing the drawobjects
        for (int layer = 0; layer <= layerCount; layer++) {
            for (Draw obj : drawObjects) {
                obj.draw(batch, layer);
            }
        }
    }

	/**
     * Checks if any bodies have fallen to far. If so then removes thoose bodies. This is done to prevent crashes from the
     * y-coordinate being to big to fit in a float.
     */
    private void boundryCheck(){
        world.getBodies(tmpBodies);
        for (Body body : tmpBodies){
            if (body.getPosition().y < -1000){
                try{
                    GameObject gameObject = (GameObject) (body.getUserData());
                    drawObjectsStagedForRemoval.add(gameObject.getId());
                    updateObjectsStagedForRemoval.add(gameObject.getId());
                    inputListenersStagedForRemoval.add(gameObject.getId());
                    collisionListenersStagedForRemoval.add(gameObject.getId());
                    updateObjectsStagedForRemoval.add(gameObject.getId());
                    world.destroyBody(body);
                }
                catch (ClassCastException e){
                    world.destroyBody(body);
                }
            }
        }
    }


    /**
     * Draws the outlinings of all of the worlds fixtures.
     * @param camera The camera with wich matrix to draw.
     */
    public void debugDraw(Camera camera){
        debugRenderer.render(world, camera.combined);
    }

    /**
     * Creates a body using the world
     * @param bodyDef The bodydef from wich to create the body.
     * @return Returns the created body.
     */
    public Body createBody(BodyDef bodyDef){return world.createBody(bodyDef);}

    /**
     * @return Returns a unique id.
     */
    public long getObjectID() {
        objectID++;
        return objectID;
    }

    /**
     * Steps the world, moving the simulation forward.
     * @param timeParam The timeparameter to send to the world.
     */
    public void stepWorld(float timeParam){world.step(timeParam, velocityIterations, positionIterations);}

    public void removeBody(Body body){
        if (body != null) {
            //Controlling so that the body has a object implementing the interface 'GameObject' as userdata. If this is
            //not the case, then tries to destroy the body with the world and hopes that multiple calls for removal of this
            //body does not occur. If it does it will result in a crash!
            try {
                GameObject gameObject = (GameObject) body.getUserData();
                bodiesStagedForRemoval.add(body);
            }
            catch (ClassCastException e){
                System.out.println("Error cought! Body with 'UserData' that does not implement 'GameObject'!");
                world.destroyBody(body);
            }

        }
    }

    public void removeCollisionListener(CollisionListener listener){
        if (listener != null) {
            collisionListenersStagedForRemoval.add(listener.getId());
        }
    }

    public void removeInputListener(InputListener listener){
        if (listener != null) {
            inputListenersStagedForRemoval.add(listener.getId());
        }
    }

    public void removeDrawObject(Draw object){
        if (object != null) {
            drawObjectsStagedForRemoval.add(object.getId());
        }
    }

    public void removeUpdateObject(Update object){
        if (object != null) {
            updateObjectsStagedForRemoval.add(object.getId());
        }
    }

    public void addDrawObject(Draw object) {
        if (object != null) {
            drawObjectsStagedForAddition.add(object);
        }
    }

    public void addUpdateObject(Update object) {
        if (object != null) {
            updateObjectsStagedForAddition.add(object);
        }
    }

    public void addInputListener(InputListener object) {
        if (object != null) {
            inputListenersStagedForAddition.add(object);
        }
    }

    public void addCollisionListener(CollisionListener object) {
        if (object != null) {
            collisionListenersStagedForAddition.add(object);
        }
    }

    public float getUpdateTime() {return updateTime;}

    public Iterable<Draw> getDrawObjects() {return drawObjects;}

    public Iterable<Update> getUpdateObjects() {return updateObjects;}

    public Iterable<InputListener> getInputListenerList() {return inputListenerList;}

    public Iterable<CollisionListener> getCollisionListenerList() {return collisionListenerList;}

    public int getLayerCount() {return layerCount;}

    public ActionManager getActionManager(){return actionManager;}

    public Vector2 getGravity(){return world.getGravity();}

    public InputHandler getInputHandler(){return inputHandler;}

    /**
     * Sets the drawLayerCount variable to the highest value found in the list, if any exceeds the one already in the map.
     * @param rubeSprites A list of RubeSprite objects
     */
    public void updateLayerDepth(Iterable<RubeSprite> rubeSprites){
        for (RubeSprite rubeSprite : rubeSprites){
            if (rubeSprite.getRubeImage().renderOrder > layerCount){
                layerCount = rubeSprite.getRubeImage().renderOrder;
            }
        }
    }

	/**
     * Updates the layerdepth with the given integer.
     * @param layer The depth of layer to update with.
     */
    public void updateLayerDepth(int layer){
        layerCount = (layerCount > layer) ?  layerCount : layer;
    }
}
